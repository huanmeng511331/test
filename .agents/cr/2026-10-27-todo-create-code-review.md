# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-…86c4-46a2-ba82-7dd86dc5a34b` / `f3fddb2` · **日期** `2026-10-27` · **审查者** AI

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 13 |
| 变更行数 | `+1024 / -0`（20 files） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoApplication | `src/main/java/com/dtaz/todo/TodoApplication.java` | 启动类 |
| TodoController | `src/main/java/com/dtaz/todo/api/controller/TodoController.java` | 新增接口入口 |
| TodoErrorCodeEnum | `src/main/java/com/dtaz/todo/common/constant/TodoErrorCodeEnum.java` | 错误码枚举 |
| BusinessException | `src/main/java/com/dtaz/todo/common/exception/BusinessException.java` | 业务异常 |
| GlobalExceptionHandler | `src/main/java/com/dtaz/todo/common/exception/GlobalExceptionHandler.java` | 异常处理 |
| ApiResponse | `src/main/java/com/dtaz/todo/common/model/ApiResponse.java` | 统一响应 |
| TodoItemMapper | `src/main/java/com/dtaz/todo/dao/mapper/TodoItemMapper.java` | 数据访问接口 |
| TodoItemDO | `src/main/java/com/dtaz/todo/model/do/TodoItemDO.java` | 数据对象 |
| TodoCreateRequest | `src/main/java/com/dtaz/todo/model/dto/TodoCreateRequest.java` | 请求 DTO |
| TodoItemVO | `src/main/java/com/dtaz/todo/model/vo/TodoItemVO.java` | 视图对象 |
| TodoItemService | `src/main/java/com/dtaz/todo/service/TodoItemService.java` | 服务接口 |
| TodoItemServiceImpl | `src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java` | 业务实现 |
| TodoItemServiceImplTest | `src/test/java/com/dtaz/todo/service/impl/TodoItemServiceImplTest.java` | 单元测试 |

> 非 Java（仅作配置/契约佐证）：`pom.xml`、`application.yml`、`db/schema.sql`、`mapper/TodoItemMapper.xml`、`.agents/…/impl.md`、`docs/ARCHITECTURE.md`、`docs/modules/todo/README.md`。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1 新增待办事项

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 有效请求 POST /todos → 落库并返回 | ✅ | 「核心功能：新增待办事项」「最小闭环：仅创建」 | `TodoController.java:35-39` `TodoItemServiceImpl.java:42-65` `TodoItemMapper.xml:14-18` | 全链路贯通 |

### REQ-2 事项名称必填

| Scenario | 结果 | Spec证据 | 代码证据 |
|----------|------|----------|----------|
| title 为空 → 参数错误 | ✅ | impl.md L89「@Valid/@NotBlank」 | `TodoCreateRequest.java:15` `GlobalExceptionHandler.java:46-53` |

### REQ-3 事项描述可选、限长

| Scenario | 结果 | Spec证据 | 代码证据 |
|----------|------|----------|----------|
| description 可空/≤500 | ✅ | impl.md L89「@Size」 | `TodoCreateRequest.java:20` 测试 `should_setCurrentTime_when_createTodo` |

### REQ-4 统一响应契约

| Scenario | 结果 | Spec证据 | 代码证据 |
|----------|------|----------|----------|
| 返回 ApiResponse | ✅ | impl.md L93「ApiResponse 统一封装」 | `ApiResponse.java:37-60` |

### REQ-5 回写主键 ID

| Scenario | 结果 | Spec证据 | 代码证据 |
|----------|------|----------|----------|
| id 回填 | ✅ | mapper 变更 | `TodoItemMapper.xml:14-15` `TodoItemServiceImpl.java:61` |

### REQ-6 异常路径

| Scenario | 结果 | Spec证据 | 代码证据 |
|----------|------|----------|----------|
| 参数非法/系统失败 → 对应错误码 | ✅ | impl.md TEST 章节 | `TodoItemServiceImpl.java:43-45,55-58` `GlobalExceptionHandler.java:31-67` |

### REQ-7 时间字段一致性

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 返回值 gmtCreate 与落库值一致 | ⚠️ | impl.md L90「必备字段 gmt_create/gmt_modified」 | `TodoItemServiceImpl.java:50-52` vs `TodoItemMapper.xml:16-17` | service 层设置 gmtCreate/gmtModified，但 insert 仅写 title/description，落库实际由 DB `DEFAULT CURRENT_TIMESTAMP` 决定，与返回值来源不同源（P1，见 §7） |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | `A2.4` `TodoItemServiceImpl.java:15`：`import java.time.LocalDateTime;` 置于 org.* import 之后且空行组之后，应置顶按字典序；其余 A1–A7 无违规（预扫 A 类 8 条无命中） |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | `G14.4` `TodoItemServiceImpl.java:50` `LocalDateTime.now()` 未显式指定时区；`G14.3` 时间语义依赖默认时区。其余 G 项 ✅/N/A（预扫 G 类 4 条无命中） |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1 | `S9.1` `application.yml:9-10` 硬编码 root/root 凭据；其余 S 项 ✅/N/A（预扫 S 类 7 条无命中） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | 预扫命中 `M016` `TodoItemServiceImpl.java:50` `LocalDateTime.now()` 未显式指定时区（Major→P1）；其余 B/M/I ✅/N/A |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | P0–P2 | `U1.1` Controller 已用 `@Valid`（TodoController.java:36）；其余 U 项未启用 |

