# Code Review Checklist

> **Change** `todo 仅创建` · **分支/Commit** `AI/task-DEV-...ae7494f4` / `9b4c3e7` · **日期** `2026-10-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已运行 `references/script/scan-all-rules.sh`（targets: `src/main/java src/test/java src/main/resources`），输出见下方「预扫结果」；LLM 已完成 Step 2–5 中脚本未覆盖项及复核。
>
> **环境说明**：本环境无 `java`/`mvn`/`javac`，编译与单测未执行，全部为静态审查（[降级说明]）。

---

## 预扫结果（scan-all-rules.sh）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java src/test/java src/main/resources
Engine:  ripgrep

[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtazziboot/todo/service/impl/TodoItemServiceImpl.java:39

=== Summary: 1 findings (P0=0, P1=1, P2=0) | 52/222 rules scanned ===
EXIT=0
```

脚本覆盖 52/222 条；命中 1 条（M016）。其余 B/M/I/G/S 由 LLM 逐条核销（见 §4）。

---

## Step 1 — 执行队列（产物 A）

> Java 文件 12 个（全部参与审查）；非 Java 配置文件（xml/yml/sql/pom）作为辅助上下文，不进入 Java 逐文件队列但在 Step 4 SQL/安全/可靠性中核销。
> **守卫**：含 `.java`，继续。
> Step4 列采用「整节 N/A」约定：某文件对某大类（G 并发/事务/MQ/缓存/调度/资源释放…、S XSS/SSRF/命令执行/XXE/反序列化/文件…）无关时，对应列标 `N/A`；命中风险标 `⚠️`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|-----|--------|
| 1 | `src/main/java/com/dtazziboot/todo/TodoApplication.java` | 启动入口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `src/main/java/com/dtazziboot/todo/common/exception/BusinessException.java` | REQ-3 公共层 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | `src/main/java/com/dtazziboot/todo/common/result/ApiResult.java` | REQ-3 统一响应 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 4 | `src/main/java/com/dtazziboot/todo/controller/GlobalExceptionHandler.java` | REQ-3 异常处理 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ⚠️ | N/A | N/A | ✅ | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 5 | `src/main/java/com/dtazziboot/todo/controller/TodoItemController.java` | REQ-1 控制器 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 6 | `src/main/java/com/dtazziboot/todo/dao/mapper/TodoItemMapper.java` | REQ-1 DAO | ✅ | ✅ | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 7 | `src/main/java/com/dtazziboot/todo/model/dto/TodoItemCreateRequest.java` | REQ-1/2 入参 | ✅ | ✅ | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 8 | `src/main/java/com/dtazziboot/todo/model/dto/TodoItemVO.java` | REQ-1/4 出参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 9 | `src/main/java/com/dtazziboot/todo/model/entity/TodoItemDO.java` | REQ-1 数据对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 10 | `src/main/java/com/dtazziboot/todo/service/TodoItemService.java` | REQ-1 服务接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 11 | `src/main/java/com/dtazziboot/todo/service/impl/TodoItemServiceImpl.java` | REQ-1/2 业务实现 | ✅ | ✅ | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | ⚠️ | ⚠️ | ⚠️ | ⚠️ | N/A | ✅ | ⚠️ | N/A | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 12 | `src/test/java/com/dtazziboot/todo/service/impl/TodoItemServiceImplTest.java` | REQ-1 自测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |

> 非文件说明：`G2 幂等` 全部 `N/A`（待办创建业务允许重复，无幂等去重需求）；`G5 MQ` / `G6 缓存` / `G7 调度` / `G12 资损` / `S2 XSS` / `S3 SSRF` / `S4 命令执行` / `S5 XXE` / `S6 反序列化` / `S7 文件上传` 对所有文件 `N/A`（本变更无相关场景）。

---

## Step 2 — 功能（产物 B）

