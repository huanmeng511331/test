# 架构文档

## 模块列表

| 模块 | 职责 | 文档 |
|------|------|------|
| todo | 内部用户日常待办事项管理（当前最小闭环：新增待办事项） | [docs/modules/todo/README.md](modules/todo/README.md) |

## 分层与约束

- 分层：Controller（开放接口/Web）→ Service → Mapper（DAO）→ MySQL。
- 上层可依赖下层，禁止反向依赖与循环依赖。
- 统一响应结构 `ApiResponse<T>`，业务异常统一走 `BusinessException` + `GlobalExceptionHandler`。
- SQL 一律参数化（MyBatis `#{}`），禁止 `${}` 拼接。
- 数据库表必备字段：`id`、`gmt_create`、`gmt_modified`。