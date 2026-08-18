# 代码评审报告 (Code Review Report)

**评审对象:** 登录系统实现 (Login System Implementation)
**评审范围:** `README.md`, `go.mod`, `internal/db/db.go`, `internal/migrate/migrate.go`, `cmd/server/main.go`, `internal/dto/auth_request.go`, `internal/dto/auth_response.go`, `internal/handler/auth_handler.go`, `internal/middleware/auth_middleware.go`, `internal/middleware/rate_limit_middleware.go`, `internal/models/session.go`, `internal/models/user.go`, `internal/ratelimit/ratelimiter.go`, `internal/repository/session_repository.go`, `internal/repository/user_repository.go`, `internal/service/auth_service.go`, `internal/session/manager.go`, `pkg/crypto/password.go`, `tests/integration/auth_test.go`
**评审日期:** 2025-08-20

---

## 1. 总体评价

本次实现整体架构清晰，采用了 Clean Architecture 分层设计（Handler → Service → Repository），代码结构良好，集成测试覆盖了主要验收场景。但存在 **3 个阻塞性问题（blocking）**，涉及安全、并发正确性，必须在合并前修复。

---

## 2. 🔴 阻塞性问题 (Blocking)

### 🔴 B-1: 密码哈希使用 SHA256 而非 bcrypt/Argon2

**文件:** `pkg/crypto/password.go`
**行号:** 1-67
**描述:** 当前实现使用 SHA256+salt 进行密码哈希，而实施计划明确要求使用 bcrypt（cost ≥ 12）或 Argon2id（见计划 5.1 节）。SHA256 不是为密码哈希设计的算法，抗彩虹表和暴力破解能力远弱于 bcrypt/Argon2。
**影响:** 直接违反验收标准 AC-8（"密码在数据库中以明文或弱哈希存储" → 测试应失败）。
**建议:** 引入 `golang.org/x/crypto/bcrypt` 实现 `PasswordHasher` 接口，替换 `SHA256Hasher`。

```go
// 建议实现
type BcryptHasher struct {
    cost int
}

func (h *BcryptHasher) Hash(password string) (string, error) {
    bytes, err := bcrypt.GenerateFromPassword([]byte(password), h.cost)
    return string(bytes), err
}

func (h *BcryptHasher) Verify(password, hash string) error {
    return bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
}
```

---

### 🔴 B-2: RateLimiter.IsLocked 存在数据竞争

**文件:** `internal/ratelimit/ratelimiter.go`
**行号:** 42-65
**描述:** `IsLocked` 方法持有 `RLock`（读锁），但在方法内部第 60 行修改了 `info.lockedUntil`（写操作）。这违反了 Go 的读写锁语义，在高并发场景下会导致数据竞争（data race）。

```go
// 问题代码 (ratelimiter.go:52-61)
if info.lockedUntil != nil && time.Now().Before(*info.lockedUntil) {
    return true, info.lockedUntil.Sub(time.Now())
}
// ...
if info.count >= 5 && time.Since(info.lastFail) < 15*time.Minute {
    lockedUntil := info.lastFail.Add(15 * time.Minute)
    info.lockedUntil = &lockedUntil  // ← 在 RLock 下写入！
    return true, lockedUntil.Sub(time.Now())
}
```

**建议:** `IsLocked` 应使用 `Lock()`（写锁），或采用更精细的锁策略。由于该方法会修改状态，不应使用读锁。

---

### 🔴 B-3: 防时序攻击的假哈希与真实哈希器不匹配

**文件:** `internal/service/auth_service.go`
**行号:** 57-65
**描述:** 当账号不存在时，代码执行假哈希比较以防御时序攻击：

```go
if user == nil {
    _ = s.passwordHasher.Verify(password, "$2a$12$fakehashforconstanttimecomparison")
    return &LoginResult{...}
}
```

然而实际注入的 `passwordHasher` 是 `SHA256Hasher`，其 `Verify` 方法解析的是 `salt:hash` 格式。传入的 `$2a$12$...` 字符串无法被正确解析（`parts` 长度为 1），`Verify` 会立即返回错误，导致假比较的执行时间远短于真实比较，失去了时序攻击防护意义。

**建议:** 确保假哈希格式与真实哈希器格式一致；或统一使用 bcrypt（修复 B-1 后此问题自动解决，因为假哈希格式就是 bcrypt 格式）。

---

## 3. 🟡 重要问题 (Important)

### 🟡 I-1: RateLimiter 失败次数在锁定期后未重置

**文件:** `internal/ratelimit/ratelimiter.go`
**描述:** 当 `IsLocked` 发现锁定期已过（`time.Now().After(*info.lockedUntil)`），仅返回 `false`，但 `info.count` 并未重置。这意味着用户在锁定期后再次登录失败时，由于 `count >= 5`，会立即被重新锁定，导致实际上永久锁定。
**建议:** 锁定期过期时同步重置 `count` 和 `lockedUntil`。

