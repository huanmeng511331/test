# Task 3: Phase 3 - 核心认证流程

## 目标
实现登录、认证和退出登录的核心 API。

## 范围
在 Task 1 和 Task 2 的基础上，实现认证相关的 HTTP 接口和中间件。

## 具体要求

### 1. JWT 工具
- 创建 `internal/auth/jwt.go`
- 实现以下函数：
  - `GenerateToken(userID string, rememberMe bool) (string, time.Time, error)`：
    - 使用 HS256 算法
    - 普通登录：JWT_EXPIRE_HOURS（默认 2 小时）
    - 记住我：JWT_REMEMBER_EXPIRE_HOURS（默认 168 小时 = 7 天）
    - 返回 token 字符串、过期时间、错误
  - `ValidateToken(tokenString string) (*TokenClaims, error)`：
    - 解析并验证 token
    - 返回自定义 claims 结构体
- 定义 `TokenClaims` 结构体包含 `UserID string` 和 `jwt.RegisteredClaims`

### 2. 认证中间件
- 创建 `internal/middleware/auth.go`
- 实现 `AuthMiddleware()` gin 中间件：
  - 从请求头 `Authorization: Bearer <token>` 中提取 token
  - 验证 token 有效性
  - 将 `userID` 存入 gin context
  - token 无效或过期返回 401

### 3. 登录 API
- 创建 `internal/handlers/auth.go`
- 实现 `LoginHandler`：
  - 请求体：`{ username, password, remember_me, captcha }`
  - 查询用户（通过 username，支持邮箱）
  - 检查账号状态（disabled/locked）
  - 验证密码
  - 生成 JWT token
  - 更新 LastLoginAt，重置 FailedLoginCount
  - 返回：`{ token, expires_at, user }`
  - 错误：账号不存在和密码错误统一返回"账号或密码错误"

### 4. 退出登录 API
- 实现 `LogoutHandler`：
  - 需要认证（使用 AuthMiddleware）
  - 客户端提交 token
  - 服务端将 token 加入黑名单（使用 Redis 或内存 map）
  - 返回成功响应

### 5. 路由配置
- 在 `internal/server/server.go` 中添加路由：
  - `POST /api/v1/auth/login` — 公开
  - `POST /api/v1/auth/logout` — 需要认证

### 6. 测试
- 创建 `internal/handlers/auth_test.go`
  - 测试登录成功
  - 测试账号不存在/密码错误返回统一文案
  - 测试账号禁用
  - 测试退出登录

## 验收标准
- [x] 登录 API 返回正确的 token 和用户信息
- [x] 账号不存在和密码错误统一返回"账号或密码错误"
- [x] 禁用账号返回"账号已被禁用，请联系管理员"
- [x] 退出登录后 token 失效
- [x] 认证中间件正确拦截未授权请求
- [x] `go test ./...` 通过

## 注意
- **不要执行 git commit**
- 由于没有 Redis，token 黑名单使用内存 map + sync.RWMutex 实现
- 登录成功时重置失败次数
