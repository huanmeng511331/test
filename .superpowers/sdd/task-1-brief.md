# Task 1: Phase 1 - 基础框架搭建

## 目标
为登录系统搭建基础 Go 项目框架，包括 Web 框架、配置管理和数据库连接。

## 范围
当前仓库已有基本 Go 文件（sort.go, review_test.go）。本任务需要：
1. 初始化/确认 Go 模块（go.mod），引入必要的依赖
2. 引入并初始化 Web 框架（Gin）
3. 实现配置管理模块（支持环境变量 + 配置文件）
4. 实现数据库连接（使用 GORM + SQLite 内存模式，便于测试和开发）

## 具体要求

### 1. go.mod 初始化
- 如果已存在 go.mod，确认并更新依赖
- 引入 `github.com/gin-gonic/gin` 作为 Web 框架
- 引入 `gorm.io/gorm` 和 `gorm.io/driver/sqlite` 作为 ORM
- 引入 `github.com/golang-jwt/jwt/v5` 用于后续 JWT 功能
- 引入 `golang.org/x/crypto/bcrypt` 用于密码哈希
- 引入 `github.com/stretchr/testify` 用于测试

### 2. Web 框架初始化（Gin）
- 创建 `internal/server/server.go`
- 实现 `Server` 结构体，包含 `*gin.Engine`
- 实现 `New()` 和 `Run(addr string) error` 方法
- 创建 `cmd/main.go` 作为应用入口

### 3. 配置管理模块
- 创建 `internal/config/config.go`
- 使用环境变量加载配置（支持 `.env` 文件可选）
- 配置项包括：
  - `APP_ENV`: 环境（development/production）
  - `APP_PORT`: 服务端口（默认 8080）
  - `DATABASE_URL`: 数据库连接字符串（默认 SQLite 内存）
  - `JWT_SECRET`: JWT 密钥
  - `JWT_EXPIRE_HOURS`: JWT 默认过期时间（默认 2）
  - `JWT_REMEMBER_EXPIRE_HOURS`: "记住我" 过期时间（默认 168 = 7天）

### 4. 数据库连接
- 创建 `internal/database/database.go`
- 使用 GORM + SQLite 实现数据库连接
- 提供 `InitDB()` 和 `CloseDB()` 函数
- 支持数据库自动迁移（后续 Task 2 会定义模型）

## 验收标准
- [x] `go mod tidy` 成功，无依赖冲突
- [x] `go build ./cmd/main.go` 成功编译
- [x] 配置模块可以正确读取环境变量
- [x] 数据库连接可以正常初始化

## 注意
- **不要执行 git commit**，只需完成代码实现
- 保持代码结构清晰，遵循 Go 项目最佳实践
- 文件路径均基于仓库根目录
