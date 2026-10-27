# ARCHITECTURE.md

## 模块边界与依赖方向

controller → service → dao(mapper) → model

| 模块 | 职责 | 不得包含 |
|------|------|----------|
| common | 通用响应、异常、错误码、常量 | 业务逻辑、Spring Bean、DB 依赖 |
| model | DO / VO / DTO 数据模型 | 业务逻辑、Spring 注解 |
| dao（mapper） | MyBatis 数据访问 | Controller、业务编排 |
| service | 业务校验与编排 | HTTP 处理、直接 SQL |
| controller（api） | REST 接口、参数校验 | DB 操作、业务计算 |

## 配置分层

application.yml 通过环境变量覆盖（DB_URL / DB_USERNAME / DB_PASSWORD），敏感配置禁止硬编码。

## 错误处理

所有业务异常使用 `BusinessException`，携带 `errorCode` 和 `message`，通过 `GlobalExceptionHandler` 统一转换为 `ApiResponse`。禁止吞异常。

## 安全约束

- 凭证不得硬编码，通过环境变量注入
- SQL 一律使用 `#{}` 参数化，禁止 `${}`
- 用户输入使用 `@Valid` + Jakarta Validation 双重校验
- 日志禁止输出敏感信息

## 测试策略

- Service 层：单元测试 + Mockito，覆盖正常路径、边界值、异常路径
- Mapper/Repository 层：H2 内存数据库（MySQL 模式）验证 SQL

## AI 编码约束

- 改动前先读本文件
- 不跨模块边界修改
- 数据库 Schema 变更必须同步 schema.sql
- 新增 REST 端点必须进行参数校验