> REQ 来源：`requirement_section`（核心功能：新增待办事项；任务信息：事项名称和描述）+ `docs/ARCHITECTURE.md` §4 约束 + `docs/modules/todo/README.md`。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 名称+描述 / When POST /todo_items / Then 返回含 id 的新建事项 | 「核心功能：新增待办事项」「最小闭环：仅创建」(requirement) + README §4 `POST /todo_items` | `controller/TodoItemController.java`, `service/impl/TodoItemServiceImpl.java`, `dao/mapper/TodoItemMapper.java`, `model/entity/TodoItemDO.java` | ✅ | Controller:35 create → ServiceImpl:34 createTodoItem → Mapper.insert(useGeneratedKeys) → VO.id=String(id)。测试 `should_returnTodoItemVO_when_requestIsValid` ✅ |
| REQ-2 | Given 名称为空/空白 / When 创建 / Then 拒绝 | 「任务信息：事项名称和描述」（名称为必填项，impl README 错误响应 A0001 事项名称不能为空） | `model/dto/TodoItemCreateRequest.java:15`, `service/impl/TodoItemServiceImpl.java:35-37`, `controller/GlobalExceptionHandler.java:27-35` | ✅ | `@NotBlank(message="事项名称不能为空")` + ServiceImpl:35 `request==null || !hasText(name)` → BusinessException。测试 `should_throwBusinessException_when_nameIsBlank` / `_requestIsNull` ✅ |
| REQ-3 | Given 任意场景 / When 响应 / Then 统一 ApiResult(code/errorCode/errorMessage/data) | ARCHITECTURE §4「响应统一为 ApiResult<T> 结构，错误含 errorCode/errorMessage」+ README §成功/错误响应 | `common/result/ApiResult.java`, `controller/GlobalExceptionHandler.java`, `controller/TodoItemController.java` | ⚠️ | success/error 工厂已实现 ✅；但 `GlobalExceptionHandler` 仅处理 `BusinessException` 与 `MethodArgumentNotValidException`，DB 层 `DataAccessException` 等未兜底 → 非 ApiResult 默认 500，契约不完整（见 §4 G8.1/G10.1） |
| REQ-4 | Given 新建事项 / When 返回 id / Then id 用 String 防前端 Long 精度丢失 | README §2「TodoItemVO … id 使用 String 防精度丢失」 | `model/dto/TodoItemVO.java:13`, `service/impl/TodoItemServiceImpl.java:58` | ✅ | `vo.setId(String.valueOf(todoItem.getId()))` ✅ |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `readability-checklist.md` A1–A7，跨文件合并核销。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式（文件名=类名、UTF-8、无 Tab） | ✅ | 12 个 Java 文件均符合；扫描未命中 Tab |
| A2 | 源文件结构/import 顺序（无 `import *`、静态/非静态分组） | ✅ | 无通配符 import；import 顺序规范 |
| A3 | 代码样式（K&R、4 空格缩进、行宽≤120、`if (` 空格） | ⚠️ | 扫描 A3.7 未命中；A3.4 行宽均≤120。可读性建议：`GlobalExceptionHandler.java:31` `.map(error -> error.getDefaultMessage())` 可改方法引用 `FieldError::getDefaultMessage`（P2） |
| A4 | 命名规范（包/类/方法/常量/字段） | ✅ | 包全小写；类 UpperCamelCase；常量 UPPER_SNAKE（`SUCCESS_CODE`/`ERR_BUSINESS`）；字段 lowerCamelCase；`gmtCreate/gmtModified` 符合数科命名 |
| A5 | 编码实践（`@Override`、非空 catch、静态方法类名调用、不重写 finalize） | ✅ | 所有重写均加 `@Override`（ServiceImpl:33、DO/VO/Request toString）；无空 catch；扫描 A5.4 未命中 finalize |
| A6 | 特定元素样式（数组方括号、switch default、修饰符顺序、long 大写 L） | ✅ | `serialVersionUID = 1L` 大写 L；无 switch；修饰符顺序正确；扫描 A6.3/A6.5 未命中 |
| A7 | Javadoc 规范（public 类/成员有 Javadoc） | ✅ | public 类与 public 方法均有 Javadoc；getter/setter 自解释可省；`@param/@return` 顺序正确 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫覆盖 25 Blocker / 6 Major / 2 Info。LLM 已对其余规则逐条核销。命中项标 `⚠️` 并写 `path:line`。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 LocalDateTime.parse/UUID.fromString 字面量调用 |
| B002-B005 | N/A | 无数组比较/填充/toString/asList 基本类型数组 |
| B006 | N/A | 测试使用 AssertJ assertThat，非 assertEquals |
| B007 | N/A | 无 catch(Throwable/Error) |
| B008 | N/A | 无 Executors 线程池 |
| B009 | N/A | 无位移 |
| B010 | N/A | 无 BigDecimal |
| B011 | N/A | 无包装类型 `==` 比较 |
| B012-B013 | N/A | 无 Calendar |
| B014-B018 | N/A | 无 Map 泛型误用/toArray/Comparable/this==null/三目提升 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无编译期常量乘法溢出（serialVersionUID=1L） |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 SimpleDateFormat |
| B023 | ✅ | 所有 `new BusinessException(...)` 均被 throw（ServiceImpl:36,50） |
| B024 | N/A | 无 Thread |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 `.equals(null)` |
| B027-B028 | N/A | 无 equals 实现冲突/DateUtil |
| B029 | ✅ | setter 赋值正确（DO/Request/VO setName→this.name 等） |
| B030-B031 | N/A | 无浮点比较/String.format |
| B032-B040 | N/A | 无注解 getClass/Unsafe/Hashtable/二元自指/varargs/递归/indexOf/isInstance |
| B041 | N/A | 无手写 JDBC 连接管理（MyBatis 托管） |
| B042-B044 | N/A | 使用 JUnit5，无 JUnit3 误用 |
| B045 | N/A | 无 synchronized(包装类型) |
| B046 | N/A | 无死循环 |
| B047-B052 | N/A | 无数值 compare/Math.round/日期格式串（DD/hh/YYYY/Boolean.getBoolean） |
| B053 | N/A | 异常期望用 assertThatThrownBy，无需 Assert.fail |
| B054 | N/A | 无 EqualsTester |
| B055 | ✅ | `when(...).thenReturn/thenAnswer` 与 `verify(mock).insert(...)` 用法正确 |
| B056-B058 | N/A | 无 Arrays.asList 修改/增强 for 修改/集合自身操作 |
| B059-B060 | N/A | 无 nCopies/三目 null 拆箱 |
| B061-B063 | N/A | 无 sun.misc/javax.xml 弃用类 |
| B064 | N/A | 无 Optional `==` |
| B065 | ✅ | 无 Pojo 自赋值 |
| B066-B068 | N/A | 无 RandomCast/RandomMod/自赋值 |
| B069-B070 | N/A | 无自比较/自相等 |
| B071 | N/A | 无 `.size()>=0` |
| B072-B075 | N/A | 无 Stream.toString/StringBuilder(char)/substring(0)/可疑 for |
| B076 | N/A | 无 @Transactional（单条 insert 无需事务） |
| B077 | N/A | 无 catch(Throwable) 吞断言 |
| B078 | ✅ | `assertThat(result).isEqualTo(String.valueOf(TEST_ID))` 非同一对象 |
| B079 | ✅ | `@Mock` 未显式赋值 |
| B080 | ✅ | 5 个测试方法均含断言/verify |
| B081 | N/A | 无新建集合原地 sort |
| M001 | ✅ | `if (request==null || !hasText(name))` 单一条件，无重复判断 |
| M002 | N/A | 无 instanceof |
| M003 | ✅ | 无 `new Long/Integer(...)`（用 `TEST_ID = 1001L` 字面量） |
| M004 | ✅ | 无 printStackTrace |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无编译期布尔常量 |
| M007 | N/A | 无 catch 块 |
| M008 | N/A | 未重写 equals，无需 hashCode |
| M009 | N/A | 无 equals 误用 |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无类型转换算术 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无字段隐藏 |
| M016 | ⚠️ | **命中** — `TodoItemServiceImpl.java:39` `LocalDateTime.now()` 依赖系统默认时区（P1） |
| M017 | N/A | JUnit5，测试方法均有 @Test |
| M018 | N/A | 无显式锁 |
| M019 | N/A | 无 switch 枚举 |
| M020 | ✅ | 所有重写方法均加 @Override（ServiceImpl:33、DO:68/VO:45/Request:37 toString） |
| M021 | N/A | 无非重写 equals |
| M022 | N/A | 无 Optional.of(null) |
| M023 | N/A | 无 toString 误打印 |
| M024 | N/A | 无 Optional.get 误用 |
| M025 | N/A | 无 final 类 protected 成员 |
| M026 | ✅ | `@Mock` 非 static |
| M027 | N/A | 无 ThreadLocal |
| I001 | ✅ | 异常用 assertThatThrownBy + hasMessageContaining（优于 @Test(expected)，符合推荐） |
| I002-I003 | N/A | 被 Mock 的 TodoItemMapper 无 @DoNotMock/@AutoValue |
| I004 | ✅ | 无 java.util.Date（使用 LocalDateTime） |
| I005-I008 | N/A | JUnit5，无 JUnit3/4 混用 |
| I009 | N/A | 统计项 |
| I010 | ✅ | 纯 MockitoExtension 单测，未启 Spring 容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 单条 insert 无并发先读后写 |
| G1.2 | N/A | 无加锁后未二次校验 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多锁 |
| G2.1 | N/A | 待办创建业务允许重复，无幂等去重需求（非订单/资金） |
| G2.2 | N/A | 无重试/MQ/定时写库 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 单条 insert 无 @Transactional，无外部 I/O 在事务内 |
| G4.1 | ✅ | SQL 仅单条 insert，无复杂业务分支堆 SQL |
| G4.2 | ⚠️ | 入参未限长，超长可能触发 DataIntegrityViolation（关联 G11.3，`TodoItemCreateRequest.java:15-19`） |
| G4.3 | N/A | 无查询/分页 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度 |
| G7.2 | N/A | 无调度 |
| G8.1 | ⚠️ | `GlobalExceptionHandler` 无兜底 `@ExceptionHandler(Exception.class)`，DB `DataAccessException` 等未捕获 → 非 ApiResult 默认 500（P1） |
| G8.2 | N/A | 无核心链路强依赖非核心 |
| G8.3 | N/A | 无手写 I/O/连接（MyBatis 托管） |
| G8.4 | N/A | 无线程池/定时任务需 shutdown |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 业务线程池 |
| G9.1 | N/A | 无外部 HTTP/RPC 调用区分（仅 DB） |
| G9.2 | ⚠️ | `application.yml` 未显式配置 DB 连接/读超时（依赖 Hikari 默认值，建议显式 connection-timeout/max-lifetime）（P2） |
| G9.3 | N/A | 无重试 |
| G10.1 | ⚠️ | 非 ApiResult 兜底路径下错误响应不统一（关联 G8.1，`GlobalExceptionHandler.java`）（P1） |
| G10.2 | N/A | 新接口首次引入，无契约变更 |
| G11.1 | ✅ | 新逻辑含 5 个单测且均含断言 |
| G11.2 | ⚠️ | 未覆盖超长 name/description 边界（`TodoItemServiceImplTest.java`）（P2） |
| G11.3 | ⚠️ | 入参未做长度防御校验（`TodoItemCreateRequest.java:15-19` name/description 无 @Size）（P1） |
| G11.4 | N/A | 无数值/金额运算 |
| G12.1 | N/A | 无资金/库存/积分 |
| G12.2 | N/A | 无资损场景 |
| G13.1 | ✅ | 业务异常 WARN、写库失败 ERROR，日志级别正确 |
| G14.1 | N/A | 无金额 double |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️ | `LocalDateTime.now()` 默认时区 + DATETIME 存储，跨环境时区不一致（`TodoItemServiceImpl.java:39`）（P1，与 M016 关联） |
| G14.4 | N/A | 无 SimpleDateFormat/DateTimeFormatter 解析 |
| G15.1 | ✅ | `schema.sql` CREATE TABLE IF NOT EXISTS，加列式向前兼容 |
| G15.2 | N/A | 新表首次建，无旧接口共存 |
| G15.3 | N/A | 无不兼容切换逻辑 |
| G16.1 | ⚠️ | 最小闭环暂无关键指标埋点（成功率/耗时/调用量），建议后续接入（P2，非阻塞） |
| G16.2 | ⚠️ | 写库失败路径已记 ERROR+上下文 ✅；但未兜底的 DB 异常无可观测日志（关联 G8.1）（P1） |
| G16.3 | ✅ | WARN(业务)/ERROR(系统) 级别正确 |
| G16.4 | ✅ | 无空 catch、无 printStackTrace |
| G17.1 | ⚠️ | 无功能开关支持紧急关闭，建议后续迭代（P2，非阻塞） |
| G17.2 | N/A | 最小闭环无降级预案需求 |
| G17.3 | ✅ | DDL 加列式兼容，回滚仅需删表（开发期） |
| G18.1 | N/A | 无安全补强项适用 |
| G18.2 | N/A | 无 |
| G18.3 | N/A | 无 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | `TodoItemMapper.xml` 全部 `#{}` 参数化，无 `${}` |
| S1.2 | N/A | 无 order by/group by/动态表名字段名 |
| S1.3 | N/A | 无 like/in 查询（仅 insert） |
| S2.1 | N/A | REST JSON API，无 HTML/JS 输出（由前端编码） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1-S3.3 | N/A | 无外部 URL 请求 |
| S4.1-S4.2 | N/A | 无系统命令/外部进程 |
| S5.1-S5.2 | N/A | 无用户输入 XML 解析（mapper xml 为静态资源） |
| S6.1-S6.3 | N/A | 无反序列化 |
| S7.1-S7.3 | N/A | 无文件上传/下载 |
| S8.1 | ⚠️ | 接口未接入鉴权/资源归属校验（`TodoItemController.java:34`）；目标为内部用户，建议生产前由网关/鉴权层覆盖（P1） |
| S8.2 | ✅ | 增删改使用 POST，无 GET 写操作 |
| S8.3 | N/A | 自增 id，待办非安全敏感资源，可预测可接受 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ⚠️ | `application.yml:7` 密码取 `${MYSQL_PASSWORD:}` 默认空（非硬编码字面量，dev 可接受，生产应强制非空）（P2） |
| S9.2 | ✅ | 日志未记录敏感信息（仅 name/description/affectedRows） |
| S9.3 | N/A | 传输加密由网关/反代负责 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | REST Token 场景，无 Cookie 会话，CSRF 不适用 |
| S10.2 | N/A | 未配置 CORS，默认不跨域 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> `customized-checklist.md` 仅含 U1.1 示例项；U2 为空。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | Controller 入参使用 `@Valid` + `@NotBlank` 统一校验（`TodoItemController.java:35`） |
| U1.2 | N/A | 示例项未启用 |
| U1.3 | N/A | 示例项未启用 |
| U2.1 | N/A(未启用自定义规则) | 业务红线节为空 |
| U2.2 | N/A(未启用自定义规则) | 业务红线节为空 |
| U2.3 | N/A(未启用自定义规则) | 业务红线节为空 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（已全部核销，N/A 均附原因）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`（REQ-1/2/4 ✅，REQ-3 ⚠️）
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，均有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（U1.1 ✅，其余 N/A(未启用自定义规则)）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
