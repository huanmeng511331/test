# 登录系统实现 — 代码评审报告

> **评审对象：** 登录系统实现（Login System Implementation）
> **评审范围：** `README.md`, `go.mod`, `internal/db/db.go`, `internal/migrate/migrate.go`, `cmd/server/main.go`, `internal/dto/auth_request.go`, `internal/dto/auth_response.go`, `internal/handler/auth_handler.go`, `internal/middleware/auth_middleware.go`, `internal/middleware/rate_limit_middleware.go`, `internal/models/session.go`, `internal/models/user.go`, `internal/ratelimit/ratelimiter.go`, `internal/repository/session_repository.go`, `internal/repository/user_repository.go`, `internal/service/auth_service.go`, `internal/session/manager.go`, `pkg/crypto/password.go`, `tests/integration/auth_test.go`
> **评审日期：** 2025-08-20

---

## 1. 总体评价

本次实现整体架构清晰，采用了 Clean Architecture 分层设计（Handler → Service → Repository），代码结构良好，集成测试覆盖了主要验收场景。但存在 **3 个阻塞性问题（blocking）**，涉及安全、资源管理与正确性，必须在合并前修复。

---

## 2. 🔴 阻塞性问题 (Blocking)

### 🔴 B-1: 假哈希格式无效，时序攻击防护失效

**文件:** `internal/service/auth_service.go:59`
**代码:**
```go
_ = s.passwordHasher.Verify(password, "$2a$12$fakehashforconstanttimecomparison")
```

**问题:** 该字符串不是合法的 bcrypt 哈希。`bcrypt.CompareHashAndPassword` 在接收到格式错误的哈希时会 **立即返回错误**（耗时 ≈0 ms），而对比真实 bcrypt 哈希（cost=12）约需 **100 ms**。攻击者可以通过测量响应时间差异轻松枚举有效账号，完全违背了“防枚举”设计目标。

**建议修复:**
```go
// 在包初始化时生成一个合法的假哈希
var fakeHash string

func init() {
    fakeHash, _ = bcrypt.GenerateFromPassword([]byte("fake"), bcrypt.DefaultCost)
}

// 登录流程中使用
_ = s.passwordHasher.Verify(password, string(fakeHash))
```

---

### 🔴 B-2: RateLimiter 内存泄漏 / DoS 风险

**文件:** `internal/ratelimit/ratelimiter.go:11`
**代码:**
```go
type RateLimiter struct {
    mu       sync.RWMutex
    attempts map[string]*attemptInfo
}
```

**问题:** `attempts` 中的条目仅在成功登录时通过 `Reset()` 删除。对于不存在的账号或持续失败的暴力破解请求，条目将 **无限累积**，最终耗尽内存导致 OOM。在生产环境中，攻击者可以轻易利用此漏洞发起 DoS 攻击。

**建议修复:**
1. 为 `attemptInfo` 增加 TTL 机制，定期清理过期条目；
2. 或改用支持过期时间的缓存（如 Redis / 支持 TTL 的本地缓存）。

```go
type attemptInfo struct {
    count       int
    lastFail    time.Time
    lockedUntil *time.Time
}

// 在 IsLocked/RecordFailure 中清理 15 分钟前未更新的条目
```

---

### 🔴 B-3: 登出时 Cookie 未被正确清除

**文件:** `internal/session/manager.go:120`
**代码:**
```go
func ClearCookie(w http.ResponseWriter, secure bool) {
    http.SetCookie(w, &http.Cookie{
        Name:     "session_id",
        Value:    "",
        Path:     "/",
        HttpOnly: true,
        Secure:   secure,
        SameSite: http.SameSiteLaxMode,
        MaxAge:   -1,  // ❌ 问题所在
    })
}
```

**问题:** Go 的 `http.Cookie` 中，`MaxAge < 0` 表示 **不设置 Max-Age 属性**。浏览器会将其视为 Session Cookie，仅在浏览器关闭时删除，而非立即清除。登出后客户端仍会继续发送该 Cookie，导致会话状态不一致，不符合“清除客户端登录态 Cookie”的验收要求。

**建议修复:**
```go
func ClearCookie(w http.ResponseWriter, secure bool) {
    http.SetCookie(w, &http.Cookie{
        Name:     "session_id",
        Value:    "",
        Path:     "/",
        HttpOnly: true,
        Secure:   secure,
        SameSite: http.SameSiteLaxMode,
        MaxAge:   0,
        Expires:  time.Unix(0, 0),
    })
}
```

---

## 3. 🟡 重要问题 (Important)

### 🟡 I-1: 禁用账号枚举风险

**文件:** `internal/service/auth_service.go:77-83`
**问题:** 当攻击者恰好输入正确密码但账号已被禁用时，返回 `"账号已被禁用"`（HTTP 403），与错误密码返回的 `"账号或密码错误"`（HTTP 401）不同。虽然需要知道正确密码才能触发，但在特定场景下仍可能泄露账号存在性信息，与“防枚举”原则相违背。

