# 待办事项（Todo）系统架构

## 模块边界与依赖方向

应用采用经典四层分层（Web/API → Service → DAO → DB），接口与实现分离。

| 层次 | 包路径 | 职责 | 不得包含 |
|------|--------|------|----------|
| common | `com.alipay.todo.common` | 常量、错误码、业务异常、统一响应体 | 业务编排、DB 访问、HTTP 处理 |
| api | `com.alipay.todo.api` | REST Controller、请求对象、参数校验 | DB 操作、业务计算 |
| service | `com.alipay.todo.service` | 待办事项创建的业务规则与事务编排 | HTTP 处理、直接 SQL |
| dao | `com.alipay.todo.dao` | 待办事项数据访问（MyBatis Mapper） | Controller、业务编排 |
| model | `com.alipay.todo.model` | 视图对象（vo） | Spring Bean、DB 依赖 |

依赖方向：`api → service → dao → model/common`，下层不得反向依赖上层。

## 错误处理

- 所有业务异常使用 `TodoException`，携带 `errorCode`（格式「错误来源 + 四位数字」，A 表示用户端错误）。
- `ApiResponse` 统一封装成功与错误响应，错误响应包含 `errorCode`、`errorMessage`、`userTip`。

## 安全约束

- 所有 SQL 使用 `#{}` 参数化，禁止 `${}` 拼接。
- 请求参数通过 `jakarta.validation`（`@NotBlank`、`@Size`）双重校验。

## 数据库

- 表 `todo_item` 使用小写表名，包含必备字段 `id`、`gmt_create`、`gmt_modified`，主键索引名 `pk_id`。

## 测试策略

- Service 层：单元测试 + Mockito，覆盖正常路径、参数校验、异常路径与边界值。
