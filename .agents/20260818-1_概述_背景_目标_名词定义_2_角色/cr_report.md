# Code Review Report - Login System Implementation

> **Review Date**: 2025-08-20
> **Scope**: Go Login System (Authentication, Session Management, Rate Limiting)
> **Reviewer**: DTCoder

---

## 1. 概述 (Overview)

本次评审针对基于 Go 的账号密码登录子系统，涵盖认证服务、会话管理、限流、密码哈希、数据持久化等模块。代码整体结构清晰，采用分层架构（Handler → Service → Repository），使用了 bcrypt 进行密码哈希，并实现了基于内存的防暴力破解锁定机制。但经逐行审查，发现 **3 个 Blocker 级别安全问题** 必须在合并前修复。

---

## 2. 评审范围 (Review Scope)

| 文件 | 说明 |
|------|------|
| `cmd/server/main.go` | 服务入口与路由组装 |
| `internal/handler/auth_handler.go` | 登录/退出 HTTP 接口 |
| `internal/middleware/auth_middleware.go` | 认证中间件 |
| `internal/middleware/rate_limit_middleware.go` | 限流中间件 |
| `internal/service/auth_service.go` | 认证业务逻辑 |
| `internal/session/manager.go` | 会话管理 |
| `internal/ratelimit/ratelimiter.go` | 限流器实现 |
| `internal/repository/user_repository.go` | 用户数据访问 |
| `internal/repository/session_repository.go` | 会话数据访问 |
| `pkg/crypto/password.go` | 密码哈希 (bcrypt) |
| `tests/integration/auth_test.go` | 集成测试 |

---

## 3. Blocker 问题（必须修复）

### 🔴 [BLOCKING-1] `internal/ratelimit/ratelimiter.go:128` — IPRateLimiter 拒绝请求仍记录时间戳，导致内存持续增长（DoS）

**问题描述**：
`IPRateLimiter.Allow` 在判断限流前先将当前时间 `now` 追加到 `valid` 切片中，即使请求最终会被拒绝。这导致窗口期内 `r.requests[ip]` 随请求数线性增长，攻击者可持续发送请求耗尽内存。

**代码位置**：
```go
// internal/ratelimit/ratelimiter.go:114-132
func (r *IPRateLimiter) Allow(ip string) bool {
    // ... 过滤旧记录 ...
    valid = append(valid, now)  // ❌ 即使超限也追加
    r.requests[ip] = valid
    return len(valid) <= r.limit
}
```

**影响**：拒绝服务（DoS），攻击者可在短时间内耗尽服务内存。

**修复建议**：
```go
if len(valid) >= r.limit {
    r.requests[ip] = valid
    return false
}
valid = append(valid, now)
r.requests[ip] = valid
return true
```

**关联需求**：5.1 安全 — 防暴力破解（IP 限流失效）。

---

### 🔴 [BLOCKING-2] `internal/service/auth_service.go:58-64` — `GetByAccount` 返回 error 时未执行 fake hash 比较，存在 Timing Attack

**问题描述**：
需求明确要求"账号不存在与密码错误统一返回「账号或密码错误」"并具备"timing-attack resistant login flow"。当前实现中，当 `GetByAccount` 返回 error（非 nil）时，直接返回 401，**跳过了 `passwordHasher.Verify` 调用**。这与"账号不存在"时的处理路径（执行 fake hash 比较）不一致，攻击者可通过响应时间差异推断账号是否存在。

**代码位置**：
```go
// internal/service/auth_service.go:56-64
user, err := s.userRepo.GetByAccount(account)
if err != nil {
    // ❌ 未执行 fake hash 比较，直接返回
    return &LoginResult{
        Success:    false,
        Message:    "账号或密码错误",
        StatusCode: http.StatusUnauthorized,
    }
}
```

**影响**：账号枚举攻击 — 攻击者可通过测量响应时间推断账号是否存在。

**修复建议**：
```go
user, err := s.userRepo.GetByAccount(account)
if err != nil {
    _ = s.passwordHasher.Verify(password, fakeHash) // 保持时间一致性
    return &LoginResult{
        Success:    false,
        Message:    "账号或密码错误",
        StatusCode: http.StatusUnauthorized,
    }
}
```

**关联需求**：3.3 核心认证流程（假比对流程）、5.1 安全（防枚举）。

---

### 🔴 [BLOCKING-3] `internal/handler/auth_handler.go:67-69` & `internal/middleware/rate_limit_middleware.go:22-24` — `X-Forwarded-For` 直接取首个 IP，客户端可伪造以绕过 IP 限流

**问题描述**：
两处代码均通过 `strings.Split(fwd, ",")[0]` 直接取 `X-Forwarded-For` 的第一个 IP。该请求头可由客户端任意设置，攻击者只需在请求中附加 `X-Forwarded-For: <任意IP>`，即可绕过基于 IP 的限流机制，使防暴力破解能力形同虚设。

**代码位置**：
```go
// internal/handler/auth_handler.go:67-69
if fwd := r.Header.Get("X-Forwarded-For"); fwd != "" {
    ip = strings.Split(fwd, ",")[0]  // ❌ 可被伪造
}

// internal/middleware/rate_limit_middleware.go:22-24
if fwd := r.Header.Get("X-Forwarded-For"); fwd != "" {
    ip = strings.Split(fwd, ",")[0]  // ❌ 可被伪造
}
```

**影响**：IP 限流失效，攻击者可无限次尝试暴力破解。

**修复建议**：
- 配置可信代理 IP 列表，从 `X-Forwarded-For` 右侧（靠近服务端）向左遍历，取第一个非可信代理的 IP。
- 若未配置反向代理，应直接忽略 `X-Forwarded-For`，使用 `r.RemoteAddr`。