**建议:** 在密码校验通过后、返回禁用信息前，增加一个模拟的假哈希比对，确保与正常账号的响应时序一致；或考虑在需求层面统一返回“账号或密码错误”。

---

### 🟡 I-2: X-Forwarded-For 可被伪造，导致 IP 限流绕过

**文件:** `internal/handler/auth_handler.go:67-68`、`internal/middleware/rate_limit_middleware.go:22-23`
**代码:**
```go
if fwd := r.Header.Get("X-Forwarded-For"); fwd != "" {
    ip = strings.Split(fwd, ",")[0]
}
```

**问题:** 直接信任 `X-Forwarded-For` 首部，攻击者可轻易伪造该字段绕过 IP 限流。
**建议:** 仅在确认服务部署在可信反向代理后时读取该字段，或增加代理 IP 白名单校验。

---

### 🟡 I-3: 需求缺失 — IP 维度的登录失败锁定

**文件:** `internal/ratelimit/ratelimiter.go`
**问题:** 需求文档明确要求“同一账号 **或同一 IP** 连续失败 5 次后锁定”，但当前仅实现了账号维度的锁定。IP 维度的 `IPRateLimiter` 仅做速率限制（10 次/分钟），未实现基于失败次数的锁定逻辑。

**建议:** 为 IP 维度增加与账号类似的失败计数与锁定机制。

---

### 🟡 I-4: 二进制文件不应提交到版本库

**文件:** `server`、`tests.test`
**问题:** 编译产物（可执行文件）被直接提交到仓库，增加仓库体积，且存在安全风险（可能包含调试信息）。

**建议:**
1. 删除已提交的二进制文件；
2. 在 `.gitignore` 中添加：
```
server
tests.test
*.exe
```

---

### 🟡 I-5: 缺少安全审计日志

**问题:** 系统未记录登录失败、账号锁定、登出等安全事件。生产环境无法追踪攻击行为、满足合规审计要求。

**建议:** 在关键路径增加结构化日志：
- 登录成功 / 失败（脱敏处理，不记录密码）
- 账号被锁定 / 解锁
- 会话创建 / 销毁

---

## 4. 🟢 建议 (Nits)

### 🟢 N-1: 验证码逻辑未实现

**文件:** `internal/handler/auth_handler.go`
**问题:** `LoginRequest` 包含 `Captcha` 字段，但 handler 中未做校验。需求文档标注为“可选”，建议至少预留接口并在文档中说明。

---

### 🟢 N-2: 账号格式校验过于宽松

**文件:** `internal/handler/auth_handler.go:47-51`
**问题:** 仅校验了账号长度（3-64 字符），未校验邮箱/手机号格式。需求文档要求“允许字母/数字/下划线/邮箱格式/手机号格式”。

**建议:** 增加正则校验，或在文档中明确说明本期暂不校验格式。

---

### 🟢 N-3: IP 提取逻辑重复

**文件:** `internal/handler/auth_handler.go:66-69`、`internal/middleware/rate_limit_middleware.go:21-24`
**问题:** 相同的 IP 提取逻辑在两个地方重复。

**建议:** 提取为 `internal/util/ip.go` 中的公共函数，如 `GetClientIP(r *http.Request) string`。

---

## 5. 验收标准（AC）对应检查

| 编号 | 验收项 | 状态 | 说明 |
|------|--------|------|------|
| AC-1 | 正确账号密码登录 | ✅ | 实现正确，返回 200 与用户信息 |
| AC-2 | 错误账号或密码返回统一错误 | ⚠️ | 功能正确，但存在 **B-1 时序攻击漏洞** 可泄露账号存在性 |
| AC-3 | 连续 5 次失败后锁定 | ⚠️ | 功能正确，但存在 **B-2 内存泄漏** |
| AC-4 | 禁用账号返回 403 | ⚠️ | 实现正确，但存在 **I-1 信息泄露风险** |
| AC-5 | 有效 Cookie 访问受保护接口 | ✅ | 实现正确 |
| AC-6 | 无效/过期 Cookie 返回 401 | ✅ | 实现正确 |
| AC-7 | 退出登录清除 Cookie | ⚠️ | 服务端已清除，但 **B-3 客户端 Cookie 未正确删除** |
| AC-8 | 密码使用 bcrypt/Argon2 | ✅ | `pkg/crypto/password.go` 使用 bcrypt，cost=12 |

---

## 6. 总结

本次提交的登录系统整体架构清晰，核心功能实现完整，集成测试覆盖了主要场景。但 **3 个阻塞级问题** 涉及安全底线（时序攻击防护失效、资源耗尽风险、会话清理不彻底），必须在合并前修复。

**修复优先级：**
1. **立即：** B-1（时序攻击漏洞）、B-3（Cookie 清理）
2. **高优先级：** B-2（内存泄漏）、I-2（IP 伪造）、I-4（清理二进制）
3. **中优先级：** I-1（禁用账号枚举）、I-3（IP 锁定）、I-5（审计日志）
