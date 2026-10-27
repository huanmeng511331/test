# Code Review Checklist

> **Change** 编码实现 (stage: coding) · **分支/Commit** `AI/task-DEV-…` / `aa1fc34` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已先对变更路径运行 `scan-all-rules.sh`（Engine: ripgrep，52/222 条），输出并入 Step 3 / Step 4 备注。

---

## Step 1 — 执行队列（产物 A）

> 变更范围 = commit `aa1fc34^..aa1fc34`（21 文件，+947/-0）。非 Java 文件标 `跳过`。

### 跳过清单（非 Java，7 个）

- `.agents/todo/impl.md` — 跳过（Markdown 报告）
- `docs/ARCHITECTURE.md` — 跳过（文档）
- `docs/modules/todo/README.md` — 跳过（文档）
- `pom.xml` — 跳过（Maven 配置）
- `src/main/resources/application.yml` — 跳过（配置）
- `src/main/resources/db/schema.sql` — 跳过（DDL，S1/G15 在 §4 核销）
- `src/main/resources/mapper/TodoMapper.xml` — 跳过（MyBatis XML，S1 在 §4 核销）

### Java 执行队列（14 个）

> G/S 逐文件结论为概要，最终以 §4.2 / §4.3 逐条 ID 为准。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|-----|--------|
| 1 | `src/main/java/com/dtazziboot/todo/TodoApplication.java` | 启动类 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `src/main/java/com/dtazziboot/todo/common/constant/TodoConstants.java` | 常量 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | `src/main/java/com/dtazziboot/todo/common/enums/TodoErrorCodeEnum.java` | 错误码 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `src/main/java/com/dtazziboot/todo/common/exception/BusinessException.java` | 业务异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | `src/main/java/com/dtazziboot/todo/common/exception/GlobalExceptionHandler.java` | 全局异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ⚠️ 已审有问题 |
| 6 | `src/main/java/com/dtazziboot/todo/common/model/ApiResponse.java` | 统一响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ⚠️ 已审有问题 |
| 7 | `src/main/java/com/dtazziboot/todo/controller/TodoController.java` | 控制器 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ 已审有问题 |
| 8 | `src/main/java/com/dtazziboot/todo/dao/mapper/TodoMapper.java` | DAO 接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 9 | `src/main/java/com/dtazziboot/todo/model/dto/TodoCreateRequest.java` | 请求 DTO | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 10 | `src/main/java/com/dtazziboot/todo/model/dto/TodoVO.java` | 视图对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 11 | `src/main/java/com/dtazziboot/todo/model/entity/TodoDO.java` | 数据对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 12 | `src/main/java/com/dtazziboot/todo/service/TodoService.java` | 服务接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 13 | `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java` | 服务实现 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ⚠️ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ⚠️ 已审有问题 |
| 14 | `src/test/java/com/dtazziboot/todo/service/impl/TodoServiceImplTest.java` | 单元测试 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |

---

## Step 2 — 功能（产物 B）

> spec/design 来源：`docs/modules/todo/README.md`、`docs/ARCHITECTURE.md`、任务需求描述（需求侧）、`.agents/todo/impl.md`。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 新增待办事项 | Given 合法 name+description，When POST /todos，Then 落库并返回新记录 | 需求「核心功能：新增待办事项」；README「POST /todos 新增待办事项」 | TodoController.java / TodoServiceImpl.java / TodoMapper.xml | ✅ | `TodoController.java:26-30`；`TodoServiceImpl.java:48` insert；`TodoMapper.xml:14-18` |
| REQ-2 任务信息=名称+描述，名称必填 | Given name 空/空白，When 校验，Then 拒绝(PARAM_INVALID)；description 可空 | 需求「任务信息：事项名称和描述」；README 请求示例含 name/description | TodoCreateRequest.java / TodoServiceImpl.java | ✅ | `TodoCreateRequest.java:12-16` `@NotBlank name`；`TodoServiceImpl.java:37-39` 二次校验；测试 87-108 覆盖 null/blank |
| REQ-3 返回统一响应含待办视图 | Then code=200/message=success/data{id,name,description,createTime} | README「响应示例」；impl.md「统一 ApiResponse」 | ApiResponse.java / TodoVO.java / TodoServiceImpl.java | ✅ | `ApiResponse.java:22-28` success；`TodoServiceImpl.java:58-66` buildTodoVO；`TodoServiceImplTest.java:60-63` 断言 |
| REQ-4 表结构含必备三字段 id/gmt_create/gmt_modified | 落库表 todo_item 满足架构约束 | ARCHITECTURE「数据库表必备字段：id、gmt_create、gmt_modified」 | schema.sql / TodoDO.java | ✅ | `schema.sql:1-10`；`TodoDO.java:13-21` |

