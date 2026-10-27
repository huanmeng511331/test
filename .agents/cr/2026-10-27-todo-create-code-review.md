# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-.../12bcc56` · **日期** `2026-10-27` · **审查者** AI
>
> 已先运行 `scan-all-rules.sh`（覆盖 52/222 条可程序化规则）对 13 个 `.java` + 1 个 `.xml` + 1 个 `.sql` 统一预扫，**无命中**；随后 LLM 完成 Step 2–5 未覆盖项并人工复核。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 13 |
| 变更行数 | `+939 / -0`（commit `12bcc56`，相对基线 `65c462d`） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoApplication | src/main/java/com/dtazza/todo/TodoApplication.java | 启动入口 |
| GlobalExceptionHandler | src/main/java/com/dtazza/todo/api/controller/GlobalExceptionHandler.java | 全局异常处理 |
| TodoController | src/main/java/com/dtazza/todo/api/controller/TodoController.java | REST 创建接口 |
| CreateTodoRequest | src/main/java/com/dtazza/todo/api/request/CreateTodoRequest.java | 请求 DTO + 校验 |
| BusinessException | src/main/java/com/dtazza/todo/common/exception/BusinessException.java | 业务异常 |
| ApiResponse | src/main/java/com/dtazza/todo/common/model/ApiResponse.java | 统一成功响应 |
| ErrorResponse | src/main/java/com/dtazza/todo/common/model/ErrorResponse.java | 统一错误响应 |
| TodoDO | src/main/java/com/dtazza/todo/dao/entity/TodoDO.java | 数据对象 |
| TodoMapper | src/main/java/com/dtazza/todo/dao/mapper/TodoMapper.java | 数据访问接口 |
| TodoVO | src/main/java/com/dtazza/todo/model/vo/TodoVO.java | 展示对象 |
| TodoService | src/main/java/com/dtazza/todo/service/TodoService.java | 服务接口 |
| TodoServiceImpl | src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java | 业务编排 |
| TodoServiceImplTest | src/test/java/com/dtazza/todo/service/impl/TodoServiceImplTest.java | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 4 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 合法标题/描述 → POST /todos → 落库并返回创建对象 | ✅ | 需求「核心功能：新增待办事项」；README「POST /todos」 | TodoController.java:32-34；TodoServiceImpl.java:27-46；TodoMapper.xml:14-18 | 链路完整；`useGeneratedKeys` 回填 id，返回含 id |
| Given 标题为空白 → 拒绝 | ✅ | README「title 必填」 | CreateTodoRequest.java:15-16；TodoServiceImpl.java:29-32 | 注解 + Service 二次校验（trim 后空串兜底，防绕过） |
| Given 描述为 null → 接受为 '' | ✅ | README「description 选填」 | TodoServiceImpl.java:33 | null→""，且 DB 列 `NOT NULL DEFAULT ''` 兜底 |
| 最小闭环仅创建 | ✅ | 需求「最小闭环：仅创建」 | TodoController.java 全文件 | 仅 `@PostMapping`，无多余接口 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | `A2.3/A2.4`：`TodoMapper.java:3-5` 非静态 import 排序非字典序（`org.apache.ibatis...` 应排在 `com.dtazza...` 之后）；`CreateTodoRequest.java:3-6` `java.*` 与 `jakarta.*` 非静态 import 间被空行拆分为两组，违反「非静态 import 同组连续、空行一节」约定。均为风格问题，P2。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P0–P1 | 见下方明细；无并发/事务/MQ/缓存/调度/资金场景，均 N/A |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P0–P1 | 无 SQL 注入（`#{}`）、无 XSS/SSRF/命令/XXE/反序列化/文件上传：N/A；鉴权缺失（S8.1）与 DB 密码默认值（S9.1）见明细 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P0–P2 | 预扫 `scan-all-rules.sh` 无命中；LLM 补扫：`I004` 命中（Date 类型建议），余均 N/A/✅ |

### 可靠性明细

- **P0** `G16.3`：`TodoServiceImpl.java:40-42` 将数据库写入失败（`affected != 1`，系统级异常）抛为 `BusinessException("B0001", "新增待办事项失败")`，经 `GlobalExceptionHandler.handleBusinessException` 返回 **HTTP 400 + errorCode=B0001**，误标「系统内部错误」为客户端错误；而真正的系统异常 `B0001` 应走 5xx 或独立 DB 异常路径。属契约/级别错位。
- **P1** `G16.2`：`ErrorResponse.java:12-16` / `GlobalExceptionHandler.java:42-44` 错误响应缺 `traceId/requestId` 等可追溯上下文，跨系统排障定位困难。
- **P1** `G11.3`：`TodoServiceImpl.java:29` 未对 `request == null` 做防御性校验（依赖 Controller `@RequestBody` 保证非空；若被其他调用方直接调用存在 NPE 风险）。

### 安全明细

- **P1** `S8.1`：`TodoController.java:16-34` 创建接口未接入鉴权/网关访问控制。当前「内部用户」场景未提供任何身份校验，建议接入数科网关或应用级鉴权。
- **P1** `S9.1`：`application.yml:8` `${DB_PASSWORD:todo}` 提供明文默认值 `todo`，若部署未注入 `DB_PASSWORD` 将使用弱口令；建议去掉默认值或改为占位必填。

