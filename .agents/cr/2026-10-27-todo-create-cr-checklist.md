# Code Review Checklist

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-…aef-/4b56849` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
> **执行顺序（强制）**：链接已完成 `scan-all-rules.sh` 预扫（P0=0, P1=1, P2=0），结果并入 Step 3/4 备注。

---

## Step 1 — 执行队列（产物 A）

> 变更范围：`git diff --name-status 94234b8~1 HEAD` → 23 个新增文件，其中 **14 个 `.java`**，其余为 pom / yml / xml / sql / md（非 Java 源码，标 `跳过`，仍作为关联证据阅读）。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G 结论 | S 结论 | 总状态 |
|---|----------------------|----------|-------|-------|--------|--------|--------|
| 1 | src/main/java/com/dtk/todo/TodoApplication.java | 启动类 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 2 | src/main/java/com/dtk/todo/common/constant/TodoErrorCodeEnum.java | 错误码枚举 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 3 | src/main/java/com/dtk/todo/common/exception/GlobalExceptionHandler.java | 全局异常 | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 4 | src/main/java/com/dtk/todo/common/exception/TodoException.java | 业务异常 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 5 | src/main/java/com/dtk/todo/common/model/ApiResponse.java | 统一响应体 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 6 | src/main/java/com/dtk/todo/controller/TodoItemController.java | REST 接口 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 7 | src/main/java/com/dtk/todo/dao/mapper/TodoItemMapper.java | 数据访问接口 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 8 | src/main/java/com/dtk/todo/model/dto/TodoItemCreateRequest.java | 请求 DTO | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 9 | src/main/java/com/dtk/todo/model/dto/TodoItemVO.java | 视图 VO | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 10 | src/main/java/com/dtk/todo/model/entity/TodoItemDO.java | 数据对象 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 11 | src/main/java/com/dtk/todo/service/TodoItemService.java | 服务接口 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 12 | src/main/java/com/dtk/todo/service/impl/TodoItemServiceImpl.java | 服务实现 | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 13 | src/test/java/com/dtk/todo/controller/TodoItemControllerTest.java | 控制器切片测试 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 14 | src/test/java/com/dtk/todo/service/impl/TodoItemServiceImplTest.java | 服务单测 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| — | src/main/resources/application.yml | 数据源配置 | 跳过(非Java) | 跳过 | — | — | 跳过 |
| — | src/main/resources/mapper/TodoItemMapper.xml | MyBatis SQL | 跳过(非Java) | 跳过 | — | — | 跳过 |
| — | src/main/resources/sql/schema.sql | 建表 DDL | 跳过(非Java) | 跳过 | — | — | 跳过 |
| — | pom.xml | 依赖配置 | 跳过(非Java) | 跳过 | — | — | 跳过 |

- 执行队列 `.java` 数：**14**（main 12 + test 2），`⬜ 待审 = 0`。
- 守卫：存在 `.java` → 通过。

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 REQ，勿臆造。需求原文（requirement_section）：「目标：帮助用户记录日常待办事项；核心功能：新增待办事项；任务信息：事项名称和描述；目标用户：内部用户；最小闭环：仅创建」。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 内部用户，When POST 新增待办，Then 返回创建结果 | 核心功能：新增待办事项 / 最小闭环：仅创建 | TodoItemController.java、TodoItemService.java、TodoItemServiceImpl.java、TodoItemMapper.java、TodoItemMapper.xml | ✅ | `TodoItemController.java:40-44` POST `/todo_items`；`TodoItemServiceImpl.java:38-71`；`TodoItemMapper.xml:14-18` insert |
| REQ-2 | Given title+description，When 提交，Then 两项均落库并回显 | 任务信息：事项名称和描述 | TodoItemCreateRequest.java、TodoItemDO.java、TodoItemVO.java、schema.sql | ✅ | `TodoItemCreateRequest.java:14-18`；`schema.sql:2-9`；`TodoItemServiceImpl.java:54-55,66-69` |
| REQ-3 | Given title 为空/空白，When 提交，Then 拒绝且不落库 | 事项名称（必填，据单元测试与 @NotBlank） | TodoItemCreateRequest.java、TodoItemServiceImpl.java、GlobalExceptionHandler.java、测试 | ✅ | `TodoItemCreateRequest.java:14` `@NotBlank`；`TodoItemServiceImpl.java:40-44`；`TodoItemServiceImplTest.java:90-105` |
| REQ-4 | Given 仅创建，Then 提供 insert 而非查询/更新/删除 | 最小闭环：仅创建 | TodoItemMapper.java、TodoItemMapper.xml | ✅ | `TodoItemMapper.java:21` 仅 `insert`；XML 仅 `<insert>` |

- 功能无 P0 不符；四 REQ 全部满足。

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | UTF-8、无 Tab（脚本 A1.3 未命中）、文件名=类名 |
| A2 | 源文件结构/import 顺序 | ✅ | 无 `import *`；static/non-static 分组且空行分隔（测试类）；import 按组排序 |
| A3 | 代码样式 | ✅ | 无超长行（脚本 A3.4 未命中）、K&R、缩进 4 空格 |
| A4 | 命名规范 | ✅ | 包全小写、类名大驼峰、方法小驼峰、常量 UPPER_SNAKE |
| A5 | 编码实践 | ✅ | `@Override` 齐全、无空 catch、无 finalize 覆写 |
| A6 | 特定元素样式 | ✅ | 无 C 风格数组、无 switch、修饰符顺序正常、Long 用大写 L |
| A7 | Javadoc 规范 | ✅ | public 类/方法有 Javadoc；getter/setter 依 A7.3 省略 |

- 可读性：**无违规**。

---

## Step 4 — 可靠性检查（产物 D）

> 逐条核销。Bug 模式 Blocker→P0、Major→P1、Info→P2。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| B001 | ✅ | 无字面量 parse/of 必抛异常调用 |
| B002 | ✅ | 无数组 equals 比较 |
| B003 | ✅ | 无 Arrays.fill |
| B004 | ✅ | 无数组 toString |
| B005 | ✅ | 无 Arrays.asList(基本类型数组)（脚本未命中） |
| B006 | ✅ | 测试用 AssertJ `assertThat`，非 assertEquals |
| B007 | ✅ | 无 catch Throwable 吞断言 |
| B008 | ✅ | 无 Executors（脚本未命中） |
| B009 | ✅ | 无 int 移位 |
| B010 | ✅ | 无 new BigDecimal(double) |
| B011 | ✅ | 无包装类型 == 比较 |
| B012 | ✅ | 无 Calendar.add(DATE,365) |
| B013 | ✅ | 无 Calendar.HOUR |
| B014 | ✅ | 无集合不兼容类型查询 |
| B015 | ✅ | 无 toArray 类型不匹配 |
| B016 | ✅ | 无 Comparable 泛型错误 |
| B017 | ✅ | 无 this==null |
| B018 | ✅ | 无数值三目类型提升 |
| B019 | ✅ | 无 Money API |
| B020 | ✅ | 无常量溢出 |
| B021 | ✅ | 无 Jedis |
| B022 | ✅ | 无 SimpleDateFormat |
| B023 | ✅ | 无死异常实例（脚本未命中） |
| B024 | ✅ | 无死线程 |
| B025 | ✅ | 无双括号初始化 |
| B026 | ✅ | 无 equals(null) |
| B027 | ✅ | 无 equals 比较错误属性 |
| B028 | ✅ | 无 commons DateUtil |
| B029 | ✅ | setter 赋值正确 |
| B030 | ✅ | 无浮点 == 比较 |
| B031 | ✅ | 无 String.format 占位符错配 |
| B032 | ✅ | 无注解 getClass |
| B033 | ✅ | 无 Unsafe |
| B034 | ✅ | 无 Hashtable |
| B035 | ✅ | 无同一对象二元运算 |
| B036 | ✅ | 无 IdentityHashMap 包装 key |
| B037 | ✅ | 无 varargs 条件混用 |
| B038 | ✅ | 无无限递归 |
| B039 | ✅ | 无 indexOf 参数颠倒 |
| B040 | ✅ | 无 isInstance 不兼容 |
| B041 | ✅ | 无 JDBC 手写连接管理（MyBatis 托管） |
| B042 | ✅ | JUnit5，非 JUnit3 |
| B043 | ✅ | 无内部类 @Test |
| B044 | ✅ | 无 JUnit3+4 混用 |
| B045 | ✅ | 无 synchronized 包装类型 |
| B046 | ✅ | 无死循环条件 |
| B047 | ✅ | 无 compare 精度损失 |
| B048 | ✅ | 无 Math.round(long) |
| B049 | ✅ | 无 "yyyy-MM-DD" |
| B050 | ✅ | 无 hh/HH 误用 |
| B051 | ✅ | 无 Boolean.getBoolean |
| B052 | ✅ | 无 "YYYY-MM-dd" |
| B053 | ✅ | 无 expect-throw try 缺 Assert.fail |
| B054 | ✅ | 无 EqualsTester |
| B055 | ✅ | Mockito when/verify 用法正确 |
| B056 | ✅ | 无 Arrays.asList 后 add/remove |
| B057 | ✅ | 无增强 for 修改集合 |
| B058 | ✅ | 无集合以自身为参 |
| B059 | ✅ | 无 Collections.nCopies(char) |
| B060 | ✅ | 无 null 三目拆箱 |
| B061 | ✅ | 无 sun.misc Base64 |
| B062 | ✅ | 无 URLClassLoader 强转 |
| B063 | ✅ | 无 javax.xml.bind |
| B064 | ✅ | 无 Optional == |
| B065 | ✅ | 无 Pojo 自赋值 |
| B066 | ✅ | 无 (int)Math.random |
| B067 | ✅ | 无 nextInt()% |
| B068 | ✅ | 无变量自赋值 |
| B069 | ✅ | 无 compareTo 自身 |
| B070 | ✅ | 无 equals 自身 |
| B071 | ✅ | 无 size()>=0 |
| B072 | ✅ | 无 Stream.toString |
| B073 | ✅ | 无 new StringBuilder(char) |
| B074 | ✅ | 无 substring(0) |
| B075 | ✅ | 无矛盾 for 循环 |
| B076 | ✅ | 无 @Transactional（单条 insert） |
| B077 | ✅ | 无 catch Throwable 吞 AssertionError |
| B078 | ✅ | 无 assertThat(x).isEqualTo(x) |
| B079 | ✅ | @Mock 无显式赋值 |
| B080 | ✅ | 单测均含断言（assertThat / andExpect） |
| B081 | ✅ | 无新建集合 sort 未用 |
| M001 | ✅ | 无重复条件分支 |
| M002 | ✅ | 无子类 instanceof 父类 |
| M003 | ✅ | 无 new 包装类构造器 |
| M004 | ✅ | 无 printStackTrace |
| M005 | ✅ | 无内部类 |
| M006 | ✅ | 无编译期常量布尔 |
| M007 | ✅ | 无空 catch |
| M008 | ✅ | 无重写 equals/hashCode |
| M009 | ✅ | 无不兼容类型 equals |
| M010 | ✅ | 无位运算恒 0 |
| M011 | ✅ | 无 switch |
| M012 | ✅ | 无 finally return |
| M013 | ✅ | 无 float 强转 |
| M014 | ✅ | 无枚举 getClass |
| M015 | ✅ | 无字段隐藏 |
| M016 | ⚠️ | **脚本命中 P1**：`LocalDateTime.now()` 使用系统默认时区 → `TodoItemServiceImpl.java:52` |
| M017 | ✅ | 测试方法均带 @Test |
| M018 | ✅ | 无 lock |
| M019 | ✅ | 无枚举 switch |
| M020 | ✅ | 重写方法均带 @Override |
| M021 | ✅ | 无 equals(特定类型) |
| M022 | ✅ | 无 Optional.of(null) |
| M023 | ✅ | POJO 均实现 toString |
| M024 | ✅ | 无 Optional.get |
| M025 | ✅ | 无 final 类 protected |
| M026 | ✅ | @Mock 非 static |
| M027 | ✅ | 无 ThreadLocal |
| I001 | ✅ | 异常断言含 errorCode/message，符合最佳实践 |
| I002 | ✅ | 无 @DoNotMock |
| I003 | ✅ | 无 @AutoValue |
| I004 | ✅ | 无 java.util.Date（用 LocalDateTime） |
| I005 | ✅ | JUnit5 |
| I006 | ✅ | 无 setUp |
| I007 | ✅ | 无 tearDown |
| I008 | ✅ | 无 dataProvider |
| I009 | ✅ | 统计项，忽略 |
| I010 | ✅ | 未用容器型 Runner |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 单表插入，无先读后写 |
| G1.2 | N/A | 无加锁后更新 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁顺序 |
| G2.1 | N/A | 内部待办新增无业务单号，重复创建业务上可接受；如需防重可后续加唯一约束 |
| G2.2 | N/A | 无重试/定时/MQ |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 单库单表，无分布式事务 |
| G3.2 | N/A | 无显式 @Transactional 含外部 I/O |
| G4.1 | ✅ | SQL 极简，无多层子查询 |
| G4.2 | ✅ | 无索引列隐式转换；`map-underscore-to-camel-case` 正常 |
| G4.3 | ✅ | 无列表查询 |
| G4.4 | N/A | 清单未定义该项（模板扩展） |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 异常路径均有 log（warn/error），未吞异常 |
| G8.2 | N/A | 最小闭环，无外部强依赖编排 |
| G8.3 | N/A | 无手写 I/O 流/锁（DB 连接由 MyBatis 托管） |
| G8.4 | N/A | 无自建线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 线程池 |
| G8.7 | N/A | 清单未定义该项 |
| G9.1 | N/A | DB 连接由 starter 连接池托管，无手写调用 |
| G9.2 | N/A | 同上 |
| G9.3 | N/A | 无重试逻辑 |
| G10.1 | ✅ | `description` null 表示「无描述」，无「系统异常」双重语义 |
| G10.2 | N/A | 首次交付，无版本兼容问题 |
| G10.3 | N/A | 清单未定义该项 |
| G11.1 | ✅ | 两测试类范式齐全，断言充分 |
| G11.2 | ⚠️ | **P2**：未覆盖最大值边界——`title`/`description` 无 `@Size` 长度校验（`TodoItemCreateRequest.java:14-18`），超长落库将触发 DB `Data too long` |
| G11.3 | ✅ | `@NotBlank` + service 层 `title.trim()` 二次防御，空值已防护 |
| G11.4 | N/A | 无数值运算 |
| G12.1 | N/A | 无资金相关 |
| G12.2 | N/A | 无资损场景 |
| G13.1 | ✅ | 业务异常 warn、系统异常 error、成功 info，级别正确 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️ | **P1**（与 M016 合并）：应用侧 `LocalDateTime.now()`（JVM 默认时区）与 `jdbcUrl serverTimezone=UTC` 时区基准不一致，跨时区部署存在偏差风险 |
| G14.4 | N/A | 无 SimpleDateFormat/DateTimeFormatter |
| G15.1 | ✅ | `CREATE TABLE IF NOT EXISTS`，新增表，兼容 |
| G15.2 | N/A | 无新旧接口共存 |
| G15.3 | N/A | 无不兼容逻辑 |
| G16.1 | N/A | 最小闭环未要求埋点；已有成功日志（id/title） |
| G16.2 | ✅ | 异常路径均记录日志（warn 含 errorCode/message，error 含堆栈） |
| G16.3 | ✅ | 级别正确 |
| G16.4 | ✅ | 无空 catch / printStackTrace / 异常后静默继续 |
| G17.1 | N/A | 无功能开关（最小闭环） |
| G17.2 | N/A | 无降级预案需求 |
| G17.3 | N/A | 无破坏性数据变更 |
| G18.1 | N/A | 清单未定义（G18 非 reliability-checklist.md 条目） |
| G18.2 | N/A | 清单未定义 |
| G18.3 | N/A | 清单未定义 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | `TodoItemMapper.xml:17` 全用 `#{}`，脚本 S1.1 未命中 `${}` |
| S1.2 | N/A | 无 order by/动态表名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | N/A | 纯 JSON API，无 HTML/JS 渲染；回显字段防 XSS 由前端转义 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 处理 |
| S3.3 | N/A | 无外部 HTTP 调用 |
| S4.1 | ✅ | 无 Runtime.exec/ProcessBuilder（脚本未命中） |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | MyBatis XML 静态，无 XML 外部实体解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无 ObjectInputStream（脚本未命中） |
| S6.2 | N/A | 无 @JsonTypeInfo 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件路径处理 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | N/A | 需求未要求鉴权（内部用户），依赖上游/网关；建议接入鉴权后复核 |
| S8.2 | ✅ | 使用 POST 创建，非 GET |
| S8.3 | N/A | 自增 ID 内部系统可接受 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | `application.yml:5` 密码用 `${DB_PASSWORD:}` 环境变量，无硬编码（脚本未命中） |
| S9.2 | ✅ | 日志仅 id/title，无敏感信息 |
| S9.3 | N/A | 内网，无传输加密要求 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 内部 API，无 CSRF 场景 |
| S10.2 | N/A | 无 CORS 通配符配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(未启用自定义规则) | `customized-checklist.md` 仅含示例项，无项目自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列每个文件 Step2、Step3、G/S 均非 `⬜`（跳过文件除外）
- [x] Step 2 每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081 / M001–M027 / I001–I010 ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，含 `ID + path:line`