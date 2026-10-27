# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-…aef-/4b56849` · **日期** `2026-10-27` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；预扫 `scan-all-rules.sh` 已完成并并入 §5。问题含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14（main 12 + test 2） |
| 变更行数 | `+1120 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoApplication | src/main/java/com/dtk/todo/TodoApplication.java | 启动类 |
| TodoErrorCodeEnum | src/main/java/com/dtk/todo/common/constant/TodoErrorCodeEnum.java | 错误码枚举 |
| GlobalExceptionHandler | src/main/java/com/dtk/todo/common/exception/GlobalExceptionHandler.java | 全局异常 |
| TodoException | src/main/java/com/dtk/todo/common/exception/TodoException.java | 业务异常 |
| ApiResponse | src/main/java/com/dtk/todo/common/model/ApiResponse.java | 统一响应体 |
| TodoItemController | src/main/java/com/dtk/todo/controller/TodoItemController.java | REST 接口 |
| TodoItemMapper | src/main/java/com/dtk/todo/dao/mapper/TodoItemMapper.java | 数据访问 |
| TodoItemCreateRequest / TodoItemVO | src/main/java/com/dtk/todo/model/dto/ | 请求/视图对象 |
| TodoItemDO | src/main/java/com/dtk/todo/model/entity/TodoItemDO.java | 数据对象 |
| TodoItemService / TodoItemServiceImpl | src/main/java/com/dtk/todo/service/ | 业务编排 |
| TodoItemControllerTest / TodoItemServiceImplTest | src/test/java/com/dtk/todo/ | 测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 1 | 3 |

---

## 3. Step 2 — 功能（REQ）

| REQ | 结果 | Spec证据 | 代码证据 | 说明 |
|-----|------|----------|----------|------|
| REQ-1 新增待办 | ✅ | 核心功能：新增待办事项 / 最小闭环：仅创建 | `TodoItemController.java:40-44`；`TodoItemMapper.xml:14-18` | POST `/todo_items` 全链路闭环 |
| REQ-2 名称+描述 | ✅ | 任务信息：事项名称和描述 | `TodoItemCreateRequest.java:14-18`；`schema.sql:2-9` | 两项均落库并回显 |
| REQ-3 标题必填校验 | ✅ | 事项名称（据 @NotBlank/测试） | `TodoItemCreateRequest.java:14`；`TodoItemServiceImpl.java:40-44`；`TodoItemServiceImplTest.java:90-105` | 空白标题被拒且不落库 |
| REQ-4 仅创建 | ✅ | 最小闭环：仅创建 | `TodoItemMapper.java:21`；`TodoItemMapper.xml` | 仅 insert，无查询/更新/删除 |

功能层面与需求一致，无 P0 不符。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明 |
|------|------|
| ✅ | A1–A7 均通过：无 Tab/超长行/通配 import；命名、@Override、Javadoc、Long 大写 L 符合规范 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | G1–G17 | ⚠️ | P1/P2 | G11.2（P2）长度边界、G14.3/M016（P1）时区不一致；其余 N/A/✅ |
| 安全 | S1–S10 | ✅ | — | S1.1 参数化 `#{}`；S9.1 密码走环境变量；S8.2 POST；其余 N/A |
| Bug 模式 | B/M/I（120） | ⚠️ | P1 | 预扫：`scan-all-rules.sh`（52/222，P0=0/P1=1/P2=0）→ 命中 `M016` `TodoItemServiceImpl.java:52`；LLM 补扫无新增 | 

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | U* | N/A | — | 未启用自定义规则（`customized-checklist.md` 仅示例项） |

---

## 7. 结论

- **合并建议**：修复后合并（无 P0 阻塞项；1 个 P1 合并前应修复，3 个 P2 可选改进）
- **P0**：无。
- **P1**：
  1. `M016` / `G14.3` 时区不一致——`LocalDateTime.now()` 依赖系统默认时区，与数据源 `serverTimezone=UTC` 基准不一，跨时区部署存在时间偏差风险。
- **P1/P2**：
  1. `P2` `G11.2` `TodoItemCreateRequest.java:14-18` —— `title`/`description` 缺少 `@Size` 长度校验，超长落库会触发 DB `Data too long` 并被兜底为「系统异常」。
  2. `P2` `GlobalExceptionHandler.java:68` —— 系统异常错误码 `"B0001"` 硬编码，未纳入 `TodoErrorCodeEnum`（违背模块自身「无魔法值」约定）。
  3. `P2` `TodoItemServiceImpl.java:59` —— 未校验 `insert` 受影响行数，且 `id` 依赖 `useGeneratedKeys` 回填，插入异常时可能静默返回 `id=null`。