---

## Step 3 — 可读性检查（产物 C）

> 脚本预扫命中（A1.3 Tab / A2.2 通配 / A3.4 行宽 / A3.7 关键字 / A4.1 包名 / A5.4 finalize / A6.3 修饰符 / A6.5 long 后缀）：**均无命中**。以下为 LLM 全文核对。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名、UTF-8、无 Tab（脚本 A1.3 已扫无命中） |
| A2 | 源文件结构/import 顺序 | ⚠️ | **A2.4** import 未按 ASCII 字典序：`TodoCreateRequest.java:3-5`（jakarta 在 com 前）、`TodoServiceImpl.java:3-16`（`exception`/`enums` 逆序；`java.*` 置于 `org.*` 后）、`TodoServiceImplTest.java:3-4`（`exception`/`enums` 逆序）。P2 |
| A3 | 代码样式 | ✅ | K&R 大括号、4 空格缩进、行宽≤120（脚本 A3.4 无命中）、关键字空格（脚本 A3.7 无命中） |
| A4 | 命名规范 | ✅ | 包全小写（脚本 A4.1 无命中）、类大驼峰、常量 UPPER_SNAKE_CASE、字段 lowerCamelCase；测试类 `TodoServiceImplTest`（A4.7）符合 |
| A5 | 编码实践 | ✅ | `@Override`（`TodoServiceImpl.java:35`）规范；无空 catch；无 `finalize`（脚本 A5.4 无命中） |
| A6 | 特定元素样式 | ✅ | 无裸 long 字面量（脚本 A6.5 无命中）；修饰符顺序正确（脚本 A6.3 无命中）；无 switch |
| A7 | Javadoc 规范 | ✅ | public 类/接口均有类级 Javadoc；getter/setter 依 A7.3 可省略 |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销**。Bug 模式 Blocker→P0、Major→P1、Info→P2。

### 4.1 Bug 模式（`bug-pattern-checklist.md`，120 条）

