# dtaz-todo 架构文档

## 模块边界

| 模块 | 职责 |
|------|------|
| todo | 待办事项管理（最小闭环：仅创建） |

## 分层约束

```
api(Controller) → service(Service/Impl) → dao(Mapper) → MySQL
```

- 上层可依赖下层，下层禁止依赖上层。
- 接口与实现分离，ServiceImpl 位于 `service/impl` 包。
- 公共模型/常量/异常统一放在 `common` 包。

## 技术栈

- Spring Boot 3.2.5 / Java 17
- MyBatis 3.0.3 + MySQL
- JUnit 5 + Mockito + AssertJ