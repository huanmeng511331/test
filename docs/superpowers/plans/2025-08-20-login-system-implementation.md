# 登录系统实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建安全、可测试、符合行业标准的用户登录子系统。

**Architecture:** Go + HTTP API + 数据库存储用户凭据与登录态。

**Tech Stack:** Go, PostgreSQL/MySQL, bcrypt/Argon2, HttpOnly Cookie。

---

## Global Constraints

- 本期明确**不包含**：用户注册、找回密码、SSO、第三方登录、MFA、扫码登录。
- 密码必须加盐哈希存储（bcrypt/Argon2）。
- 账号不存在与密码错误必须统一返回「账号或密码错误」。
- 登录态使用 HttpOnly + Secure + SameSite Cookie。
- 失败次数限制 + 验证码 + IP 限流防暴力破解。

---

## 1. 概述

### 1.1 背景
系统需要一套独立的用户登录模块，支持用户通过账号密码完成身份认证并获取会话态，同时保障认证过程的安全性，防止常见的暴力破解和账号枚举攻击。

### 1.2 目标
- 提供标准化的账号密码登录能力。
- 通过安全加固（哈希、限速、验证码）降低常见攻击面。
- 支持长短有效期会话，兼顾安全与用户体验。

### 1.3 名词定义
| 名词 | 说明 |
|------|------|
| 会话（Session） | 服务器端维护的用户登录状态 |
| 记住我（Remember Me） | 延长 Cookie 有效期以维持长期登录态 |
| 暴力破解锁定 | 连续登录失败后临时锁定账号或 IP 的机制 |
| 账号枚举 | 通过服务端响应差异推断账号是否存在的攻击方式 |

---

## 2. 角色与范围

### 2.1 目标用户
- 已注册并持有有效账号密码的普通用户。

### 2.2 本期包含范围
- 账号密码登录入口
- 输入格式校验（前端 + 后端）
- 核心认证流程（校验密码、生成会话）
- 登录态维持（普通会话 / 记住我会话）
- 退出登录（主动登出）
- 安全加固（哈希、限速、防枚举）

### 2.3 本期不包含范围（明确排除）
- 用户注册
- 找回密码 / 重置密码
- 单点登录（SSO）
- 第三方登录（OAuth / WeChat / GitHub 等）
- 多因素认证（MFA / OTP）
- 扫码登录

---

## 3. 功能需求

### 3.1 登录入口
- 提供 Web 登录页面或 API 端点。
- 支持输入项：账号（用户名/邮箱/手机号）、密码、可选「记住我」复选框。

### 3.2 输入校验
- 账号：非空，长度 3–64 字符，允许字母/数字/下划线/邮箱格式/手机号格式。
- 密码：非空，长度 8–128 字符。
- 验证码（启用时）：非空，4–6 位字符/数字。
- 校验失败立即返回 400 并提示具体字段错误。

### 3.3 核心认证流程
1. 接收账号密码。
2. 查询数据库获取用户记录（不存在也继续执行假比对流程）。
3. 校验密码哈希（bcrypt/Argon2 比较）。
4. 若校验失败，记录失败次数并返回统一错误「账号或密码错误」。
5. 若用户被禁用，返回「账号已被禁用」。
6. 若通过校验，生成会话 ID，设置 Cookie，清除失败次数。
7. 返回登录成功响应。

### 3.4 登录态维持
- 普通会话：Cookie 有效期 2 小时（HttpOnly, Secure, SameSite=Lax/Strict）。
- 记住我：Cookie 有效期 30 天。
- 服务端会话存储在内存缓存或数据库中，记录 user_id、创建时间、过期时间、user_agent、ip。
- 每次请求通过会话 ID 校验登录态。

### 3.5 退出登录
- 客户端调用退出接口，服务端销毁会话记录。
- 清除客户端登录态 Cookie（Max-Age=0）。
- 返回退出成功响应。

---

## 4. 异常与边界场景

### 4.1 账号不存在
- 按正常流程执行密码哈希假比对。
- 统一返回「账号或密码错误」（HTTP 401）。
- 记录 IP 失败次数。

### 4.2 密码错误
- 统一返回「账号或密码错误」（HTTP 401）。
- 增加该账号/ IP 的失败计数。

