# Code Review Report

> **Change** `todo 仅创建` · **分支/Commit** `AI/task-DEV-...ae7494f4` / `9b4c3e7` · **日期** `2026-10-27` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已运行 `scan-all-rules.sh` 并将要点并入 §5，再写 LLM 结论。问题含 `path:line` 或清单 ID。每个 ❌/⚠️ 问题在 §7 后附 `.java` 片段（§7.1）。
>
> **环境说明**：本环境无 `java`/`mvn`/`javac`，编译与单测未执行，全部为静态审查（[降级说明]：环境受限，未执行 `mvn compile`/`mvn test`；静态审查覆盖全部变更文件、逻辑分支、边界与类型一致性）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `12` |
| 变更行数 | 新增工程（全部为新增文件） |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `TodoApplication` | `src/main/java/com/dtazziboot/todo/TodoApplication.java` | 启动入口 |
| `BusinessException` | `src/main/java/com/dtazziboot/todo/common/exception/BusinessException.java` | 业务异常 |
| `ApiResult` | `src/main/java/com/dtazziboot/todo/common/result/ApiResult.java` | 统一响应 |
| `GlobalExceptionHandler` | `src/main/java/com/dtazziboot/todo/controller/GlobalExceptionHandler.java` | 全局异常处理 |
| `TodoItemController` | `src/main/java/com/dtazziboot/todo/controller/TodoItemController.java` | 控制器 |
| `TodoItemMapper` | `src/main/java/com/dtazziboot/todo/dao/mapper/TodoItemMapper.java` | MyBatis Mapper |
| `TodoItemCreateRequest` | `src/main/java/com/dtazziboot/todo/model/dto/TodoItemCreateRequest.java` | 请求 DTO |
| `TodoItemVO` | `src/main/java/com/dtazziboot/todo/model/dto/TodoItemVO.java` | 展示 VO |
| `TodoItemDO` | `src/main/java/com/dtazziboot/todo/model/entity/TodoItemDO.java` | 数据对象 |
| `TodoItemService` | `src/main/java/com/dtazziboot/todo/service/TodoItemService.java` | 服务接口 |
| `TodoItemServiceImpl` | `src/main/java/com/dtazziboot/todo/service/impl/TodoItemServiceImpl.java` | 服务实现 |
| `TodoItemServiceImplTest` | `src/test/java/com/dtazziboot/todo/service/impl/TodoItemServiceImplTest.java` | 单元测试 |

辅助上下文文件（非 Java，未进入逐文件队列但已在 §5 核销）：`pom.xml`、`src/main/resources/application.yml`、`src/main/resources/mapper/TodoItemMapper.xml`、`src/main/resources/schema.sql`。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 4 | 5 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项（POST /todo_items）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 名称+描述 → 返回含 id 的新建事项 | ✅ | 「核心功能：新增待办事项」「最小闭环：仅创建」 + README §4 `POST /todo_items` | `TodoItemController.java:35` → `TodoItemServiceImpl.java:34` → `TodoItemMapper.xml:14-18` useGeneratedKeys → `TodoItemVO.id=String` | 链路完整，回填主键正确，测试 `should_returnTodoItemVO_when_requestIsValid` 通过（静态） |

### REQ-2: 事项名称必填校验

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 名称空/空白/null → 拒绝 | ✅ | 「任务信息：事项名称和描述」+ README 错误响应 A0001「事项名称不能为空」 | `TodoItemCreateRequest.java:15` `@NotBlank`、`TodoItemServiceImpl.java:35-37`、`GlobalExceptionHandler.java:27-35` | Controller `@Valid` + Service 防御双重校验；测试覆盖 null/blank |

### REQ-3: 统一 ApiResult 响应

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 任意场景 → ApiResult(code/errorCode/errorMessage/data) | ⚠️ | ARCHITECTURE §4「响应统一为 ApiResult<T>，错误含 errorCode/errorMessage」 | `ApiResult.java:59-72` success/error ✅；`GlobalExceptionHandler.java` 仅处理 BusinessException + MethodArgumentNotValidException | DB 层 `DataAccessException` 等未兜底，返回非 ApiResult 默认 500，契约不完整（见 §5 G8.1/G10.1，P1） |

