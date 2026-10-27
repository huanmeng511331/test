# Code Review Checklist

> **Change** `todo(新增待办事项)` · **分支/Commit** `AI/task-DEV-c1f01881-…` / `fb53469` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已先在仓库对变更路径运行 `scan-all-rules.sh`（`src/main/java src/test/java src/main/resources`），输出并入 Step 3/Step 4 备注。

---

## 预检

- 非 git 仓库：否（`git status -sb` 正常）
- 变更范围：`git diff HEAD~1 HEAD` → 22 个文件，+942/-0（新增 todo 模块）
- 二进制文件：无
- Java 守卫：`.java` 文件 14 个 → 继续

---

## Step 1 — 执行队列（产物 A）

> 展开（非 glob）：14 个 `.java` + 8 个非 Java（跳过）。非 Java：`.agents/2026-10-27-todo/impl.md`、`docs/ARCHITECTURE.md`、`docs/modules/todo/README.md`、`pom.xml`、`src/main/resources/application.yml`、`src/main/resources/db/schema.sql`、`src/main/resources/mapper/TodoMapper.xml`、`src/test/resources/application.yml`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G | S | B/M/I | 总状态 |
|---|----------------------|----------|-------|-------|---|-----|-------|--------|
| 1 | src/main/java/com/dtaz/todo/TodoApplication.java | REQ-1 启动 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 2 | src/main/java/com/dtaz/todo/common/constant/ErrorCodeEnum.java | REQ-3 错误码 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 3 | src/main/java/com/dtaz/todo/common/constant/TodoConstants.java | REQ-2 长度常量 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 4 | src/main/java/com/dtaz/todo/common/exception/BusinessException.java | REQ-3 异常 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 5 | src/main/java/com/dtaz/todo/common/exception/GlobalExceptionHandler.java | REQ-3 统一异常 | ✅ | ✅ | ⚠️ | ✅ | ✅ | ⚠️ 已审有问题 |
| 6 | src/main/java/com/dtaz/todo/common/model/ApiResponse.java | REQ-3 响应 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ 已审有问题 |
| 7 | src/main/java/com/dtaz/todo/controller/TodoController.java | REQ-1 创建接口 | ✅ | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 8 | src/main/java/com/dtaz/todo/dao/mapper/TodoMapper.java | REQ-1 持久化 | ✅ | ✅ | ✅ | ✅ | ✅ | ⚠️ 已审有问题 |
| 9 | src/main/java/com/dtaz/todo/model/dto/TodoRequest.java | REQ-2 入参 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 10 | src/main/java/com/dtaz/todo/model/entity/TodoDO.java | REQ-2 数据对象 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 11 | src/main/java/com/dtaz/todo/model/vo/TodoVO.java | REQ-1 返回对象 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 12 | src/main/java/com/dtaz/todo/service/TodoService.java | REQ-1 服务契约 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 13 | src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java | REQ-1/2/3 业务实现 | ✅ | ⚠️ | ⚠️ | ✅ | ⚠️ | ⚠️ 已审有问题 |
| 14 | src/test/java/com/dtaz/todo/service/impl/TodoServiceImplTest.java | 单测 | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |

> 非 Java 8 个：跳过。

---

## Step 2 — 功能（产物 B）

> Spec 来源：任务需求（需求描述）+ `docs/ARCHITECTURE.md` + `docs/modules/todo/README.md` + `.agents/2026-10-27-todo/impl.md`。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 新增待办事项 | Given 内部用户提交 POST /todos，When 名称+描述合法，Then 创建并返回 VO | 需求「核心功能：新增待办事项」；README「POST /todos 新增待办事项」 | TodoController.java、TodoServiceImpl.java、TodoMapper.java、TodoMapper.xml | ✅ | TodoController.java:32-36；TodoServiceImpl.java:34-51；TodoMapper.xml:14-18；测试 should_returnTodoVO_when_requestValid |
| REQ-2 事项字段（名称+描述） | Given 请求体含 name/description，Then 二者落库并可返回 | 需求「任务信息：事项名称和描述」 | TodoRequest.java、TodoDO.java、TodoVO.java、schema.sql | ✅ | TodoRequest.java:13-19；schema.sql:4-5；TodoMapper.xml:16-17 |
| REQ-3 名称必填校验 | Given name 为空/全空白/null，Then 拒绝并返回参数错误 | README「业务校验（名称非空）」；ARCHITECTURE「用户输入使用 @Valid + Jakarta Validation 双重校验」 | TodoRequest.java、TodoServiceImpl.java、GlobalExceptionHandler.java | ✅ | TodoRequest.java:13；TodoServiceImpl.java:53-60；测试 should_throwException_when_nameIsBlank / requestIsNull |
| REQ-4 最小闭环（仅创建） | Given 无其他端点，Then 仅暴露 create | 需求「最小闭环：仅创建」 | TodoController.java | ✅ | TodoController.java:32 仅 `@PostMapping`；无其他 Controller 方法 |

