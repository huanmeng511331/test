# Task 4: Phase 4 - 异常与安全防护

## 目标
实现登录失败次数限制、账号锁定、IP 限流和统一错误响应。

## 范围
在 Task 3 的基础上，增强安全防护机制。

## 具体要求

### 1. 登录失败次数记录与账号锁定
- 创建 `internal/auth/lockout.go`
- 实现 `FailedAttemptTracker`：
  - 使用内存存储（sync.Map 或 map + mutex）
  - `RecordFailedAttempt(key string) int`：记录失败，返回当前失败次数
  - `ResetFailedAttempts(key string)`：重置失败次数
  - `IsLocked(key string) bool`：检查是否被锁定
  - `Lock(key string, duration time.Duration)`：锁定指定时间
  - `GetLockoutRemaining(key string) time.Duration`：获取剩余锁定时间
- 配置：
  - 最大失败次数：5 次
  - 锁定时间：30 分钟
- 在 `LoginHandler` 中集成：
  - 登录失败时记录失败次数（以 username 为 key）
  - 失败达到 5 次后锁定账号 30 分钟
  - 锁定期间登录返回"账号已被锁定，请 30 分钟后重试"
  - 登录成功后重置失败次数

### 2. IP 限流中间件
- 创建 `internal/middleware/ratelimit.go`
- 实现 `IPRateLimitMiddleware()` gin 中间件：
  - 基于客户端 IP 地址限流
  - 使用令牌桶或滑动窗口算法
  - 配置：10 分钟内最多 20 次登录请求
  - 超限返回 429 Too Many Requests
  - 仅限制 `/api/v1/auth/login` 端点

### 3. 统一错误响应封装
- 创建 `internal/utils/response.go`
- 实现统一的 JSON 响应格式：
  ```go
  type Response struct {
    Code    int         `json:"code"`
    Message string      `json:"message"`
    Data    interface{} `json:"data,omitempty"`
  }
  ```
- 实现常用响应函数：
  - `Success(c *gin.Context, data interface{})`
  - `Error(c *gin.Context, code int, message string)`
  - `BadRequest(c *gin.Context, message string)`
  - `Unauthorized(c *gin.Context)`
  - `Forbidden(c *gin.Context)`
  - `TooManyRequests(c *gin.Context)`
- 更新所有 handler 使用统一响应格式

### 4. 测试
- 创建 `internal/auth/lockout_test.go`
  - 测试失败次数记录
  - 测试锁定和解锁
- 创建 `internal/middleware/ratelimit_test.go`
  - 测试 IP 限流
- 创建 `internal/utils/response_test.go`
  - 测试统一响应格式

## 验收标准
- [x] 连续失败 5 次后账号锁定 30 分钟
- [x] 锁定期间登录返回正确提示
- [x] IP 限流在 10 分钟内超过 20 次请求时触发 429
- [x] 所有 API 返回统一格式的 JSON 响应
- [x] `go test ./...` 通过

## 注意
- **不要执行 git commit**
- 锁定和限流都使用内存存储（简化实现，无 Redis）
- 错误响应格式必须与接口示例一致
