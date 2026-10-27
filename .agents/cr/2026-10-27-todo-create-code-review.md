# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-…` / `6e83908` · **日期** `2026-10-27` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以表头为准（Blocker→P0、Major→P1、Info→P2）。已先运行 `scan-all-rules.sh`（52/222 条可程序化规则）并将命中并入 §5。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 13（主代码 12 + 测试 1） |
| 变更行数 | `+1299 / -0`（coding stage，含文档/配置） |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `TodoApplication` | `src/main/java/com/dtazziboot/todo/TodoApplication.java` | 启动类 |
| `TodoController` | `src/main/java/com/dtazziboot/todo/api/controller/TodoController.java` | 新增待办 HTTP 入口 |
| `TodoService` / `TodoServiceImpl` | `src/main/java/com/dtazziboot/todo/service/…` | 业务服务（核心） |
| `TodoMapper` / `TodoDO` | `src/main/java/com/dtazziboot/todo/dao/…` | 数据访问/数据对象 |
| `TodoRequest` / `TodoVO` | `src/main/java/com/dtazziboot/todo/model/dto/…` | 请求/展示对象 |
| `ApiResponse` / `BusinessException` / `GlobalExceptionHandler` / `TodoConstants` | `src/main/java/com/dtazziboot/todo/common/…` | 公共基础设施 |
| `TodoServiceImplTest` | `src/test/java/com/dtazziboot/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 5 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项（最小闭环仅创建）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /todos 合法请求 → 落库并返回 VO | ✅ | 「核心功能：新增待办事项」「最小闭环：仅创建」 | `TodoController.java:45-49` → `TodoServiceImpl.java:61-93` → `TodoMapper.xml:14-18` | useGeneratedKeys 回填 id，链路完整 |

### REQ-2: 任务信息含名称+描述

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name+description 提交并持久化返回 | ✅ | 「任务信息：事项名称和描述」 | `TodoRequest.java:19-27`；`TodoMapper.xml:16-17` | 双字段贯穿 DTO→DO→VO |

### REQ-3: 名称必填+长度限制

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| null/blank/超长被拒绝 | ✅ | impl.md §TEST | `TodoRequest.java:19-20` + `TodoServiceImpl.java:63-69`；测试 `TodoServiceImplTest.java:93-124` | 注解校验+Service 双重兜底 |

### REQ-4: 描述长度限制（可空）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 超 500 拒绝、null 允许 | ✅ | impl.md §TEST | `TodoRequest.java:26` + `TodoServiceImpl.java:71-74`；测试 `:126-136` | 边界覆盖完整 |

### REQ-5: 异常统一响应

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 校验/业务/系统异常 → ApiResponse | ✅ | impl.md §CHECK(异常日志) | `GlobalExceptionHandler.java:34-68` | 三分支覆盖，HTTP 状态与业务码一致 |

**结论**：功能点 100% 覆盖需求，无功能性不符（无 P0），无范围蔓延（仅创建）。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | `A2.4` import 未按 ASCII 字典序：`TodoController.java:3-13`、`TodoServiceImpl.java:3-17`（`com.dtazziboot` 应置于 `java`/`org` 之前）；其余 A1/A3–A7 均通过 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1–P2 | G14.3/G14.4 时区隐患(P1)；G2.1 幂等(P2)；G9.2 超时(P2)；G16.1 埋点(P2) |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1–P2 | S8.1 未接入鉴权(P1)；S9.1 明文凭证(P1)；S2.1/S8.3/S9.3(P2) |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | 预扫命中 `M016`（`TodoServiceImpl.java:76`）；LLM 全表核销无其他命中 |

**自动化预扫证据**（`scan-all-rules.sh`，52/222 条）：
```
Engine: ripgrep
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:76
=== Summary: 1 findings (P0=0, P1=1, P2=0) ===
```

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | — | 仅示例项 U1.1（`@Valid`）满足；其余 `N/A(未启用自定义规则)` |

---

## 7. 结论

- **合并建议**：修复后合并（无 P0 阻塞；3 个 P1 建议上线前处理）
- **P0**：无。
- **P1**：
  1. `M016 / G14.4` `TodoServiceImpl.java:76` — 时间用 `LocalDateTime.now()` 依赖系统默认时区，`DateTimeFormatter` 未显式指定时区；与 DB `CURRENT_TIMESTAMP` 可能跨时区不一致。
  2. `S9.1` `application.yml:10-11` — 数据库账号密码明文硬编码（root/root）。
  3. `S8.1` `TodoController.java:45` — 接口未接入鉴权，内部用户场景若暴露公网则任何人可写。
- **P1/P2 综述**：
  - P2：`G2.1` 写接口无幂等键（重复提交生成重复待办）；`A2.4` import 未按字典序；`G9.2` 数据源未显式超时；`G16.1` 缺埋点；`S2.1/S8.3/S9.3` 安全加固建议。
- **一句话**：功能闭环完整、代码规范度高，仅存在时区、明文凭证、未鉴权三项上线前需处理的 P1，以及若干 P2 改进项。

---

## 7.1 问题片段（必填）

**P1** `M016 / G14.4` `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:76` — `LocalDateTime.now()` 未显式指定时区。
片段范围：`src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:31-32,74-82`

```java
L31|    private static final DateTimeFormatter DATE_TIME_FORMATTER =
L32|            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
...
L76|        LocalDateTime now = LocalDateTime.now();
L77|
L78|        TodoDO todoDO = new TodoDO();
L79|        todoDO.setName(name.trim());
L80|        todoDO.setDescription(description);
L81|        todoDO.setGmtCreate(now);
L82|        todoDO.setGmtModified(now);
```

**P1** `S8.1` `src/main/java/com/dtazziboot/todo/api/controller/TodoController.java:45-49` — 接口未接入鉴权。
片段范围：`src/main/java/com/dtazziboot/todo/api/controller/TodoController.java:44-49`

```java
L44|    @PostMapping
L45|    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoRequest request) {
L46|        TodoVO todoVO = todoService.createTodo(request);
L47|        return ApiResponse.success(todoVO);
L48|    }
L49|}
```

**P1** `S9.1` `src/main/resources/application.yml:10-11` — 数据库凭证明文硬编码（不在 Java 文件）。
片段范围：`N/A(非 Java)`

```yaml
L08|    driver-class-name: com.mysql.cj.jdbc.Driver
L09|    url: jdbc:mysql://localhost:3306/todo_db?…&useSSL=false&…
L10|    username: root
L11|    password: root
```

**P2** `G2.1` `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:84-91` — 写接口无幂等键。
片段范围：`src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:84-91`

```java
L84|        int affectedRows = todoMapper.insert(todoDO);
L85|        if (affectedRows != 1) {
L86|            throw new BusinessException("B0001", "待办事项创建失败");
L87|        }
L88|
L89|        if (logger.isInfoEnabled()) {
L90|            logger.info("待办事项创建成功, id: {}", todoDO.getId());
L91|        }
```

**P2** `A2.4` `src/main/java/com/dtazziboot/todo/api/controller/TodoController.java:3-13` — import 未按 ASCII 字典序。
片段范围：`src/main/java/com/dtazziboot/todo/api/controller/TodoController.java:3-13`

```java
L03|import org.springframework.web.bind.annotation.PostMapping;
L04|import org.springframework.web.bind.annotation.RequestBody;
L05|import org.springframework.web.bind.annotation.RequestMapping;
L06|import org.springframework.web.bind.annotation.RestController;
L07|
L08|import com.dtazziboot.todo.common.model.ApiResponse;
L09|import com.dtazziboot.todo.model.dto.TodoRequest;
L10|import com.dtazziboot.todo.model.dto.TodoVO;
L11|import com.dtazziboot.todo.service.TodoService;
L12|
L13|import jakarta.validation.Valid;
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:76` — 将时间改为显式时区（如 `Instant.now()` / `LocalDateTime.now(ZoneId.of("UTC"))`），并为 `DateTimeFormatter` 指定时区，避免系统默认时区与 DB `CURRENT_TIMESTAMP` 不一致。
- [ ] **P1** `src/main/resources/application.yml:10-11` — 移除明文数据库凭证，改用环境变量（`${DB_USERNAME}`/`${DB_PASSWORD}`）或配置中心注入。
- [ ] **P1** `src/main/java/com/dtazziboot/todo/api/controller/TodoController.java:45` — 接入统一鉴权（或将接口明确限定于内网），否则公网暴露下任何人可写。

### P2

- [ ] **P2** `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:84` — 评估是否需要幂等键（业务单号/唯一约束），防止重复提交产生重复待办。
- [ ] **P2** `src/main/resources/application.yml:9` — 显式配置数据源 `connectTimeout`/`socketTimeout`，并评估内网关闭 `useSSL` 的安全性。
- [ ] **P2** `A2.4` `TodoController.java`、`TodoServiceImpl.java` 等 — 按完整引入名 ASCII 字典序重排 import。
- [ ] **P2** `G16.1` — 为核心链路补充调用量/成功率/耗时埋点（当前最小闭环未引入监控）。
- [ ] **P2** `S2.1/S8.3` — 前端渲染时对 name/description 转义；评估主键可预测性是否需要 UUID。