- **一句话**：分层清晰、命名与异常/响应体统一，功能符合「仅创建」最小闭环；主要风险集中在时区一致性（P1）与输入长度边界校验（P2），建议修复后合并。

---

## 7.1 问题片段（必填）

- **P1** `M016`/`G14.3` `src/main/java/com/dtk/todo/service/impl/TodoItemServiceImpl.java:52` — 当前时间取系统默认时区，与 `application.yml` 中 `serverTimezone=UTC` 基准不一致，跨时区部署存在偏差。 片段范围：`src/main/java/com/dtk/todo/service/impl/TodoItemServiceImpl.java:46-57`

```java
L46|        String trimmedTitle = title.trim();
L47|        String description = request.getDescription();
L48|        if (description != null) {
L49|            description = description.trim();
L50|        }
L51|
L52|        LocalDateTime now = LocalDateTime.now();
L53|        TodoItemDO todoItemDO = new TodoItemDO();
L54|        todoItemDO.setTitle(trimmedTitle);
L55|        todoItemDO.setDescription(description);
L56|        todoItemDO.setCreateTime(now);
L57|        todoItemDO.setModifiedTime(now);
```

- **P2** `G11.2` `src/main/java/com/dtk/todo/model/dto/TodoItemCreateRequest.java:14-18` — 字段仅 `@NotBlank`，无 `@Size` 上限（`title VARCHAR(128)`、`description VARCHAR(512)`）。 片段范围：`src/main/java/com/dtk/todo/model/dto/TodoItemCreateRequest.java:13-18`

```java
L13|    /** 事项名称 */
L14|    @NotBlank(message = "事项名称不能为空")
L15|    private String title;
L16|
L17|    /** 事项描述 */
L18|    private String description;
```

- **P2** `GlobalExceptionHandler.java:68` — 系统异常错误码与文案硬编码，未复用 `TodoErrorCodeEnum`。 片段范围：`src/main/java/com/dtk/todo/common/exception/GlobalExceptionHandler.java:65-69`

```java
L65|    @ExceptionHandler(Exception.class)
L66|    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
L67|        LOGGER.error("系统异常: {}", exception.getMessage(), exception);
L68|        ApiResponse<Void> response = ApiResponse.error("B0001", "系统异常", "系统繁忙，请稍后重试");
L69|        return ResponseEntity.status(HttpStatus.OK).body(response);
```

- **P2** `TodoItemServiceImpl.java:59` — `insert` 返回值未校验，`getId()` 依赖 `useGeneratedKeys` 回填。 片段范围：`src/main/java/com/dtk/todo/service/impl/TodoItemServiceImpl.java:59-66`

```java
L59|        todoItemMapper.insert(todoItemDO);
L60|
L61|        if (LOGGER.isInfoEnabled()) {
L62|            LOGGER.info("新增待办事项成功, id: {}, title: {}", todoItemDO.getId(), trimmedTitle);
L63|        }
L64|
L65|        TodoItemVO todoItemVO = new TodoItemVO();
L66|        todoItemVO.setId(todoItemDO.getId());
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/dtk/todo/service/impl/TodoItemServiceImpl.java:52` — 改用显式时区（如 `LocalDateTime.now(ZoneOffset.UTC)` 或 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))`）并与 `application.yml` 的 `serverTimezone` 对齐；或将时间统一交由 DB `CURRENT_TIMESTAMP` 生成。
- [ ] **P1** `M016`/`G14.3` — 确认应用时区约定，消除「应用生成时间」与「JDBC serverTimezone=UTC」两种时间基准的混用。

### P2（可选）

- [ ] **P2** `src/main/java/com/dtk/todo/model/dto/TodoItemCreateRequest.java:14` — 为 `title` 增加 `@Size(max = 128)`、为 `description` 增加 `@Size(max = 512)`，与 schema 列长对齐，避免超长触发系统异常。
- [ ] **P2** `src/main/java/com/dtk/todo/common/exception/GlobalExceptionHandler.java:68` — 将 `B0001`/「系统异常」纳入 `TodoErrorCodeEnum`，消除魔法值。
- [ ] **P2** `src/main/java/com/dtk/todo/service/impl/TodoItemServiceImpl.java:59` — 校验 `insert` 返回值，失败时抛出业务异常并确保 `id` 回填非空后再返回。