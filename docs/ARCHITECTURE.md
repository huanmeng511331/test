# 待办事项服务架构文档

## 1. 项目概览

待办事项服务（todo-service）为内部用户提供日常待办事项记录能力。当前最小闭环仅支持「新增待办事项」。

## 2. 模块划分

| 模块 | 职责 | 说明 |
|------|------|------|
| todo | 待办事项 | 待办事项的新增（创建）能力 |

## 3. 分层架构

遵循 MVC 分层，依赖方向自上而下：

```
Controller (web) → Service (业务逻辑) → Mapper (数据访问) → MySQL
```

| 层 | 包路径 | 说明 |
|----|--------|------|
| Web 层 | `com.dtazziboot.todo.controller` | 接口接入、参数校验、统一异常处理 |
| Service 层 | `com.dtazziboot.todo.service` / `.impl` | 业务逻辑，接口与实现分离 |
| DAO 层 | `com.dtazziboot.todo.dao.mapper` | MyBatis 数据访问 |
| 数据模型 | `com.dtazziboot.todo.model` | entity（DO）/ dto（VO、Request） |
| 公共层 | `com.dtazziboot.todo.common` | 业务异常、统一响应结构 |

## 4. 约束

- 接口与实现分离，实现类以 `Impl` 结尾。
- MySQL 表名/字段名小写 + 下划线；表必备 `id`、`gmt_create`、`gmt_modified`。
- SQL 一律使用 `#{}` 参数化，禁止 `${}`。
- 日志使用 SLF4J + 占位符；禁止 `System.out`/`printStackTrace`。
- 响应统一为 `ApiResult<T>` 结构，错误含 `errorCode`/`errorMessage`。

## 5. 模块文档索引

- [todo 模块](../modules/todo/README.md)