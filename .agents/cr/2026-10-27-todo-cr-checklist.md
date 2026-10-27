# Code Review Checklist

> **Change** `todo 新增待办事项（最小闭环）` · **分支/Commit** `AI/task-DEV-c1f01881-…` / `9128e79` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **预扫结果**：`scan-all-rules.sh`（Src 目录）共 6 条命中，均为 `[P1] M016`（`LocalDateTime.now()` 未显式时区），详见 Step 4 备注。

---

## Step 1 — 执行队列（产物 A）

> 变更范围来自 `git diff --name-only 440cdae 9128e79`（21 个文件，14 个 Java + 7 个非 Java）。非 Java 文件标 `跳过`，但资源类（.xml/.sql/.yml）仍纳入 Step 4 相关安全/可靠性核销。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | 总状态 |
|---|----------------------|----------|:----:|:----:|:------:|
| 1 | src/main/java/com/example/todo/TodoApplication.java | 启动类 | ✅ | ✅ | ✅ 已审 |
| 2 | src/main/java/com/example/todo/common/constant/ErrorCodeEnum.java | 错误码 | ✅ | ✅ | ✅ 已审 |
| 3 | src/main/java/com/example/todo/common/constant/TodoConstants.java | 常量 | ✅ | ✅ | ✅ 已审 |
| 4 | src/main/java/com/example/todo/common/exception/BusinessException.java | 业务异常 | ✅ | ✅ | ✅ 已审 |
| 5 | src/main/java/com/example/todo/common/exception/GlobalExceptionHandler.java | 全局异常 | ✅ | ✅ | ⚠️ 已审有问题 |
| 6 | src/main/java/com/example/todo/common/result/ApiResponse.java | 统一响应 | ✅ | ✅ | ✅ 已审 |
| 7 | src/main/java/com/example/todo/controller/TodoItemController.java | 控制器 | ✅ | ✅ | ⚠️ 已审有问题 |
| 8 | src/main/java/com/example/todo/dao/mapper/TodoItemMapper.java | Mapper 接口 | ✅ | ✅ | ✅ 已审 |
| 9 | src/main/java/com/example/todo/model/do/TodoItemDO.java | 数据对象 | ✅ | ✅ | ✅ 已审 |
| 10 | src/main/java/com/example/todo/service/TodoItemService.java | 服务接口 | ✅ | ✅ | ✅ 已审 |
| 11 | src/main/java/com/example/todo/service/dto/TodoItemRequest.java | 请求 DTO | ✅ | ✅ | ✅ 已审 |
| 12 | src/main/java/com/example/todo/service/dto/TodoItemVO.java | 视图对象 | ✅ | ✅ | ✅ 已审 |
| 13 | src/main/java/com/example/todo/service/impl/TodoItemServiceImpl.java | 服务实现 | ✅ | ✅ | ⚠️ 已审有问题 |
| 14 | src/test/java/com/example/todo/service/impl/TodoItemServiceImplTest.java | 单元测试 | ✅ | ⚠️ | ⚠️ 已审有问题 |
| — | pom.xml / application.yml / schema.sql / TodoItemMapper.xml / impl.md / 两份 docs | 非 Java | 跳过 | 跳过 | 跳过（资源文件已并入 S/G 核查） |

- **守卫**：本次变更包含 14 个 `.java` 文件，通过 Java 守卫。
- **收口**：14 个 Java 文件全部审完，无 `⬜ 待审`。

---

## Step 2 — 功能（产物 B）

