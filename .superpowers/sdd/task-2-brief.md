# Task 2: Phase 2 - 用户模型与密码安全

## 目标
设计用户数据模型，实现密码安全存储和验证。

## 范围
在 Task 1 搭建的框架基础上，实现用户相关数据模型和密码安全工具。

## 具体要求

### 1. 用户表结构
- 创建 `internal/models/user.go`
- 定义 `User` 结构体，使用 GORM 标签：
  - `ID`: uint, 主键, 自增
  - `Username`: string, 非空, 唯一索引, 长度 3-50
  - `Email`: string, 非空, 唯一索引, 邮箱格式
  - `PasswordHash`: string, 非空, 存储 bcrypt 哈希值
  - `DisplayName`: string, 可选
  - `Status`: string, 默认 "active"，可选值 "active" / "disabled" / "locked"
  - `FailedLoginCount`: int, 默认 0
  - `LockedUntil`: *time.Time, 可空
  - `LastLoginAt`: *time.Time, 可空
  - `CreatedAt`: time.Time
  - `UpdatedAt`: time.Time
- 实现 `TableName() string` 返回 "users"
- 在 `internal/database/database.go` 中添加 AutoMigrate 调用

### 2. 密码哈希工具
- 创建 `internal/utils/hash.go`
- 实现 `HashPassword(password string) (string, error)`：
  - 使用 bcrypt.GenerateFromPassword，cost = 12
- 实现 `CheckPassword(password, hash string) error`：
  - 使用 bcrypt.CompareHashAndPassword
- 实现 `IsValidPassword(password string) error`：
  - 长度 8-128 字符
  - 返回适当的错误信息

### 3. 测试
- 创建 `internal/utils/hash_test.go`
  - 测试密码哈希生成和验证
  - 测试密码校验规则
- 创建 `internal/models/user_test.go`
  - 测试用户模型创建和查询

## 验收标准
- [x] bcrypt 密码哈希可以正确生成和验证
- [x] 密码校验规则生效（长度限制）
- [x] 用户模型可以正常创建和查询
- [x] `go test ./...` 通过

## 注意
- **不要执行 git commit**
- 密码明文**绝对不可**存储
- 使用 bcrypt cost = 12（安全与性能平衡）
