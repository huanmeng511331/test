# Code Review Checklist

> **Change** todo（编码实现：新增待办事项最小闭环） · **分支/Commit** AI/task-DEV-… / 60f5414 · **日期** 2026-10-27
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项非 `⬜`；`N/A` 需写原因。
> **执行顺序**：已先运行 `references/script/scan-all-rules.sh`（对 src/main/java、src/test/java、src/main/resources），输出「No findings. 52/222 rules scanned」；LLM 已完成剩余覆盖项与人工复核。

---

## Step 1 — 执行队列（产物 A）

> 变更范围：`git diff --name-only 94234b8..60f5414`，Java 文件 13 个，其余为配置/SQL/文档。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | Step4(G) | Step4(S) | 总状态 |
|---|----------------------|----------|-------|-------|----------|----------|--------|
| 1 | src/main/java/com/antdigital/todo/TodoApplication.java | 启动类 | ✅ | ✅ | ✅(见4.2) | ✅(见4.3) | ✅ 已审 |
| 2 | src/main/java/com/antdigital/todo/api/controller/TodoController.java | REQ-1 接口入口 | ✅ | ✅ | ⚠️ | ⚠️ | ⚠️ 已审有问题 |
| 3 | src/main/java/com/antdigital/todo/common/enums/ErrorCodeEnum.java | 错误码规范 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 4 | src/main/java/com/antdigital/todo/common/exception/BusinessException.java | 业务异常 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 5 | src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java | 全局异常映射 | ✅ | ✅ | ❌ | ✅ | ⚠️ 已审有问题 |
| 6 | src/main/java/com/antdigital/todo/common/response/ApiResponse.java | 统一响应 | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 7 | src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java | REQ-1 数据访问 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 8 | src/main/java/com/antdigital/todo/model/do/TodoItemDO.java | REQ-2 数据对象 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 9 | src/main/java/com/antdigital/todo/model/dto/TodoCreateRequest.java | REQ-2 请求对象 | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 10 | src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java | 展示对象 | ✅ | ✅ | ❌ | ✅ | ⚠️ 已审有问题 |
| 11 | src/main/java/com/antdigital/todo/service/TodoService.java | 业务接口 | ✅ | ✅ | ✅ | ✅ | ✅ 已审 |
| 12 | src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java | REQ-1/2 业务实现 | ✅ | ⚠️ | ❌ | ✅ | ⚠️ 已审有问题 |
| 13 | src/test/java/com/antdigital/todo/service/impl/TodoServiceImplTest.java | 单测 | ✅ | ✅ | ⚠️ | ✅ | ⚠️ 已审有问题 |

非 Java（跳过）：`pom.xml`、`src/main/resources/application.yml`、`src/main/resources/mapper/TodoItemMapper.xml`（在 Step4 中按 XML/SQL 规则核销）、`src/main/resources/sql/todo_item.sql`、`.agents/2026-10-27-todo/impl.md`、`docs/ARCHITECTURE.md`、`docs/modules/todo/README.md`。

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据 |
|-----|----------|----------------------|----------|------|----------|
| REQ-1 新增待办事项 | 给定合法请求，当调用 POST /todos，则持久化并返回展示对象 | 需求「核心功能：新增待办事项」；docs/modules/todo/README.md §4 | TodoController.java / TodoServiceImpl.java / TodoItemMapper.java / TodoItemMapper.xml | ✅ | TodoController.java:36-42 → TodoServiceImpl.java:34-63 → mapper insert:18 |
| REQ-2 任务信息=名称+描述 | 名称必填(≤200)、描述可选(≤1000)、名称首尾 trim | 需求「任务信息：事项名称和描述」；README §4校验规则 | TodoCreateRequest.java / TodoServiceImpl.java | ✅ | TodoCreateRequest.java:12-18；TodoServiceImpl.java:39-53 |
| REQ-3 最小闭环仅创建 | 仅提供创建，无查询/更新/删除对外接口 | 需求「最小闭环：仅创建」 | TodoController.java / TodoService.java | ✅ | TodoController 仅 @PostMapping；TodoService 仅 createTodo；selectById 未暴露为接口 |

REQ-1 备注（非 P0）：核心创建路径满足；但返回 VO 的 `gmtCreate/gmtModified` 因 insert 未回填时间而恒为 null（详见 Step 4 G10.1，P1）。REQ-2 备注：DTO `@NotBlank/@Size` 与 Service 手动校验双重实现（P2）。

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1 | 源文件格式 | ✅ | UTF-8、文件名=顶层类+`.java`、无 Tab（脚本 A1.3 无命中） |
| A2 | 源文件结构/import 顺序 | ❌ | **A2.4** `TodoServiceImpl.java:3-4`：`common.exception.BusinessException` 排在 `common.enums.ErrorCodeEnum` 前，违反 ASCII 字典序；其余文件分组（静态/非静态）正确，无 `*` 导入（A2.2 脚本无命中） |
| A3 | 代码样式 | ✅ | K&R、4 空格缩进、行宽≤120（A3.4 脚本无命中）、成员间空行（A3.6） |
| A4 | 命名规范 | ✅ | 包/类/方法/常量均合规（A4.1/A4.2/A4.3/A4.4） |
| A5 | 编码实践 | ✅ | `@Override` 齐全（A5.1）、无空 catch、静态用类名调用、无 finalize（A5.4 脚本无命中） |
| A6 | 特定元素样式 | ✅ | 无 switch、修饰符顺序合规（A6.3 脚本无命中）、无 long 字面量 |
| A7 | Javadoc 规范 | ✅ | public 类均有 Javadoc；getter/setter/`@Override` 依 A7.3 豁免；无 `@param/@return/@throws` 顺序问题 |

