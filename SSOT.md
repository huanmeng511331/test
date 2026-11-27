# SSOT — 项目单一事实源

## 文档路径

| 文档类型 | 路径 | 说明 |
|----------|------|------|
| 架构文档 | `docs/ARCHITECTURE.md` | 模块边界、分层、约束 |
| 模块文档 | `docs/modules/{module}/README.md` | 模块职责、关键类 |
| 系分方案 | `.agents/{变更目录}/design.md` | 由变更流水线指定 |
| 编码报告 | `.agents/{变更目录}/impl.md` | 编码实现产出报告 |

## 技术栈

| 项 | 选型 |
|----|------|
| 语言/JDK | Java 17 |
| 框架 | Spring Boot 3.2.x |
| 持久层 | MyBatis-Plus(spring-boot3-starter) |
| 数据库 | MySQL 8(utf8mb4) |
| 构建 | Maven 单体应用 `todo-base-app` |

## 工程约定

- 根包:`com.dtazziboot.base`;业务模块包 `com.dtazziboot.base.{module}`;公共能力 `common`。
- 分层:Controller → Service(接口+Impl) → Mapper(MyBatis-Plus);公共类在 `common` 包。
- 响应结构:统一 `{code, msg, data}`(`common/api/ApiResult`),成功 `OK/SUCCESS`。
- 错误码:字符串 `{MODULE}_{SEQ}`(如 `TODO_001`);常量收口在 `common/constant` 与各模块 `constant`。
- 登录态:`/api/**` 统一由 `LoginUserInterceptor` 校验(网关透传 `X-User-Id`),业务从 `LoginUserContext` 取当前用户,未登录返回 `AUTH_001`。
- 数据库:表必备 `id/gmt_create/gmt_modified`;时间字段 datetime 由应用层维护(`GmtFieldsMetaObjectHandler` 自动填充);DDL 在 `src/main/resources/db/init/`。
- 应急开关:模块级配置项(如 `todo.create.enabled`)优先于回滚。