### 4.3 暴力破解锁定
- 同一账号或同一 IP 连续失败 5 次后，临时锁定 15 分钟。
- 锁定期间登录请求返回「操作过于频繁，请稍后再试」（HTTP 429）。
- 可选：锁定期间要求输入图形验证码。

### 4.4 账号禁用
- 密码校验通过后，检查用户状态。
- 若 status == disabled，返回「账号已被禁用」（HTTP 403）。

### 4.5 会话过期
- 请求时携带的会话 ID 不存在或已过期。
- 返回「登录已过期，请重新登录」（HTTP 401）。

### 4.6 并发登录限制
- 支持配置最大并发会话数（如 3 个），超过时踢掉最早会话。
- 本期可简化：允许多端登录，仅做会话数量上限限制。

### 4.7 验证码错误
- 若当前场景已触发验证码校验，验证码错误返回「验证码错误」（HTTP 400）。
- 不重置失败次数。

---

## 5. 非功能需求

### 5.1 安全
- 密码存储：bcrypt（cost >= 12）或 Argon2id。
- 防枚举：账号不存在与密码错误返回完全一致的响应内容与延迟。
- 防暴力破解：失败次数限制 + IP 限流（如每分钟 10 次）+ 验证码机制。
- Cookie 属性：HttpOnly + Secure + SameSite=Lax（生产环境 SameSite=Strict）。
- 传输层：仅允许 HTTPS。
- 敏感日志脱敏：禁止明文记录密码。

### 5.2 性能
- 登录接口 P99 < 300ms（含哈希比对）。
- 会话校验通过缓存（Redis）实现，P99 < 10ms。
- 数据库查询使用账号索引。

### 5.3 可用性
- 登录服务可用性目标 >= 99.9%。
- 会话存储具备持久化能力，支持服务重启不丢失登录态。

### 5.4 兼容性
- API 兼容主流浏览器和移动端。
- Cookie 遵循 RFC 6265。
- 后端接口响应 Content-Type 为 application/json。

---

## 6. 接口示例

### 6.1 登录

**Request:**
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "account": "user@example.com",
  "password": "YourPass123!",
  "remember_me": false,
  "captcha": "a3f9"        // 仅在触发验证码时必填
}
```

**Response (成功):**
```
HTTP/1.1 200 OK
Set-Cookie: session_id=xxx; HttpOnly; Secure; SameSite=Lax; Max-Age=7200

{
  "code": 0,
  "message": "登录成功",
  "data": {
    "user_id": "uuid",
    "nickname": "Nick",
    "created_at": "..."
  }
}
```

**Response (失败):**
```
HTTP/1.1 401 Unauthorized

{
  "code": 10001,
  "message": "账号或密码错误"
}
```

### 6.2 退出登录

**Request:**
```
POST /api/v1/auth/logout
Cookie: session_id=xxx
```

**Response (成功):**
```
HTTP/1.1 200 OK
Set-Cookie: session_id=; Max-Age=0; HttpOnly; Secure; SameSite=Lax