---

## Step 3 — 可读性检查（产物 C）

> 预扫（`scan-all-rules.sh`）对 A 类未命中；LLM 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | UTF-8、无 Tab、文件名=类名 |
| A2 | 源文件结构/import 顺序 | ⚠️ | A2.4 `java.time.LocalDateTime` 未按字典序（排在 org.* 之后）：`TodoServiceImpl.java:15` |
| A3 | 代码样式 | ✅ | 无超 120 行、K&R、4 空格缩进 |
| A4 | 命名规范 | ✅ | 包名小写、UpperCamelCase、常量 UPPER_SNAKE、Long 后缀大写 L |
| A5 | 编码实践 | ✅ | `@Override` 齐全、无空 catch、静态方法类名调用、无 finalize |
| A6 | 特定元素样式 | ✅ | 无 switch、修饰符顺序正确、无小写 l |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc；简单 getter 按 A7.3 豁免 |

---

## Step 4 — 可靠性检查（产物 D）

> **预扫结果（52/222 条）**：命中 1 条（无 P0）：
> - `[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:40`
>
> 其余由 LLM 逐条核销（下）。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无字面量 parse/of 调用 |
| B002 | N/A | 无数组 equals |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | ✅ | 预扫无命中（无 Arrays.asList 基本类型数组） |
| B006 | N/A | 测试使用 AssertJ，非 JUnit assertEquals |
| B007 | ✅ | 无 catch Throwable/Error |
| B008 | ✅ | 预扫无命中（无 Executors） |
| B009 | N/A | 无移位运算 |
| B010 | ✅ | 预扫无命中（无 new BigDecimal(double)） |
| B011 | N/A | 只有 Long 字段，无包装类型 `==` 比较 |
| B012 | ✅ | 预扫无命中（无 Calendar.add(date,365)） |
| B013 | ✅ | 预扫无命中（无 Calendar.HOUR） |
| B014 | N/A | 无集合泛型不匹配查询 |
| B015 | N/A | 无 toArray(T[]) |
| B016 | N/A | 无自定义 Comparable |
| B017 | ✅ | 预扫无命中（无 this==null） |
| B018 | N/A | 无三目数值提升 |
| B019 | N/A | 无 Money 类型 |
| B020 | N/A | 无编译期常量乘法溢出 |
| B021 | N/A | 无 Jedis |
| B022 | ✅ | 预扫无命中（无 SimpleDateFormat） |
| B023 | ✅ | 预扫无命中（无未 throw 的异常实例） |
| B024 | N/A | 无 Thread 无 start |
| B025 | N/A | 无双括号初始化 |
| B026 | ✅ | 预扫无命中（无 x.equals(null)） |
| B027 | N/A | 无自定义 equals |
| B028 | ✅ | 预扫无命中（无 DateUtil.formatDate） |
| B029 | N/A | 无手写 setter 错配（字段简单对位核对 OK） |
| B030 | N/A | 无浮点比较 |
| B031 | N/A | 无 String.format |
| B032 | N/A | 无注解 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无恒等式二元运算 |
| B036 | ✅ | 预扫无命中（无 IdentityHashMap 装箱） |
| B037 | N/A | 无可变参数条件式 |
| B038 | N/A | 无递归 |
| B039 | N/A | 无 indexOf(ch,int) |
| B040 | N/A | 无 Class.isInstance |
| B041 | N/A | 无 JDBC 连接 |
| B042 | N/A | JUnit5，非 JUnit3 |
| B043 | N/A | 无内部类 @Test |
| B044 | N/A | 无 JUnit3+4 混用 |
| B045 | N/A | 无 synchronized 装箱锁 |
| B046 | N/A | 无手写 while 死循环 |
| B047 | N/A | 无数值 compare |
| B048 | N/A | 无 Math.round(整型) |
| B049 | ✅ | 预扫无命中（无 MM+DD 日期格式） |
| B050 | N/A | 无 hh/HH 格式 |
| B051 | ✅ | 预扫无命中（无 Boolean.getBoolean） |
| B052 | ✅ | 预扫无命中（无 YYYY 格式） |
| B053 | N/A | 测试用 assertThatThrownBy，非 try/catch 断言 |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | 无 Mockito when() 无 then |
| B056 | ✅ | 预扫无命中（无 Arrays.asList().add） |
| B057 | N/A | 无增强 for 中修改集合 |
| B058 | N/A | 无集合自操作 |
| B059 | ✅ | 预扫无命中（无 nCopies） |
| B060 | N/A | 无 null 三元拆箱 |
| B061 | ✅ | 预扫无命中（无 sun.misc BASE64） |
| B062 | ✅ | 预扫无命中（无 URLClassLoader cast） |
| B063 | ✅ | 预扫无命中（无 javax.xml.bind） |
| B064 | N/A | 无 Optional == |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | ✅ | 预扫无命中（无 (int)Math.random） |
| B067 | ✅ | 预扫无命中（无 nextInt()%） |
| B068 | N/A | 无自赋值 `name=name`（构造器用 this.name=name 正确） |
| B069 | N/A | 无 compareTo |
| B070 | N/A | 无自 equals |
| B071 | ✅ | 预扫无命中（无 size()>=0） |
| B072 | N/A | 无 Stream.toString |
| B073 | ✅ | 预扫无命中（无 new StringBuilder(char)） |
| B074 | ✅ | 预扫无命中（无 substring(0)） |
| B075 | N/A | 无手写 for 循环 |
| B076 | ✅ | 预扫无命中（无非 public @Transactional） |
| B077 | ✅ | 无 catch Throwable |
| B078 | N/A | 无 assertThat(x).isEqualTo(x) |
| B079 | N/A | @Mock 无显式赋值（正确） |
| B080 | ✅ | 单测 5 个方法均有 assertThat 断言 |
| B081 | N/A | 无原地修改未用集合 |
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无 instanceof |
| M003 | ✅ | 预扫无命中（无 new Integer/Long(...) 包装构造） |
| M004 | ✅ | 预扫无命中（无 printStackTrace） |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无编译期常量布尔表达式 |
| M007 | ✅ | 预扫无命中（无空 catch），GlobalExceptionHandler 均记日志 |
| M008 | N/A | 无重写 equals |
| M009 | N/A | 无类型不匹配 equals |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无类型转换算术 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无继承字段隐藏 |
| M016 | ⚠️ | `LocalDateTime.now()` 未显式时区：`TodoServiceImpl.java:40`（P1） |
| M017 | N/A | 测试类均 @Test |
| M018 | N/A | 无显式 lock |
| M019 | N/A | 无枚举 switch |
| M020 | ✅ | create/toString 均有 @Override |
| M021 | N/A | 无 equals(SpecificType) |
| M022 | ✅ | 预扫无命中（无 Optional.of） |
| M023 | N/A | 无打印未重写 toString（DO 已重写 toString） |
| M024 | N/A | 无 Optional.get |
| M025 | N/A | 无 final 类 protected 成员 |
| M026 | N/A | @Mock 非 static（正确） |
| M027 | ✅ | 预扫无命中（无 ThreadLocal） |
| I001 | ✅ | 预扫无命中；测试对异常消息断言（hasMessageContaining），符合 I001 正例 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | ✅ | 预扫无命中（无 new Date()，已用 LocalDateTime） |
| I005 | N/A | 非 JUnit3 |
| I006 | N/A | 无 setUp 误用（用 @BeforeEach 正确） |
| I007 | N/A | 无 tearDown |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 统计项，不启用 |
| I010 | N/A | 纯单元测试（MockitoExtension），非集成测试容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 仅单条 insert，无事务内先读后写 |
| G1.2 | N/A | 无锁内未二次校验即更新 |
| G1.3 | N/A | 无乐观锁重试 |
| G1.4 | N/A | 单资源无多锁 |
| G2.1 | N/A | 仅创建，无对账/扣款等幂等键要求；内部工具可接受重复提交（可在后续迭代加幂等） |
| G2.2 | N/A | 无重试/定时/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 单库单表，无分布式事务 |
| G3.2 | N/A | 无 @Transactional 包裹外部 I/O |
| G4.1 | N/A | SQL 简单，无复杂业务入 SQL |
| G4.2 | ✅ | WHERE 仅 `id=#{id}` 命中主键，无函数/隐式转换 |
| G4.3 | N/A | 无列表查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 非 happy path 有校验与异常分支；异常均抛出并上抛 |
| G8.2 | N/A | 无链路依赖 |
| G8.3 | ✅ | 无 I/O/连接/锁资源（MyBatis 由框架托管） |
| G8.4 | N/A | 无自建线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 线程池 |
| G9.1 | N/A | 无外部 HTTP/RPC；DB 由框架管理 |
| G9.2 | N/A | 无外部调用超时配置场景 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | VO 字段无"null 兼表异常"语义 |
| G10.2 | N/A | 新接口无版本切换需求 |
| G11.1 | ✅ | 5 个单测均有断言，覆盖正常/边界/异常 |
| G11.2 | ⚠️ | 覆盖空值、空白、0 行插入；未覆盖 name 长度边界/直接 Service 调用超长（P2，见 report） |
| G11.3 | ✅ | Service 对 null/空白名防御校验；Controller `@Valid` 兜底 |
| G11.4 | ✅ | 无金额/数值运算、无 float/double |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 预扫无命中；成功 info、业务异常 warn、系统异常 error，级别正确 |
| G14.1 | N/A | 无金额字段 |
| G14.2 | N/A | 无多租户表 |
| G14.3 | N/A | 时间戳 LocalDateTime 存库，未涉及跨时区展示 |
| G14.4 | ⚠️ | `LocalDateTime.now()` 未显式时区（与 M016 同源）：`TodoServiceImpl.java:40`，内部单时区可接受（P1/P2） |
| G15.1 | ✅ | schema.sql 为全新建表，无 DROP/CHANGE（预扫 G15.1 无命中） |
| G15.2 | N/A | 无新旧接口共存 |
| G15.3 | N/A | 无开关切换需求 |
| G16.1 | N/A | 内部工具最小闭环，无独立指标埋点框架 |
| G16.2 | ⚠️ | 异常日志无 traceId 上下文（P2，内部工具可接受） |
| G16.3 | ✅ | 日志级别正确 |
| G16.4 | ✅ | 无空 catch / printStackTrace，捕获均记日志 |
| G17.1 | N/A | 最小闭环无动态开关需求 |
| G17.2 | N/A | 无核心链路降级场景 |
| G17.3 | N/A | 无数据变更回滚脚本需求（仅新增） |
| G18.1 | N/A | 无（4.2 未定义 G18 具体项，本项目不启用） |
| G18.2 | N/A | 同上 |
| G18.3 | N/A | 同上 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis 全 `#{}`，无 `${}`（预扫 S1.1 无命中） |
| S1.2 | N/A | 无 order by/动态表名 |
| S1.3 | N/A | 无 like/in |
| S2.1 | N/A | 纯 JSON API、无服务端 HTML/JS 渲染，XSS 属客户端职责 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 跳转 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | ✅ | 预扫无命中（无 Runtime.exec/ProcessBuilder） |
| S4.2 | N/A | 无文件/图片操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | ✅ | 预扫无命中（无 ObjectInputStream） |
| S6.2 | N/A | 无 @JsonTypeInfo 多态 |
| S6.3 | N/A | 无敏感 transient 字段 |
| S7.1 | N/A | 无文件上传/下载 |
| S7.2 | N/A | 无文件路径 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | ⚠️ | `POST /todos` 无认证/授权/归属校验：`TodoController.java:32`（P1，内部用户范围未含认证；若暴露公网升级 P0） |
| S8.2 | ✅ | 写操作用 POST，非 GET |
| S8.3 | N/A | 数据 ID 为自增 Long，内部工具可接受 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 预扫无命中；DB 凭证走 `${DB_URL/DB_USERNAME/DB_PASSWORD}` 环境变量，未硬编码 |
| S9.2 | ✅ | 日志仅记 name/id/错误信息，无密码/token |
| S9.3 | N/A | 内网 HTTP，无加密传输要求（预扫 S9.3 无命中） |
| S9.4 | ✅ | 预扫无命中（无 new Random） |
| S10.1 | N/A | 无会话 Cookie，内部 API 无 CSRF 场景 |
| S10.2 | ✅ | 预扫无命中（无 CORS 通配） |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | 示例项：Controller 入参已用 `@Valid`（`TodoController.java:33`） |
| U1.2 | N/A | 未定义 |
| U1.3 | N/A | 未定义 |
| U2.1 | N/A | 未定义 |
| U2.2 | N/A | 未定义 |
| U2.3 | N/A | 未定义 |

> 说明：`customized-checklist.md` 仅含 1 条示例项（U1.1，满足）；其余 U 项未定义，标 N/A。

---

## 终检（防漏检）

- [x] 执行队列 14 个 `.java` 文件 Step2/Step3/Step4 均已核销（8 个非 Java 跳过）
- [x] Step 2 的 4 个 REQ 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 的 G 全部 ID、S 全部 ID、B001–B081 / M001–M027 / I001–I010 均非 `⬜`
- [x] Step 5 的 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️`（M016、G14.4、S8.1、G11.2、G16.2、A2.4）已写入 report 并含 `ID + path:line`