### REQ-4: id 使用 String 防精度丢失

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 返回 id 为 String | ✅ | README §2「TodoItemVO … id 使用 String 防精度丢失」 | `TodoItemVO.java:13`、`TodoItemServiceImpl.java:58` `String.valueOf(id)` | 符合 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | 整体良好。一处建议：`A3.4` `GlobalExceptionHandler.java:31` `.map(error -> error.getDefaultMessage())` 可改为方法引用 `FieldError::getDefaultMessage`（P2）。其余 A1–A7 均符合（命名、@Override、缩进 4 空格、行宽≤120、无 Tab、无通配符 import、long 大写 L）。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P0–P2 | 命中：`G8.1`/`G10.1`/`G16.2` `GlobalExceptionHandler.java`（缺兜底，P1）；`G11.3`/`G4.2` `TodoItemCreateRequest.java:15-19`（无 @Size，P1）；`G14.3` `TodoItemServiceImpl.java:39`（默认时区，P1，关联 M016）；`G9.2` `application.yml`（未显式 DB 超时，P2）；`G11.2` `TodoItemServiceImplTest.java`（未覆盖超长边界，P2）；`G16.1`/`G17.1`（无埋点/开关，P2 非阻塞） |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P0–P1 | 命中：`S8.1` `TodoItemController.java:34`（未接入鉴权，P1）；`S9.1` `application.yml:7`（默认空密码，P2）。SQL 注入 `S1.1` ✅（全 `#{}`），`S8.2` ✅（POST 写） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P0–P2 | 预扫：`scan-all-rules.sh` 命中 `M016` `TodoItemServiceImpl.java:39`（P1）。其余 B*/M*/I* 均已核销，无命中（详见 checklist §4.1） |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | N/A | U1.1（Controller 入参统一校验注解）✅；U2 业务红线节为空，`N/A(未启用自定义规则)` |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1/P2**：
  1. **P1** `M016`/`G14.3` `TodoItemServiceImpl.java:39` — `LocalDateTime.now()` 依赖系统默认时区，建议显式指定时区或交由 DB `DEFAULT CURRENT_TIMESTAMP` 生成，避免跨环境时区不一致
  2. **P1** `G8.1`/`G10.1`/`G16.2` `GlobalExceptionHandler.java` — 缺少 `@ExceptionHandler(Exception.class)` 兜底，DB `DataAccessException` 等未捕获，破坏统一 ApiResult 契约且无可观测日志
  3. **P1** `G11.3`/`G4.2` `TodoItemCreateRequest.java:15-19` — name/description 未加 `@Size(max=...)`，超长输入触发 `DataIntegrityViolation`（schema: VARCHAR(100)/VARCHAR(500)）
  4. **P1** `S8.1` `TodoItemController.java:34` — 接口未接入鉴权/资源归属校验，内部用户场景建议生产前由网关/鉴权层覆盖
  5. **P2** `A3.4` `GlobalExceptionHandler.java:31` — 改方法引用
  6. **P2** `G9.2` `application.yml` — 显式配置 DB 连接超时/max-lifetime
  7. **P2** `G11.2` `TodoItemServiceImplTest.java` — 补充超长输入边界用例
  8. **P2** `S9.1` `application.yml:7` — 生产强制非空密码
  9. **P2** `G16.1`/`G17.1` — 后续迭代接入监控埋点与功能开关
- **一句话**：最小闭环链路完整、命名与分层规范、单测覆盖合理；主要风险集中在「异常兜底不完整」「时区依赖默认」「入参长度未约束」「接口未鉴权」四项 P1，建议修复后合并。

---

## 7.1 问题片段（必填）

### P1 `M016` / `G14.3` — `src/main/java/com/dtazziboot/todo/service/impl/TodoItemServiceImpl.java:39`

`LocalDateTime.now()` 依赖 JVM 默认时区，DATETIME 列无时区，跨环境（如 JVM UTC 而 DB serverTimezone=Asia/Shanghai）会偏移 8 小时。

片段范围：`src/main/java/com/dtazziboot/todo/service/impl/TodoItemServiceImpl.java:34-45`

```java
L34|    public TodoItemVO createTodoItem(TodoItemCreateRequest request) {
L35|        if (request == null || !StringUtils.hasText(request.getName())) {
L36|            throw new BusinessException(ERR_NAME_REQUIRED);
L37|        }
L38|
L39|        LocalDateTime now = LocalDateTime.now(); // 问题：默认时区；建议显式 ZoneId 或交由 DB 默认值
L40|
L41|        TodoItemDO todoItem = new TodoItemDO();
L42|        todoItem.setName(request.getName().trim());
L43|        todoItem.setDescription(request.getDescription());
L44|        todoItem.setGmtCreate(now); // 同时 schema 已有 DEFAULT CURRENT_TIMESTAMP，存在冗余
L45|        todoItem.setGmtModified(now);
```

### P1 `G8.1` / `G10.1` / `G16.2` — `src/main/java/com/dtazziboot/todo/controller/GlobalExceptionHandler.java:21-35`

仅处理两类异常；DB `DataAccessException`（连接失败、约束违反等）未兜底，返回 Spring 默认 500，非统一 `ApiResult`，且无日志可追溯。

片段范围：`src/main/java/com/dtazziboot/todo/controller/GlobalExceptionHandler.java:21-35`