> 需求原文：目标「帮助用户记录日常待办事项」；核心功能「新增待办事项」；任务信息「事项名称和描述」；目标用户「内部用户」；最小闭环「仅创建」。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|:----:|----------------------------|
| REQ-1 新增待办事项 | Given name+description When POST /todos Then 持久化并返回创建结果 | 需求「核心功能：新增待办事项」；模块 README「API POST /todos」 | TodoItemController.java / TodoItemServiceImpl.java / TodoItemMapper.java | ✅ | Controller:43 → ServiceImpl:53→59 → Mapper.xml:14-18（insert + useGeneratedKeys）；测试 `should_returnTodoItemVO_when_createSuccess` ✅ |
| REQ-2 事项名称与描述 | Given name 必填、description 可选 Then 长度受限 | 需求「任务信息：事项名称和描述」 | TodoItemRequest.java | ✅ | `@NotBlank`(name) + `@Size(100)`(name) + `@Size(500)`(description) Request.java:19-27 |
| REQ-3 最小闭环「仅创建」 | Then 仅提供创建接口，无更新/删除/查询列表 | 需求「最小闭环：仅创建」 | TodoItemController.java | ✅ | 仅 `@PostMapping` 一个端点 Controller.java:42-46 |
| REQ-4 统一响应契约 | Then 返回 ApiResponse，id 以 String 返回 | docs/ARCHITECTURE.md「API 响应统一……」 | ApiResponse.java / TodoItemVO.java | ✅ | ApiResponse.success(vo)；VO.id 为 String；ServiceImpl:74 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|:----:|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名、UTF-8、无 Tab（脚本 A1.3 无命中） |
| A2 | 源文件结构/import 顺序 | ⚠️ | **A2.4** `TodoItemServiceImplTest.java:19` `org.junit.jupiter…ExtendWith` 位于 `org.mockito…MockitoExtension` 之后，非 ASCII 字典序；无 import *（脚本 A2.2 无命中） |
| A3 | 代码样式 | ✅ | 缩进 4 空格、K&R、无超 120 行（脚本 A3.4/A3.7 无命中） |
| A4 | 命名规范 | ✅ | 包全小写、类大驼峰、常量 UPPER_SNAKE、`Long` 大写 `L`（脚本 A4.1/A6.5 无命中） |
| A5 | 编码实践 | ✅ | `@Override` 齐全（Service impl:45）；无空 catch、无 finalize（脚本 A5.4 无命中） |
| A6 | 特定元素样式 | ✅ | 无 switch、修饰符顺序正确（脚本 A6.3 无命中） |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc；getter/setter 属自解释可省略（A7.3） |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫（`scan-all-rules.sh src`）命中：`[P1] M016` ×6。其余 52 条脚本规则无命中；下表中脚本未覆盖规则由 LLM 逐条核对。

