# ARCHITECTURE.md

## 模块边界与依赖方向

controller → service → dao → model(do)

| 层 | 职责 | 不得包含 |
|------|------|----------|
| controller | REST 接口、参数校验、统一响应 | 业务逻辑、SQL |
| service | 待办事项业务编排、参数校验 | HTTP 处理、直接 SQL |
| dao | MyBatis 数据访问 | 业务逻辑 |
| model | DO / DTO / VO 数据对象 | 业务逻辑 |

## 配置分层

application.yml < 环境变量

敏感配置（DB 密码）通过环境变量注入，禁止写入配置文件。

## 错误处理

- 业务异常使用 `TodoException`，包含 `errorCode`、`errorMessage`、`userTip`。
- 通过 `GlobalExceptionHandler` 统一转换为统一响应 `ApiResponse`。
- 错误码枚举见 `TodoErrorCodeEnum`。

## 安全约束

- 凭证不得硬编码
- SQL 参数化，使用 `#{}`，禁止 `${}`

## 测试策略

- Service 层：单元测试 + Mockito
- Controller 层：切片测试 + MockMvc