---

## 7. 结论

- **合并建议**：修复后合并（无 P0 阻塞；3 个 P1 建议合并前修复）
- **P0**：无
- **P1/P2**：
  1. **P1** `S9.1` `application.yml:9-10` — 硬编码数据库凭据，应从环境变量/配置中心注入。
  2. **P1** `M016` / `G14.4` `TodoItemServiceImpl.java:50` — `LocalDateTime.now()` 未显式指定时区。
  3. **P1** `G14.3` + REQ-7 — service 层设置的 gmtCreate/gmtModified 未被 insert 落库，返回值与库值不同源。
  4. **P2** `S8.3` `TodoItemMapper.xml:2` schema 自增 BIGINT 主键可预测。
  5. **P2** `G2.1` POST /todos 无幂等键（低成本新增场景，参考项）。
  6. **P2** `A2.4` `TodoItemServiceImpl.java:15` import 顺序。
- **一句话**：核心「新增待办」闭环实现正确、分层清晰、测试充分；主要风险集中在时间语义与敏感配置硬编码，均为可快速收敛的 P1。

---

## 7.1 问题片段（必填）

- **P1** `S9.1` `src/main/resources/application.yml:9-10` — 数据库账号口令硬编码，应改用环境变量注入。  
  片段范围：`src/main/resources/application.yml:7-11`

```yaml
L7 |  datasource:
L8 |    url: jdbc:mysql://localhost:3306/dtaz_todo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
L9 |    username: root
L10|    password: root
L11|    driver-class-name: com.mysql.cj.jdbc.Driver
```

- **P1** `M016` / `G14.4` `src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java:50` — `LocalDateTime.now()` 未显式指定时区；且随后设置的时间字段未进入 insert。  
  片段范围：`src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java:50-58`

```java
L50|        LocalDateTime now = LocalDateTime.now();
L51|        todoItemDO.setGmtCreate(now);
L52|        todoItemDO.setGmtModified(now);
L53|
L54|        int rows = todoItemMapper.insert(todoItemDO);
L55|        if (rows != 1) {
L56|            LOGGER.error("新增待办事项失败, rows: {}, request: {}", rows, request);
L57|            throw new BusinessException(TodoErrorCodeEnum.SYSTEM_ERROR);
L58|        }
```

- **P1** `G14.3` / REQ-7 `src/main/resources/mapper/TodoItemMapper.xml:16-17` — insert 仅写 title/description，gmt_create/gmt_modified 由 DB 默认值落库，与 service 层设置的返回值时间不同源。  
  片段范围：`src/main/resources/mapper/TodoItemMapper.xml:14-18`

```xml
L14|    <insert id="insert" parameterType="com.dtaz.todo.model.do.TodoItemDO"
L15|            useGeneratedKeys="true" keyProperty="id">
L16|        INSERT INTO todo_item (title, description)
L17|        VALUES (#{title}, #{description})
L18|    </insert>
```

- **P2** `A2.4` `src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java:14-15` — `java.time` import 排在 org.* 之后且隔了空行，应置顶。  
  片段范围：`src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java:12-15`

```java
L12|import org.springframework.stereotype.Service;
L13|import org.springframework.transaction.annotation.Transactional;
L14|
L15|import java.time.LocalDateTime;
```

---

## 8. 修复任务列表

### P0

- [ ] 无待修复项。

### P1

- [ ] **P1** `src/main/resources/application.yml:9-10` — 将 `username`/`password` 改为从环境变量/配置中心读取，移除硬编码凭据。
- [ ] **P1** `TodoItemServiceImpl.java:50` — `LocalDateTime.now()` 显式指定时区（如 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))`），保持与 DB `serverTimezone` 一致。
- [ ] **P1** `TodoItemMapper.xml:16-17` — 统一时间字段写入策略：或让 insert 显式写 `gmt_create`/`gmt_modified` 落库，或返回 VO 后从库值回读，消除返回值与库值不同源。

### P2（可选）

- [ ] **P2** `TodoItemServiceImpl.java:15` — 调整 import 顺序，将 `java.time.LocalDateTime` 置顶（字典序）。
- [ ] **P2** `TodoController.java:35` — 若正式化，为新增接口评估幂等/防重（如客户端请求 token）。
- [ ] **P2** `db/schema.sql` — 若需不可预测 ID，评估改用 UUID/雪花主键（当前自增 BIGINT 可预测，内部系统可接受）。