# Code Review Report

> **Change** todo（编码实现：新增待办事项最小闭环） · **分支/Commit** AI/task-DEV-… / 60f5414 · **日期** 2026-10-27 · **审查者** AI
>
> 等级 P0/P1/P2；G/S 以 checklist 行内定义为准；Bug 模式 Blocker→P0、Major→P1、Info→P2。已先运行 `scan-all-rules.sh`（52/222 条，No findings）再写 LLM 结论。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 13 |
| 变更行数 | +1082 / -0（commit 60f5414，20 文件） |
| 自动化预扫 | `scan-all-rules.sh`，52/222 规则，No findings |
| 静态/动态 | 环境无 JDK/Maven，`mvn compile/test` 无法执行，转为静态代码审查 |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoApplication | src/main/java/com/antdigital/todo/TodoApplication.java | 启动类 |
| TodoController | src/main/java/com/antdigital/todo/api/controller/TodoController.java | REST 接口层 |
| ErrorCodeEnum | src/main/java/com/antdigital/todo/common/enums/ErrorCodeEnum.java | 错误码枚举 |
| BusinessException | src/main/java/com/antdigital/todo/common/exception/BusinessException.java | 业务异常 |
| GlobalExceptionHandler | src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java | 全局异常处理 |
| ApiResponse | src/main/java/com/antdigital/todo/common/response/ApiResponse.java | 统一响应 |
| TodoItemMapper | src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java | 数据访问 |
| TodoItemDO | src/main/java/com/antdigital/todo/model/do/TodoItemDO.java | 数据对象 |
| TodoCreateRequest | src/main/java/com/antdigital/todo/model/dto/TodoCreateRequest.java | 请求 DTO |
| TodoItemVO | src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java | 展示 VO |
| TodoService | src/main/java/com/antdigital/todo/service/TodoService.java | 业务接口 |
| TodoServiceImpl | src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java | 业务实现 |
| TodoServiceImplTest | src/test/java/com/antdigital/todo/service/impl/TodoServiceImplTest.java | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 2 | 8 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 给定合法请求，调用 POST /todos，持久化并返回 VO | ✅ | 需求「核心功能：新增待办事项」 | TodoController.java:36-42 → TodoServiceImpl.java:34-63 → TodoItemMapper.xml:14-18 | 链路完整，insert 回填 id |

### REQ-2: 任务信息=名称+描述

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 名称必填≤200、描述可选≤1000、名称 trim | ✅ | 需求「任务信息：事项名称和描述」 | TodoCreateRequest.java:12-18；TodoServiceImpl.java:39-53 | DTO 注解 + Service 双重校验 |

### REQ-3: 最小闭环仅创建

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 仅暴露创建，无查询/更新/删除 | ✅ | 需求「最小闭环：仅创建」 | TodoController 仅 @PostMapping；selectById 未暴露 | 符合 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明 |
|------|------|
| ❌ | **A2.4** `TodoServiceImpl.java:3-4` — import 未按 ASCII 字典序（exception 在 enums 前） |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | reliability-checklist.md G1–G17 | ❌ | P1–P2 | G10.1(P1)；G9.2/G11.2/G16.1(P2) |
| 安全 | security-checklist.md S1–S10 | ⚠️ | P2 | S8.1/S9.1/S9.3(P2) |
| Bug 模式 | bug-pattern-checklist.md B/M/I（120） | ✅ | — | 预扫 + LLM 全量核销，无命中 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | customized-checklist.md U* | ✅/N/A | — | U1.1(@Valid) 已满足；其余 N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1**：
  1. `G10.1` `TodoService`/`TodoItemMapper.xml` — VO 的 `gmtCreate/gmtModified` 恒为 null（insert 未回填时间字段，返回体时间字段语义不明确）。
  2. `G16.2` `GlobalExceptionHandler.java:38-43` — 业务异常一律映射 HTTP 500 且丢弃结构化错误码。名称非法/超长属用户侧错误，应映射 400，并通过 `code` 字段返回 `INVALID_PARAMETER(A0001)`。
- **P1/P2**：见 §8。
- **一句话**：功能闭合、分层清晰、SQL 参数化与单测到位；主要问题是业务异常被误判为系统错误（500）及返回体时间字段恒空，需修复后合并；另有若干可读性/加固类 P2。