> 脚本已扫 52 条可程序化规则，唯一命中：M016（JavaTimeDefaultTimeZone，`TodoServiceImpl.java:44`）。LLM 补齐未覆盖项。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | ✅ | 无字面量 parse/of |
| B002 | ✅ | 无数组 equals |
| B003 | ✅ | 无 Arrays.fill |
| B004 | ✅ | 无数组 toString |
| B005 | ✅ | 无 Arrays.asList 基本类型 |
| B006 | ✅ | 无 assertEquals 参数（用 AssertJ） |
| B007 | ✅ | 无 catch Throwable |
| B008 | ✅ | 无 Executors |
| B009 | ✅ | 无移位 |
| B010 | ✅ | 无 BigDecimal(double) |
| B011 | ✅ | 无包装 `==` |
| B012 | ✅ | 无 Calendar.add |
| B013 | ✅ | 无 Calendar.HOUR |
| B014 | ✅ | 无集合泛型不匹配 |
| B015 | ✅ | 无 toArray 错误参数 |
| B016 | ✅ | 无自定义 Comparable |
| B017 | ✅ | 无 this==null |
| B018 | ✅ | 无三目数值提升 |
| B019 | ✅ | 无 Money API |
| B020 | ✅ | 无常量溢出 |
| B021 | ✅ | 无 Jedis |
| B022 | ✅ | 无 SimpleDateFormat（用 DateTimeFormatter） |
| B023 | ✅ | 无未抛出异常实例 |
| B024 | ✅ | 无裸 Thread |
| B025 | ✅ | 无双括号初始化 |
| B026 | ✅ | 无 equals(null) |
| B027 | ✅ | 无 equals 比错属性 |
| B028 | ✅ | 无 DateUtil.formatDate |
| B029 | ✅ | setter 赋值字段正确 |
| B030 | ✅ | 无浮点 `==` |
| B031 | ✅ | 无 String.format |
| B032 | ✅ | 无注解 getClass |
| B033 | ✅ | 无 Unsafe |
| B034 | ✅ | 无 Hashtable |
| B035 | ✅ | 无恒等二元表达式 |
| B036 | ✅ | 无 IdentityHashMap |
| B037 | ✅ | 无可变参数条件表达式 |
| B038 | ✅ | 无递归 |
| B039 | ✅ | 无 indexOf 参数颠倒 |
| B040 | ✅ | 无 isInstance |
| B041 | ✅ | 无 JDBC 连接 |
| B042 | ✅ | JUnit5 |
| B043 | ✅ | 无内部类 @Test |
| B044 | ✅ | JUnit5 |
| B045 | ✅ | 无 synchronized |
| B046 | ✅ | 无循环 |
| B047 | ✅ | 无数值 compare |
| B048 | ✅ | 无 Math.round(int) |
| B049 | ✅ | 格式 yyyy-MM-dd（`TodoServiceImpl.java:27` 含 `dd` 小写） |
| B050 | ✅ | 格式含 HH（24 小时） |
| B051 | ✅ | 无 Boolean.getBoolean |
| B052 | ✅ | 无 YYYY |
| B053 | ✅ | 用 assertThatThrownBy，非 try-catch |
| B054 | ✅ | 无 EqualsTester |
| B055 | ✅ | when/verify 用法正确 |
| B056 | ✅ | 无 Arrays.asList 修改 |
| B057 | ✅ | 无增强 for 中改集合 |
| B058 | ✅ | 无集合传自身 |
| B059 | ✅ | 无 nCopies |
| B060 | ✅ | 无 null 三目拆箱 |
| B061 | ✅ | 无 sun.misc |
| B062 | ✅ | 无 ClassLoader 强转 |
| B063 | ✅ | 无 javax.xml.bind |
| B064 | ✅ | 无 Optional `==` |
| B065 | ✅ | 无自赋值 |
| B066 | ✅ | 无 Math.random 强转 |
| B067 | ✅ | 无 nextInt% |
| B068 | ✅ | 无自赋值 |
| B069 | ✅ | 无 compareTo |
| B070 | ✅ | 无 equals 自身 |
| B071 | ✅ | 无 size()>=0 |
| B072 | ✅ | 无 Stream.toString |
| B073 | ✅ | 无 new StringBuilder(char) |
| B074 | ✅ | 无 substring(0) |
| B075 | ✅ | 无 for 循环（影响范围外） |
| B076 | ✅ | 无 @Transactional |
| B077 | ✅ | 无 catch Throwable |
| B078 | ✅ | 断言非自身 |
| B079 | ✅ | @Mock 未显式赋值 |
| B080 | ✅ | 5 个测试均含断言 |
| B081 | ✅ | 无集合 sort 丢弃 |
| M001 | ✅ | 无重复条件 |
| M002 | ✅ | 无 instanceof |
| M003 | ✅ | 无包装构造器 |
| M004 | ✅ | 无 printStackTrace（脚本已扫） |
| M005 | ✅ | 无内部类 |
| M006 | ✅ | 无常量布尔 |
| M007 | ✅ | 无空 catch（脚本已扫） |
| M008 | ✅ | 未重写 equals |
| M009 | ✅ | 无不匹配 equals |
| M010 | ✅ | 无位运算 |
| M011 | ✅ | 无 switch |
| M012 | ✅ | 无 finally |
| M013 | ✅ | 无强转算术 |
| M014 | ✅ | 枚举未用 getClass |
| M015 | ✅ | 无继承字段隐藏 |
| M016 | ❌ | **P1（Major）** `TodoServiceImpl.java:44` `LocalDateTime.now()` 依赖系统默认时区，与 `application.yml:6` `serverTimezone=UTC` 语义不一致（脚本命中） |
| M017 | ✅ | 测试均有 @Test |
| M018 | ✅ | 无显式锁 |
| M019 | ✅ | 无 switch 枚举 |
| M020 | ✅ | 重写方法有 @Override |
| M021 | ✅ | 未重写 equals |
| M022 | ✅ | 无 Optional.of(null) |
| M023 | ✅ | toString 已重写 |
| M024 | ✅ | 无 Optional.get |
| M025 | ✅ | 无 final 类 protected |
| M026 | ✅ | @Mock 非 static |
| M027 | ✅ | 无 ThreadLocal |
| I001 | ✅ | 异常断言含消息（`TodoServiceImplTest.java:91` hasMessageContaining） |
| I002 | ✅ | 无 @DoNotMock |
| I003 | ✅ | 无 @AutoValue |
| I004 | ✅ | 用 LocalDateTime（脚本已扫无 new Date） |
| I005 | ✅ | JUnit5 |
| I006 | ✅ | 用 @BeforeEach（`TodoServiceImplTest.java:39`） |
| I007 | ✅ | 用 JUnit5，无 tearDown |
| I008 | ✅ | 无 dataProvider |
| I009 | ✅ | 统计项 |
| I010 | ✅ | 未启动 Spring 容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无先读后写并发场景 |
| G1.2 | N/A | 无加锁 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 写接口无幂等键；最小闭环单次创建，内部工具可接受（列为后续） |
| G2.2 | N/A | 无重试/定时/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 单库单表 insert，无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 单表简单 insert |
| G4.2 | N/A | 无 WHERE 函数 |
| G4.3 | N/A | 无列表查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 非 happy path 已处理（rows<=0 抛业务异常 `TodoServiceImpl.java:49-52`） |
| G8.2 | N/A | 无外部强依赖链 |
| G8.3 | N/A | 无 I/O/连接/锁 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G9.1 | N/A | DB 由连接池托管，无显式 HTTP/RPC |
| G9.2 | N/A | 无显式外部 HTTP/RPC 调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | ⚠️ | **P1** `ApiResponse.java:32` error 恒 `code=500`，参数校验失败（400 语义）与系统错误（500 语义）无法区分；详见 report §5 |
| G10.2 | N/A | 无契约版本 |
| G11.1 | ✅ | 5 个单测均含断言 |
| G11.2 | ⚠️ | **P2** 缺 name=100/description=500 长度边界用例（仅覆盖 null/blank） |
| G11.3 | ✅ | 入参 null/blank 有防御（`TodoServiceImpl.java:37`） |
| G11.4 | N/A | 无数值/金额运算 |
| G12.1 | N/A | 无资损 |
| G12.2 | N/A | 无资损 |
| G13.1 | ✅ | 日志级别匹配（业务 warn、系统 error） |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 单租户内部用户 |
| G14.3 | ⚠️ | **P1** `TodoServiceImpl.java:44` `LocalDateTime.now()` 未显式时区，与 DB `serverTimezone=UTC` 不一致（同 M016） |
| G14.4 | ✅ | `DateTimeFormatter` 未指定时区，但仅格式化本地 LocalDateTime；建议后续统一 UTC |
| G15.1 | ✅ | 新建表，无 ALTER DROP/CHANGE（脚本 scan_sql 无命中） |
| G15.2 | N/A | 无新旧接口 |
| G15.3 | N/A | 无开关切换 |
| G16.1 | N/A | 最小闭环，无埋点；列为后续 |
| G16.2 | ✅ | 异常路径均有日志、无空 catch（脚本 G16.2 无命中） |
| G16.3 | ✅ | 业务异常 WARN、系统异常 ERROR（`GlobalExceptionHandler.java:21,28,34`） |
| G16.4 | ✅ | 无空 catch/printStackTrace |
| G17.1 | N/A | 无开关 |
| G17.2 | N/A | 最小闭环 |
| G17.3 | N/A | 无数据迁移脚本 |
| G18.1 | N/A | 安全补强项见 §4.3 |
| G18.2 | N/A | — |
| G18.3 | N/A | — |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | `TodoMapper.xml:17` 全 `#{}` 预编译，无 `${}`（脚本 scan_xml 无命中） |
| S1.2 | N/A | 无 order by/group by/动态表名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | N/A | 无 HTML/前端输出；JSON 返回由 Spring 序列化 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部调用 |
| S4.1 | ✅ | 无命令执行（脚本已扫） |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 Jackson 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件下载 |
| S7.3 | N/A | 无文件 |
| S8.1 | ⚠️ | **P2** `POST /todos` 无鉴权；内部用户最小闭环暂可接受，需接入认证 |
| S8.2 | ✅ | 增改走 POST（`TodoController.java:26`），未用 GET |
| S8.3 | ✅ | 自增 id 不用于外部读取（当前无查询接口） |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 密码走 `${DB_PASSWORD:}`（`application.yml:8`），无硬编码明文 |
| S9.2 | ✅ | 日志仅记 name/description/id，无密码/token |
| S9.3 | N/A | 内网内部用户，传输加密属部署项 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 CSRF 场景（内部 REST，无 Cookie 会话） |
| S10.2 | ✅ | 无 CORS（脚本已扫无 `*` 通配） |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | 示例项：Controller 已使用 `@Valid`（`TodoController.java:27`），符合 |
| U1.2 | N/A | 未定义 |
| U1.3 | N/A | 未定义 |
| U2.1 | N/A(未启用自定义规则) | 项目未配置业务红线 |
| U2.2 | N/A | 未定义 |
| U2.3 | N/A | 未定义 |

> 该清单仅含 1 条示例项（U1.1），U2 为空，视作「未启用自定义规则」。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`