### Bug 模式明细

- **P2** `I004`：`TodoDO.java:4` 使用 `java.util.Date` 承载 `gmtCreate/gmtModified`，建议改用 `Instant`/`LocalDateTime`（配合 MyBatis 类型处理器）以规避时区/线程安全与可读性问题。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则（文件仅示例项） |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：1. `G16.3` — DB 写入失败被误标为业务异常/HTTP 400，错误语义与错误码（B0001）冲突。
- **P1/P2**：
  1. `G16.2` — 错误响应缺少 traceId 上下文（P1）
  2. `S8.1` — 接口无鉴权（P1）
  3. `S9.1` — DB 密码明文默认值（P1）
  4. `G11.3` — Service 入参无 null 防御（P1）
  5. `A2.4` — import 排序/分组（P2）
  6. `I004` — `java.util.Date` 建议替换（P2）
- **一句话**：功能闭合完整、分层清晰、测试充分，SQL 均参数化、无注入风险；主要问题在「DB 写入失败被误标为客户端 4xx 错误」的语义错位，以及缺少鉴权与可追溯日志上下文，建议修复后合并。

---

## 7.1 问题片段（必填）

- **P0** `G16.3` `src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java:40-42` — DB 写入失败（系统级）被抛为 BusinessException，同码 B0001 却走 HTTP 400。
  片段范围：`src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java:39-42`

```java
L39|        int affected = todoMapper.insert(todo);
L40|        if (affected != 1) {
L41|            throw new BusinessException("B0001", "新增待办事项失败");
L42|        }
```

- **P1** `G16.2` `src/main/java/com/dtazza/todo/api/controller/GlobalExceptionHandler.java:42-44` — 系统异常响应缺 traceId/requestId 上下文。
  片段范围：`src/main/java/com/dtazza/todo/api/controller/GlobalExceptionHandler.java:40-45`

```java
L40|    @ExceptionHandler(Exception.class)
L41|    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
L42|    public ErrorResponse handleException(Exception ex) {
L43|        logger.error("系统异常", ex);
L44|        return new ErrorResponse("B0001", "系统内部错误", "系统繁忙，请稍后重试");
L45|    }
```

- **P1** `S8.1` `src/main/java/com/dtazza/todo/api/controller/TodoController.java:32-34` — 创建接口未接入鉴权。
  片段范围：`src/main/java/com/dtazza/todo/api/controller/TodoController.java:32-35`

```java
L32|    @PostMapping
L33|    public ApiResponse<TodoVO> create(@Valid @RequestBody CreateTodoRequest request) {
L34|        return ApiResponse.success(todoService.create(request));
L35|    }
```

- **P1** `S9.1` `src/main/resources/application.yml:8` — DB 密码含明文默认值。
  片段范围：`src/main/resources/application.yml:6-8`

```yaml
L6|    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:todo}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
L7|    username: ${DB_USER:todo}
L8|    password: ${DB_PASSWORD:todo}
```

- **P1** `G11.3` `src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java:27-29` — `request` 入参无 null 防御。
  片段范围：`src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java:27-31`

```java
L27|    @Override
L28|    public TodoVO create(CreateTodoRequest request) {
L29|        String title = trimToNull(request.getTitle());
L30|        if (title == null) {
L31|            throw new BusinessException("A0001", "事项名称不能为空");
```

- **P2** `I004` `src/main/java/com/dtazza/todo/dao/entity/TodoDO.java:4` — 建议 `Instant`/`LocalDateTime` 替代 `java.util.Date`。
  片段范围：`src/main/java/com/dtazza/todo/dao/entity/TodoDO.java:19-21`

```java
L19|    private Date gmtCreate;
L20|
L21|    private Date gmtModified;
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java:40-42` — 将数据库写入失败改为系统异常语义（抛独立 `SystemException` 或统一走 5xx），与「客户端参数错误（A 码）/系统内部错误（B 码）」错误码契约对齐。

### P1

- [ ] **P1** `G16.2` — 为 `ErrorResponse`/`GlobalExceptionHandler` 增加 `traceId`（或 requestId）字段并在异常日志与响应中透出。
- [ ] **P1** `S8.1` — 在 `TodoController` 创建接口接入鉴权/网关访问控制（内部用户身份校验）。
- [ ] **P1** `S9.1` — 去掉 `application.yml` 中 `${DB_PASSWORD:todo}` 的明文默认值，改为必填占位或从配置中心注入。
- [ ] **P1** `G11.3` — `TodoServiceImpl.create` 增加 `request == null` 防御性校验（或加 `@NonNull` 约定并统一兜底）。

### P2（可选）

- [ ] **P2** `A2.4` — 修正 `TodoMapper.java`、`CreateTodoRequest.java` 的 import 分组与字典序。
- [ ] **P2** `I004` — 将 `TodoDO.gmtCreate/gmtModified` 由 `java.util.Date` 改为 `Instant`/`LocalDateTime`（同步调整 XML jdbcType 与 `map-underscore-to-camel-case` 映射）。