---

## 7.1 问题片段（必填）

- **P1** `G16.2` `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:38-43` — 业务异常映射为 HTTP 500 并丢弃错误码，参数类业务错误应返回 400 且回填 `A0001`。

```java
L38|    @ExceptionHandler(BusinessException.class)
L39|    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
L40|    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
L41|        logger.warn("业务异常, errorCode: {}, message: {}", ex.getErrorCode(), ex.getMessage());
L42|        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
L43|    }
```

- **P1** `G10.1` `src/main/resources/mapper/TodoItemMapper.xml:14-18` — insert 仅回填 `id`，`gmt_create/gmt_modified` 未回填，导致返回 VO 时间字段恒空。

```xml
L14|    <insert id="insert" parameterType="com.antdigital.todo.model.do.TodoItemDO"
L15|            useGeneratedKeys="true" keyProperty="id">
L16|        INSERT INTO todo_item (item_name, description)
L17|        VALUES (#{itemName}, #{description})
L18|    </insert>
```

- **P2** `A2.4` `src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java:3-4` — import 顺序违反 ASCII 字典序。

```java
L3|import com.antdigital.todo.common.exception.BusinessException;
L4|import com.antdigital.todo.common.enums.ErrorCodeEnum;
```

- **P2** `G11.2` `src/test/java/com/antdigital/todo/service/impl/TodoServiceImplTest.java:31` — 仅覆盖 Service 层，缺 Controller `@Valid` 与异常处理映射测试。

```java
L30|@DisplayName("TodoServiceImpl 单元测试")
L31|class TodoServiceImplTest {
```

- **P2** `S8.1` `src/main/java/com/antdigital/todo/api/controller/TodoController.java:36-42` — 接口未接入鉴权/归属校验。

```java
L36|    @PostMapping
L37|    public ApiResponse<TodoItemVO> createTodo(
L38|            @Valid @RequestBody TodoCreateRequest request) {
L39|        logger.info("收到新增待办事项请求, itemName: {}", request.getItemName());
L40|        TodoItemVO vo = todoService.createTodo(request);
L41|        return ApiResponse.success(vo);
L42|    }
```

- **P2** `S9.1` `src/main/resources/application.yml:6-7` — 弱默认凭证（`todo`）。

```yaml
L6|    username: ${DB_USERNAME:todo}
L7|    password: ${DB_PASSWORD:todo}
```

- **P2** `S9.3` `src/main/resources/application.yml:5` — 关闭 SSL（`useSSL=false`）。

```yaml
L5|    url: jdbc:mysql://127.0.0.1:3306/todo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:38-43` — 将 `handleBusinessException` 的 `@ResponseStatus` 改为 `BAD_REQUEST`，返回体携带 `ex.getErrorCode()`（区分用户侧 A0001 与系统侧错误）。
- [ ] **P1** `src/main/resources/mapper/TodoItemMapper.xml:14-18` + `src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java:59-62` — 回填创建/更新时间（insert 后重新 selectById，或 SQL 显式插入 `gmt_create/gmt_modified` 并 `useGeneratedKeys keyProperty="id, gmtCreate, gmtModified"`…），确保返回 VO 的 `gmtCreate/gmtModified` 非空。

### P2（可选）

- [ ] **P2** `src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java:3-4` — 调整 import 为 ASCII 字典序（`enums` 在 `exception` 前）。
- [ ] **P2** `src/main/resources/application.yml:6-7` — 移除弱默认口令或改为强随机占位，强制环境注入。
- [ ] **P2** `src/main/resources/application.yml:5` — 评估启用 SSL（`useSSL=true`）。
- [ ] **P2** `src/main/java/com/antdigital/todo/api/controller/TodoController.java:36` — 补充控制器层校验/异常映射测试（或接入网关鉴权后在网关注册该接口）。
- [ ] **P2** 测试补充 — 为 `TodoController @Valid`、`GlobalExceptionHandler` 状态码/错误码映射新增 Web 层测试。
- [ ] **P2** 运维加固 — 补充核心链路指标埋点、DB 连接超时显式配置、评估接口鉴权（内部自保服务，可选）。