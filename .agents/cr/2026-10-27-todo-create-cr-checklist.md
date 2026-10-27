# Code Review Checklist

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-…86c4-46a2-ba82-7dd86dc5a34b` / `f3fddb2` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

> **预检结果**：git 仓库 ✅；变更 `+1024/-0`，20 files；无二进制；Java 文件 13 个，不触发 Java 守卫。
> **自动化预扫**（`scan-all-rules.sh src/main/java src/test/java src/main/resources`）已执行，Exit 0，命中 **1** 项：`[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java:50`。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1-G17 | S1-S10 | 总状态 |
|---|----------------------|----------|-------|-------|--------|--------|--------|
| 1 | `src/main/java/com/dtaz/todo/TodoApplication.java` | 启动类/变更 | N/A(启动引导) | ✅ | N/A(无业务) | N/A | ✅ 已审 |
| 2 | `src/main/java/com/dtaz/todo/api/controller/TodoController.java` | 新增接口入口 | ✅ | ✅ | S8.3⚠️ 其余N/A | ⚠️ | ⚠️ 已审有问题 |
| 3 | `src/main/java/com/dtaz/todo/common/constant/TodoErrorCodeEnum.java` | 错误码枚举 | N/A(无业务逻辑) | ✅ | N/A | N/A | ✅ 已审 |
| 4 | `src/main/java/com/dtaz/todo/common/exception/BusinessException.java` | 业务异常 | N/A | ✅ | N/A | N/A | ✅ 已审 |
| 5 | `src/main/java/com/dtaz/todo/common/exception/GlobalExceptionHandler.java` | 异常处理 | N/A(非功能点) | ✅ | G16.2✅ G16.3✅ 其余N/A | S9.2✅ 其余N/A | ✅ 已审 |
| 6 | `src/main/java/com/dtaz/todo/common/model/ApiResponse.java` | 统一响应 | N/A | ✅ | N/A | N/A | ✅ 已审 |
| 7 | `src/main/java/com/dtaz/todo/dao/mapper/TodoItemMapper.java` | 数据访问接口 | ✅ | ✅ | N/A | S1.1✅(XML已扫) | ✅ 已审 |
| 8 | `src/main/java/com/dtaz/todo/model/do/TodoItemDO.java` | 数据对象 | N/A | ✅ | N/A | N/A | ✅ 已审 |
| 9 | `src/main/java/com/dtaz/todo/model/dto/TodoCreateRequest.java` | 请求DTO/校验 | ✅(校验) | ✅ | G11.3✅ | N/A | ✅ 已审 |
| 10 | `src/main/java/com/dtaz/todo/model/vo/TodoItemVO.java` | 视图对象 | N/A | ✅ | N/A | N/A | ✅ 已审 |
| 11 | `src/main/java/com/dtaz/todo/service/TodoItemService.java` | 服务接口 | ✅ | ✅ | N/A | N/A | ✅ 已审 |
| 12 | `src/main/java/com/dtaz/todo/service/impl/TodoItemServiceImpl.java` | 核心业务 | ✅ | ⚠️(A2.4) | G3✅ G11✅ G16✅ G14.4⚠️ M016⚠️ | S9.1⚠️(yml) 其余N/A | ⚠️ 已审有问题 |
| 13 | `src/test/java/com/dtaz/todo/service/impl/TodoItemServiceImplTest.java` | 单元测试 | ✅ | ✅ | G11✅ | N/A | ✅ 已审 |

> 非 Java 文件（跳过审代码，仅作配置/契约佐证核对）：`pom.xml`、`src/main/resources/application.yml`、`src/main/resources/db/schema.sql`、`src/main/resources/mapper/TodoItemMapper.xml`、`.agents/2026-10-27-todo-create/impl.md`、`docs/ARCHITECTURE.md`、`docs/modules/todo/README.md`。

---

## Step 2 — 功能（产物 B）

> spec 来源：`<requirement_section>` + `.agents/2026-10-27-todo-create/impl.md` + `docs/modules/todo/README.md`（API 章节 POST /todos）。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 新增待办 | Given 有效请求(title,description) When POST /todos Then 落库并返回待办 | 「核心功能：新增待办事项」「最小闭环：仅创建」；docs/modules/todo/README.md API POST /todos | TodoController.java, TodoItemServiceImpl.java, TodoItemMapper.java/xml | ✅ | `TodoController.java:35-39`；`TodoItemServiceImpl.java:42-65`；`TodoItemMapper.xml:14-18` |
| REQ-2 事项名称必填 | Given 空 title When 提交 Then 返回参数错误 | impl.md L89「输入校验 @Valid/@NotBlank」；README 事项名称字段 | TodoCreateRequest.java, GlobalExceptionHandler.java | ✅ | `TodoCreateRequest.java:15 @NotBlank`；`GlobalExceptionHandler.java:46-53 MethodArgumentNotValidException` |
| REQ-3 事项描述可选、限长 | Given description 可空或≤500 When 提交 Then 通过 | impl.md L89「@Size」 | TodoCreateRequest.java | ✅ | `TodoCreateRequest.java:20 @Size(max=500)`；测试 `should_setCurrentTime_when_createTodo` 描述为 null |
| REQ-4 统一响应契约 | Given 任意响应 When 返回 Then 包 ApiResponse(success/errorCode/data) | impl.md L93「ApiResponse 统一封装」 | ApiResponse.java | ✅ | `ApiResponse.java:37-60` success/error |
| REQ-5 回写主键ID | Given 插入 When MyBatis useGeneratedKeys Then id 回填 DO | mapper 变更 | TodoItemMapper.xml | ✅ | `TodoItemMapper.xml:14-15 useGeneratedKeys/keyProperty="id"`；`TodoItemServiceImpl.java:61` |
| REQ-6 异常路径 | Given 参数非法/系统失败 When 处理 Then 返回对应错误码 | impl.md TEST 章节 | GlobalExceptionHandler.java, TodoItemServiceImpl.java | ✅ | `TodoItemServiceImpl.java:43-45,55-58`；`GlobalExceptionHandler.java:31-67` |
| REQ-7 时间字段一致性 ⚠️ | Given 创建 When 返回 gmtCreate Then 应与落库值一致 | impl.md L90「必备字段 gmt_create/gmt_modified」 | TodoItemServiceImpl.java, TodoItemMapper.xml | ⚠️ | 见 Step4 §说明：`TodoItemServiceImpl.java:50-52` 设置 gmtCreate/gmtModified，但 `TodoItemMapper.xml:16-17` 仅 insert(title, description)，落库值由 DB DEFAULT CURRENT_TIMESTAMP 决定，与返回值来源不一致（P1） |

---

## Step 3 — 可读性检查（产物 C）

> 预扫 A 类规则（8 条）无命中（无 Tab、无 import *、无超120行、无 finalize 等）。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名为顶层类名；UTF-8；无 Tab（预扫 A1.3 无命中） |
| A2 | 源文件结构/import 顺序 | ⚠️ | **A2.4** `TodoItemServiceImpl.java:15` `import java.time.LocalDateTime;` 置于 org.* 之后、空行组之后，应排字典序最前 |
| A3 | 代码样式 | ✅ | K&R、4空格、行宽≤120、类成员空行；预扫 A3.4/A3.7 无命中 |
| A4 | 命名规范 | ✅ | 包全小写、类大驼峰、方法小驼峰、常量 UPPER_SNAKE、测试类 `XxxImplTest` |
| A5 | 编码实践 | ✅ | `@Override` 已加（TodoItemServiceImpl.java:40）；无空 catch；无 finalize |
| A6 | 特定元素样式 | ✅ | 无边括号数组、无 switch、修饰符顺序正确、无 long/l 字面量 |
| A7 | Javadoc 规范 | ✅ | public 类/接口有 Javadoc；getter 省略合规（A7.3） |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫脚本覆盖 B(25)+M(6)+I(2)，命中 **M016**；帧本变革代码简单 CRUD，其余脚本已扫规则无命中、LLM 对未覆盖项复核无新增命中。为语义精确，逐条标注如下（无关联一律 `N/A(与本变更无关)`）。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无字面量必定抛异常的 parse/of 调用 |
| B002 | N/A | 无数组 equals |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | N/A | 无基本类型数组 Arrays.asList（预扫无命中） |
| B006 | N/A | JUnit5 + AssertJ，无 assertEquals |
| B007 | N/A | 无 catch Throwable |
| B008 | N/A | 无 Executors 建池（预扫无命中） |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 new BigDecimal(double) |
| B011 | N/A | 无包装类型 == 比较 |
| B012 | N/A | 无 Calendar.add（预扫无命中） |
| B013 | N/A | 无 Calendar.HOUR |
| B014 | N/A | 无泛型不兼容集合查询 |
| B015 | N/A | 无 Collection.toArray |
| B016 | N/A | 无可比较类型 |
| B017 | N/A | 无 this==null（预扫无命中） |
| B018 | N/A | 无三目类型提升 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无常量乘法 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 SimpleDateFormat（预扫无命中） |
| B023 | N/A | 无游离异常实例（预扫无命中） |
| B024 | N/A | 无 Thread 实例 |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 x.equals(null)（预扫无命中） |
| B027 | N/A | 无 equals 异常属性 |
| B028 | N/A | 无 DateUtil.formatDate |
| B029 | N/A | setter 赋值正确（无错字段） |
| B030 | N/A | 无浮点 == 比较 |
| B031 | N/A | 无 String.format |
| B032 | N/A | 无注解 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无恒等表达式 |
| B036 | N/A | 无 IdentityHashMap（预扫无命中） |
| B037 | N/A | 无可变参数条件 |
| B038 | N/A | 无递归 |
| B039 | N/A | 无 indexOf |
| B040 | N/A | 无 isInstance |
| B041 | N/A | 无 JDBC close-in-catch |
| B042 | N/A | JUnit5，非 JUnit3 |
| B043 | N/A | 无内部类 @Test |
| B044 | N/A | 纯 JUnit5，无 TestCase 混用 |
| B045 | N/A | 无 synchronized |
| B046 | N/A | 无循环 |
| B047 | N/A | 无数值 compare |
| B048 | N/A | 无 Math.round |
| B049 | N/A | 无日期格式串（预扫无命中） |
| B050 | N/A | 无日期格式 hh/KK |
| B051 | N/A | 无 Boolean.getBoolean（预扫无命中） |
| B052 | N/A | 无 YYYY 格式串（预扫无命中） |
| B053 | N/A | 测试用 assertThatThrownBy，无 try-fail |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | Mockito when().thenReturn() 用法正确 |
| B056 | N/A | 无 Arrays.asList（预扫无命中） |
| B057 | N/A | 无增强 for 删改 |
| B058 | N/A | 无集合自身参数 |
| B059 | N/A | 无 nCopies |
| B060 | N/A | 无 null 三目拆箱 |
| B061 | N/A | 无 sun.misc.Base64（预扫无命中） |
| B062 | N/A | 无 URLClassLoader 强转 |
| B063 | N/A | 无 javax.xml.bind（预扫无命中） |
| B064 | N/A | 无 Optional == |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | N/A | 无 Math.random 强转 |
| B067 | N/A | 无 nextInt()% |
| B068 | N/A | 无字段自赋值 |
| B069 | N/A | 无 compareTo 自比较 |
| B070 | N/A | 无 equals 自比较 |
| B071 | N/A | 无 .size()>=0 |
| B072 | N/A | 无 Stream.toString |
| B073 | N/A | 无 StringBuilder(char) |
| B074 | N/A | 无 substring(0) |
| B075 | N/A | 无 for 循环 |
| B076 | ✅ | `@Transactional` 作用于 public 方法 `TodoItemServiceImpl.java:41-42`（预扫无命中） |
| B077 | N/A | 无 catch Throwable |
| B078 | N/A | 无 Truth 自等 |
| B079 | N/A | 无 @Mock 显式赋值 |
| B080 | ✅ | 4 个测试方法均有断言（assertThat/assertThatThrownBy/verify） |
| B081 | N/A | 无排序未用集合 |
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无 instanceof |
| M003 | N/A | 无 new Long()/Integer() 等（预扫无命中） |
| M004 | N/A | 无 printStackTrace（预扫无命中） |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无常量布尔 |
| M007 | N/A | 无 catch 块 |
| M008 | N/A | 无 equals 重写 |
| M009 | N/A | 无异类型 equals |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally |
| M013 | N/A | 无浮点强转 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无继承字段 |
| M016 | ⚠️ | `TodoItemServiceImpl.java:50` `LocalDateTime.now()` 未显式指定时区 |
| M017 | N/A | 测试方法均有 @Test |
| M018 | N/A | 无 lock |
| M019 | N/A | 无 switch |
| M020 | ✅ | 接口/父类重写方法有 `@Override`（TodoItemServiceImpl.java:40） |
| M021 | N/A | 无 equals(SpecificType) |
| M022 | N/A | 无 Optional.of(null) |
| M023 | N/A | 无未重写 toString 打印 |
| M024 | N/A | 无 Optional.get |
| M025 | N/A | 无 final 类 protected |
| M026 | N/A | @Mock 字段非 static |
| M027 | N/A | 无 ThreadLocal |
| I001 | N/A | 用 assertThatThrownBy + extracting 断言错误码，优于 expected（符合 I001 建议） |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | N/A | 未用 java.util.Date，用 LocalDateTime |
| I005 | N/A | JUnit5 |
| I006 | N/A | JUnit5 用 @BeforeEach |
| I007 | N/A | 无 teardown |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 仅统计项 |
| I010 | N/A | 纯 Mockito 单测，未用 Pandora/容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无先读后写并发场景 |
| G1.2 | N/A | 无锁 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多锁 |
| G2.1 | ⚠️ | POST /todos 无幂等键；待办创建为低成本新增场景，风险低，标 P2 参考（建议后续客户端防重 token） |
| G2.2 | N/A | 无重试/定时/MQ |
| G2.3 | N/A | 无幂等约定 |
| G3.1 | N/A | 单库单表 insert，无分布式事务 |
| G3.2 | ✅ | `@Transactional` 范围内仅单条本地 insert，无外部 I/O |
| G4.1 | N/A | 无复杂 SQL |
| G4.2 | N/A | 无 WHERE 索引列函数 |
| G4.3 | N/A | 无列表查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 异常均有抛出/记录，无吞异常 |
| G8.2 | N/A | 无外部强依赖 |
| G8.3 | N/A | 无 I/O 流/连接/锁手动管理（MyBatis 连接由框架托管） |
| G8.4 | N/A | 无自定义线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 建池 |
| G9.1 | N/A | 无显式外部调用（DB 由框架管理连接） |
| G9.2 | N/A | 无显式外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无 null 双义字段 |
| G10.2 | N/A | 新接口，无兼容负担 |
| G11.1 | ✅ | 4 个 TestCase 均有断言 |
| G11.2 | ✅ | 覆盖正常/空 description/null 请求/写库失败边界 |
| G11.3 | ✅ | Controller `@Valid` + `@NotBlank/@Size`；service 层 null 防御（TodoItemServiceImpl.java:43-45） |
| G11.4 | N/A | 无金额/数值运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 业务异常 WARN、系统异常 ERROR、无级别倒置（预扫无命中） |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️ | 见 G14.4/M016：`LocalDateTime.now()` + driverserverTimezone=Asia/Shanghai，时间语义依赖默认时区（P1） |
| G14.4 | ⚠️ | `TodoItemServiceImpl.java:50` 未显式指定时区（P1，与 M016 同根因） |
| G15.1 | ✅ | schema.sql 仅 CREATE TABLE IF NOT EXISTS + 加列，无 DROP/CHANGE（预扫无命中） |
| G15.2 | N/A | 新接口 |
| G15.3 | N/A | 无开关需求 |
| G16.1 | N/A | 最小闭环，未要求埋点（可后续补充） |
| G16.2 | ✅ | 异常路径均有日志：GlobalExceptionHandler.java:35/64、TodoItemServiceImpl.java:56 |
| G16.3 | ✅ | 业务 WARN / 系统 ERROR 分级正确 |
| G16.4 | ✅ | 无空 catch、无 printStackTrace |
| G17.1 | N/A | 无开关需求 |
| G17.2 | N/A | 无降级需求 |
| G17.3 | N/A | schema.sql 为新建表，无数据变更 |
| G18.1 | N/A | 安全补强见 4.3 |
| G18.2 | N/A | 安全补强见 4.3 |
| G18.3 | N/A | 安全补强见 4.3 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis `#{}` 预编译，无 `${}`（预扫 XML 无命中） |
| S1.2 | N/A | 无 order by/动态表名 |
| S1.3 | N/A | 无 like/in |
| S2.1 | N/A | 后端返回 JSON，无 HTML 拼接（前端未实现） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部调用 |
| S4.1 | N/A | 无命令执行（预扫无命中） |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析器 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 @JsonTypeInfo |
| S6.3 | N/A | 无敏感序列化字段 |
| S7.1 | N/A | 无文件上传下载 |
| S7.2 | N/A | 无文件路径 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | ⚠️ | 内部用户、最小闭环未接入鉴权；需求未明确要求，标 N/A→P2 参考（正式环境应补鉴权） |
| S8.2 | ✅ | 新增使用 POST |
| S8.3 | ⚠️ | 主键为 DB 自增 BIGINT，可预测（P2 参考；内部系统可接受） |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ⚠️ | `application.yml:9-10` 硬编码 `username: root` / `password: root`（P1：应走环境变量/配置中心） |
| S9.2 | ✅ | 日志/响应不输出密码、不向前端暴露系统异常详情（GlobalExceptionHandler.java:66 用统一兜底文案） |
| S9.3 | N/A | 本地开发配置；正式环境建议 HTTPS/密钥托管（P2 参考） |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 CSRF（前后端分离内部系统，未实现前端） |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | Controller 入参已使用 `@Valid` 统一校验（TodoController.java:36），符合示例项约束 |
| U1.2 | N/A(未启用自定义规则) | |
| U1.3 | N/A(未启用自定义规则) | |
| U2.1 | N/A(未启用自定义规则) | |
| U2.2 | N/A(未启用自定义规则) | |
| U2.3 | N/A(未启用自定义规则) | |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、`S1-S10 / G1-G17` 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`