# Code Review Report — todo 新增待办

> **Change** `待办事项——新增待办(底座变更,最小闭环仅创建)` · **分支/Commit** `task/DEV-f025f710…` / `5423973` · **日期** `2026-11-27` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。清单全文见同目录 `2026-11-27-todo-create-cr-checklist.md`。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `20`（main 18 + test 2） |
| 变更行数 | `+1170 / -0`（纯新增，空底座首个工程） |
| 审查依据 | `design.md`(系分 v1.0) + `impl.md`(编码报告) + 阿里风格/蚂蚁军规/安全清单/阿里缺陷模式 120 条 |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoBaseApplication | src/main/java/com/dtazziboot/base/TodoBaseApplication.java | 启动入口 |
| ApiResult | …/common/api/ApiResult.java | 统一出参 {code,msg,data} |
| LoginUserContext / LoginUserInterceptor | …/common/auth/*.java | 登录态上下文与 /api/** 拦截 |
| GmtFieldsMetaObjectHandler / WebMvcConfig | …/common/config/*.java | gmt 自动填充 / 拦截器注册 |
| CommonErrorCodeConstants / BizException / GlobalExceptionHandler | …/common/… | 错误码与异常处理 |
| TodoProperties / TodoErrorCodeConstants | …/todo/config,constant | 应急开关 / 模块错误码 |
| TodoItemController | …/todo/controller/TodoItemController.java | W01 POST /api/todo/items |
| TodoItemService(Impl) / TodoItemMapper / TodoItemDO | …/todo/service,mapper,model/entity | 业务与持久层 |
| CreateTodoItemRequest / TodoItemResult | …/todo/model/dto | 入出参 DTO |
| TodoItemControllerTest / TodoItemServiceImplTest | src/test/java/… | 切片测试 / 单测 |

非 Java 附件（pom.xml、application.yml、todo_item.sql、.gitignore、SSOT.md、docs/*、impl.md）作为 REQ 证据与发现项证据核对，不列入 Java 队列（详见 checklist Step 1）。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|----|
| **4** | **2** | **7** |

---

## 3. Step 2 — 功能（REQ）

REQ 全部摘自 design.md，逐条 spec 证据 + 代码证据核对于 checklist Step 2。结论：**10/10 REQ 实现层均正确**，其中 4 条（REQ-2/3/4/10）对应的控制器负向测试当前不可通过（受 P0-1 影响，代码正确但证据链断裂）。要点：

| REQ | Scenario | 结果 | Spec证据 | 代码证据 |
|-----|----------|------|----------|----------|
| REQ-1 | POST /api/todo/items 接口形态 | ✅ | design §4.1/§5.1.2 | Controller:20,37-44 |
| REQ-2 | R01 名称必填→TODO_001 | ⚠️ | design §5.1.3 R01 | Controller:50-53 正确;测试受 P0-1 |
| REQ-3 | R02 名称≤64→TODO_002 | ⚠️ | design §5.1.3 R02 | Controller:54-56 正确;测试受 P0-1 |
| REQ-4 | R03 描述≤500→TODO_003 | ⚠️ | design §5.1.3 R03 | Controller:57-60 正确;测试受 P0-1 |
| REQ-5 | R04 创建人登录态注入+AUTH_001 | ✅ | design §5.1.3 R04/§6.4.2.3 | Interceptor:32-42;Context:31-37;ServiceImpl:38;单测 verify never insert |
| REQ-6 | 出参 {code,msg,data} 四字段+时间格式 | ✅ | design §5.1.2 出参表 | ApiResult:29-35;Result:16-25;ServiceImpl:52-54 |
| REQ-7 | 落库组装(tenant/modifier/deleted/缺省描述/trim) | ✅ | design §5.1.1.1/§5.1.3.1 | ServiceImpl:36-47;单测 3 用例 |
| REQ-8 | todo_item 表结构与设计一致 | ✅ | design §5.1.1.1 | todo_item.sql:3-14 逐字段核对一致 |
| REQ-9 | DB 失败→日志+SYS_999 | ✅ | design §5.1.3 异常场景 | Handler:30-34;ServiceImpl:47 上抛 |
| REQ-10 | 开关关闭→功能维护中 | ⚠️ | design §7.3(TODO_004 为已记录扩展码) | Controller:39-41 正确;测试受 P0-1;动态性缺口见 P0-4 |

良性偏差 2 处（不构成不符，见 checklist Step 2 附注）：创建人获取时点 Controller→Service 等价迁移；DB 失败日志 WARN→ERROR（符合军规 G16.3，予以采纳）。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明 |
|------|------|
| ⚠️（整体良好，1 项 P2） | A1/A2/A3/A5/A6/A7 全部通过（import 分组字典序、@Override 全覆盖、Javadoc 齐备、0L 大写）。A4 命中 1 项：**P2-3** `GMT_FORMATTER` 命名误导（`TodoItemServiceImpl.java:24`，实际按 JVM 默认时区格式化而非 GMT），随 P1-1 一并修复。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | G1–G17 | ❌ | **P0×3, P1×1** | 命中：**G9.2**(P0, DB 无超时)、**G16.1**(P0, 无指标埋点)、**G17.1**(P0, 开关需重启不满足秒级止血)、G16.2(P1, 异常日志无 traceId)。G2.1 幂等键为设计 A05 明确豁免（✅ spec 决策）。其余 ✅/N/A 逐条核销见 checklist §4.2 |
| 安全 | S1–S10 | ✅ | P2 注记×3 | SQL 参数化、JSON 输出、反序列化绑定 DTO、口令环境变量化、日志脱敏、POST 写操作均通过。注记：S8.1 网关头信任(P2-1)、S9.1 默认空口令(P2-6) |
| Bug 模式 | B/M/I（120） | ❌ | **P1×1(3 处)** | 预扫 `scan-all-rules.sh` 命中 **M016×3**（真阳性，复核确认，关联 G14.3/G14.4）；其余 119 条 LLM 逐条补扫无命中（checklist §4.1） |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | - | N/A(未启用自定义规则，清单仅示例项)。备注：若未来启用「入参必须 @Valid」，需将手动校验改造为注解校验 |

---

## 7. 结论

- **合并建议**：**修复后合并**（P0×4 阻塞）
- **P0**：
  1. **P0-1** `TodoItemControllerTest.java:40-41` — standalone MockMvc 未注册 `setControllerAdvice(new GlobalExceptionHandler())`，TODO_001/002/003/004 四个负向用例在断言 `{code:...}` 前即抛 `NestedServletException`，**必然失败**；impl.md 声称的「10 用例通过」实际不成立（L2 动态验证因无 mvn 被跳过，此缺陷未被暴露）。
  2. **P0-2** `G9.2` `application.yml:8` / `TodoItemServiceImpl.java:47` — DB 调用无任何超时（connectTimeout/socketTimeout 缺省=无限等待），网络分区可致线程永久挂起、连接池耗尽扩散全应用。
  3. **P0-3** `G16.1` `TodoItemController.java:37-44` — 设计 §7.1 承诺的接口埋点（QPS/RT/成功率/错误码计数、DB 失败单独计数）完全未落地，pom 无 micrometer/actuator。
  4. **P0-4** `G17.1` `TodoProperties.java` / `application.yml:19-20` — 开关为静态配置，变更需重启，不满足设计 §7.3「动态配置/秒级止血」；yml 注释「可应急秒级止血」与实际能力不符。
- **P1/P2**：
  - **P1-1** `M016/G14.3/G14.4` ×3 处 — `LocalDateTime.now()` 依赖 JVM 默认时区（宿主为 UTC+00:00），与 JDBC `serverTimezone=Asia/Shanghai` 不一致，跨机部署存在 8 小时偏差风险；`GMT_FORMATTER` 命名同源误导。
  - **P1-2** `G16.2` — 异常日志无 traceId/链路上下文，设计 §7.1「DB 失败携带 traceId」未落地。
  - **P2×7**：网关头信任部署约束(P2-1)、Service 层 name 无 null 防御(P2-2)、formatter 命名(P2-3，随 P1-1)、测试全限定类名(P2-4)、starter-validation 未使用(P2-5)、默认空口令需生产强制注入(P2-6)、登录拦截器无自动化测试(P2-7)。
- **一句话**：功能实现与系分高度一致、代码分层清晰可读，单测断言质量高；但控制器测试装配缺陷使质量门禁失效，加上 DB 超时缺失、埋点与动态开关两项非功能承诺未兑现，须修复 P0 后方可合并。

---

## 7.1 问题片段（必填）

### P0-1 测试装配缺陷（4 个负向用例必然失败）

`src/test/java/com/dtazziboot/base/todo/controller/TodoItemControllerTest.java:36-42`

```java
L36|    @BeforeEach
L37|    void setUp() {
L38|        todoItemService = mock(TodoItemService.class);
L39|        todoProperties = new TodoProperties();
L40|        mockMvc = MockMvcBuilders.standaloneSetup(new TodoItemController(todoItemService, todoProperties))
L41|                .build();
L42|    }
```

问题：standalone 模式不会自动装配 `@RestControllerAdvice`。L70-72、L87-88、L103-104、L119-120 处 `jsonPath("$.code").value("TODO_001"…)` 期望的 JSON 错误体永远不会出现——控制器抛出的 `BizException` 未经 `GlobalExceptionHandler` 转换，`perform()` 直接抛 `NestedServletException`，4 个用例失败。修复：`.setControllerAdvice(new GlobalExceptionHandler())`。

### P0-2 DB 调用无超时（G9.2）

`src/main/resources/application.yml:7-11`（N/A(非 Java) 配置证据 + Java 调用点）

```yaml
L 7|  datasource:
L 8|    url: jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3306}/${DB_NAME:todo_base}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
L 9|    username: ${DB_USERNAME:root}
L10|    password: ${DB_PASSWORD:}
L11|    driver-class-name: com.mysql.cj.jdbc.Driver
```

```java
L45|        todoItem.setModifier(creator);
L46|        todoItem.setDeleted(NOT_DELETED);
L47|        todoItemMapper.insert(todoItem);   // TodoItemServiceImpl.java:47 — 全链路唯一 DB 调用,无超时保障
```

问题：URL 无 `connectTimeout`/`socketTimeout`，未显式配置 Hikari 超时参数；MySQL 驱动默认均为 0（无限），DB 网络分区时业务线程永久挂起。

### P0-3 核心链路无指标埋点（G16.1）

`src/main/java/com/dtazziboot/base/todo/controller/TodoItemController.java:37-44`

```java
L37|    @PostMapping("/items")
L38|    public ApiResult<TodoItemResult> createTodoItem(@RequestBody CreateTodoItemRequest request) {
L39|        if (!todoProperties.getCreate().isEnabled()) {
L40|            throw new BizException(TodoErrorCodeConstants.TODO_MAINTENANCE, "功能维护中,请稍后重试");
L41|        }
L42|        validateCreateRequest(request);
L43|        return ApiResult.ok(todoItemService.createTodoItem(request));
L44|    }
```

问题：无任何 QPS/RT/成功率/错误码计数埋点，违反 design §7.1「服务埋点：todo_item_create 接口埋点，记录调用来源、处理结果（成功/各错误码）、处理耗时」。

### P0-4 功能开关非动态（G17.1）

`src/main/java/com/dtazziboot/base/todo/config/TodoProperties.java:10-22`

```java
L10| @Component
L11| @ConfigurationProperties(prefix = "todo")
L12| public class TodoProperties {
L13|
L14|     private final Create create = new Create();
L15|
L16|     @Data
L17|     public static class Create {
L18|
L19|         /** 新增待办功能开关,默认开启 */
L20|         private boolean enabled = true;
L21|     }
L22| }
```

`src/main/resources/application.yml:17-20`（N/A(非 Java) 配置证据）

```yaml
L17| todo:
L18|   create:
L19|     # 功能开关: false 时 /api/todo/items 返回"功能维护中"(可应急秒级止血)
L20|     enabled: true
```

问题：纯静态 yml 配置，改值需重启发布，与 design §7.3「动态配置开关…秒级止血」不符；L19 注释夸大实际能力。

### P1-1 时区依赖 JVM 默认值（M016 / G14.3 / G14.4，3 处）

`src/main/java/com/dtazziboot/base/common/config/GmtFieldsMetaObjectHandler.java:14-19`

```java
L14|    @Override
L15|    public void insertFill(MetaObject metaObject) {
L16|        LocalDateTime now = LocalDateTime.now();          // 问题:JVM 默认时区(宿主 UTC+0)
L17|        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, now);
L18|        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, now);
L19|    }
```

`src/main/java/com/dtazziboot/base/todo/service/impl/TodoItemServiceImpl.java:23-24,49-51`

```java
L23|    /** 出参创建时间格式 */
L24|    private static final DateTimeFormatter GMT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  // 问题:命名暗示 GMT,实际按 JVM 默认时区
L49|        LocalDateTime gmtCreate = todoItem.getGmtCreate() == null
L50|                ? LocalDateTime.now()                     // 问题:同上(预扫 M016 第三处)
L51|                : todoItem.getGmtCreate();
```

问题：宿主时区为 UTC+00:00 而 JDBC 会话声明 Asia/Shanghai，`gmt_create` 落库墙钟与运维预期存在 8 小时偏差风险。修复：统一显式 `ZoneId`（与 `serverTimezone` 对齐）并同步修正 formatter 命名。

### P1-2 异常日志无 traceId（G16.2）

`src/main/java/com/dtazziboot/base/common/exception/GlobalExceptionHandler.java:18-34`

```java
L18|    @ExceptionHandler(BizException.class)
L19|    public ApiResult<Void> handleBizException(BizException e) {
L20|        log.warn("biz exception, code: {}, msg: {}", e.getCode(), e.getMessage());   // 问题:无 traceId/上下文
L21|        return ApiResult.fail(e.getCode(), e.getMessage());
L22|    }
...
L30|    @ExceptionHandler(Exception.class)
L31|    public ApiResult<Void> handleException(Exception e) {
L32|        log.error("unexpected exception, msg: {}", e.getMessage(), e);               // 问题:同上
L33|        return ApiResult.fail(CommonErrorCodeConstants.SYS_ERROR, "系统繁忙,请稍后重试");
L34|    }
```

### P2 问题片段（摘要）

- **P2-1** `S8.1` `LoginUserInterceptor.java:32-34` — `String user = request.getHeader(USER_HEADER);` 身份完全信任网关头，需部署层保障应用端口仅网关可达（Javadoc 已声明假设，属部署约束注记）。
- **P2-2** `G11.3` `TodoItemServiceImpl.java:36` — `String name = request.getName().trim();` 无 null 防御；当前唯一入口 Controller:50 已前置校验，属内部契约依赖。
- **P2-3** `A4` `TodoItemServiceImpl.java:24` — `GMT_FORMATTER` 命名误导（见 P1-1 片段），随 P1-1 一并改名（如 `CREATED_AT_FORMATTER`）。
- **P2-4** `TodoItemServiceImplTest.java:62` — `entity.setGmtCreate(java.time.LocalDateTime.of(…))` 应改 import 引入。
- **P2-5** `pom.xml:33-37` — `spring-boot-starter-validation` 引入但未使用（手动校验方案）；保留或移除需明确。
- **P2-6** `S9.1` `application.yml:9-10` — `username: ${DB_USERNAME:root}` / `password: ${DB_PASSWORD:}` 本地默认值合理，生产环境需强制注入（建议启动时非空校验）。
- **P2-7** `G11.2` — 登录拦截器 `/api/**` AUTH_001 出口无自动化测试（Service 层已覆盖 AUTH_001 业务兜底）。

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/test/java/com/dtazziboot/base/todo/controller/TodoItemControllerTest.java:40` — 在 standaloneSetup 后链式调用 `.setControllerAdvice(new GlobalExceptionHandler())`，使 4 个负向用例能获得统一错误体并可通过；修复后确认 10/10 用例断言成立。
- [ ] **P0** `G9.2` `src/main/resources/application.yml:8` — 为 JDBC URL 追加 `connectTimeout=1000&socketTimeout=3000`（按团队规范取值），并显式配置 Hikari `connection-timeout`/`validation-timeout`，消除 DB 无限等待。
- [ ] **P0** `G16.1` `src/main/java/com/dtazziboot/base/todo/controller/TodoItemController.java:37` — 落地设计 §7.1 接口埋点：引入 spring-boot-starter-actuator(Micrometer)，对 create 接口补充调用计数、耗时 Timer 与按错误码分流的失败计数（含 DB 写失败计数），或与平台确认改由网关统计并修订 design §7.1。
- [ ] **P0** `G17.1` `src/main/java/com/dtazziboot/base/todo/config/TodoProperties.java` — 使 `todo.create.enabled` 支持不停机动/reset 时生效（接入配置中心或 actuator/refresh 类机制）；若最小闭环暂不引入配置中心，则修订 design §7.3 为「重启级止血（多副本滚动）」并同步修正 application.yml:19 注释，消除能力承诺与实现的偏差。

### P1

- [ ] **P1** `M016/G14.3` `GmtFieldsMetaObjectHandler.java:16,23`、`TodoItemServiceImpl.java:50` — 将三处 `LocalDateTime.now()` 改为显式时区（`LocalDateTime.now(ZONE)`，ZONE 与 JDBC `serverTimezone=Asia/Shanghai` 统一为常量），消除宿主 UTC+0 下的 8 小时偏差风险。
- [ ] **P1** `G16.2/A4` `GlobalExceptionHandler.java:20,26,32`、`TodoItemServiceImpl.java:24` — 为异常/关键日志补充 traceId（MDC 或日志组件透传），并将 `GMT_FORMATTER` 改名为语义准确名称、格式化使用显式时区。

### P2（可选）

- [ ] **P2** `S8.1` `LoginUserInterceptor.java` — 在 README/部署文档中固化「应用端口仅网关可达」约束，或为网关→应用透传增加共享密钥校验。
- [ ] **P2** `G11.3` `TodoItemServiceImpl.java:36` — 对 `request`/`request.getName()` 增加 null 防御或断言，避免内部调用方绕过 Controller 时 NPE。
- [ ] **P2** `TodoItemServiceImplTest.java:62` — 将 `java.time.LocalDateTime` 改为 import 引入。
- [ ] **P2** `pom.xml:33-37` — 明确 `spring-boot-starter-validation` 去留（当前手动校验未使用它）。
- [ ] **P2** `S9.1` `application.yml` — 增加生产环境 DB 口令非空启动校验或部署检查项。
- [ ] **P2** `G11.2` — 补充 LoginUserInterceptor 的 MockMvc 拦截测试（带/不带 X-User-Id 两分支）。
- [ ] **P2** `GlobalExceptionHandler.java` — 为 REQ-9 的「DB 异常→SYS_999 响应」补一条 Controller 层断言用例。
