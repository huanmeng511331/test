# Code Review Checklist

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-.../12bcc56` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

---

## Step 1 — 执行队列（产物 A）

> 审查范围：`git diff --name-only 65c462d..HEAD`，筛选 `.java` 共 13 个；`.yml/.sql/.xml/.md` 标 `跳过(非 Java)` 但作为契约/数据上下文，SQL 注入由 `scan-all-rules.sh`（S1.1）覆盖。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | src/main/java/com/dtazza/todo/TodoApplication.java | 启动入口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | src/main/java/com/dtazza/todo/api/controller/GlobalExceptionHandler.java | 异常→错误响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 3 | src/main/java/com/dtazza/todo/api/controller/TodoController.java | REST 创建入口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ 已审有问题 |
| 4 | src/main/java/com/dtazza/todo/api/request/CreateTodoRequest.java | 请求校验 DTO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | src/main/java/com/dtazza/todo/common/exception/BusinessException.java | 业务异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 6 | src/main/java/com/dtazza/todo/common/model/ApiResponse.java | 统一成功响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 7 | src/main/java/com/dtazza/todo/common/model/ErrorResponse.java | 统一错误响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 8 | src/main/java/com/dtazza/todo/dao/entity/TodoDO.java | 数据对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 9 | src/main/java/com/dtazza/todo/dao/mapper/TodoMapper.java | 数据访问接口 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 10 | src/main/java/com/dtazza/todo/model/vo/TodoVO.java | 展示对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 11 | src/main/java/com/dtazza/todo/service/TodoService.java | 服务接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 12 | src/main/java/com/dtazza/todo/service/impl/TodoServiceImpl.java | 业务编排 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 13 | src/test/java/com/dtazza/todo/service/impl/TodoServiceImplTest.java | 单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| - | src/main/resources/mapper/TodoMapper.xml | SQL 映射（非 Java） | 跳过(非Java) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | ✅(#{} 预编译) | — | — | — | — | — | — | — | — | — | 跳过(非Java,S1.1 已扫) |
| - | src/main/resources/db/schema.sql | 建表（非 Java） | 跳过(非Java) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 跳过(非Java) |
| - | src/main/resources/application.yml | 配置（非 Java） | 跳过(非Java) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | ⚠️ | — | 跳过(非Java) |
| - | pom.xml / 文档 | 构建与文档（非 Java） | 跳过(非Java) | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 跳过(非Java) |

---

## Step 2 — 功能（产物 B）

> REQ 仅源自需求原文与 `docs/modules/todo/README.md`。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 新增待办事项 | Given 合法标题/描述 When POST /todos Then 落库并返回创建对象 | 需求「核心功能：新增待办事项」；README「POST /todos 新增待办事项」| TodoController.java / TodoServiceImpl.java / TodoMapper.java / TodoMapper.xml | ✅ | TodoController.java:32-34；TodoServiceImpl.java:27-46；TodoMapper.xml:14-18（useGeneratedKeys 回填 id） |
| REQ-2 事项名称必填 | Given 名称为空/空白 When 创建 Then 拒绝并提示 | 需求「任务信息：事项名称」；README「title 必填」| CreateTodoRequest.java / TodoServiceImpl.java | ✅ | CreateTodoRequest.java:15-16 `@NotBlank`；TodoServiceImpl.java:29-32 二次校验（trimToNull） |
| REQ-3 事项描述选填 | Given 描述为空/null When 创建 Then 接受为 '' | 需求「任务信息：…和描述」；README「description 选填」| CreateTodoRequest.java / TodoServiceImpl.java | ✅ | CreateTodoRequest.java:19-20 `@Size(max=2000)`；TodoServiceImpl.java:33 null→"" |
| REQ-4 最小闭环仅创建 | Given 无列表/完成/删除需求 When 交付 Then 无多余接口 | 需求「最小闭环：仅创建」 | 全部 Controller/Service/Mapper | ✅ | TodoController.java 仅 `@PostMapping`（无 GET/PUT/DELETE） |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=顶层类名、UTF-8、无 Tab（scan A1.3 无命中） |
| A2 | 源文件结构/import 顺序 | ⚠️ | A2.4：`TodoMapper.java:3-5` org.apache.* 在 com.dtazza.* 前；且非静态 import 被空行拆成两组（A2.3）。`CreateTodoRequest.java:3-6` java 与 jakarta 非静态 import 间空行拆分 |
| A3 | 代码样式 | ✅ | 无超长行（scan A3.4 无命中）、K&R 大括号、缩进一致 |
| A4 | 命名规范 | ✅ | 类大驼峰、方法小驼峰、常量 UPPER_SNAKE、包全小写 |
| A5 | 编码实践 | ✅ | `@Override` 齐全；无空 catch；无实例调用静态方法 |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确；long 无字面量；无数组/Switch 特殊问题 |
| A7 | Javadoc 规范 | ✅ | public 类均有 Javadoc；getter/setter 属自解释可省略 |

---

## Step 4 — 可靠性检查（产物 D）

> 预扫：`scan-all-rules.sh` 对 13 个 Java + 1 xml + 1 sql 全量扫描，**52/222 条规则无命中**。以下为 LLM 对脚本未覆盖项及误报/漏报复核。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> Blocker→P0、Major→P1、Info→P2。无命中=✅；代码不涉及该模式=N/A。

| ID | 状态 | 备注 | ID | 状态 | 备注 | ID | 状态 | 备注 |
|----|------|------|----|------|------|----|------|------|
| B001 | N/A | 无字面量 parse | B028 | N/A | 无 commons DateUtil | B055 | N/A | 无 Mockito when 裸调用 |
| B002 | N/A | 无数组 equals | B029 | N/A | 未命中 | B056 | ✅ | 无 (预扫) |
| B003 | N/A | 无 Arrays.fill | B030 | N/A | 无浮点 == | B057 | N/A | 无增强 for 内删除 |
| B004 | N/A | 无数组 toString | B031 | ✅ | 无 String.format 错位 | B058 | N/A | 无集合自参 |
| B005 | ✅ | 无 (预扫) | B032 | N/A | 无注解 getClass | B059 | ✅ | 无 (预扫) |
| B006 | ✅ | assertEquals 顺序正确（AssertJ） | B033 | N/A | 无 Unsafe | B060 | N/A | 无三目拆箱 |
| B007 | N/A | 无 catch Throwable | B034 | N/A | 无 Hashtable | B061 | ✅ | 无 (预扫) |
| B008 | ✅ | 无 (预扫) | B035 | N/A | 无恒等二元 | B062 | ✅ | 无 (预扫) |
| B009 | N/A | 无移位 | B036 | ✅ | 无 (预扫) | B063 | ✅ | 无 (预扫) |
| B010 | ✅ | 无 (预扫) | B037 | N/A | 无变参三目 | B064 | N/A | 无 Optional == |
| B011 | N/A | 无装箱 == | B038 | N/A | 无递归 | B065 | N/A | 无自赋值 |
| B012 | ✅ | 无 (预扫) | B039 | N/A | 无 indexOf 颠倒 | B066 | ✅ | 无 (预扫) |
| B013 | ✅ | 无 (预扫) | B040 | N/A | 无 isInstance | B067 | ✅ | 无 (预扫) |
| B014 | N/A | 无集合类型错配 | B041 | N/A | 无 JDBC 手写 | B068 | N/A | 无自赋值 |
| B015 | N/A | 无 toArray | B042 | ✅ | JUnit5，无 JUnit3 | B069 | N/A | 无 compareTo 自比较 |
| B016 | N/A | 无 Comparable | B043 | ✅ | 无内部类 @Test | B070 | N/A | 无 equal 自比较 |
| B017 | ✅ | 无 (预扫) | B044 | ✅ | 未混用 JUnit3/4 | B071 | ✅ | 无 (预扫) |
| B018 | N/A | 无三目数值提升 | B045 | N/A | 无装箱锁 | B072 | N/A | 无 Stream.toString |
| B019 | N/A | 无 Money | B046 | N/A | 无死循环 | B073 | ✅ | 无 (预扫) |
| B020 | N/A | 无常量溢出 | B047 | N/A | 无 compare 精度 | B074 | ✅ | 无 (预扫) |
| B021 | N/A | 无 Jedis | B048 | N/A | 无 Math.round 整型 | B075 | N/A | 无可疑 for |
| B022 | ✅ | 无 (预扫) | B049 | ✅ | 无 (预扫) | B076 | ✅ | 无 (预扫) |
| B023 | ✅ | 无 (预扫) | B050 | N/A | 无日期格式串 | B077 | N/A | 测试未 catch Throwable |
| B024 | N/A | 无 Thread | B051 | ✅ | 无 (预扫) | B078 | ✅ | assertThat 对象各异 |
| B025 | N/A | 无双括号初始化 | B052 | ✅ | 无 (预扫) | B079 | ✅ | @Mock 未显式赋值 |
| B026 | ✅ | 无 (预扫) | B053 | ✅ | 期望异常用 assertThatThrownBy | B080 | ✅ | 测试均含断言 |
| B027 | N/A | 无 equals 实现 | B054 | N/A | 无 EqualsTester | B081 | N/A | 无 in-place 排序 |
| M001 | N/A | 无重复条件 | M010 | N/A | 无位运算 | M019 | N/A | 无枚举 switch |
| M002 | N/A | 无 instanceof 父子 | M011 | N/A | 无 switch fall-through | M020 | ✅ | @Override 齐全 |
| M003 | ✅ | 无 (预扫) | M012 | N/A | 无 finally return | M021 | N/A | 无 equals 重写 |
| M004 | ✅ | 无 (预扫) | M013 | N/A | 无浮点强转 | M022 | ✅ | 无 (预扫) |
| M005 | N/A | 无内部类 | M014 | N/A | 无枚举 getClass | M023 | N/A | 无重写 toString 问题 |
| M006 | N/A | 无编译期布尔 | M015 | N/A | 无父子字段隐藏 | M024 | N/A | 无 Optional.get |
| M007 | ✅ | 无 (预扫) | M016 | ✅ | 未用 LocalDateTime.now() 默认时区 | M025 | N/A | 无 final 类 |
| M008 | N/A | 未重写 equals/hashCode | M017 | ✅ | 测试均有 @Test | M026 | ✅ | @Mock 非 static |
| M009 | N/A | 无异构 equals | M018 | N/A | 无显式锁 | M027 | ✅ | 无 (预扫) |
| I001 | N/A | 未用 @Test(expected) | I004 | ⚠️ | `TodoDO.java:4,19,21` 用 `java.util.Date`，建议 Instant/LocalDateTime | I008 | ✅ | 无 dataProvider |
| I002 | N/A | 无 DoNotMock | I005 | ✅ | JUnit5 | I009 | ✅ | 测试方法均已识别 |
| I003 | N/A | 无 AutoValue mock | I006 | ✅ | @BeforeEach 规范 | I010 | ✅ | 纯单元测试，未启动容器 |
| I007 | N/A | 无 tearDown | — | — | — | — | — | — |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 | ID | 状态 | 备注 |
|----|------|------|----|------|------|
| G1.1 | N/A | 无先读后写/事务并发场景 | G8.7 | N/A | 无 I/O/连接/锁资源 |
| G1.2 | N/A | 无锁 | G9.1 | N/A | 无外部调用 |
| G1.3 | N/A | 无乐观锁 | G9.2 | N/A | 无外部调用超时 |
| G1.4 | N/A | 无多资源加锁 | G9.3 | N/A | 无重试 |
| G2.1 | N/A(单次创建无重试/幂等要求) | 纯 INSERT 幂等性由业务后续决定 | G10.1 | N/A | 无 null 双语义字段 |
| G2.2 | N/A | 无定时/MQ 重投 | G10.2 | N/A | 新接口，无升级兼容 |
| G2.3 | N/A | 无幂等键约定 | G11.1 | ✅ | 5 个单测均有断言 |
| G3.1 | N/A | 无分布式事务 | G11.2 | ✅ | 覆盖空值/空白/首尾空格/0行分支 |
| G3.2 | N/A | 单条 INSERT 无 @Transactional，无外部 I/O | G11.3 | ⚠️ | Service `request` 入参无 null 防御（Controller 层 @RequestBody 保证非空），见 TodoServiceImpl.java:29 |
| G4.1 | N/A | 无复杂 SQL | G11.4 | N/A | 无数值/金额运算 |
| G4.2 | N/A | WHERE 无函数/隐式转换（当前仅 INSERT） | G12.1 | N/A | 无资金/库存场景 |
| G4.3 | N/A | 无列表查询 | G12.2 | N/A | 无资金止血需求 |
| G5.1 | N/A | 无 MQ | G13.1 | ✅ | 日志级别正确（业务 WARN/系统 ERROR/成功 INFO） |
| G6.1 | N/A | 无缓存 | G14.1 | N/A | 无金额 |
| G6.2 | N/A | 无缓存 | G14.2 | N/A | 单租户，无 tenantId |
| G7.1 | N/A | 无调度任务 | G14.3 | N/A | 时间由 DB DEFAULT CURRENT_TIMESTAMP |
| G7.2 | N/A | 无调度任务 | G14.4 | N/A | 无手工时区解析 |
| G8.1 | ✅ | 异常路径均有抛出/日志，未吞异常 | G15.1 | ✅ | 新建表（非 ALTER），无破坏性 DDL |
| G8.2 | N/A | 无强依赖 | G15.2 | N/A | 新接口 |
| G8.3 | N/A | 无 I/O 资源 | G15.3 | N/A | 无新老逻辑切换 |
| G8.4 | N/A | 无线程池 | G16.1 | N/A | 未埋成功率/耗时指标（P2 建议，非阻塞） |
| G8.5 | N/A | 无 ThreadLocal | G16.2 | ⚠️ | 异常/错误响应缺 traceId/requestId 上下文，跨系统排障定位困难 |
| G8.6 | N/A | 无 Executors 线程池 | G16.3 | ⚠️ | 系统级 DB 写失败（insert 0 行）走 BusinessException→HTTP 400，级别/契约错位（详见 §7.1） |
| G16.4 | ✅ | 无空 catch / printStackTrace | G17.1 | N/A | 无功能开关（仅创建，P2 建议） |
| G17.2 | N/A | 无降级 | G17.3 | N/A | 仅新增数据，无破坏性变更 |
| G18.1 | N/A | 见 S 节 | G18.2 | N/A | 见 S 节 |
| G18.3 | N/A | 见 S 节 | — | — | — |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 | ID | 状态 | 备注 |
|----|------|------|----|------|------|
| S1.1 | ✅ | `#{title}`/`#{description}` 预编译（预扫 XML 无 `${}`） | S6.1 | N/A | 无反序列化 |
| S1.2 | N/A | 无 order by/动态表名 | S6.2 | N/A | 无 JSON 多态 |
| S1.3 | N/A | 无 like/in 查询 | S6.3 | N/A | 无敏感字段 transient 需求 |
| S2.1 | N/A | 无 HTML/JS 输出（JSON API） | S7.1 | N/A | 无文件上传 |
| S2.2 | N/A | 无富文本 | S7.2 | N/A | 无文件下载 |
| S2.3 | N/A | 无模板引擎 | S7.3 | N/A | 无文件存储 |
| S3.1 | N/A | 无外部 URL 请求 | S8.1 | ⚠️ | 接口未接入鉴权/网关（内部系统，建议后续加访问控制） |
| S3.2 | N/A | 无 302 | S8.2 | ✅ | 创建用 POST，未用 GET 增删改 |
| S3.3 | N/A | 无外部 URL | S8.3 | N/A | 仅创建，无按 id 查询暴露；id 为自增（后续列表/详情需注意） |
| S4.1 | ✅ | 无 (预扫，无命令执行) | S8.4 | N/A | 无 Cookie |
| S4.2 | N/A | 无文件操作 | S9.1 | ⚠️ | `application.yml:8` `${DB_PASSWORD:todo}` 明文默认值（可被环境变量覆盖，建议去除默认值） |
| S5.1 | N/A | 无 XML 解析 | S9.2 | ✅ | 日志未打印密码/令牌 |
| S5.2 | N/A | 无 XPath | S9.3 | N/A | 内网 HTTP（网关外无 TLS 配置，P2 建议） |
| S9.4 | N/A | 无随机数 | S10.1 | N/A | 无表单/Cookie 会话 |
| S10.2 | N/A | 无 CORS 配置 | S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> `customized-checklist.md` 仅含示例项，无团队/项目私有规则。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(未启用自定义规则) | 示例项；CreateTodoRequest 已使用 `@Valid` + Jakarta 校验注解（符合示例精神） |
| U1.2 | N/A | 无 |
| U1.3 | N/A | 无 |
| U2.1 | N/A | 无业务红线 |
| U2.2 | N/A | 无 |
| U2.3 | N/A | 无 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（N/A 均已注原由）
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`