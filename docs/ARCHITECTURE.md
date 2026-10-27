# 待办事项服务 架构文档

## 1. 架构概述

本服务为单体 Spring Boot 应用，遵循 MVC 分层架构，提供待办事项的最小闭环能力（当前仅支持新增）。

## 2. 技术栈

| 组件 | 选型 |
|------|------|
| 语言 | Java 17 |
| 框架 | Spring Boot 3.3.5 |
| ORM | MyBatis 3 (mybatis-spring-boot-starter 3.0.3) |
| 数据库 | MySQL 8 |
| 参数校验 | Jakarta Validation |
| 单元测试 | JUnit 5 + Mockito + AssertJ |

## 3. 模块划分

```
com.example.todo
├── common          # 公共层：常量、异常、统一响应、全局异常处理
├── model           # 数据模型层：DO
├── service         # 服务层：接口 + 实现 + DTO
├── dao             # 数据访问层：Mapper 接口
├── controller      # 控制器层
└── TodoApplication # 启动类
```

## 4. 分层调用关系

```
Controller → Service → DAO(MyBatis Mapper) → MySQL
```

上层依赖下层，禁止反向依赖；服务层接口与实现分离（`service` / `service.impl`）。

## 5. 依赖关系

| 依赖方 | 被依赖方 | 说明 |
|--------|----------|------|
| controller | service | 控制器调用服务接口 |
| service.impl | dao.mapper | 服务实现调用数据访问接口 |
| dao.mapper | MySQL | MyBatis 映射访问数据库 |

## 6. 模块清单

| 模块 | 职责 |
|------|------|
| todo | 待办事项的新增 |

## 7. 约束

- SQL 一律使用 `#{}` 参数化，禁止 `${}` 拼接，防止 SQL 注入。
- 表必备 `id`、`gmt_create`、`gmt_modified` 三字段。
- 错误码遵循「来源 + 四位编号」5 位字符串格式。
- API 响应统一使用 `ApiResponse`，长整型 `id` 以字符串返回。