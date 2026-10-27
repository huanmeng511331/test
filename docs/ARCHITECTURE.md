# 架构文档（待办事项服务）

## 1. 系统概述

`todo-service` 是一个基于 Spring Boot 的单体应用，用于帮助内部用户记录日常待办事项。当前最小闭环仅支持「新增待办事项」。

## 2. 模块边界与分层

采用 MVC 分层架构，调用关系自上而下：

```
开放接口层(api) → 服务层(service) → 数据访问层(dao/entity → mapper)
```

| 层 | 包 | 职责 |
|---|---|---|
| 开放接口层 | `com.dtazziboot.todo.api.controller` | HTTP 接口暴露、参数校验转发 |
| 服务层 | `com.dtazziboot.todo.service` / `service.impl` | 业务逻辑（接口与实现分离） |
| 数据访问层 | `com.dtazziboot.todo.dao.mapper` / `dao.entity` | MyBatis 映射与数据对象 |
| 模型层 | `com.dtazziboot.todo.model.dto` | 请求/展示对象（Request/VO） |
| 公共层 | `com.dtazziboot.todo.common` | 统一响应、异常、常量、全局异常处理 |

## 3. 模块依赖关系

```
TodoController → TodoService → (TodoServiceImpl) → TodoMapper → TodoDO
     ↓                                  ↓
ApiResponse                       BusinessException / TodoConstants
```

- 依赖单向向下，无循环依赖。
- 接口与实现分离：`TodoService` / `TodoServiceImpl`。

## 4. 技术栈

| 组件 | 选型 | 版本 |
|---|---|---|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.2.5 |
| 数据访问 | MyBatis | mybatis-spring-boot-starter 3.0.3 |
| 数据库 | MySQL | 8.x |
| 测试 | JUnit 5 + Mockito + AssertJ | 随 Spring Boot 管理 |

## 5. 关键约束

- 表字段/表名小写+下划线；表必备字段 `id`、`gmt_create`、`gmt_modified`。
- MyBatis SQL 参数使用 `#{}`，禁止 `${}`。
- API 响应统一封装为 `ApiResponse<T>`。
- 生产环境禁止 `System.out` / `printStackTrace`；日志使用 SLF4J 占位符。
- 用户输入后端双重校验（Bean Validation + Service 兜底）。