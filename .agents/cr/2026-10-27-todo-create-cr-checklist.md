# Code Review Checklist

> **Change** `todo-create` · **Commit** `36cb0d4`（stage: coding, round: 1） · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **执行顺序**：已先运行 `scan-all-rules.sh`（52/222），产物并入 Step 4 备注；LLM 已补扫其余规则。

---

## Step 1 — 执行队列（产物 A）

> 变更范围来自 commit 36cb0d4（17 文件，820 行）。`.java` 14 个纳入审查；非 Java 标「跳过」。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|--------|
| 1 | `src/main/java/com/alipay/todo/api/controller/TodoController.java` | REQ-1 入口 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | ✅ | N/A | ⚠️ 已审有问题 |
| 2 | `src/main/java/com/alipay/todo/api/request/TodoCreateRequest.java` | REQ-2 校验 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | ✅ | N/A | ✅ 已审 |
| 3 | `src/main/java/com/alipay/todo/common/constant/TodoConstants.java` | 常量 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `src/main/java/com/alipay/todo/common/exception/TodoErrorCodeEnum.java` | REQ-2 错误码 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | `src/main/java/com/alipay/todo/common/exception/TodoException.java` | REQ-2 异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 6 | `src/main/java/com/alipay/todo/common/model/ApiResponse.java` | 统一响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 7 | `src/main/java/com/alipay/todo/dao/entity/TodoDO.java` | REQ-1 持久化 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 8 | `src/main/java/com/alipay/todo/dao/mapper/TodoMapper.java` | REQ-1 DAO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 9 | `src/main/java/com/alipay/todo/model/vo/TodoVO.java` | REQ-1 出参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 10 | `src/main/java/com/alipay/todo/service/TodoService.java` | REQ-1 服务 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 11 | `src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java` | REQ-1/2 核心业务 | ✅ | ⚠️ | N/A | ⚠️ | ✅ | ✅ | N/A | N/A | N/A | ✅ | N/A | ✅ | ✅ | N/A | N/A | ⚠️ | ⚠️ | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 12 | `src/test/java/com/alipay/todo/service/impl/TodoServiceImplTest.java` | REQ-1/2 测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| — | `src/main/resources/mapper/TodoMapper.xml` | 非 Java | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 |
| — | `src/main/resources/sql/todo_item.sql` | 非 Java | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 |
| — | `docs/ARCHITECTURE.md` / `docs/modules/todo/README.md` / `.agents/.../impl.md` | 非 Java 文档 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 |

---

## Step 2 — 功能（产物 B）