{
  "code": 0,
  "message": "退出成功"
}
```

---

## 7. 验收标准

| 编号 | 验收项 | 期望结果 |
|------|--------|----------|
| AC-1 | 使用正确的账号密码登录 | HTTP 200，Set-Cookie 包含 session_id，响应包含用户信息 |
| AC-2 | 使用错误的账号或密码登录 | HTTP 401，响应 message 为「账号或密码错误」，不暴露账号是否存在 |
| AC-3 | 连续 5 次登录失败后再次登录 | HTTP 429，响应 message 为「操作过于频繁，请稍后再试」 |
| AC-4 | 使用被禁用的账号登录 | HTTP 403，响应 message 为「账号已被禁用」 |
| AC-5 | 携带有效 Cookie 访问受保护接口 | 正常访问，返回受保护资源 |
| AC-6 | 携带过期/无效 Cookie 访问受保护接口 | HTTP 401，响应 message 为「登录已过期，请重新登录」 |
| AC-7 | 调用退出登录接口 | HTTP 200，Set-Cookie 清除 session_id，再次访问受保护接口返回 401 |
| AC-8 | 密码在数据库中以明文或弱哈希存储 | 测试失败；要求使用 bcrypt/Argon2 且无法反解 |

---

## 8. 后续规划

- 第三方登录：微信、GitHub、Google OAuth2 接入。
- 扫码登录：App 扫码 Web 端登录。
- 多因素认证（MFA）：支持 TOTP / 短信验证码二次校验。
- 单点登录（SSO）：企业级统一认证中心接入。

---

## 9. 实施任务分解

### Task 1: 数据库设计与用户表初始化

**Files:**
- Create: `migrations/001_create_users_table.up.sql`
- Create: `migrations/001_create_users_table.down.sql`
- Create: `migrations/002_create_sessions_table.up.sql`
- Create: `migrations/002_create_sessions_table.down.sql`
- Update: `.env.example`（添加数据库配置）

**Interfaces:**
- Consumes: None
- Produces: `users` 表（id, account, password_hash, status, created_at, updated_at）；`sessions` 表（id, user_id, token, expires_at, ip, user_agent, created_at）

- [ ] **Step 1: 创建 users 表迁移文件**
- [ ] **Step 2: 创建 sessions 表迁移文件**
- [ ] **Step 3: 执行迁移并验证表结构**

---

### Task 2: 密码工具与认证服务基础

**Files:**
- Create: `pkg/crypto/password.go`
- Create: `internal/service/auth_service.go`
- Create: `internal/repository/user_repository.go`
- Create: `internal/models/user.go`

**Interfaces:**
- Consumes: 数据库连接（由框架/主程序注入）
- Produces: PasswordHasher 接口及 bcrypt 实现；AuthService 结构体；User 模型

- [ ] **Step 1: 实现 PasswordHasher 接口（bcrypt/Argon2）**
- [ ] **Step 2: 实现 UserRepository（按账号查询用户）**
- [ ] **Step 3: 实现 AuthService 登录核心逻辑（校验密码、生成会话）**
- [ ] **Step 4: 编写单元测试并确保通过**

---

### Task 3: 会话管理与 Cookie 中间件

**Files:**
- Create: `internal/session/manager.go`
- Create: `internal/middleware/auth_middleware.go`
- Create: `internal/models/session.go`

**Interfaces:**
- Consumes: Redis/Cache 或数据库连接
- Produces: SessionManager 接口及实现；AuthMiddleware

- [ ] **Step 1: 实现 SessionManager（创建、校验、删除、刷新会话）**
- [ ] **Step 2: 实现 AuthMiddleware（从 Cookie 提取 session_id 并校验）**
- [ ] **Step 3: 配置 Cookie 属性（HttpOnly, Secure, SameSite, Max-Age）**
- [ ] **Step 4: 编写单元测试并确保通过**

---

### Task 4: 登录与退出 HTTP 接口

**Files:**
- Create: `internal/handler/auth_handler.go`
- Create: `internal/dto/auth_request.go`
- Create: `internal/dto/auth_response.go`
- Update: `cmd/server/main.go`（注册路由）

**Interfaces:**
- Consumes: AuthService, SessionManager
- Produces: `POST /api/v1/auth/login`, `POST /api/v1/auth/logout`

- [ ] **Step 1: 定义 LoginRequest / LoginResponse DTO**
- [ ] **Step 2: 实现 AuthHandler.Login 方法**
- [ ] **Step 3: 实现 AuthHandler.Logout 方法**
- [ ] **Step 4: 注册路由并启动服务验证**

---

### Task 5: 防暴力破解与安全加固

**Files:**
- Create: `internal/ratelimit/ratelimiter.go`
- Create: `internal/middleware/rate_limit_middleware.go`
- Update: `internal/handler/auth_handler.go`

**Interfaces:**
- Consumes: Redis 或内存存储
- Produces: RateLimiter 接口；登录失败计数器

- [ ] **Step 1: 实现基于 IP + 账号的登录失败计数器**
- [ ] **Step 2: 实现 RateLimitMiddleware（限制 /login 接口频率）**
- [ ] **Step 3: 在登录逻辑中集成失败次数判断和锁定逻辑**
- [ ] **Step 4: 验证连续失败 5 次后锁定 15 分钟的场景**

---

### Task 6: 集成测试与验收

**Files:**
- Create: `tests/integration/auth_test.go`
- Update: `README.md`（补充登录接口文档）

**Interfaces:**
- Consumes: 已启动的服务实例
- Produces: 测试报告

- [ ] **Step 1: 编写 AC-1 ~ AC-7 的集成测试用例**
- [ ] **Step 2: 运行测试并确保全部通过**
- [ ] **Step 3: 补充 README 登录接口说明**