| ID | 状态 | 备注（命中写 `path:line`） |
|----|:----:|--------------------------|
| B001–B028 | N/A | 无对应用法（无字面量 parse、数组、Calendar、SimpleDateFormat、Executors、BigDecimal、Jedis、死异常等） |
| B029-B065 中 B029 | ✅ | setter 均赋值正确字段（TodoItemDO/VO/Request） |
| B053 | ✅ | 测试用 `assertThatThrownBy`，无 try+漏 fail 问题 |
| B055 | ✅ | `when(...)->thenAnswer/thenReturn`、`verify(...)` 用法正确（Test:58-63、80-81） |
| B065 | ✅ | 无 Pojo 自赋值 |
| B068 | ✅ | 构造器 `this.x = x` 赋值正确 |
| B075 | ✅ | 无 for 循环（仅 stream） |
| B076 | ✅ | 无 `@Transactional`（单表操作，未引入事务注解） |
| B079 | ✅ | `@Mock` 未显式赋值 |
| B080 | ✅ | 每个 `@Test` 均含断言 |
| B081 | N/A | 无新建集合 sort/shuffle |
| 其余 B 项 | N/A | 无对应用法 |
| M004 | ✅ | 无 `printStackTrace`（脚本无命中） |
| M007 | ✅ | 无 catch 块（脚本无命中） |
| M016 | ⚠️ | **P1** `LocalDateTime.now()` 未显式时区：`TodoItemServiceImpl.java:50,51`、`TodoItemServiceImplTest.java:68,69,99,100` |
| M020 | ✅ | `createTodoItem` 已 `@Override`（ServiceImpl:45） |
| M026 | ✅ | `@Mock` 为实例字段而非 static |
| 其余 M 项 | N/A | 无对应用法 |
| I001 | ✅ | 异常断言含 `hasMessageContaining(错误码)`，优于仅判类型（Test:120-122） |
| I004 | ✅ | 使用 `LocalDateTime`，无 `java.util.Date` |
| I010 | ✅ | 用 JUnit5 + MockitoExtension，未启动容器 |
| 其余 I 项 | N/A | 无对应用法 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|:----:|------|
| G1.1–G1.4 | N/A | 仅新增，无事务内先读后写/乐观锁/多锁顺序 |
| G2.1–G2.3 | N/A | 最小闭环仅创建，无重试/消息/幂等去重需求 |
| G3.1 | N/A | 单库单表操作，无跨库读后写 |
| G3.2 | N/A | 无 `@Transactional` 包裹外部 I/O |
| G4.1 | ✅ | SQL 简单，业务逻辑在代码 |
| G4.2 | ✅ | `WHERE id=#{id}`，无函数/隐式类型转换 |
| G4.3 | N/A | 无列表查询 |
| G5.1 | N/A | 无 MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 已处理插入失败/回查为空的异常分支，非仅 happy path |
| G8.2 | N/A | 无强依赖非核心 |
| G8.3 | ✅ | 无 I/O 流/连接/锁资源 |
| G8.4–G8.6 | N/A | 无线程池/ThreadLocal |
| G9.1 | N/A | 无显式外部 RPC/HTTP |
| G9.2 | ✅ | 依赖 HikariCP 默认连接超时（Spring Boot 默认），无自定义外部调用 |
| G9.3 | N/A | 无重试逻辑 |
| G10.1 | ✅ | `description` 允许 null 语义清晰（可空描述） |
| G10.2 | N/A | 新增接口，无契约变更 |
| G11.1 | ✅ | 4 个单测均有断言 |
| G11.2 | ⚠️ | **P2** 边界覆盖不全：未覆盖 name 为空/超长、description 超长（Test 仅覆盖 null） |
| G11.3 | ✅ | 入参由 Controller `@Valid` + `@NotBlank/@Size` 防御（Request:19-27） |
| G11.4 | N/A | 无数值/金额运算 |
| G12.1–G12.2 | N/A | 非资金场景 |
| G13.1 | ✅ | 成功 INFO、系统 ERROR，级别正确 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️ | **P1** 并入 M016：`LocalDateTime.now()` 未显式时区；DB `DATETIME` + `serverTimezone=UTC` |
| G14.4 | N/A | 无显式格式化 |
| G15.1 | ✅ | schema.sql 为 `CREATE TABLE`（新增表），无 ALTER/DROP |
| G15.2–G15.3 | N/A | 无新旧接口共存 |
| G16.1 | N/A | 最小闭环内部工具，未设埋点要求 |
| G16.2 | ⚠️ | **P1** 系统侧业务异常（如插入失败/回查为空）抛出后未记录日志：`GlobalExceptionHandler.java:48-52`、`TodoItemServiceImpl.java:53-62` |
| G16.3 | ✅ | 正常/异常日志级别基本正确 |
| G16.4 | ✅ | 无空 catch / 无 printStackTrace |
| G17.1–G17.3 | N/A | 最小闭环，无开关/降级/数据迁移 |
| G18.1–G18.3 | N/A | 安全补强项已并入下方 S 系列核销 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|:----:|------|
| S1.1 | ✅ | SQL 全用 `#{}` 预编译（TodoItemMapper.xml:17,23），脚本无命中 |
| S1.2 | N/A | 无 order by/group by/动态表名字段名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1–S2.3 | N/A | 纯 JSON API，无 HTML/JS 渲染、无富文本 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1 | ✅ | 无命令执行（脚本无命中） |
| S4.2 | N/A | 无文件操作 |
| S5.1–S5.2 | N/A | 无 XML 解析/XPath |
| S6.1–S6.3 | N/A | 无自定义反序列化；Jackson 未启用多态 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1 | ⚠️ | **P2** `POST /todos` 未接入鉴权（TodoItemController.java:42-46）；需求「内部用户」+「仅创建」可接受，但需确认省去鉴权是否合规 |
| S8.2 | ✅ | 创建使用 POST，无 GET 增删改 |
| S8.3 | ⚠️ | **P2** 主键自增 id 可被顺序猜测（schema.sql:3）；VO 已以 String 返回 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ⚠️ | **P1** `application.yml:7-8` 明文数据库凭据 `root/root` |
| S9.2 | ✅ | 日志仅记录 id，不记录敏感信息 |
| S9.3 | N/A | 本地开发 HTTP，范围内无传输加密配置 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无状态 API + 内部用户，未启用 CSRF |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|:----:|------|
| U1.1 | ✅ | 示例项：Controller 入参已使用 `@Valid` 统一校验（TodoItemController.java:43） |
| U1.2–U1.3 | N/A | 未启用 |
| U2.1–U2.3 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列 14 个 Java 文件 Step2/Step3 均非 `⬜`（非 Java 文件标跳过）
- [x] Step 2 每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 A1–A7 均非 `⬜`
- [x] Step 4 G/S 全部 ID 与 B/M/I 全部 ID 均非 `⬜`（允许 N/A 且有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，并含 ID + path:line