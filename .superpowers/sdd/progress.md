# SDD Progress Ledger

## Task 1: Phase 1 - 基础框架搭建
- Status: complete
- Description: 初始化 Go 项目模块、引入 Web 框架、配置管理模块、数据库连接
- Files: go.mod, go.sum, cmd/main.go, internal/server/server.go, internal/config/config.go, internal/database/database.go, internal/config/config_test.go

## Task 2: Phase 2 - 用户模型与密码安全
- Status: complete
- Description: 设计用户表结构、实现密码哈希工具（bcrypt）、实现密码比对与验证
- Files: internal/models/user.go, internal/utils/hash.go, internal/utils/hash_test.go, internal/models/user_test.go

## Task 3: Phase 3 - 核心认证流程
- Status: complete
- Description: 实现登录 API、JWT/Session 生成与验证、认证中间件、退出登录 API
- Files: internal/auth/jwt.go, internal/auth/jwt_test.go, internal/middleware/auth.go, internal/handlers/auth.go

## Task 4: Phase 4 - 异常与安全防护
- Status: complete
- Description: 登录失败次数记录、账号锁定机制、IP 限流中间件、统一错误响应封装
- Files: internal/auth/lockout.go, internal/auth/lockout_test.go, internal/middleware/ratelimit.go, internal/middleware/ratelimit_test.go, internal/utils/response.go, internal/utils/response_test.go

## Task 5: Phase 5 - 测试与验收
- Status: complete
- Description: 单元测试、集成测试、测试辅助工具
- Files: internal/handlers/auth_test.go, internal/handlers/auth_integration_test.go, internal/testutil/testutil.go

## Review Fixes (7 Blocking Issues)
- **B-001**: AuthMiddleware now checks `TokenBlacklist.IsBlacklisted()` after JWT validation — logout properly invalidates tokens.
- **B-002**: Fixed `string(user.ID)` → `strconv.FormatUint(uint64(user.ID), 10)` to avoid Unicode code-point corruption.
- **B-003**: IP rate limiter now runs a background goroutine that periodically deletes expired `ipBucket` entries.
- **B-004**: Replaced in-memory `map[string]bool` token blacklist with `auth.TokenBlacklist` that stores expiration times and runs periodic cleanup.
- **B-005**: `FailedAttemptTracker` now stores `lastFailed` and runs a background cleanup goroutine to remove expired lockout entries.
- **B-006**: `database.AutoMigrate` error is now properly captured and returned during `InitDB`.
- **B-007**: `LoginHandler` now calls `user.IsLocked()` after loading the user from the database to enforce DB-level lock status.

## Notes
- 所有代码文件基于仓库根目录创建/修改
- 由于运行时环境缺少 Go 编译器，未执行 `go build` / `go test`
- Git 操作受限于只读模式，未执行 commit