---

## Step 4 — 可靠性检查（产物 D）

> 预扫结论：`scan-all-rules.sh`（B/M/I + A/S/G，52/222 条）**No findings**，脚本已覆盖项标 ✅/脚本摘要。以下为脚本覆盖 + LLM 补扫合并结果。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | ID | 状态 | ID | 状态 | ID | 状态 | ID | 状态 | ID | 状态 |
|----|------|----|------|----|------|----|------|----|------|----|------|
| B001 | ✅ | B002 | ✅ | B003 | ✅ | B004 | ✅ | B005 | ✅脚本 | B006 | ✅ |
| B007 | ✅ | B008 | ✅脚本 | B009 | ✅ | B010 | ✅脚本 | B011 | ✅ | B012 | ✅脚本 |
| B013 | ✅脚本 | B014 | ✅ | B015 | ✅ | B016 | ✅ | B017 | ✅脚本 | B018 | ✅ |
| B019 | N/A(未用Money) | B020 | ✅ | B021 | N/A(未用Jedis) | B022 | ✅脚本 | B023 | ✅脚本 | B024 | ✅ |
| B025 | ✅ | B026 | ✅脚本 | B027 | ✅ | B028 | ✅脚本 | B029 | ✅ | B030 | ✅ |
| B031 | ✅ | B032 | N/A(无注解反射) | B033 | N/A(未用Unsafe) | B034 | ✅ | B035 | ✅ | B036 | ✅脚本 |
| B037 | ✅ | B038 | ✅ | B039 | ✅ | B040 | ✅ | B041 | N/A(未用JDBC直连) | B042 | ✅(JUnit5) |
| B043 | ✅(@Nested合法) | B044 | ✅ | B045 | ✅ | B046 | ✅ | B047 | N/A(无float/double比较) | B048 | N/A(未用Math.round) |
| B049 | ✅脚本 | B050 | ✅ | B051 | ✅脚本 | B052 | ✅脚本 | B053 | ✅ | B054 | N/A(未用EqualsTester) |
| B055 | ✅ | B056 | ✅脚本 | B057 | ✅ | B058 | ✅ | B059 | ✅脚本 | B060 | ✅ |
| B061 | ✅脚本 | B062 | ✅脚本 | B063 | ✅脚本 | B064 | ✅ | B065 | ✅ | B066 | ✅脚本 |
| B067 | ✅脚本 | B068 | ✅ | B069 | ✅ | B070 | ✅ | B071 | ✅脚本 | B072 | ✅ |
| B073 | ✅脚本 | B074 | ✅脚本 | B075 | ✅ | B076 | ✅脚本 | B077 | ✅ | B078 | ✅(断言为常量/字符串，非自比较) |
| B079 | ✅ | B080 | ✅(测试均有断言) | B081 | ✅ | M001 | ✅ | M002 | ✅ | M003 | ✅脚本 |
| M004 | ✅脚本 | M005 | ✅(@Nested需访问外部成员，不可static) | M006 | ✅ | M007 | ✅脚本 | M008 | ✅(未重写equals) | M009 | ✅ |
| M010 | ✅ | M011 | ✅(无switch) | M012 | ✅(无finally) | M013 | ✅ | M014 | ✅ | M015 | ✅(无继承字段隐藏) |
| M016 | ✅(无 now() 调用) | M017 | ✅(@Test齐全) | M018 | ✅(无显式锁) | M019 | ✅ | M020 | ✅(@Override齐全) | M021 | ✅ |
| M022 | ✅脚本 | M023 | ✅(toString已重写) | M024 | ✅ | M025 | ✅ | M026 | ✅(@Mock非static) | M027 | ✅脚本(无ThreadLocal) |
| I001 | ✅脚本(无expected) | I002 | ✅ | I003 | ✅ | I004 | ✅脚本(无new Date) | I005 | ✅(JUnit5) | I006 | ✅ |
| I007 | ✅ | I008 | ✅ | I009 | N/A(统计) | I010 | ✅(单测无Spring容器) | | | | |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 仅单条 insert，无先读后写/加锁场景 |
| G1.2 | N/A | 无加锁再更新场景 |
| G1.3 | N/A | 无乐观锁重试 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 纯新增待办，无业务幂等键可设计，非资金/消息场景，重复提交重复落库可接受 |
| G2.2 | N/A | 无重试/定时/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 单库本地事务，无跨库读后写 |
| G3.2 | ✅ | `@Transactional` 范围内仅 insert，无外部 I/O/RPC |
| G4.1 | ✅ | 业务逻辑在 Java，SQL 极简 |
| G4.2 | ✅ | 无对索引列函数/隐式转换（id 等值查询） |
| G4.3 | ✅ | 无列表查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 参数校验 + 全局异常兜底，非仅 happy path |
| G8.2 | N/A | 无强依赖外部非核心链路 |
| G8.3 | N/A | 无 I/O 流/连接/锁 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | ✅ | 未用 Executors（脚本 B008 无命中），亦无自建线程池 |
| G9.1 | N/A | 无重试/错误三态处理需求 |
| G9.2 | ⚠️ | DB 连接未显式配置超时，依赖 Spring Boot 默认 Hikari 默认值（P2，内部小服务可接受） |
| G9.3 | N/A | 无重试 |
| G10.1 | ❌ | TodoItemVO.gmtCreate/gmtModified 因 insert 仅回填 id 恒为 null，字段语义不明（P1） |
| G10.2 | N/A | 新接口，无版本兼容诉求 |
| G11.1 | ✅ | TodoServiceImplTest 7 个测试均有断言 + verify |
| G11.2 | ⚠️ | 覆盖 Service 层空/边界/超长/trim，但未覆盖 Controller `@Valid` 与 GlobalExceptionHandler 状态码映射（P2） |
| G11.3 | ✅ | itemName 空值/空白有校验，description null 有兜底 |
| G11.4 | N/A | 无金额/数值运算 |
| G12.1 | N/A | 无资金/库存/积分场景 |
| G12.2 | N/A | 内部最小闭环，无止血开关诉求 |
| G13.1 | ✅ | 日志级别正确（warn=业务/校验，error=系统，info=正常） |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ✅ | 时间由 DB CURRENT_TIMESTAMP（UTC 配置）生成，未存本地时区字符串 |
| G14.4 | N/A | 无 SimpleDateFormat/DateTimeFormatter |
| G15.1 | ✅ | 建新表（CREATE TABLE），无破坏性 DDL（脚本 G15.1 无命中） |
| G15.2 | N/A | 新接口 |
| G15.3 | N/A | 无逻辑切换 |
| G16.1 | ⚠️ | 无核心链路指标埋点（P2，内部最小闭环暂可接受） |
| G16.2 | ✅ | 异常路径均有日志记录（handler 与 service） |
| G16.3 | ✅ | 业务/校验 WARN、系统 ERROR、正常 INFO |
| G16.4 | ✅ | 无空 catch / printStackTrace（脚本 G16.2/M004 无命中） |
| G17.1 | N/A | 内部最小闭环，无紧急关闭开关诉求 |
| G17.2 | N/A | 无备降级预案诉求 |
| G17.3 | N/A | 无需数据变更回滚 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis 全部 `#{}` 预编译（脚本 S1.1 XML 无命中） |
| S1.2 | ✅ | 无 order by/group by/动态表名 |
| S1.3 | ✅ | 无 like / in 拼接 |
| S2.1 | ✅ | 纯 JSON 输出，Spring Jackson 序列化，无富文本 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | ✅ | 无外部命令拼接（脚本 S4.1 无命中） |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无二进制/Java 反序列化 |
| S6.2 | ✅ | Jackson 默认无多态反序列化 |
| S6.3 | N/A | 无敏感字段序列化场景 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件路径处理 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | ⚠️ | 接口未接入鉴权/归属校验（P2，内部用户系统建议网关统一鉴权） |
| S8.2 | ✅ | 增删改用 POST，无 GET 增删改 |
| S8.3 | ✅ | 仅创建返回自身 id，无按 id 的对外查询/修改接口，自增 id 可预测风险低 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ⚠️ | application.yml 含默认口令 `todo`（`${DB_PASSWORD:todo}`），弱默认凭证（P2） |
| S9.2 | ✅ | 日志仅记 itemName（业务数据），不记密码/token |
| S9.3 | ⚠️ | `useSSL=false` 明文传输（P2，内网待办可接受，建议评估） |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无状态 REST，无 Cookie 会话，无 CSRF 面 |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1（示例：Controller 入参使用 @Valid） | ✅ | TodoController.java:38 使用 `@Valid @RequestBody` |
| U2.x | N/A | customized-checklist 无可启用业务红线；整体 N/A(未启用自定义规则) |

---

## 终检（防漏检）

- [x] 执行队列 13 个 Java 文件 Step2/Step3/G/S 均非 `⬜`（跳过项已标注）
- [x] Step 2 的 REQ-1/2/3 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081 / M001–M027 / I001–I010 均非 `⬜`（N/A 已写原因）
- [x] Step 5 U* 均非 `⬜`
- [x] 所有 ❌/⚠️ 已写入 report 并含 `path:line`