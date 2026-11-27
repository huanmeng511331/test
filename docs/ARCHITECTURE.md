# todo-base-app 架构文档

## 模块边界与依赖方向

```
common(底座公共能力) ← todo(待办模块) ← Web 控制台(oneapi)
todo → MySQL(todo_item 单表)
```

| 模块 | 职责 | 不得包含 |
|------|------|----------|
| common | 统一响应/异常处理/错误码常量/登录态上下文与拦截器/MyBatis-Plus 填充 | 业务逻辑、模块私有 DTO |
| todo | 待办事项业务(新增待办 F01:参数校验、创建人注入、落库) | 跨模块编排、直接 HTTP 客户端 |

## 分层与调用关系

`TodoItemController`(Web 层:功能开关、R01~R03 参数校验)
→ `TodoItemService`/`TodoItemServiceImpl`(业务层:实体组装、创建人注入)
→ `TodoItemMapper`(DAO 层:MyBatis-Plus 单表插入,参数化预编译)
→ MySQL `todo_item`。

## 配置分层

application.yml < application-{profile}.yml < 环境变量 < 启动参数。
DB 密码经环境变量(`DB_USERNAME`/`DB_PASSWORD`)注入,禁止硬编码。
业务开关:`todo.create.enabled`(关闭后新增待办返回 `TODO_004` 功能维护中,秒级止血)。

## 错误处理

业务异常统一抛 `BizException(code, message)`,由 `GlobalExceptionHandler` 转为 `{code, msg, data}`;
未识别异常兜底 `SYS_999` 并记录 ERROR 日志;禁止吞异常。
错误码:TODO_001/002/003(参数校验)、TODO_004(开关关闭)、AUTH_001(登录态)、SYS_999(系统兜底)。

## 安全约束

- 创建人等身份一律服务端从 `LoginUserContext` 注入,不信任前端入参。
- `/api/**` 全部经 `LoginUserInterceptor` 登录态校验,无白名单;未登录返回 `AUTH_001`。
- SQL 全参数化(MyBatis-Plus `#{}`),无 `${}` 拼接。
- 日志不输出 description 全文,仅记录长度摘要。

## 测试策略

- Service 层:JUnit 5 + Mockito 纯单测,不启动容器(`TodoItemServiceImplTest`)。
- Controller 层:MockMvc standalone + Mock Service(`TodoItemControllerTest`)。
- DAO/SQL:当前最小闭环仅单表插入,暂不建集成测试;查询类功能上线时按 Testcontainers+MySQL 补齐。

## AI 编码约束

- 改动前先读本文件与 `SSOT.md`。
- 新增业务模块需同步本文件模块表与 `docs/modules/{module}/README.md`。
- 数据库 Schema 变更通过 `src/main/resources/db/init/` 新增 DDL,已上线表禁止删除。