> REQ 来源：需求原文（`requirement_section`）+ 实现报告 `.agents/2026-10-27-todo/impl.md`。不发明 change 外功能。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据 |
|-----|----------|----------------------|----------|------|-----------|
| REQ-1 | 新增待办事项：合法 `{name, description}` 落库并返回含 id 的 `TodoVO` | 需求「核心功能：新增待办事项；任务信息：事项名称和描述」 | `TodoServiceImpl.java` / `TodoMapper.xml` / `TodoController.java` | ✅ | `TodoServiceImpl.java:30-42`（validate→insert→toVO）；`TodoMapper.xml:14-18`（`useGeneratedKeys`）；测试 `TodoServiceImplTest.java:44-66` |
| REQ-1b | 描述可为空（`description` 允许 null） | impl.md「should_createTodo_when_descriptionIsNull」 | `TodoServiceImpl.java` | ✅ | `TodoServiceImpl.java:52` null 判断；测试 `TodoServiceImplTest.java:69-83` |
| REQ-2 | 名称空/超长、描述超长必须被校验拒绝并返回业务错误码 | impl.md TEST 表 `should_throwException_*` | `TodoCreateRequest.java` / `TodoServiceImpl.java` / `TodoErrorCodeEnum.java` | ✅ | `TodoServiceImpl.java:45-55`；`TodoErrorCodeEnum.java:11-17`；测试 `TodoServiceImplTest.java:86-119` |
| REQ-3 | 最小闭环仅创建：不提供查询/更新/删除能力 | 需求「最小闭环：仅创建」 | `TodoMapper.java` / `TodoController.java` | ✅ | `TodoMapper.java:16` 仅 `insert`；`TodoController.java:32` 仅 `@PostMapping`，无 GET/PUT/DELETE |
| REQ-4 | 错误响应统一封装（错误码+提示） | impl.md CHECK「异常日志/统一响应」 | `ApiResponse.java` / `TodoException.java` | ✅ | `ApiResponse.java:28-35`；`TodoException.java:12-23` |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `readability-checklist.md` A1–A7；脚本预扫 A* 无命中。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式（文件名/UTF-8/无 Tab） | ✅ | 文件名与顶层类一致；UTF-8；脚本 A1.3 无 Tab 命中 |
| A2 | 源文件结构/import 顺序 | ⚠️ | 无 `import *`（A2.2 ✅）；`TodoController.java:3-14` 导入顺序不完全按 ASCII（`common.model`→`common.exception`→`model.vo`→`service`），建议按规范重排（A2.4，P2） |
| A3 | 代码样式（大括号/缩进/行宽） | ✅ | K&R、4 空格缩进、行宽 ≤120 均符合；脚本 A3.4/A3.7 无命中 |
| A4 | 命名规范 | ✅ | 包全小写、类 UpperCamelCase、方法 lowerCamelCase、常量 UPPER_SNAKE；`id=1L` 使用 long 字面量正确（A6.5） |
| A5 | 编码实践（@Override/空 catch/静态方法调用） | ✅ | `createTodo` 有 `@Override`；无空 catch；A5.4 无 finalize |
| A6 | 特定元素样式 | ✅ | 数组无 C 风格；无 switch；修饰符顺序正确；注解每行一个 |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc；`setter/getter/toString` 自解释可省略 |

---

## Step 4 — 可靠性检查（产物 D）

