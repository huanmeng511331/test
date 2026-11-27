# Code Review Checklist — todo 新增待办

> **Change** `待办事项——新增待办(底座变更,最小闭环仅创建)` · **分支/Commit** `task/DEV-f025f710…` / `5423973` · **日期** `2026-11-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项已从 `⬜` 变为其他状态；`N/A` 已写原因。
> **产出目录** `{OUTPUT_DIR}` = `.agents/20261127-底座变更_目标_帮助用户记录日常待办事项/`（用户指定 `cr_report.md`，checklist 按技能命名随附同目录）。

---

## 自动化预扫输出（scan-all-rules.sh，强制先行）

```text
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java/com/dtazziboot/base src/test/java/com/dtazziboot/base
Engine:  ripgrep

[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtazziboot/base/common/config/GmtFieldsMetaObjectHandler.java:16
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtazziboot/base/common/config/GmtFieldsMetaObjectHandler.java:23
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtazziboot/base/todo/service/impl/TodoItemServiceImpl.java:50

=== Summary: 3 findings (P0=0, P1=3, P2=0) | 52/222 rules scanned ===
```

复核结论：M016 ×3 确认为真阳性（另见 G14.3/G14.4 关联风险：JDBC `serverTimezone=Asia/Shanghai` vs JVM 默认时区，宿主时区为 UTC+0，存在 8 小时偏差风险）。脚本未覆盖规则已由 LLM 按 checklist 逐条补扫，无新增命中。

---

## Step 1 — 执行队列（产物 A）

**列说明**：G1–G17 = `reliability-checklist.md` 各节；S1–S10 = `security-checklist.md` 各节；Bug 模式（B/M/I）在 §4.1 按 ID 核销。无关列填 `N/A`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|----|----|----|----|----|----|----|----|----|-----|
| 1 | src/main/java/com/dtazziboot/base/TodoBaseApplication.java | 工程入口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | src/main/java/com/dtazziboot/base/common/api/ApiResult.java | 统一出参(REQ-6) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 3 | src/main/java/com/dtazziboot/base/common/auth/LoginUserContext.java | 登录态上下文(REQ-5) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ |
| 4 | src/main/java/com/dtazziboot/base/common/auth/LoginUserInterceptor.java | 登录拦截(REQ-5) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ |
| 5 | src/main/java/com/dtazziboot/base/common/config/GmtFieldsMetaObjectHandler.java | gmt 自动填充(REQ-7) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ |
| 6 | src/main/java/com/dtazziboot/base/common/config/WebMvcConfig.java | 拦截器注册 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 7 | src/main/java/com/dtazziboot/base/common/constant/CommonErrorCodeConstants.java | 通用错误码 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 8 | src/main/java/com/dtazziboot/base/common/exception/BizException.java | 业务异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 9 | src/main/java/com/dtazziboot/base/common/exception/GlobalExceptionHandler.java | 全局异常(REQ-9) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | ⚠️ | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | ⚠️ |
| 10 | src/main/java/com/dtazziboot/base/todo/config/TodoProperties.java | 应急开关(REQ-10) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ |
| 11 | src/main/java/com/dtazziboot/base/todo/constant/TodoErrorCodeConstants.java | TODO 错误码 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 12 | src/main/java/com/dtazziboot/base/todo/controller/TodoItemController.java | W01 入口(REQ-1~4,10) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | ⚠️ | N/A | N/A | N/A | N/A | ❌ | ✅ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ❌ |
| 13 | src/main/java/com/dtazziboot/base/todo/mapper/TodoItemMapper.java | 数据访问(REQ-7) | ✅ | ✅ | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 14 | src/main/java/com/dtazziboot/base/todo/model/dto/CreateTodoItemRequest.java | 入参 DTO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ |
| 15 | src/main/java/com/dtazziboot/base/todo/model/dto/TodoItemResult.java | 出参 DTO(REQ-6) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 16 | src/main/java/com/dtazziboot/base/todo/model/entity/TodoItemDO.java | 实体(REQ-8) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 17 | src/main/java/com/dtazziboot/base/todo/service/TodoItemService.java | 服务接口(S01) | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 18 | src/main/java/com/dtazziboot/base/todo/service/impl/TodoItemServiceImpl.java | 服务实现(REQ-5,7,9) | ✅ | ✅ | N/A | ✅ | ✅ | N/A | N/A | N/A | N/A | ✅ | ❌ | ✅ | ⚠️ | N/A | N/A | ❌ | N/A | ❌ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ |
| 19 | src/test/java/com/dtazziboot/base/todo/controller/TodoItemControllerTest.java | 控制器切片测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ |
| 20 | src/test/java/com/dtazziboot/base/todo/service/impl/TodoItemServiceImplTest.java | 服务单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 21 | .gitignore | 构建 | 跳过 | N/A(非 Java,风格核对 ✅) |
| 22 | SSOT.md | 文档 | 跳过 | N/A(非 Java) |
| 23 | docs/ARCHITECTURE.md | 文档 | 跳过 | N/A(非 Java) |
| 24 | docs/modules/todo/README.md | 文档 | 跳过 | N/A(非 Java) |
| 25 | pom.xml | 构建(证据引用) | 跳过 | ⚠️ P2-5(starter-validation 未使用) |
| 26 | src/main/resources/application.yml | 配置(证据引用:G9.2/G17.1/S9.1) | 跳过 | ⚠️(发现项证据) |
| 27 | src/main/resources/db/init/todo_item.sql | DDL(REQ-8 证据) | 跳过 | ✅(REQ-8 逐字段核对通过) |
| 28 | .agents/…/impl.md | 编码报告 | 跳过 | N/A(非 Java) |

**Java 守卫**：存在 20 个 `.java` 文件，继续审查。
**收口**：全部队列项均非 `⬜`；跳过项为非 Java 文件。

---

## Step 2 — 功能（产物 B）

REQ 均摘自 `design.md`（系分 v1.0），未发明范围外功能点。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | W01 接口:POST /api/todo/items,入参 name(必填)/description(选填) | design §4.1/§5.1.2「URI: POST /api/todo/items」 | TodoItemController.java | ✅ | Controller:20,37-44;CreateTodoItemRequest:14-17;测试 should_returnOk |
| REQ-2 | R01 名称必填,trim 后非空,否则 TODO_001 | design §5.1.3 R01 | TodoItemController.java | ⚠️ | Controller:50-53 实现正确;测试 should_returnTodo001 因 P0-1(advice 未注册)当前不可通过 |
| REQ-3 | R02 名称 trim 后 ≤64,否则 TODO_002 | design §5.1.3 R02 | TodoItemController.java | ⚠️ | Controller:54-56 实现正确;测试 should_returnTodo002 受 P0-1 影响 |
| REQ-4 | R03 描述选填 ≤500,否则 TODO_003 | design §5.1.3 R03 | TodoItemController.java | ⚠️ | Controller:57-60 实现正确;测试 should_returnTodo003 受 P0-1 影响 |
| REQ-5 | R04 创建人取登录态,服务端注入,无登录态 AUTH_001 不落库 | design §5.1.3 R04/§6.4.2.3 | LoginUserInterceptor.java, LoginUserContext.java, TodoItemServiceImpl.java | ✅ | Interceptor:32-42(/api/** 无白名单);Context:31-37(AUTH_001);ServiceImpl:38;ServiceImplTest should_throwAuthException(verify never insert) |
| REQ-6 | 出参 {code,msg,data}:data.id/name(trim 回显)/creator/gmtCreate(yyyy-MM-dd HH:mm:ss) | design §5.1.2 出参表 | ApiResult.java, TodoItemResult.java, TodoItemServiceImpl.java | ✅ | ApiResult:29-35(OK/SUCCESS);Result:16-25;ServiceImpl:52-54;ControllerTest:55-60(happy path) |
| REQ-7 | 落库组装:tenant_id=0、modifier=creator、is_deleted=0、描述缺省空串、name trim | design §5.1.1.1/§5.1.3.1 时序 | TodoItemServiceImpl.java, GmtFieldsMetaObjectHandler.java, TodoItemDO.java | ✅ | ServiceImpl:36-47;Handler:15-19(自动填充);ServiceImplTest 3 用例(合法/trim/缺省) |
| REQ-8 | 表 todo_item 与设计 5.1.1.1 逐字段一致(自增主键/datetime/utf8mb4/无外键 enum) | design §5.1.1.1 | db/init/todo_item.sql, TodoItemDO.java | ✅ | SQL:3-14 逐字段核对一致;DO 映射一致(注:MySQL 主键约束名固定 PRIMARY,`pk_todo_item` 为设计描述性命名,不构成违规) |
| REQ-9 | DB 插入失败→记录日志,返回 SYS_999,可重试 | design §5.1.3 异常场景/§6.1 | GlobalExceptionHandler.java, TodoItemServiceImpl.java | ✅ | ServiceImpl:47 异常上抛;Handler:30-34 ERROR 含堆栈+SYS_999;ServiceImplTest should_propagateException(断言上抛;Handler 转换未单测,见 P2-7 注记) |
| REQ-10 | 应急开关关闭→返回「功能维护中」,不进入业务 | design §7.3(错误码未定义,TODO_004 为已记录扩展) | TodoItemController.java, TodoProperties.java | ⚠️ | Controller:39-41 实现正确;测试 should_returnMaintenance 受 P0-1 影响;开关动态性缺口见 G17.1(P0-4) |

实现与设计的两处**良性偏差**（已核，不构成不符合）：
1. 创建人获取时点：设计时序图为 Controller 取用户后调 Service，实现在 Service 内 `requireCurrentUser()`（ServiceImpl:38）。行为等价（拦截器已在 /api/** 前置拦截，Service 兜底 AUTH_001），满足 R04 语义。
2. DB 失败日志级别：设计写 WARN，实现按军规 G16.3 用 ERROR（Handler:32）。实现更规范，予以采纳。

---

## Step 3 — 可读性检查（产物 C）

对照 `readability-checklist.md` A1–A7（20 个 Java 文件全文核对）：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 全部 UTF-8、空格缩进、无 Tab |
| A2 | 源文件结构/import | ✅ | 无通配符 import;static/非 static 分组、组内字典序(含 jakarta<java 校验);成员间空行 |
| A3 | 代码样式 | ✅ | K&R 大括号、4 空格缩进、续行 ≥8 空格(Interceptor:46-47)、行宽 ≤120、关键字后空格 |
| A4 | 命名规范 | ⚠️ | 基本合规(常量 UPPER、DO/DTO/Test 后缀、无 is 前缀布尔);**P2-3**:ServiceImpl:24 `GMT_FORMATTER` 命名误导——实际按 JVM 默认时区格式化而非 GMT,随 P1-1 一并改名 |
| A5 | 编码实践 | ✅ | 重写方法均带 @Override;无空 catch;静态方法经类名调用;无 finalize |
| A6 | 特定元素样式 | ✅ | 注解每行一个;long 字面量大写 L(0L);无数组 C 风格/switch |
| A7 | Javadoc 规范 | ✅ | public 类/成员均有 Javadoc;setCurrentUser/clear 等自解释简单方法按 A7.3 豁免 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`，120 条逐条核销）

> 预扫：`scan-all-rules.sh` 命中 M016×3；其余由 LLM 按清单逐条补扫。等级映射 Blocker→P0、Major→P1、Info→P2。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无字面量 parse/of 调用 |
| B002 | N/A | 无数组比较 |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | N/A | 无 Arrays.asList |
| B006 | ✅ | 未用 JUnit assertEquals,统一 AssertJ(isEqualTo 语义方向安全) |
| B007 | ✅ | 无 catch Throwable/Error |
| B008 | N/A | 无 Executors 线程池 |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 BigDecimal |
| B011 | ✅ | 无包装类型 ==(getGmtCreate()==null 为引用判空,合规;AssertJ 处理装箱比较) |
| B012 | N/A | 无 Calendar |
| B013 | N/A | 无 Calendar |
| B014 | N/A | 无集合泛型查询 |
| B015 | N/A | 无 toArray(T[]) |
| B016 | N/A | 无 Comparable 实现 |
| B017 | N/A | 无 this==null |
| B018 | N/A | 无数值三目混型 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无常量乘法链 |
| B021 | N/A | 无 Jedis |
| B022 | ✅ | 使用线程安全 DateTimeFormatter 静态常量(ServiceImpl:24) |
| B023 | ✅ | 无未抛出的异常实例 |
| B024 | N/A | 无裸 Thread |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 equals(null) |
| B027 | N/A | 无 equals 重写 |
| B028 | N/A | 无 httpclient DateUtil |
| B029 | ✅ | 字段组装正确(setName→name 等,ServiceImpl:41-46) |
| B030 | N/A | 无浮点比较 |
| B031 | ✅ | 日志占位符与参数数量逐处一致(ServiceImpl:52-53;Handler:20,26,32) |
| B032 | N/A | 无注解实例 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无同对象二元运算 |
| B036 | N/A | 无 IdentityHashMap |
| B037 | N/A | 无 varargs 条件混用 |
| B038 | N/A | 无递归 |
| B039 | N/A | 无 indexOf |
| B040 | N/A | 无 isInstance |
| B041 | N/A | 无手动 JDBC 连接(连接池托管) |
| B042 | ✅ | JUnit5,无 JUnit3 方法 |
| B043 | ✅ | 无内部类 @Test |
| B044 | ✅ | 无 JUnit3/4 混用 |
| B045 | N/A | 无包装类型锁 |
| B046 | N/A | 无 while 循环 |
| B047 | N/A | 无数值 compare 转换 |
| B048 | N/A | 无 Math.round 整型 |
| B049 | ✅ | 格式串无 DD(yyyy-MM-dd HH:mm:ss) |
| B050 | ✅ | HH 24 小时制且未配 a |
| B051 | N/A | 无 Boolean.getBoolean |
| B052 | ✅ | 使用 yyyy 非 YYYY |
| B053 | ✅ | 异常用例用 assertThatThrownBy/catchThrowableOfType,无需 fail 补位 |
| B054 | N/A | 无 EqualsTester |
| B055 | ✅ | when 均接 thenReturn/thenAnswer;verify 无嵌套调用(Test:59-64,76,90,98,114,131,138) |
| B056 | N/A | 无 Arrays.asList 可变列表操作 |
| B057 | N/A | 无增强 for 改集合 |
| B058 | N/A | 无集合自参 |
| B059 | N/A | 无 nCopies |
| B060 | N/A | 无拆箱三目 |
| B061 | N/A | 无 sun.misc.BASE64 |
| B062 | N/A | 无 ClassLoader 强转 |
| B063 | N/A | 无 javax.xml.bind |
| B064 | N/A | 无 Optional 比较 |
| B065 | ✅ | 无 Pojo 自赋值 |
| B066 | N/A | 无 Math.random |
| B067 | N/A | 无 Random 取余 |
| B068 | ✅ | 无变量自赋值 |
| B069 | N/A | 无 compareTo |
| B070 | N/A | 无自身 equals |
| B071 | N/A | 无 size()>=0 |
| B072 | N/A | 无 Stream.toString |
| B073 | N/A | 无 StringBuilder(char) |
| B074 | N/A | 无 substring(0) |
| B075 | N/A | 无可疑 for |
| B076 | ✅ | 未用 @Transactional(单条语句级,与设计事务说明一致) |
| B077 | ✅ | 测试无 catch Throwable |
| B078 | ✅ | 断言对象非同一引用 |
| B079 | ✅ | @Mock 字段无显式赋值;ControllerTest 用 mock() 工厂合规 |
| B080 | ✅ | 每个用例均含 assertThat/andExpect/verify 断言 |
| B081 | N/A | 无新建集合排序 |
| M001 | ✅ | 无重复条件分支 |
| M002 | N/A | 无 instanceof |
| M003 | ✅ | 无包装类构造器 |
| M004 | ✅ | 无 printStackTrace |
| M005 | ✅ | 内部类 TodoProperties.Create 已声明 static |
| M006 | N/A | 无编译期常量布尔 |
| M007 | ✅ | 无空 catch |
| M008 | N/A | 无 equals 重写 |
| M009 | N/A | 无异类型 equals |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally 块 |
| M013 | N/A | 无转换优先级算术 |
| M014 | N/A | 无枚举 |
| M015 | ✅ | 无字段隐藏 |
| M016 | ❌ | **预扫命中×3(真阳性)→P1**:GmtFieldsMetaObjectHandler.java:16,23;TodoItemServiceImpl.java:50 — LocalDateTime.now() 依赖 JVM 默认时区;JDBC serverTimezone=Asia/Shanghai vs 宿主 UTC+0,存在 8h 偏差风险(关联 G14.3) |
| M017 | ✅ | 测试方法均带 @Test |
| M018 | N/A | 无显式 Lock |
| M019 | N/A | 无枚举 switch |
| M020 | ✅ | 重写方法均带 @Override(Handler/Interceptor/ServiceImpl/WebMvcConfig) |
| M021 | N/A | 无 SpecificType equals |
| M022 | N/A | 无 Optional.of(null) |
| M023 | ✅ | 日志输出字段值而非对象 toString |
| M024 | N/A | 无 Optional.get |
| M025 | ✅ | final 工具类(CommonErrorCodeConstants/TodoErrorCodeConstants/LoginUserContext)无 protected 成员 |
| M026 | ✅ | @Mock 均非 static |
| M027 | ✅ | ThreadLocal 为 private static final(LoginUserContext:14),符合要求 |
| I001 | ✅ | 断言含错误码+消息(ServiceImplTest:130;ControllerTest:71-72),优于仅类型断言 |
| I002 | N/A | 无 @DoNotMock 类 |
| I003 | N/A | 无 @AutoValue 类 |
| I004 | ✅ | 统一 java.time,无 java.util.Date |
| I005 | N/A | 无 JUnit3 特性 |
| I006 | ✅ | setUp 带 @BeforeEach(两个测试类) |
| I007 | ✅ | tearDown 带 @AfterEach(ServiceImplTest:47-50) |
| I008 | N/A | 无 dataProvider |
| I009 | ✅ | 统计:测试方法 10 个(ServiceImplTest×5 + ControllerTest×5) |
| I010 | ✅ | 纯 Mockito 单测,无容器启动,分层合理 |

### 4.2 可靠性（`reliability-checklist.md` G1–G17）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 单条 INSERT,无读后写/无锁竞态场景 |
| G1.2 | N/A | 无锁后更新 |
| G1.3 | N/A | 无乐观锁重试 |
| G1.4 | N/A | 无多资源锁 |
| G2.1 | ✅ | 设计 §1.6 A05/§5.1.3/附录14 明确豁免幂等键(最小闭环+前端防重,记录为扩展点);属 spec 决策,不构成违规,风险已在设计中知悉 |
| G2.2 | N/A | 无重试/MQ 重投/定时落库 |
| G2.3 | N/A | 无上游幂等约定 |
| G3.1 | N/A | 无跨库读后写 |
| G3.2 | ✅ | 无 @Transactional;单条语句级原子性与设计 §5.1.3 事务说明一致,事务内无外部 I/O |
| G4.1 | N/A | 无复杂 SQL(通用 INSERT) |
| G4.2 | N/A | 无 WHERE 查询 |
| G4.3 | N/A | 无大列表/深分页 |
| G4.4 | N/A | 模板冗余行,参考清单无此 ID |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 无吞异常:业务 WARN(Handler:20)、参数 WARN(Handler:26)、系统 ERROR 含堆栈(Handler:32) |
| G8.2 | N/A | 无弱依赖调用(唯一下游 MySQL,超时问题见 G9.2) |
| G8.3 | N/A | 无手动 I/O 流/锁,连接池托管 |
| G8.4 | N/A | 无自建线程池/定时器 |
| G8.5 | ✅ | ThreadLocal 在 afterCompletion 中 remove(Interceptor:48),非 set(null);preHandle 返回 false 分支未 set,无泄漏 |
| G8.6 | ✅ | 未使用 Executors 工厂线程池 |
| G8.7 | N/A | 模板冗余行,参考清单无此 ID |
| G9.1 | ✅ | DB 调用单一异常路径,失败上抛转 SYS_999,无三态混淆 |
| G9.2 | ❌ **P0** | DB 调用未设置任何超时:JDBC URL 无 connectTimeout/socketTimeout(application.yml:8),连接池未显式配置;MySQL 驱动默认 connectTimeout=0/socketTimeout=0(无限等待),网络分区可致业务线程永久挂起、池耗尽扩散至全应用。见 P0-2 |
| G9.3 | N/A | 无重试逻辑 |
| G10.1 | ✅ | code/msg 语义明确;fail 时 data=null 为契约约定,不混淆「无数据/异常」 |
| G10.2 | N/A | 纯新增接口,无契约升级 |
| G10.3 | N/A | 模板冗余行,参考清单无此 ID |
| G11.1 | ✅ | 核心逻辑均有带断言单测;注:Controller 负向用例当前因 P0-1 不可通过,修复后证据链闭环 |
| G11.2 | ⚠️ P2 | 边界值(纯空格/65/501/缺省)已覆盖;登录拦截器(AUTH_001 出口)无自动化测试(见 P2-7) |
| G11.3 | ⚠️ P2 | ServiceImpl:36 对 name 无 null 防御,依赖 Controller 前置校验(当前入口唯一,契约内安全;见 P2-2) |
| G11.4 | N/A | 无数值/金额运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | ✅ | 有功能开关+发布回滚预案(设计 §7.3);动态性缺口单列 G17.1 |
| G13.1 | ✅ | 日志级别正确:成功 INFO(ServiceImpl:52)/业务 WARN(Handler:20)/系统 ERROR(Handler:32) |
| G14.1 | N/A | 无金额 |
| G14.2 | ✅ | tenantId 为服务端常量 0(ServiceImpl:27,41),非用户可控参数 |
| G14.3 | ❌ **P1** | 与 M016 合并:datetime 存 JVM 默认时区墙钟;JDBC serverTimezone=Asia/Shanghai 与宿主时区(UTC+00:00)不一致,跨机部署存在 8h 偏差风险。见 P1-1 |
| G14.4 | ⚠️ **P1** | GMT_FORMATTER 格式化未显式时区(ServiceImpl:24,54);当前单区部署自洽,随 P1-1 一并显式化(含改名) |
| G15.1 | ✅ | 纯新增表,无存量表结构变更 |
| G15.2 | ✅ | 纯新增接口,无新旧共存问题 |
| G15.3 | N/A | 无不兼容逻辑切换(新增能力自身有开关) |
| G16.1 | ❌ **P0** | 核心链路无指标埋点(QPS/RT/成功率/错误码计数均缺失),设计 §7.1「服务埋点:todo_item_create 接口埋点,记录…处理耗时;DB 写入失败单独计数」未落地;pom 亦无 micrometer/actuator。见 P0-3 |
| G16.2 | ❌ **P1** | 异常日志无 traceId/链路上下文,设计 §7.1「DB 写失败…携带 traceId」未落地(Handler:20,26,32;ServiceImpl:52)。见 P1-2 |
| G16.3 | ✅ | 级别正确(见 G13.1) |
| G16.4 | ✅ | 无空 catch/printStackTrace;无捕获后继续执行 |
| G17.1 | ❌ **P0** | 功能开关 todo.create.enabled 为静态 application.yml 配置,变更需重启发布,不满足设计 §7.3「动态配置开关…秒级止血」;application.yml:19 注释「可应急秒级止血」与实际能力不符。见 P0-4 |
| G17.2 | ✅ | SYS_999 兜底返回+网关限流(设计 §6.5);开关兜底能力受 G17.1 限制 |
| G17.3 | ✅ | 纯新增表,回滚保留数据,设计 §7.3 已声明,无需订正脚本 |
| G18.1–G18.3 | N/A | 模板冗余行,参考清单无 G18 定义 |

### 4.3 安全（`security-checklist.md` S1–S10）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis-Plus 通用 INSERT 参数化预编译;无手写 SQL、无 ${} 拼接 |
| S1.2 | N/A | 无 order by/group by/动态表名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | ✅ | JSON 出参经 Jackson 序列化,无 HTML/JS 输出面 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无命令执行/文件操作 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1 | ✅ | 请求体绑定具体 DTO(CreateTodoItemRequest),无不可信多态反序列化 |
| S6.2 | ✅ | 无 @JsonTypeInfo |
| S6.3 | N/A | DTO 无敏感字段需 transient |
| S7.1–S7.3 | N/A | 无文件上传下载 |
| S8.1 | ⚠️ P2 | 拦截器统一鉴权、创建人服务端注入,无水平越权面 ✅;但身份完全信任网关头 X-User-Id(Interceptor:32),应用端口若绕过网关可达则可伪造身份——需部署层保障(仅网关可达),见 P2-1 |
| S8.2 | ✅ | 创建操作使用 POST |
| S8.3 | N/A | 无按 ID 资源访问 |
| S8.4 | N/A | 无 Cookie 会话(登录态为网关透传头) |
| S9.1 | ⚠️ P2 | 口令经环境变量注入无硬编码 ✅;application.yml:10 默认空口令+root 仅适用本地,生产需强制注入(见 P2-6) |
| S9.2 | ✅ | 日志不输出 description 全文(仅 nameLength,ServiceImpl:52-53);无口令/token/身份证 |
| S9.3 | ✅ | HTTPS 由网关/SLB 卸载(部署架构 §2.3 覆盖),应用内无 TLS 属预期形态 |
| S9.4 | N/A | 无随机数使用 |
| S10.1 | N/A | 登录态经网关头而非 Cookie 传递,CSRF 攻击面不适用 |
| S10.2 | ✅ | 未配置 CORS,浏览器同源策略默认生效;无 `*` 白名单 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1–U1.3 / U2.1–U2.3 | N/A | N/A(未启用自定义规则):`customized-checklist.md` 仅含示例项(U1.1 标注「示例项」)。备注:若团队未来启用「入参必须 @Valid」类规则,当前 Controller 手动校验(R01~R03,有测试背书)需改造为注解校验 |

---

## 终检（防漏检）

- [x] 执行队列每个文件 Step2/Step3 及 S1–S10/G1–G17 各列均非 `⬜`(跳过文件除外)
- [x] Step 2 的每个 REQ 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081/M001–M027/I001–I010 ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report,且包含 `ID + path:line`

**队列收口核对**：20/20 Java 文件已审(❌×5、⚠️×2、✅×13),8 个非 Java 文件跳过并标注;report §1 文件数与本队列一致。