**关联需求**：5.1 安全（防暴力破解：IP 限流）。

---

## 4. 重要问题（Important）

### 🟡 [IMPORTANT-1] `internal/service/auth_service.go:19` — `init()` 中忽略 `bcrypt.GenerateFromPassword` 错误，若失败会导致 Timing Attack

**问题描述**：
`fakeHash` 在 `init()` 中生成时忽略了错误。若生成失败（如系统熵不足），`fakeHash` 为空字符串，`Verify` 将立即返回错误，与真实 hash 比较的耗时差异显著，可被用于 timing attack。

**代码位置**：
```go
// internal/service/auth_service.go:18-20
func init() {
    fakeHashBytes, _ := bcrypt.GenerateFromPassword([]byte("fake"), bcrypt.DefaultCost)
    fakeHash = string(fakeHashBytes)
}
```

**修复建议**：
```go
func init() {
    fakeHashBytes, err := bcrypt.GenerateFromPassword([]byte("fake"), bcrypt.DefaultCost)
    if err != nil {
        panic(fmt.Sprintf("failed to generate fake hash: %v", err))
    }
    fakeHash = string(fakeHashBytes)
}
```

---

### 🟡 [IMPORTANT-2] `internal/ratelimit/ratelimiter.go:30-37` — `cleanup` 在锁保护下遍历整个 map，高并发下阻塞严重

**问题描述**：
`cleanup` 在 `RecordFailure` 和 `IsLocked` 中调用，且持有 `sync.RWMutex` 的写锁。当 `attempts` map 较大时，会长时间阻塞其他 goroutine，在高并发场景下造成明显的性能瓶颈。

**修复建议**：
- 将清理逻辑移至独立的后台 goroutine（如 `time.Ticker` 每 5 分钟执行一次）。
- 或使用 `sync.Map` + TTL 策略减少锁竞争。

---

### 🟡 [IMPORTANT-3] `README.md:8` & `:114` — 文档描述密码使用 SHA256，实际代码使用 bcrypt

**问题描述**：
README 中多处描述"Passwords are hashed with SHA256"，但实际实现使用的是 bcrypt。文档与实现不一致，可能误导运维人员和后续开发者。

**修复建议**：
将 README 中所有 SHA256 相关描述更新为 bcrypt，并注明 cost 值。

---

### 🟡 [IMPORTANT-4] `internal/ratelimit/ratelimiter.go:72-78` — 锁定逻辑基于 `lastFail` 时间，连续失败判定窗口与锁定窗口混淆

**问题描述**：
`IsLocked` 中判定逻辑为：
```go
if info.count >= 5 && time.Since(info.lastFail) < 15*time.Minute {
    lockedUntil := info.lastFail.Add(15 * time.Minute)
    ...
}
```
此处"连续 5 次失败"的判定窗口和锁定窗口均使用 15 分钟，但逻辑上这是两个不同的概念。当前实现下，若第 5 次失败后等待 15 分钟，锁会自动解除，但失败计数不会清零，导致用户再次失败时可能立即被锁定。建议明确区分"判定窗口"和"锁定时长"。

---

## 5. 建议（Nit / Suggestion）

### 🟢 [SUGGESTION-1] `internal/session/manager.go:53-56` — 会话过期时间硬编码，建议提取为常量

**代码位置**：
```go
expiresAt := time.Now().Add(2 * time.Hour)
if rememberMe {
    expiresAt = time.Now().Add(30 * 24 * time.Hour)
}
```
与 `auth_handler.go` 中的 `maxAge` 硬编码值对应。建议统一定义为常量，避免未来修改时出现不一致。

---

### 🟢 [SUGGESTION-2] `internal/handler/auth_handler.go:77-78` — 错误响应编码未处理错误

```go
json.NewEncoder(w).Encode(map[string]interface{}{"code": result.StatusCode * 100 + 1, "message": result.Message})
```
建议检查 `Encode` 的返回值，虽然通常不会失败，但在网络异常时可能导致未定义行为。

---

### 🟢 [SUGGESTION-3] `tests/integration/auth_test.go` — 测试用例覆盖不完整

当前测试覆盖了：
- 正确登录（AC-1）
- 错误密码（AC-2 部分）
- 账号禁用（AC-4）
- 速率限制（AC-3）
- 受保护端点（AC-5, AC-6 部分）
- 退出登录（AC-7）

**缺失**：
- AC-6 完整验证（携带过期/无效 Cookie）
- AC-8 验证（密码哈希算法检查）
- 账号不存在与密码错误的响应一致性测试（timing attack 防护验证）
- 并发登录限制（AC-6 部分）

---

## 6. 总结 (Summary)

| 级别 | 数量 | 说明 |
|------|------|------|
| 🔴 Blocker | **3** | 必须在合并前修复：IPRateLimiter DoS、Timing Attack（2处）、X-Forwarded-For 伪造 |
| 🟡 Important | 4 | 建议尽快修复：init 错误忽略、 cleanup 性能、文档不一致、锁定逻辑 |
| 🟢 Suggestion | 3 | 可选优化：常量提取、错误处理、测试覆盖 |

**总体评价**：代码架构良好，分层清晰，核心安全机制（bcrypt、HttpOnly Cookie、防枚举假比对）已初步实现。但存在 **3 个 Blocker 级别安全缺陷**，主要涉及 DoS、Timing Attack 和 IP 限流绕过，必须在生产环境部署前完成修复。

---

**Co-authored-by: DTCoder <noreply@dtcoder.local>**
