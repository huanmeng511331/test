# Code Review Checklist

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-…` / `6e83908` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

---

## Step 1 — 执行队列（产物 A）

> 预检结果：git 仓库 ✅；变更范围 = coding stage（`git diff 440cdae~1 6e83908`）共 21 文件、1299 行新增；其中 **Java 文件 13 个**（守卫通过）。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | 可靠性G | 安全S | 总状态 |
|---|----------------------|----------|-------|-------|---------|-------|--------|
| 1 | `src/main/java/com/dtazziboot/todo/TodoApplication.java` | 启动类 | ✅ | ✅ | ✅(N/A) | N/A(无外部面) | ✅ 已审 |
| 2 | `src/main/java/com/dtazziboot/todo/api/controller/TodoController.java` | REQ-1 入口 | ✅ | ✅ | ✅(N/A) | ⚠️ S8.1 | ⚠️ 已审有问题 |
| 3 | `src/main/java/com/dtazziboot/todo/common/constant/TodoConstants.java` | 长度常量 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 4 | `src/main/java/com/dtazziboot/todo/common/exception/BusinessException.java` | 业务异常 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 5 | `src/main/java/com/dtazziboot/todo/common/handler/GlobalExceptionHandler.java` | 统一异常 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 6 | `src/main/java/com/dtazziboot/todo/common/model/ApiResponse.java` | 统一响应 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 7 | `src/main/java/com/dtazziboot/todo/dao/entity/TodoDO.java` | REQ-2 数据对象 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 8 | `src/main/java/com/dtazziboot/todo/dao/mapper/TodoMapper.java` | 数据访问 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 9 | `src/main/java/com/dtazziboot/todo/model/dto/TodoRequest.java` | REQ-2 请求/校验 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 10 | `src/main/java/com/dtazziboot/todo/model/dto/TodoVO.java` | REQ-2 展示对象 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 11 | `src/main/java/com/dtazziboot/todo/service/TodoService.java` | 服务接口 | ✅ | ✅ | ✅(N/A) | ✅ | ✅ 已审 |
| 12 | `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java` | 核心业务 | ✅ | ✅ | ⚠️ M016/G14.4 | ⚠️ G2.1(P2) | ⚠️ 已审有问题 |
| 13 | `src/test/java/com/dtazziboot/todo/service/impl/TodoServiceImplTest.java` | 单测 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| — | `pom.xml` | 依赖 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过(非Java) |
| — | `src/main/resources/application.yml` | 配置 | 跳过 | 跳过(非Java) | 跳过 | ⚠️ S9.1(非Java,人工) | ⚠️ 非Java有问题 |
| — | `src/main/resources/mapper/TodoMapper.xml` | MyBatis SQL | 跳过 | 跳过(非Java) | ✅(S1.1#{}预编译) | ✅ | 跳过(XML人工复核) |
| — | `src/main/resources/sql/schema.sql` | DDL | 跳过 | 跳过(非Java) | ✅(G15.1新表) | ✅ | 跳过(SQL人工复核) |
| — | `docs/*.md`、`.agents/...impl.md`、`test2.md` | 文档/杂项 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过(非Java) |

**注**：application.yml / TodoMapper.xml / schema.sql 为配置与 SQL，非 Java，按技能标「跳过」，但其安全/可靠性维度已做人工复核（见 Step 4 备注）。

---

## Step 2 — 功能（产物 B）

> spec 来源：`<requirement_section>`（目标=记录待办，核心=新增，任务信息=名称+描述，用户=内部用户，最小闭环=仅创建）+ `.agents/2026-10-27-todo-create/impl.md`。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 新增待办事项 | Given 合法请求 When POST /todos Then 落库并返回 VO | 「核心功能：新增待办事项」、「最小闭环：仅创建」 | TodoController.java / TodoServiceImpl.java / TodoMapper.xml | ✅ | `TodoController.java:45-49` → `TodoServiceImpl.java:61-93` → `TodoMapper.xml:14-18`（insert + useGeneratedKeys 回填 id） |
| REQ-2 任务信息含名称+描述 | Given name/description When 提交 Then 两者均被持久化并返回 | 「任务信息：事项名称和描述」 | TodoRequest.java / TodoDO.java / TodoVO.java | ✅ | `TodoRequest.java:19-27` 校验；`TodoDO.java:16-26` 字段；`TodoMapper.xml:16-17` 插入 name+description |
| REQ-3 仅创建（无越界实现） | When 交付 Then 不出现更新/删除/查询接口 | 「最小闭环：仅创建」 | 全模块 | ✅ | 仅 `TodoController` POST + `TodoMapper.insert`，无 update/delete/list 接口 |
| REQ-4 名称必填+长度限制 | Given name=null/blank/超100 Then 拒绝 | impl.md §TEST；「事项名称」 | TodoRequest.java / TodoServiceImpl.java | ✅ | `TodoRequest.java:19-20` @NotBlank/@Size；`TodoServiceImpl.java:63-69` 兜底；测试 `TodoServiceImplTest.java:93-124` |
| REQ-5 描述长度限制（可空） | Given description 超500 Then 拒绝；null 允许 | impl.md §TEST；「事项描述」 | TodoRequest.java / TodoServiceImpl.java | ✅ | `TodoRequest.java:26` @Size；`TodoServiceImpl.java:71-74` 兜底；测试 `:126-136` |
| REQ-6 异常统一响应 | Given 校验/业务/系统异常 Then 返回 ApiResponse | impl.md §CHECK(异常日志) | GlobalExceptionHandler.java / ApiResponse.java | ✅ | `GlobalExceptionHandler.java:34-68` 三分支；`ApiResponse.java:56-62` |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名、UTF-8、无 Tab（脚本 A1.3 无命中） |
| A2 | 源文件结构/import 顺序 | ⚠️ | `A2.4` import 未按 ASCII 字典序：`TodoController.java:3-13`、`TodoServiceImpl.java:3-17` 将 `com.dtazziboot` 排在 `org`/`java` 之后；无 `import *`、静态/非静态分组正确 |
| A3 | 代码样式 | ✅ | K&R、4 空格缩进、行宽 ≤120（脚本 A3.4 无命中）、运算符空格正确 |
| A4 | 命名规范 | ✅ | 包/类/方法/常量/字段命名符合规范，DO/VO/Request 后缀正确 |
| A5 | 编码实践 | ✅ | `@Override` 齐全、无空 catch、静态方法类名调用、无 finalize |
| A6 | 特定元素样式 | ✅ | `String[] args`、无 switch、修饰符顺序正确、无小写 `l` 字面量 |
| A7 | Javadoc 规范 | ✅ | public 类/成员均有 Javadoc；toString 等 `@Override` 可省略 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 脚本预扫（T=2026-10-27）结果：`=== Summary: 1 findings (P0=0, P1=1, P2=0)`，仅命中 `M016`（`TodoServiceImpl.java:76`）。下列按 ID 逐条核销，脚本未覆盖项由 LLM 复核。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | ✅ | 无字面量 parse/of |
| B002 | ✅ | 无数组 `.equals` |
| B003 | ✅ | 无 Arrays.fill |
| B004 | ✅ | 无数组 `.toString()` |
| B005 | ✅ | 无 Arrays.asList(基本类型数组) |
| B006 | ✅ | 测试用 AssertJ `assertThat`，无 assertEquals 顺序问题 |
| B007 | ✅ | 无捕获 Throwable 吞断言 |
| B008 | ✅ | 无 Executors |
| B009 | ✅ | 无移位 |
| B010 | ✅ | 无 new BigDecimal(double) |
| B011 | ✅ | 无包装类型 `==` 比较（Long id 用 isEqualTo 断言） |
| B012 | ✅ | 无 Calendar |
| B013 | ✅ | 无 Calendar.HOUR |
| B014 | ✅ | 无集合不兼容类型查询 |
| B015 | ✅ | 无 toArray |
| B016 | ✅ | 无 Comparable |
| B017 | ✅ | 无 this==null |
| B018 | ✅ | 无三目数值提升 |
| B019 | ✅ | 无 Money API |
| B020 | ✅ | 无常量乘法溢出 |
| B021 | ✅ | 无 Jedis |
| B022 | ✅ | 无 SimpleDateFormat |
| B023 | ✅ | 无未抛出的异常实例 |
| B024 | ✅ | 无 Thread |
| B025 | ✅ | 无双括号初始化 |
| B026 | ✅ | 无 x.equals(null) |
| B027 | ✅ | equals 未重写 |
| B028 | ✅ | 无 DateUtil.formatDate |
| B029 | ✅ | setter 赋值字段正确（逐一核对） |
| B030 | ✅ | 无浮点 `==` |
| B031 | ✅ | 无 String.format |
| B032 | ✅ | 无注解 getClass |
| B033 | ✅ | 无 Unsafe |
| B034 | ✅ | 无 Hashtable |
| B035 | ✅ | 无自比较二元表达式 |
| B036 | ✅ | 无 IdentityHashMap |
| B037 | ✅ | 无可变参数条件表达式 |
| B038 | ✅ | 无无限递归 |
| B039 | ✅ | 无 indexOf |
| B040 | ✅ | 无 isInstance |
| B041 | ✅ | JDBC 连接由 Spring/MyBatis 管理，无手动关闭 |
| B042 | ✅ | JUnit5，无 JUnit3 |
| B043 | ✅ | 无内部类 @Test |
| B044 | ✅ | 无 JUnit3/4 混用 |
| B045 | ✅ | 无对包装类型加锁 |
| B046 | ✅ | 无循环条件不更新 |
| B047 | ✅ | 无数值 compare 精度 |
| B048 | ✅ | 无 Math.round |
| B049 | ✅ | 日期格式 `yyyy-MM-dd`（非 DD），正确 |
| B050 | ✅ | 时辰格式 HH，正确 |
| B051 | ✅ | 无 Boolean.getBoolean |
| B052 | ✅ | 格式为 `yyyy`（非 YYYY），正确 |
| B053 | ✅ | 抛异常断言用 `assertThatThrownBy`（优于 try/fail） |
| B054 | ✅ | 无 EqualsTester |
| B055 | ✅ | Mockito `when().thenReturn()` 用法正确（`ServiceImpl:141`） |
| B056 | ✅ | 无 Arrays.asList().add |
| B057 | ✅ | 无增强 for 改集合 |
| B058 | ✅ | 无集合自身作参 |
| B059 | ✅ | 无 nCopies char |
| B060 | ✅ | 无 null 三元拆箱 |
| B061 | ✅ | 无 sun.misc |
| B062 | ✅ | 无 URLClassLoader 强转 |
| B063 | ✅ | 无 javax.xml.bind |
| B064 | ✅ | 无 Optional `==` |
| B065 | ✅ | 无 Pojo 自赋值 |
| B066 | ✅ | 无 (int)Math.random |
| B067 | ✅ | 无 nextInt()% |
| B068 | ✅ | 无变量自赋值 |
| B069 | ✅ | 无自比较 compareTo |
| B070 | ✅ | 无自比较 equals |
| B071 | ✅ | 无 size()>=0 |
| B072 | ✅ | 无 Stream.toString |
| B073 | ✅ | 无 new StringBuilder(char) |
| B074 | ✅ | 无 substring(0) |
| B075 | ✅ | 无异常 for |
| B076 | ✅ | `@Transactional` 在 public 方法 `ServiceImpl.java:60-61` |
| B077 | ✅ | 无捕获 Throwable |
| B078 | ✅ | 无 assertThat(x).isEqualTo(x) |
| B079 | ✅ | `@Mock` 未显式赋值（`Test:32-36`） |
| B080 | ✅ | 8 个测试方法均含断言 |
| B081 | ✅ | 无未使用集合原地排序 |
| M001 | ✅ | 无重复条件判断 |
| M002 | ✅ | 无子类 instanceof 父类 |
| M003 | ✅ | 无 new Long/Integer |
| M004 | ✅ | 无 printStackTrace（脚本无命中） |
| M005 | ✅ | 无内部类 |
| M006 | ✅ | 无编译期恒真表达式 |
| M007 | ✅ | 无空 catch（脚本无命中） |
| M008 | ✅ | equals/hashCode 未重写 |
| M009 | ✅ | 无不兼容类型 equals |
| M010 | ✅ | 无恒 0 位运算 |
| M011 | ✅ | 无 switch |
| M012 | ✅ | 无 finally return/throw |
| M013 | ✅ | 无强转优先级 |
| M014 | ✅ | 无枚举 getClass |
| M015 | ✅ | 无子类字段隐藏 |
| M016 | ⚠️ | `TodoServiceImpl.java:76` `LocalDateTime.now()` 依赖系统默认时区（脚本命中，P1） |
| M017 | ✅ | 测试方法均有 `@Test` |
| M018 | ✅ | 无 lock |
| M019 | ✅ | 无枚举 switch |
| M020 | ✅ | 重写方法有 `@Override` |
| M021 | ✅ | 无 equals(SpecificType) |
| M022 | ✅ | 无 Optional.of(null) |
| M023 | ✅ | 无未重写 toString 打印（多数重写了 toString） |
| M024 | ✅ | 无 Optional.get |
| M025 | ✅ | 无 final 类 protected 成员 |
| M026 | ✅ | `@Mock` 非 static（`Test:32`） |
| M027 | ✅ | 无 ThreadLocal |
| I001 | ✅ | 异常断言用 `hasMessageContaining`，优于 `@Test(expected)` |
| I002 | ✅ | 无 @DoNotMock |
| I003 | ✅ | 无 @AutoValue mock |
| I004 | ✅ | 使用 `LocalDateTime`（非 java.util.Date） |
| I005 | ✅ | JUnit5 |
| I006 | ✅ | `@BeforeEach` 已用（`Test:38`） |
| I007 | ✅ | 无 tearDown |
| I008 | ✅ | 无 dataProvider |
| I009 | ✅ | 8 个单测方法 |
| I010 | ✅ | 纯 Mockito 单测，不启动 Spring 容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 仅单条 insert，无「先读后写」事务并发场景 |
| G1.2 | N/A | 无锁/loadWithLock |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 单资源写 |
| G2.1 | ⚠️(P2) | 写接口无幂等键，重复提交会生成重复待办；仅创建场景语义可接受，建议确认是否需要业务幂等键 |
| G2.2 | N/A | 无重试/MQ/定时落库 |
| G2.3 | N/A | 无上游幂等约定 |
| G3.1 | N/A | 单库单表，无分布式事务 |
| G3.2 | ✅ | `@Transactional(rollbackFor=Exception.class)` 范围=单 insert，无外部 I/O（`ServiceImpl.java:60-84`） |
| G4.1 | ✅ | 业务逻辑在 Service，SQL 仅单条 insert（`TodoMapper.xml:14-18`） |
| G4.2 | N/A | 无 WHERE 索引列转换 |
| G4.3 | N/A | 无列表查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 异常分层处理：Service 抛 `BusinessException`，`GlobalExceptionHandler` 捕获并记录 |
| G8.2 | N/A | 无强依赖非核心链路 |
| G8.3 | ✅ | JDBC 连接交由 Spring/MyBatis 管理，无手动资源 |
| G8.4 | N/A | 无自建线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | ✅ | 无 Executors |
| G9.1 | N/A | 无客户端 HTTP/RPC 调用，DB 访问由数据源统一管理 |
| G9.2 | ⚠️(P2) | `application.yml:9` 数据源 URL 未显式设置 socketTimeout/connectTimeout（依赖 HikariCP 默认值），内部最小闭环可不阻塞 |
| G9.3 | N/A | 无重试 |
| G10.1 | ✅ | `description=null` 明确表示「无描述」，未复用 null 语义 |
| G10.2 | N/A | 无契约版本升级 |
| G11.1 | ✅ | 8 个单测均有断言（`TodoServiceImplTest.java:50-147`） |
| G11.2 | ✅ | 覆盖 null/blank/超长边界（`Test:93-136`），正常路径（`Test:51-65`） |
| G11.3 | ✅ | name 空/blank 双重校验（注解 + `ServiceImpl.java:63-66`）；description 可空已处理 |
| G11.4 | N/A | 无金额/数值运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资损链路 |
| G13.1 | ✅ | 正常 INFO、业务 WARN、系统 ERROR（`ServiceImpl.java:90`、`GlobalExceptionHandler.java:40/53/66`） |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 单租户内部，无租户字段 |
| G14.3 | ⚠️(P1并入M016) | `gmt_create` 由应用写入 `LocalDateTime.now()`（`ServiceImpl.java:76/81`），与 DB `CURRENT_TIMESTAMP`（`schema.sql:6`）时区可能不一致，跨区展示有偏差 |
| G14.4 | ⚠️(P1) | `DateTimeFormatter.ofPattern` 未显式指定时区（`ServiceImpl.java:31-32`），`LocalDateTime.now()` 用系统默认时区（脚本 M016 命中） |
| G15.1 | ✅ | 新表 `CREATE TABLE IF NOT EXISTS`，无 DROP/CHANGE 不兼容变更（脚本 G15.1 无命中） |
| G15.2 | N/A | 无新旧接口共存 |
| G15.3 | N/A | 无逻辑开关切换 |
| G16.1 | ⚠️(P2) | 核心链路无成功率/耗时埋点；最小闭环未引入监控，可后续补充 |
| G16.2 | ✅ | 异常路径均有日志且含 errorCode/上下文（脚本 G16.2 无命中） |
| G16.3 | ✅ | 级别正确 |
| G16.4 | ✅ | 无空 catch / printStackTrace |
| G17.1 | N/A | 无功能开关（最小闭环） |
| G17.2 | N/A | 无降级预案（写单点 DB，可接受） |
| G17.3 | N/A | 无数据变更脚本需求（仅新增） |
| G18.1 | N/A | (SKILL 模板预留，安全补强见 4.3) |
| G18.2 | N/A | 同上 |
| G18.3 | N/A | 同上 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis 全部 `#{}` 预编译（`TodoMapper.xml:17`），脚本 `${}` 无命中 |
| S1.2 | N/A | 无 order by/动态表名 |
| S1.3 | N/A | 无 like/in |
| S2.1 | ⚠️(P2) | 返回 JSON 未额外转义；纯 JSON API，前端渲染需自行转义（name/description 为用户输入回显） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | ✅ | 无命令拼接（脚本无命中） |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无不安全反序列化（脚本无命中） |
| S6.2 | N/A | Jackson 默认无多态启用 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件下载 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | ⚠️(P1) | `TodoController.java:45-49` 未接入鉴权/归属校验；需求「内部用户」最小闭环未实现认证，暴露后任何人可写 |
| S8.2 | ✅ | 写操作用 `@PostMapping`，非 GET（`TodoController.java:45`） |
| S8.3 | ⚠️(P2) | 主键为自增可预测；仅创建场景无按 ID 查询/越权读取，风险低 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ⚠️(P1) | `application.yml:10-11` 明文硬编码数据库账号密码（root/root），未从配置中心/环境变量注入 |
| S9.2 | ✅ | 日志未记录敏感信息 |
| S9.3 | ⚠️(P2) | `useSSL=false`（`application.yml:9`），内部环境可接受，公网部署需开启 TLS |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无表单会话（内部 API） |
| S10.2 | ✅ | 无 CORS 配置（默认不开放 `*`） |
| S10.3 | N/A | 无跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | Controller 已使用 `@Valid`（`TodoController.java:46`），符合示例项要求 |
| U1.2 | N/A | 未启用自定义规则 |
| U1.3 | N/A | 未启用自定义规则 |
| U2.1 | N/A | 未启用自定义规则（无业务红线配置） |
| U2.2 | N/A | 未启用自定义规则 |
| U2.3 | N/A | 未启用自定义规则 |

> 结论：项目未维护 `customized-checklist.md` 私有规则，除示例项 U1.1 外整节按 `N/A(未启用自定义规则)` 处理。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`（6/6 ✅）
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，均注明原因）
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`