> 逐条核销。Bug 模式 120 条（Blocker→P0、Major→P1、Info→P2）。脚本命中：**P1 M016 —— `TodoServiceImpl.java:33`（`LocalDateTime.now()`）**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无字面量 `parse/of` 调用 |
| B002 | N/A | 无数组 `equals` |
| B003 | N/A | 无 `Arrays.fill` |
| B004 | N/A | 无数组 `toString`（DO/VO 的 toString 为对象字段拼接） |
| B005 | ✅ | 脚本无命中（无基本类型数组 `asList`） |
| B006 | N/A | 使用 AssertJ `assertThat`，非 JUnit `assertEquals` |
| B007 | N/A | 无 `catch Throwable/Error` |
| B008 | ✅ | 脚本无命中（无 `Executors`） |
| B009 | N/A | 无 int 移位超 31 |
| B010 | ✅ | 脚本无命中（无 `new BigDecimal(double)`） |
| B011 | N/A | Long 用断言比较，无 `==` |
| B012 | ✅ | 脚本无命中（无 Calendar） |
| B013 | ✅ | 脚本无命中 |
| B014 | N/A | 无泛型集合 `get/get 不兼容类型` |
| B015 | N/A | 无 `toArray(T[])` |
| B016 | N/A | 无 `Comparable` 实现 |
| B017 | ✅ | 脚本无命中（无 `this == null`） |
| B018 | N/A | 无数值三目提升 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无编译期常量乘法 |
| B021 | N/A | 无 Jedis |
| B022 | ✅ | 脚本无命中（无 SimpleDateFormat） |
| B023 | ✅ | 脚本无命中；`TodoConstants:11` 检出 `throw new IllegalStateException` 被正确排除 |
| B024 | N/A | 无 `new Thread()` |
| B025 | N/A | 无双括号初始化 |
| B026 | ✅ | 脚本无命中（无 `x.equals(null)`） |
| B027 | N/A | 无 `equals` 实现 |
| B028 | ✅ | 脚本无命中（无 commons DateUtil） |
| B029 | ✅ | 所有 setter 赋值正确字段（`TodoDO:24-58`、`TodoVO`、`ApiResponse` 均一一对应） |
| B030 | N/A | 无浮点比较 |
| B031 | N/A | 无 `String.format` |
| B032 | N/A | 无注解 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无同一对象二元运算 |
| B036 | ✅ | 脚本无命中 |
| B037 | N/A | 无可变参数 |
| B038 | N/A | 无递归 |
| B039 | N/A | 无 `indexOf(int,int)` |
| B040 | N/A | 无 `isInstance` |
| B041 | N/A | 无 JDBC 连接 |
| B042 | N/A | JUnit5 非 JUnit3 |
| B043 | N/A | 测试类顶层非内部类 |
| B044 | N/A | 无 JUnit3/4 混用 |
| B045 | N/A | 无 synchronized 包装类型 |
| B046 | N/A | 无 while/for 循环 |
| B047 | N/A | 无数值 compare |
| B048 | N/A | 无 `Math.round` |
| B049 | ✅ | 脚本无命中（无 `MM-DD` 格式） |
| B050 | N/A | 无日期格式串 |
| B051 | ✅ | 脚本无命中 |
| B052 | ✅ | 脚本无命中 |
| B053 | N/A | 用 `assertThatThrownBy` 非 try/fail |
| B054 | N/A | 无 EqualsTester |
| B055 | ✅ | `doAnswer(...).when(...).insert(...)` 与 `verify(todoMapper).insert(captor.capture())` 用法正确（`TodoServiceImplTest:46-63`） |
| B056 | ✅ | 脚本无命中 |
| B057 | N/A | 无增强 for 修改 |
| B058 | N/A | 无集合自操作 |
| B059 | ✅ | 脚本无命中 |
| B060 | N/A | 无 null 三目拆箱 |
| B061 | ✅ | 脚本无命中 |
| B062 | ✅ | 脚本无命中 |
| B063 | N/A | 使用 `jakarta.validation`（非被移除的 `javax.xml.bind`），符合 |
| B064 | N/A | 无 Optional == |
| B065 | N/A | 无自赋值 |
| B066 | ✅ | 脚本无命中 |
| B067 | ✅ | 脚本无命中 |
| B068 | N/A | 无 `name = name` |
| B069 | N/A | 无 compareTo |
| B070 | N/A | 无 equals |
| B071 | ✅ | 脚本无命中；断言用 `hasSize` |
| B072 | N/A | 无 Stream.toString |
| B073 | ✅ | 脚本无命中 |
| B074 | ✅ | 脚本无命中 |
| B075 | N/A | 无 for 循环 |
| B076 | ✅ | 脚本无命中；`@Transactional` 在 public `createTodo` |
| B077 | N/A | 无 catch Throwable |
| B078 | N/A | 无 Truth 断言自身 |
| B079 | ✅ | `@Mock TodoMapper` 未显式赋值 |
| B080 | ✅ | 7 个测试均含断言 |
| B081 | N/A | 无原地 sort/shuffle |
| M001 | N/A | validate 各 if 独立 |
| M002 | N/A | 无 instanceof |
| M003 | ✅ | 脚本无命中（无包装构造器） |
| M004 | ✅ | 脚本无命中（无 printStackTrace） |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无编译期常量布尔 |
| M007 | ✅ | 脚本无命中（无空 catch） |
| M008 | N/A | 无 equals 重写 |
| M009 | N/A | 无跨类型 equals |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无浮点强转 |
| M014 | N/A | 无 enum getClass |
| M015 | N/A | 无继承字段隐藏 |
| M016 | ❌ | **P1**: `TodoServiceImpl.java:33` `LocalDateTime.now()` 依赖系统默认时区（脚本命中） |
| M017 | N/A | JUnit5 `@Test` 正确 |
| M018 | N/A | 无锁 |
| M019 | N/A | 无 enum switch |
| M020 | ✅ | `createTodo`/`toString` 均含 `@Override` |
| M021 | N/A | 无 `equals(SpecificType)` |
| M022 | ✅ | 脚本无命中（无 `Optional.of(null)`） |
| M023 | N/A | 无 `println(对象)`，toString 均覆写 |
| M024 | N/A | 无 Optional.get |
| M025 | N/A | `TodoConstants` final 类无 protected 成员 |
| M026 | ✅ | `@Mock` 非 static |
| M027 | ✅ | 脚本无命中（无 ThreadLocal） |
| I001 | ✅ | 脚本无 `@Test(expected=)`；使用 `assertThatThrownBy` 断言错误码更佳 |
| I002 | N/A | 无 `@DoNotMock` |
| I003 | N/A | 无 `@AutoValue` |
| I004 | ✅ | 脚本无命中；使用 `LocalDateTime` 非 `java.util.Date` |
| I005 | N/A | JUnit5 |
| I006 | N/A | JUnit5 `@BeforeEach` 正确 |
| I007 | N/A | 无 tearDown |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 统计项 |
| I010 | N/A | 无 Spring 容器 Runner |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 单表单行 INSERT，无事务内先读后写 |
| G1.2 | N/A | 无锁后二次校验场景 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | ⚠️ | **P1**：写接口无幂等键（无业务单号/去重表/唯一约束）。单库自增 id 无法拦截重复提交，异常重试或多客户端重复请求会产生重复记录 |
| G2.2 | N/A | 无重试/定时任务落库 |
| G2.3 | N/A | 无上游幂等约定 |
| G3.1 | N/A | 单库操作，无跨库 |
| G3.2 | ✅ | `@Transactional` 范围仅 validate+insert，无 RPC/MQ/文件等外部 I/O |
| G4.1 | N/A | 单表简单 INSERT，无复杂 SQL |
| G4.2 | ✅ | `name/description` 无函数/隐式转换 |
| G4.3 | N/A | 无列表查询 |
| G4.4 | N/A | 清单未定义该 ID |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | `validate` 主动抛 `TodoException`，不吞异常、不 catch |
| G8.2 | N/A | 无强依赖非核心链路 |
| G8.3 | ✅ | MyBatis 托管连接，无手动 I/O 流 |
| G8.4 | N/A | 无自建线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | ✅ | 无 `Executors`（脚本无命中） |
| G8.7 | N/A | 清单未定义该 ID |
| G9.1 | N/A | 仅 MyBatis 单库 INSERT，无手动外部调用三态处理 |
| G9.2 | N/A | 同上 |
| G9.3 | N/A | 无重试 |
| G10.1 | ✅ | `name` 非空（`@NotBlank`+validate），`description` 明确语义为可空，无 null 双义 |
| G10.2 | N/A | 无契约升级 |
| G10.3 | N/A | 清单未定义该 ID |
| G11.1 | ✅ | 单测存在且有断言（7 例） |
| G11.2 | ⚠️ | **P2**：已覆盖空/超长/边界，但未覆盖「`insert` 返回 0 行」失败路径（被影响行数未校验） |
| G11.3 | ✅ | `name` 空/空白校验；`description` 允许 null 且防御性判断存在 |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 脚本无命中；无 LogLevelMismatch |
| G14.1 | N/A | 无金额字段/浮点 |
| G14.2 | N/A | 单租户内部系统，无租户列 |
| G14.3 | ⚠️ | **P1**：`TodoServiceImpl.java:33` 用 `LocalDateTime.now()` 取默认时区写入 `gmt_create/gmt_modified`；表列有 `DEFAULT CURRENT_TIMESTAMP`，服务端时间与 DB 时间可能不一致，跨区部署存在漂移。建议 DB 默认值由服务端不写时间，或显式 `Instant`/UTC |
| G14.4 | N/A | 无 SimpleDateFormat |
| G15.1 | ✅ | `todo_item.sql` 仅 CREATE TABLE，无 `DROP COLUMN`/`CHANGE` 等不兼容 DDL（脚本无命中） |
| G15.2 | N/A | 无跨度 |
| G15.3 | N/A | 无跨度 |
| G16.1 | ⚠️ | **P1**：核心创建链路无成功率/耗时/调用量埋点 |
| G16.2 | ✅ | 异常通过 `TodoException` 携带 errorCode 传递，无需 catch；异常路径可追溯 |
| G16.3 | ⚠️ | **P2**：无任何日志输出，业务失败无 WARN、成功无 INFO |
| G16.4 | ✅ | 无空 catch/printStackTrace |
| G17.1 | N/A | 最小闭环，无开关 |
| G17.2 | N/A | 无降级预案设计 |
| G17.3 | N/A | 仅建表+代码，无数据回滚脚本需要 |
| G18.1 | N/A | 清单未定义 G18 节 |
| G18.2 | N/A | 清单未定义 G18 节 |
| G18.3 | N/A | 清单未定义 G18 节 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | `TodoMapper.xml:17` 全用 `#{}` 预编译，无 `${}`（脚本无命中） |
| S1.2 | N/A | 无 order by/group by/动态表名 |
| S1.3 | N/A | 无 like/in |
| S2.1 | N/A | 内部 JSON API，无 HTML/JS 输出 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | ✅ | 脚本无命中（无 `Runtime.exec`/ProcessBuilder） |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析（MyBatis mapper 为配置文件，非运行时解析用户 XML） |
| S5.2 | N/A | 无 XPath |
| S6.1 | ✅ | 脚本无命中（无 ObjectInputStream） |
| S6.2 | N/A | 无多态反序列化 |
| S6.3 | N/A | 无敏感 transient 字段 |
| S7.1 | N/A | 无上传 |
| S7.2 | N/A | 无下载 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | ⚠️ | **P1**：`TodoController` 未接入鉴权/授权；需求为「内部用户」，但接口未声明任何身份认证或归属校验 |
| S8.2 | ✅ | 增删改用 `@PostMapping`，非 GET |
| S8.3 | ⚠️ | **P2**：自增 `BIGINT` 主键可预测/枚举（`useGeneratedKeys`）。内部系统风险低，列为参考建议 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 脚本无命中（无硬编码密钥） |
| S9.2 | N/A | 名称/描述非敏感，且无日志 |
| S9.3 | N/A | 内部 HTTP，无凭证传输/加密需求（记录） |
| S9.4 | ✅ | 脚本无命中（无 `new Random()`） |
| S10.1 | N/A | 无 CSRF Token（内部 API，参考） |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> `customized-checklist.md` 仅含示例项 `U1.1`（Controller 入参须用 `@Valid`），未启用实质性自定义规则。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | 示例项：`TodoController.java:33` 已使用 `@Valid` 注解 |
| U1.2 | N/A | 未启用自定义规则 |
| U1.3 | N/A | 未启用自定义规则 |
| U2.1 | N/A | 未启用自定义规则 |
| U2.2 | N/A | 未启用自定义规则 |
| U2.3 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列每个 `.java` 文件 `Step2`、`Step3`、S1–S10 / G1–G17 各列均非 `⬜`
- [x] Step 2 各 REQ/Scenario 均定案，无 P0
- [x] Step 3 A1–A7 均非 `⬜`
- [x] Step 4 G/S 与 B001–B081 / M001–M027 / I001–I010 全部核销（N/A 均有原因）
- [x] Step 5 全部 U* 已核销（示例项 ✅，其余 N/A 未启用）
- [x] 所有 `❌/⚠️` 已写入 report（含 ID + path:line）