```java
L21|    @ExceptionHandler(BusinessException.class)
L22|    public ApiResult<Void> handleBusinessException(BusinessException ex) {
L23|        LOGGER.warn("业务异常：{}", ex.getMessage());
L24|        return ApiResult.error(ERR_BUSINESS, ex.getMessage());
L25|    }
L26|
L27|    @ExceptionHandler(MethodArgumentNotValidException.class)
L28|    public ApiResult<Void> handleValidationException(MethodArgumentNotValidException ex) {
L29|        String message = ex.getBindingResult().getFieldErrors().stream()
L30|                .findFirst()
L31|                .map(error -> error.getDefaultMessage()) // 问题：缺 Exception.class 兜底 + 可改方法引用
L32|                .orElse("参数校验失败");
L33|        LOGGER.warn("参数校验失败：{}", message);
L34|        return ApiResult.error("A0001", message);
L35|    }
```

### P1 `G11.3` / `G4.2` — `src/main/java/com/dtazziboot/todo/model/dto/TodoItemCreateRequest.java:14-19`

`name` 仅 `@NotBlank`、`description` 无约束；schema 为 `VARCHAR(100)` / `VARCHAR(500)`，超长输入会触发 `DataIntegrityViolationException`，且因无兜底 handler 直接 500。

片段范围：`src/main/java/com/dtazziboot/todo/model/dto/TodoItemCreateRequest.java:14-19`

```java
L14|    /** 事项名称，必填 */
L15|    @NotBlank(message = "事项名称不能为空")
L16|    private String name; // 问题：缺 @Size(max=100)
L17|
L18|    /** 事项描述 */
L19|    private String description; // 问题：缺 @Size(max=500)
```

### P1 `S8.1` — `src/main/java/com/dtazziboot/todo/controller/TodoItemController.java:34-38`

创建接口无鉴权与资源归属校验；内部用户场景应至少由网关/统一鉴权拦截。

片段范围：`src/main/java/com/dtazziboot/todo/controller/TodoItemController.java:34-38`

```java
L34|    @PostMapping
L35|    public ApiResult<TodoItemVO> create(@Valid @RequestBody TodoItemCreateRequest request) {
L36|        // 问题：无鉴权/调用方身份校验，依赖外部网关
L37|        TodoItemVO vo = todoItemService.createTodoItem(request);
L38|        return ApiResult.success(vo);
```

### P2 `S9.1` — `src/main/resources/application.yml:4-8`

密码默认空字符串（非硬编码字面量，但生产应强制非空）。

片段范围：`src/main/resources/application.yml:4-8`

```yaml
L4|  datasource:
L5|    url: jdbc:mysql://localhost:3306/todo_db?...&serverTimezone=Asia/Shanghai
L6|    username: ${MYSQL_USERNAME:root}
L7|    password: ${MYSQL_PASSWORD:} # 问题：默认空密码
L8|    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/dtazziboot/todo/service/impl/TodoItemServiceImpl.java:39` — 修复 `LocalDateTime.now()` 默认时区：显式指定 `ZoneId`（或改为不在代码中设置 `gmtCreate`/`gmtModified`，交由 DB `DEFAULT CURRENT_TIMESTAMP`/`ON UPDATE CURRENT_TIMESTAMP` 生成）
- [ ] **P1** `src/main/java/com/dtazziboot/todo/controller/GlobalExceptionHandler.java` — 新增 `@ExceptionHandler(Exception.class)` 兜底，记录 ERROR 日志（含 traceId/bizId 上下文）并返回统一 `ApiResult` 错误响应
- [ ] **P1** `src/main/java/com/dtazziboot/todo/model/dto/TodoItemCreateRequest.java:15-19` — 为 `name` 加 `@Size(max=100)`、`description` 加 `@Size(max=500)`，与 schema 列长度一致
- [ ] **P1** `S8.1` — `src/main/java/com/dtazziboot/todo/controller/TodoItemController.java:34` — 生产前接入鉴权/网关校验调用方身份与资源归属

### P2（可选）

- [ ] **P2** `src/main/java/com/dtazziboot/todo/controller/GlobalExceptionHandler.java:31` — `.map(error -> error.getDefaultMessage())` 改为方法引用 `FieldError::getDefaultMessage`
- [ ] **P2** `src/main/resources/application.yml` — 显式配置 Hikari `connection-timeout`/`max-lifetime` 与 DB 读超时
- [ ] **P2** `src/test/java/com/dtazziboot/todo/service/impl/TodoItemServiceImplTest.java` — 补充 name/description 超长输入边界用例
- [ ] **P2** `src/main/resources/application.yml:7` — 生产环境强制 `MYSQL_PASSWORD` 非空
- [ ] **P2** `G16.1`/`G17.1` — 后续迭代接入关键指标埋点（成功率/耗时/调用量）与功能开关
