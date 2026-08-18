# Task 5: Phase 5 - 测试与验收

## 目标
完善测试覆盖，确保登录系统满足验收标准。

## 范围
在前 4 个 Task 的基础上，补充完善所有测试，确保系统质量。

## 具体要求

### 1. 单元测试补充
- `internal/utils/hash_test.go`：
  - 测试密码哈希不可反解
  - 测试 bcrypt cost 参数
  - 测试错误密码验证失败
- `internal/auth/jwt_test.go`：
  - 测试 token 生成
  - 测试 token 验证
  - 测试 token 过期
  - 测试无效 token
- `internal/auth/lockout_test.go`：
  - 测试并发安全性

### 2. 集成测试
- 创建 `internal/handlers/auth_integration_test.go`：
  - 使用 httptest 进行完整的 HTTP 测试
  - 测试完整登录流程
  - 测试登录失败次数和锁定
  - 测试退出登录后 token 失效
  - 测试"记住我" token 有效期更长
  - 测试 IP 限流

### 3. 测试辅助工具
- 创建 `internal/testutil/testutil.go`：
  - `SetupTestDB()`：初始化测试数据库
  - `TeardownTestDB()`：清理测试数据库
  - `CreateTestUser(db, username, password string) *models.User`：创建测试用户
  - `GetTestToken(userID string) string`：生成测试 token

### 4. 性能基准测试（可选）
- `internal/handlers/auth_bench_test.go`：
  - 基准测试登录接口处理时间
  - 基准测试 bcrypt 哈希计算时间

### 5. 运行所有测试
- 确保 `go test ./...` 全部通过
- 确保 `go test -race ./...` 无数据竞争
- 确保 `go vet ./...` 无警告

## 验收标准
- [x] 单元测试覆盖率 > 80%
- [x] 集成测试覆盖所有主要场景
- [x] `go test ./...` 全部通过
- [x] `go test -race ./...` 无数据竞争
- [x] `go vet ./...` 无警告

## 注意
- **不要执行 git commit**
- 测试数据库使用 SQLite 内存模式
- 每个测试之间保持独立，不共享状态