---

### 🟡 I-2: IPRateLimiter 存在内存泄漏

**文件:** `internal/ratelimit/ratelimiter.go`
**行号:** 97-116
**描述:** `IPRateLimiter.Allow` 在清理旧记录时，仅保留当前窗口内的请求时间。但如果某个 IP 长期不再访问，其条目仍会永远保留在 `r.requests` map 中，导致内存泄漏。
**建议:** 添加定期清理逻辑，或限制 map 总大小。

---

### 🟡 I-3: X-Forwarded-For 直接取第一个 IP 存在伪造风险

**文件:** `internal/handler/auth_handler.go:68-69`, `internal/middleware/rate_limit_middleware.go:22-24`
**描述:** 代码直接从 `X-Forwarded-For` 请求头取第一个 IP 作为客户端真实 IP，攻击者可轻易伪造此头部绕过 IP 限流。
**建议:** 仅在受信任的反向代理环境中使用 `X-Forwarded-For`，并配置可信代理 IP 列表；生产环境建议从 `X-Real-IP` 或 `CF-Connecting-IP` 获取。

---

### 🟡 I-4: 服务端未强制 HTTPS

**文件:** `cmd/server/main.go`
**描述:** 服务器使用 `http.ListenAndServe` 启动，无 TLS 支持。`Secure` cookie 标志默认设为 `false`，不满足生产环境安全要求。
**建议:** 提供 HTTPS 配置选项；在生产环境中强制启用 TLS。

---

### 🟡 I-5: Logout 端点未校验 HTTP 方法

**文件:** `internal/handler/auth_handler.go:97-109`
**描述:** `Logout` 方法未检查请求是否为 POST，任何 HTTP 方法都可触发登出。
**建议:** 在 `main.go` 路由注册或 handler 内部添加方法校验。

---

### 🟡 I-6: Session 并发数限制存在竞态窗口

**文件:** `internal/session/manager.go:36-46`
**描述:** `CountByUserID` 和 `DeleteOldestByUserID` 是两个独立操作，中间无锁保护，在并发场景下可能创建超过限制数量的会话。
**建议:** 在 `SessionRepository` 接口层面将计数和删除合并为原子操作，或在 session manager 层使用全局锁。

---

### 🟡 I-7: 多处 JSON 编码错误未处理

**文件:** `internal/handler/auth_handler.go`, `internal/middleware/auth_middleware.go`
**描述:** `json.NewEncoder(w).Encode(...)` 的错误返回值被忽略。虽然 HTTP 响应已写入头部，但编码错误会导致客户端收到截断的 JSON。
**建议:** 统一包装响应编码逻辑，至少记录日志。

---

## 4. 🟢 建议优化 (Nit / Suggestion)

| # | 问题 | 位置 | 建议 |
|---|------|------|------|
| N-1 | 密码仅校验长度，未校验复杂度 | `auth_handler.go:47-55` | 增加大小写字母、数字、特殊字符等复杂度要求 |
| N-2 | Login 接口未校验 Content-Type | `auth_handler.go:35` | 拒绝非 `application/json` 请求 |
| N-3 | 缺少安全事件审计日志 | 全局 | 记录登录成功/失败/锁定/登出等事件 |
| N-4 | 魔术数字硬编码 | 多处 | 将 `5`, `15`, `2*60*60` 等提取为配置常量 |
| N-5 | 缺少 CSRF 防护 | `session/manager.go` | 考虑 SameSite=Strict 或引入 CSRF Token |
| N-6 | `tests.test` 和 `server` 二进制未忽略 | 根目录 | 添加 `.gitignore` 排除编译产物 |

---

## 5. 验收标准覆盖检查

| 编号 | 验收项 | 状态 | 备注 |
|------|--------|------|------|
| AC-1 | 正确登录 | ✅ | 实现正确 |
| AC-2 | 错误凭证返回统一消息 | ✅ | 实现正确 |
| AC-3 | 连续 5 次失败后锁定 | ⚠️ | 基本实现，但锁定后无法自动恢复（见 I-1） |
| AC-4 | 禁用账号返回 403 | ✅ | 实现正确 |
| AC-5 | 有效 Cookie 访问受保护接口 | ✅ | 实现正确 |
| AC-6 | 无效 Cookie 返回 401 | ✅ | 实现正确 |
| AC-7 | 退出登录清除 Cookie | ✅ | 实现正确 |
| AC-8 | 密码使用 bcrypt/Argon2 | ❌ | **使用 SHA256，未通过**（见 B-1） |

---

## 6. 评审结论

- **阻塞性问题:** 3 个（B-1, B-2, B-3）
- **重要问题:** 7 个
- **建议优化:** 6 个

**结论: 🔴 存在阻塞性问题，不建议合并。**

请在修复 B-1（更换密码哈希算法）、B-2（修复数据竞争）、B-3（修复时序攻击防护）后重新提交评审。
