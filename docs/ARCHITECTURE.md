# ARCHITECTURE.md

## 模块边界与依赖方向

todo-common <- todo-dao <- todo-service <- todo-api

| 层 | 包 | 职责 | 不得包含 |
|----|----|------|----------|
| common | `com.dtazza.todo.common` | 统一响应模型、业务异常 | 业务逻辑、Spring Bean、DB 依赖 |
| dao | `com.dtazza.todo.dao` | MyBatis 数据访问、实体 DO | Controller、业务编排 |
| service | `com.dtazza.todo.service` | 待办事项业务编排与校验 | HTTP 处理、直接 SQL |
| api | `com.dtazza.todo.api` | REST Controller、参数校验 | DB 操作、业务计算 |

## 配置分层

`application.yml` 中敏感配置（DB 用户名/密码）通过环境变量（`DB_USER` / `DB_PASSWORD`）注入，禁止硬编码明文。

## 错误处理

业务异常统一继承 `RuntimeException` 并实现为 `BusinessException`（含 `errorCode` / `userTip`），
由 `GlobalExceptionHandler` 转换为标准 `ErrorResponse`。禁止吞掉异常。

## 安全约束

- 数据库访问一律使用 MyBatis 参数化 `#{}`，禁止 `${}` 拼接。
- 输入参数通过 `@Valid` + Jakarta Validation 校验。

## 测试策略

- Service 层：Mockito 单元测试，覆盖正常路径、边界值、异常路径。
