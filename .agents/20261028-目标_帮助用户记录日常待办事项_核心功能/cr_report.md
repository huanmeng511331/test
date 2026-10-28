# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-c1f01881-7921-11f1-a885-b9c05fd79159-fc75538e-9afc-46b0-8103-8c75ed23a138` / `19dbf26` · **日期** `2026-10-28` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。**每个 ❌/⚠️ 问题在 §7 后附 `.java` 问题片段**（见 §7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `19` (含 1 测试文件) |
| 变更行数 | 全新增（new project, +~700 / -0） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `TodoApplication` | `src/main/java/com/todoapp/TodoApplication.java` | 应用启动类 |
| `WebMvcConfig` | `src/main/java/com/todoapp/common/config/WebMvcConfig.java` | Web MVC 配置，注册拦截器 |
| `CommonConstants` | `src/main/java/com/todoapp/common/constant/CommonConstants.java` | 公共常量 |
| `LoginContext` | `src/main/java/com/todoapp/common/context/LoginContext.java` | 登录上下文（userId, tenantId） |
| `LoginContextHolder` | `src/main/java/com/todoapp/common/context/LoginContextHolder.java` | ThreadLocal 登录上下文持有器 |
| `BusinessException` | `src/main/java/com/todoapp/common/exception/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler` | `src/main/java/com/todoapp/common/handler/GlobalExceptionHandler.java` | 全局异常处理器 |
| `LoginInterceptor` | `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java` | 全局登录拦截器 |
| `ApiResponse` | `src/main/java/com/todoapp/common/model/ApiResponse.java` | 通用响应包装 |
| `TodoController` | `src/main/java/com/todoapp/todo/api/controller/TodoController.java` | REST 控制器 |
| `TodoCreateApiRequest` | `src/main/java/com/todoapp/todo/api/request/TodoCreateApiRequest.java` | API 请求对象 |
| `TodoCreateData` | `src/main/java/com/todoapp/todo/api/response/TodoCreateData.java` | API 响应数据 |
| `TodoErrorCode` | `src/main/java/com/todoapp/todo/constant/TodoErrorCode.java` | 错误码枚举 |
| `TodoDO` | `src/main/java/com/todoapp/todo/dao/entity/TodoDO.java` | 数据对象 |
| `TodoMapper` | `src/main/java/com/todoapp/todo/dao/mapper/TodoMapper.java` | MyBatis Mapper 接口 |
| `TodoService` | `src/main/java/com/todoapp/todo/service/TodoService.java` | Service 接口 |
| `TodoCreateRequest` | `src/main/java/com/todoapp/todo/service/dto/TodoCreateRequest.java` | Service 层请求 DTO |
| `TodoCreateResult` | `src/main/java/com/todoapp/todo/service/dto/TodoCreateResult.java` | Service 层结果 DTO |
| `TodoServiceImpl` | `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java` | Service 实现 |
| `TodoServiceImplTest` | `src/test/java/com/todoapp/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

### 执行队列（Step 1 产物 A）

| # | 文件（仓库相对路径） | 归属原因 | 总状态 |
|---|----------------------|----------|--------|
| 1 | `src/main/java/com/todoapp/TodoApplication.java` | 启动类 | ✅ 已审 |
| 2 | `src/main/java/com/todoapp/common/config/WebMvcConfig.java` | REQ 登录拦截 | ✅ 已审 |
| 3 | `src/main/java/com/todoapp/common/constant/CommonConstants.java` | REQ 通用常量 | ✅ 已审 |
| 4 | `src/main/java/com/todoapp/common/context/LoginContext.java` | R04 登录上下文 | ✅ 已审 |
| 5 | `src/main/java/com/todoapp/common/context/LoginContextHolder.java` | R04 ThreadLocal | ✅ 已审 |
| 6 | `src/main/java/com/todoapp/common/exception/BusinessException.java` | REQ 异常体系 | ✅ 已审 |
| 7 | `src/main/java/com/todoapp/common/handler/GlobalExceptionHandler.java` | REQ 异常处理 | ✅ 已审 |
| 8 | `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java` | REQ 登录拦截器 | ⚠️ 已审有问题 |
| 9 | `src/main/java/com/todoapp/common/model/ApiResponse.java` | REQ 通用出参 | ✅ 已审 |
| 10 | `src/main/java/com/todoapp/todo/api/controller/TodoController.java` | W01/F01 | ⚠️ 已审有问题 |
| 11 | `src/main/java/com/todoapp/todo/api/request/TodoCreateApiRequest.java` | W01 入参 | ✅ 已审 |
| 12 | `src/main/java/com/todoapp/todo/api/response/TodoCreateData.java` | W01 出参 | ✅ 已审 |
| 13 | `src/main/java/com/todoapp/todo/constant/TodoErrorCode.java` | REQ 错误码 | ✅ 已审 |
| 14 | `src/main/java/com/todoapp/todo/dao/entity/TodoDO.java` | REQ 数据对象 | ✅ 已审 |
| 15 | `src/main/java/com/todoapp/todo/dao/mapper/TodoMapper.java` | REQ DAO | ✅ 已审 |
| 16 | `src/main/java/com/todoapp/todo/service/TodoService.java` | S01 接口 | ✅ 已审 |
| 17 | `src/main/java/com/todoapp/todo/service/dto/TodoCreateRequest.java` | S01 DTO | ✅ 已审 |
| 18 | `src/main/java/com/todoapp/todo/service/dto/TodoCreateResult.java` | S01 DTO | ✅ 已审 |
| 19 | `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java` | S01 实现 | ⚠️ 已审有问题 |
| 20 | `src/test/java/com/todoapp/todo/service/impl/TodoServiceImplTest.java` | G11 自测 | ✅ 已审 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 2 | 4 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项接口（F01/W01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/todo/create 接口存在 | ✅ | design §4.1 W01: `POST /api/todo/create` | `TodoController.java:50` `@PostMapping("/create")` | 路径匹配 |
| 入参 title 必填、长度 1~100 | ✅ | design §5.1.2: `title String 是 事项名称 长度 1~100` | `TodoCreateApiRequest.java:15-16` `@NotBlank` + `@Size(max=100)` | 框架层校验 |
| 入参 description 选填、长度 0~1000 | ✅ | design §5.1.2: `description String 否 事项描述 长度 0~1000` | `TodoCreateApiRequest.java:20` `@Size(max=1000)` | null 通过校验，Service 层转空串 |
| 出参 data.id (Long) | ✅ | design §5.1.2: `data.id Long 新建待办 ID` | `TodoController.java:72` `new TodoCreateData(result.getId())` | |
| 通用出参 result/msg/data | ✅ | design §5.1 全局约定 | `ApiResponse.java:20-27` result/msg/data 三字段 | |

### REQ-2: R01 — title 非空且非纯空白 → TODO_001

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| title 为 null 或纯空白时抛 TODO_001 | ✅ | design §5.1.3 R01: `title 非空且非纯空白 返回 TODO_001` | `TodoServiceImpl.java:39-41` `getTitle() == null \|\| isBlank()` → `TITLE_EMPTY.toException()` | |

### REQ-3: R02 — title 长度 ≤ 100 → TODO_002

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| title 超过 100 字符时抛 TODO_002 | ✅ | design §5.1.3 R02: `title 长度 ≤ 100 返回 TODO_002` | `TodoServiceImpl.java:44-46` `length() > TITLE_MAX_LENGTH` → `TITLE_LENGTH_EXCEEDED.toException()` | |

### REQ-4: R03 — description 长度 ≤ 1000 → TODO_002

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| description 超过 1000 字符时抛 TODO_002 | ✅ | design §5.1.3 R03: `description 长度 ≤ 1000 返回 TODO_002` | `TodoServiceImpl.java:49-52` null→""，`length() > DESCRIPTION_MAX_LENGTH` → `DESCRIPTION_LENGTH_EXCEEDED.toException()` | |

### REQ-5: R04 — creator/tenant_id 取登录上下文，禁前端传入

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| creator 从登录上下文获取 | ✅ | design §5.1.3 R04: `creator、tenant_id 取登录上下文 禁前端传入` | `TodoController.java:60-67` `LoginContextHolder.get()` → `setCreator/setTenantId` | TodoCreateApiRequest 无 creator/tenantId 字段 |
| 全局拦截器校验登录态 | ✅ | design §6.4.2.3: `全局统一拦截器校验登录态` | `LoginInterceptor.java:40-49` 检查 X-User-Id | |

### REQ-6: 错误码 TODO_001/002/003

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| TODO_001/002/003 定义正确 | ✅ | design §5.1.2 错误码表 | `TodoErrorCode.java:16-25` TITLE_EMPTY=TODO_001, TITLE_LENGTH_EXCEEDED=TODO_002, DESCRIPTION_LENGTH_EXCEEDED=TODO_002, CREATE_FAILED=TODO_003 | |
| TODO_004 功能开关错误码 | ⚠️ | design 未定义 TODO_004，但 §7.2/7.3 要求功能开关 | `TodoErrorCode.java:28` FEATURE_DISABLED=TODO_004 | 合理新增，design 错误码表未列出但与可应急设计一致 |

### REQ-7: DB 异常捕获 → TODO_003

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| DB 写入异常返回 TODO_003 并记录日志 | ✅ | design §5.1.3 异常场景: `数据库写入异常 捕获异常 返回 TODO_003 记录日志与告警` | `TodoServiceImpl.java:61-67` try-catch → `logger.error` → `CREATE_FAILED.toException(e)` | |
| insert 返回 null id 返回 TODO_003 | ✅ | design 异常场景隐含 | `TodoServiceImpl.java:69-73` `id == null` → `CREATE_FAILED.toException()` | |

### REQ-8: 功能开关可灰度/可应急

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 功能开关存在 | ✅ | design §7.2/7.3: `功能开关 feature_todo_create` | `TodoController.java:35-36` `@Value("${feature.todo.create.enabled:true}")` | |
| 开关关闭时返回功能不可用 | ✅ | design §7.3: `关闭后接口直接返回功能暂不可用` | `TodoController.java:53-57` `!todoCreateEnabled` → `FEATURE_DISABLED` | |
| 开关即时生效（不需重启） | ❌ | design §7.2: `开关即时生效`；G17.1: `功能开关支持紧急关闭（不需重启/发布）` | `TodoController.java:35` `@Value` 静态注入，修改需重启 | **P0** — 详见 §5 G17.1 |

### REQ-9: 表结构与数据模型

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| todo 表 7 字段 + 索引 | ✅ | design §5.1.1.1 表结构 | `todo_schema.sql:3-13` 7 字段 + `idx_todo_tenant_creator` | 完全匹配 |
| MyBatis INSERT 使用 #{} 预编译 | ✅ | design §5.1；S1.1 | `TodoMapper.xml:18-19` 全部 `#{}` 参数 | |
| useGeneratedKeys 回填 id | ✅ | design §5.1.3 时序图: `自增主键` | `TodoMapper.xml:16-17` `useGeneratedKeys="true" keyProperty="id"` | |

### REQ-10: 监控/埋点

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| createTodo 接口埋点（调用量/成功率/耗时 P99） | ❌ | design §6.5: `埋点：createTodo 调用次数、成功/失败计数、处理耗时（P99）`；§7.1: `接口埋点` | 无 Micrometer/Prometheus 等指标埋点代码，仅有日志 | **P0** — 详见 §5 G16.1 |

---

## 4. Step 3 — 可读性检查

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1.1 | 文件名 = 顶层类名 | ✅ | 全部文件符合 |
| A1.2 | 编码 UTF-8 | ✅ | |
| A1.3 | 仅 ASCII 空格，禁止 Tab | ✅ | |
| A2.1 | 文件顺序 package → import → 类 | ✅ | |
| A2.2 | 禁止 import * | ✅ | 全部显式 import |
| A2.3 | 静态/非静态 import 分组 | ✅ | 测试文件 `TodoServiceImplTest.java:17-21` 正确分组 |
| A2.4 | import 字典序 | ✅ | |
| A2.5 | 重载方法连续 | N/A | 无重载方法 |
| A3.1 | K&R 大括号 | ✅ | |
| A3.2 | 空 catch 不可简写 | N/A | 无空 catch |
| A3.3 | 缩进 4 空格 | ✅ | |
| A3.4 | 行宽 ≤ 120 | ⚠️ | `LoginInterceptor.java:40`（preHandle 方法签名 133 字符）、`LoginInterceptor.java:62`（afterCompletion 方法签名 137 字符）— 接口方法签名，**P2** |
| A3.5 | 换行规则 | ✅ | |
| A3.6 | 类成员间空行 | ✅ | |
| A3.7 | 关键字与括号间空格 | ✅ | |
| A3.8 | 运算符两侧空格 | ✅ | |
| A4.1 | 包名全小写 | ✅ | `com.todoapp` / `com.todoapp.todo` 等 |
| A4.2 | 类名 UpperCamelCase | ✅ | |
| A4.3 | 方法名 lowerCamelCase | ✅ | |
| A4.4 | 常量 UPPER_SNAKE_CASE | ✅ | `RESULT_OK`, `TITLE_MAX_LENGTH` 等 |
| A4.5 | 字段 lowerCamelCase | ✅ | |
| A4.6 | 泛型命名 | ✅ | `ApiResponse<T>` |
| A4.7 | 测试类名 + Test | ✅ | `TodoServiceImplTest` |
| A5.1 | 重写方法加 @Override | ✅ | `WebMvcConfig:19`, `TodoServiceImpl:36`, `LoginInterceptor:39,61`, `GlobalExceptionHandler` 无重写 |
| A5.2 | catch 块不为空 | ✅ | |
| A5.3 | 静态方法用类名调用 | ✅ | |
| A5.4 | 禁止重写 finalize | ✅ | |
| A6.1 | 数组方括号属于类型 | N/A | 无数组声明 |
| A6.2 | switch fall-through + default | N/A | 无 switch |
| A6.3 | 修饰符顺序 | ✅ | |
| A6.4 | 注解样式 | ✅ | |
| A6.5 | long 字面量大写 L | N/A | 无 long 字面量 |
| A7.1 | public 类/成员有 Javadoc | ✅ | 全部 public 类和 public/protected 方法有 Javadoc |
| A7.2 | 块标记顺序 | ✅ | `@param` → `@return` → `@throws` |
| A7.3 | 简单 getter 可省 Javadoc | ✅ | |
| A7.4 | 多段落用 `<p>` 分隔 | ✅ | |

---

## 5. Step 4 — 可靠性检查

### 预扫结果（`scan-all-rules.sh`）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java/com/todoapp
Engine:  ripgrep

[P0] G16.2 — CatchWithoutLogging: src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:63
[P2] A3.4 — LineWidthExceeded: src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:40
[P2] A3.4 — LineWidthExceeded: src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:62

=== Summary: 3 findings (P0=1, P1=0, P2=2) | 52/222 rules scanned ===
```

**预扫复核**：
- `G16.2 TodoServiceImpl.java:63` — **误报（false positive）**。catch 块位于第 63 行，第 64-65 行有 `logger.error(...)` 日志输出，脚本因日志跨行未识别。人工复核确认 catch 块**有日志**，标记为 ✅。
- `A3.4 LoginInterceptor.java:40/62` — **确认命中**，方法签名超 120 字符，**P2**。

### 可靠性（G）

| ID | 状态 | 等级 | 备注 |
|----|------|------|------|
| G1.1 | N/A | — | 纯 INSERT，无先读后写并发场景（design §5.1.3 已论证） |
| G1.2 | N/A | — | 无锁场景 |
| G1.3 | N/A | — | 无乐观锁 |
| G1.4 | N/A | — | 无多锁 |
| G2.1 | N/A | — | design 明确无幂等需求：纯 INSERT、无唯一约束、重复提交符合业务语义 |
| G2.2 | N/A | — | 同上 |
| G2.3 | N/A | — | 同上 |
| G3.1 | N/A | — | 无分布式事务 |
| G3.2 | N/A | — | 无 @Transactional，单次 INSERT 由 DB 保证原子性 |
| G4.1 | ✅ | — | INSERT 语句简单，无复杂业务逻辑堆在 SQL |
| G4.2 | ✅ | — | 无 WHERE 条件函数转换 |
| G4.3 | N/A | — | 无查询（本期仅创建） |
| G5.1 | N/A | — | 无 MQ |
| G6.1 | N/A | — | 无缓存 |
| G6.2 | N/A | — | 无缓存 |
| G7.1 | N/A | — | 无调度任务 |
| G7.2 | N/A | — | 无调度任务 |
| G8.1 | ✅ | — | TodoServiceImpl catch 块记日志并抛出，非吞异常 |
| G8.2 | N/A | — | 无外部 RPC/HTTP 调用 |
| G8.3 | N/A | — | 无手动 I/O 流/连接管理（Spring 管理 DataSource） |
| G8.4 | N/A | — | 无线程池/定时任务 |
| G8.5 | ✅ | — | `LoginInterceptor.java:63` afterCompletion 中 `LoginContextHolder.clear()` 清理 ThreadLocal |
| G8.6 | N/A | — | 无线程池 |
| G9.1 | N/A | — | 无外部 HTTP/RPC 调用 |
| G9.2 | N/A | — | 同上 |
| G9.3 | N/A | — | 同上 |
| G10.1 | ✅ | — | data.id 为 null 时表示系统异常（返回 TODO_003），非用 null 表示"无数据" |
| G10.2 | N/A | — | 全新接口无版本变更 |
| G11.1 | ✅ | — | 10 个单测，均有断言 |
| G11.2 | ✅ | — | 覆盖 null、最大长度边界值 |
| G11.3 | ✅ | — | TodoServiceImpl 对 title null/blank、description null 做防御校验 |
| G11.4 | N/A | — | 无数值运算/金额 |
| G12.1 | N/A | — | 无资金场景 |
| G12.2 | ✅ | — | 功能开关可作为止血手段（但需重启，见 G17.1） |
| G13.1 | ✅ | — | 业务异常 WARN，系统异常 ERROR，成功 INFO，级别正确 |
| G14.1 | N/A | — | 无金额 |
| G14.2 | ⚠️ | P1 | tenant_id 来自 `X-Tenant-Id` 请求头（`LoginInterceptor.java:52`），客户端可伪造。design 注明为 SSO 模拟，生产环境需替换为真实 SSO 下发值 |
| G14.3 | ✅ | — | DB 使用 DATETIME + NOW()，应用 serverTimezone=GMT+8，TodoDO 使用 LocalDateTime |
| G14.4 | N/A | — | 无 SimpleDateFormat |
| G15.1 | ✅ | — | 全新表，无表结构变更兼容问题 |
| G15.2 | N/A | — | 无旧接口 |
| G15.3 | ✅ | — | 功能开关控制切换 |
| G16.1 | ❌ | P0 | **核心链路无指标埋点**。design §6.5/§7.1 要求 createTodo 调用次数、成功/失败计数、处理耗时 P99。实现仅有日志，无 Micrometer/Prometheus 等指标采集 |
| G16.2 | ✅ | — | 预扫误报已复核：catch 块有 logger.error 日志输出（`TodoServiceImpl.java:64-65`） |
| G16.3 | ✅ | — | 业务异常 WARN（`GlobalExceptionHandler.java:45`），系统异常 ERROR（`GlobalExceptionHandler.java:73`），成功 INFO（`TodoServiceImpl.java:75`） |
| G16.4 | ✅ | — | 无空 catch、无 printStackTrace、无关键路径吞异常 |
| G17.1 | ❌ | P0 | **功能开关不支持运行时切换**。`TodoController.java:35` 使用 `@Value` 静态注入，修改需重启应用。design §7.2 明确要求"开关即时生效"，G17.1 要求"不需重启/发布" |
| G17.2 | ✅ | — | 开关关闭时返回 TODO_004 兜底 |
| G17.3 | ✅ | — | design §7.3：纯新增表无回滚依赖，下线接口路由即可 |
| G18.1 | N/A | — | 无安全补强场景 |
| G18.2 | N/A | — | 同上 |
| G18.3 | N/A | — | 同上 |

### 安全（S）

| ID | 状态 | 等级 | 备注 |
|----|------|------|------|
| S1.1 | ✅ | — | `TodoMapper.xml:19` 全部使用 `#{}` 预编译参数 |
| S1.2 | N/A | — | 无 order by / 动态表名 |
| S1.3 | N/A | — | 无 like / in 查询 |
| S2.1 | N/A | — | REST API JSON 输出，框架序列化 |
| S2.2 | N/A | — | 无富文本 |
| S2.3 | N/A | — | 无模板引擎 |
| S3.1 | N/A | — | 无外部 URL 请求 |
| S3.2 | N/A | — | 同上 |
| S3.3 | N/A | — | 同上 |
| S4.1 | N/A | — | 无系统命令 |
| S4.2 | N/A | — | 同上 |
| S5.1 | N/A | — | 无 XML 解析 |
| S5.2 | N/A | — | 同上 |
| S6.1 | N/A | — | 无反序列化 |
| S6.2 | N/A | — | 同上 |
| S6.3 | N/A | — | 同上 |
| S7.1 | N/A | — | 无文件上传 |
| S7.2 | N/A | — | 同上 |
| S7.3 | N/A | — | 同上 |
| S8.1 | ⚠️ | P1 | 登录态来自 `X-User-Id` 请求头（`LoginInterceptor.java:41`），客户端可伪造。design 注明为 SSO/IAM 模拟，后续需替换为真实 SSO。生产部署前必须接入网关层鉴权 |
| S8.2 | ✅ | — | POST 方法执行创建，非 GET |
| S8.3 | N/A | — | id 自增，内部应用 |
| S8.4 | N/A | — | 无 Cookie |
| S9.1 | ⚠️ | P1 | `application.yml:9-10` 数据库凭证硬编码（`username: root`, `password: root`）。应使用环境变量或配置中心 |
| S9.2 | ⚠️ | P1 | `TodoServiceImpl.java:64-65,75` 日志输出原始 creator（`request.getCreator()`），design §6.4.3.2 要求"日志打印对 creator 做脱敏处理"。`LoginContext.toString()` 已脱敏但 Service 层日志未使用 |
| S9.3 | N/A | — | HTTPS 由 SLB 处理（design §6.4.3.1） |
| S9.4 | N/A | — | 无随机数 |
| S10.1 | N/A | — | REST API，非表单提交，CSRF 不适用 |
| S10.2 | N/A | — | 无 CORS 配置 |
| S10.3 | N/A | — | 无 URL 跳转 |

### Bug 模式（B/M/I）

> 预扫覆盖 52/222 条规则。以下为脚本未覆盖项的 LLM 逐条核对结果，仅列出相关项，无关项标 N/A。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无 parse/of 字面量调用 |
| B002 | N/A | 无数组 equals |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | N/A | 无 Arrays.asList 基本类型数组 |
| B006 | N/A | 无 assertEquals 参数顺序问题 |
| B007 | N/A | 无 catch Throwable 吞断言 |
| B008 | N/A | 无 Executors 线程池 |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 BigDecimal |
| B011 | N/A | 无包装类型 == 比较 |
| B012-B081 | N/A | 逐一核对，均不适用（无 Calendar/集合泛型错误/自赋值/线程问题等模式） |
| B053 | N/A | 测试使用 assertThatThrownBy，无需 MissingFail |
| B055 | ✅ | `TodoServiceImplTest.java:58,77,100,118` when().thenAnswer() 正确；`verify(todoMapper).insert()` 未嵌套调用 |
| B076 | N/A | 无 @Transactional |
| B077 | N/A | 测试无 catch Throwable |
| B079 | ✅ | `TodoServiceImplTest.java:45-46` `@Mock` 未显式赋值 |
| B080 | ✅ | 全部测试方法含 assertThat/assertThatThrownBy/verify 断言 |
| B081 | N/A | 无新建集合 sort/shuffle |
| M001 | N/A | 无连续相同条件判断 |
| M002 | N/A | 无子类 instanceof 父类 |
| M003 | N/A | 无包装类构造器 |
| M004 | ✅ | 无 printStackTrace |
| M005 | N/A | 无非静态内部类 |
| M006 | N/A | 无编译期布尔常量 |
| M007 | ✅ | 无空 catch 块 |
| M008 | N/A | 无重写 equals |
| M009 | N/A | 无不兼容类型 equals |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无类型转换运算 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无字段隐藏 |
| M016 | ✅ | 无依赖系统默认时区的方法（SQL 使用 NOW() 由 DB 处理时区） |
| M017 | N/A | 测试方法均有 @Test |
| M018 | N/A | 无手动 lock |
| M019 | N/A | 无 switch 枚举 |
| M020 | ✅ | 所有重写方法均有 @Override |
| M021 | N/A | 无 equals 重写 |
| M022 | N/A | 无 Optional.of(null) |
| M023 | N/A | 无 Object.toString 无意义调用 |
| M024 | N/A | 无 Optional.get() |
| M025 | N/A | 无 final 类 protected 成员 |
| M026 | ✅ | `@Mock` 未声明为 static |
| M027 | ✅ | `LoginContextHolder.java:16` ThreadLocal 声明为 `private static final` |
| I001 | ✅ | 测试断言了错误码和消息（`TodoServiceImplTest.java:150-151,182-183,199-200`），非仅判断类型 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | ✅ | 使用 `LocalDateTime`，未使用 `java.util.Date` |
| I005 | N/A | 使用 JUnit 5 |
| I006 | N/A | 无 setUp |
| I007 | N/A | 无 tearDown |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 统计项 |
| I010 | N/A | 无 @RunWith(PandoraBootRunner) |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | `customized-checklist.md` 仅含示例项 U1.1，未启用自定义规则 |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. **G17.1** — 功能开关使用 `@Value` 静态注入（`TodoController.java:35`），不支持运行时切换。design §7.2 明确要求"开关即时生效"，G17.1 要求"不需重启/发布"。需改用 `@RefreshScope` 或配置中心动态配置。
  2. **G16.1** — 核心链路 createTodo 无指标埋点（调用次数/成功率/耗时 P99）。design §6.5/§7.1 明确要求。需引入 Micrometer 等指标采集。
- **P1/P2**：
  1. **P1 S9.1** — DB 凭证硬编码 `application.yml:9-10`（root/root），应使用环境变量或配置中心。
  2. **P1 S9.2** — Service 层日志输出原始 creator（`TodoServiceImpl.java:64-65,75`），design 要求脱敏。
  3. **P1 S8.1/G14.2** — 登录态与 tenant_id 来自客户端可伪造的请求头（`LoginInterceptor.java:41,52`），design 注明为 SSO 模拟，生产前须接入真实鉴权。
  4. **P2 A3.4** — `LoginInterceptor.java:40,62` 方法签名超 120 字符（HandlerInterceptor 接口方法，可折行处理）。
- **一句话**：核心功能实现完整、代码结构清晰、单测覆盖良好，但功能开关缺乏运行时切换能力且缺少监控埋点，需修复后方可合并。

---

## 7.1 问题片段（必填）

### P0-1: G17.1 功能开关不支持运行时切换

- **P0** `G17.1` `src/main/java/com/todoapp/todo/api/controller/TodoController.java:35` — `@Value` 静态注入，修改 `feature.todo.create.enabled` 需重启应用，design §7.2 要求"开关即时生效"。
  片段范围：`src/main/java/com/todoapp/todo/api/controller/TodoController.java:33-57`

```java
L33|    private final TodoService todoService;
L34|
L35|    @Value("${feature.todo.create.enabled:true}")
L36|    private boolean todoCreateEnabled;
L37|
L38|    public TodoController(TodoService todoService) {
L39|        this.todoService = todoService;
L40|    }
L41|
L42|    /**
L43|     * 新增待办事项
L44|     *
L45|     * <p>POST /api/todo/create
L46|     *
L47|     * @param apiRequest API 请求（title, description）
L48|     * @return 通用响应，data 包含新建待办 ID
L49|     */
L50|    @PostMapping("/create")
L51|    public ApiResponse<TodoCreateData> create(@Valid @RequestBody TodoCreateApiRequest apiRequest) {
L52|        // 功能开关：可灰度、可应急
L53|        if (!todoCreateEnabled) {
L54|            logger.warn("todo create feature disabled");
L55|            return ApiResponse.error(TodoErrorCode.FEATURE_DISABLED.getCode(),
L56|                    TodoErrorCode.FEATURE_DISABLED.getMessage());
L57|        }
```

### P0-2: G16.1 核心链路无指标埋点

- **P0** `G16.1` `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:36-77` — createTodo 仅有日志输出，无 Micrometer/Prometheus 指标埋点（调用次数、成功率、处理耗时 P99）。design §6.5/§7.1 明确要求。
  片段范围：`src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:36-77`

```java
L36|    @Override
L37|    public TodoCreateResult createTodo(TodoCreateRequest request) {
L38|        // R01: title 非空且非纯空白
L39|        if (request.getTitle() == null || request.getTitle().isBlank()) {
L40|            throw TodoErrorCode.TITLE_EMPTY.toException();
L41|        }
L42|
L43|        // R02: title 长度 <= 100
L44|        if (request.getTitle().length() > TITLE_MAX_LENGTH) {
L45|            throw TodoErrorCode.TITLE_LENGTH_EXCEEDED.toException();
L46|        }
L47|
L48|        // R03: description 长度 <= 1000，null 视为空串
L49|        String description = request.getDescription() == null ? "" : request.getDescription();
L50|        if (description.length() > DESCRIPTION_MAX_LENGTH) {
L51|            throw TodoErrorCode.DESCRIPTION_LENGTH_EXCEEDED.toException();
L52|        }
L53|
L54|        // 构建数据对象，填充 creator / tenant_id（来源于登录上下文，禁止前端传入）
L55|        TodoDO todo = new TodoDO();
L56|        todo.setTitle(request.getTitle());
L57|        todo.setDescription(description);
L58|        todo.setCreator(request.getCreator());
L59|        todo.setTenantId(request.getTenantId());
L60|
L61|        try {
L62|            todoMapper.insert(todo);
L63|        } catch (Exception e) {
L64|            logger.error("createTodo insert failed, title: {}, creator: {}",
L65|                    request.getTitle(), request.getCreator(), e);
L66|            throw TodoErrorCode.CREATE_FAILED.toException(e);
L67|        }
L68|
L69|        Long id = todo.getId();
L70|        if (id == null) {
L71|            logger.error("createTodo insert returned null id, title: {}", request.getTitle());
L72|            throw TodoErrorCode.CREATE_FAILED.toException();
L73|        }
L74|
L75|        logger.info("createTodo success, id: {}, creator: {}", id, request.getCreator());
L76|        return new TodoCreateResult(id);
L77|    }
```

### P1-1: S9.1 DB 凭证硬编码

- **P1** `S9.1` `src/main/resources/application.yml:8-10` — 数据库用户名密码硬编码为 root/root。
  片段范围：非 Java 文件，`N/A(非 Java)`

```yaml
L8|    url: jdbc:mysql://localhost:3306/todo_app?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
L9|    username: root
L10|    password: root
```

### P1-2: S9.2 日志输出原始 creator 未脱敏

- **P1** `S9.2` `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:64-65` — 日志直接输出 `request.getCreator()` 原始值，design §6.4.3.2 要求脱敏。同样问题存在于第 75 行。
  片段范围：`src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:61-76`

```java
L61|        try {
L62|            todoMapper.insert(todo);
L63|        } catch (Exception e) {
L64|            logger.error("createTodo insert failed, title: {}, creator: {}",
L65|                    request.getTitle(), request.getCreator(), e);
L66|            throw TodoErrorCode.CREATE_FAILED.toException(e);
L67|        }
L68|
L69|        Long id = todo.getId();
L70|        if (id == null) {
L71|            logger.error("createTodo insert returned null id, title: {}", request.getTitle());
L72|            throw TodoErrorCode.CREATE_FAILED.toException();
L73|        }
L74|
L75|        logger.info("createTodo success, id: {}, creator: {}", id, request.getCreator());
L76|        return new TodoCreateResult(id);
```

### P1-3: S8.1/G14.2 登录态来自客户端可伪造请求头

- **P1** `S8.1`/`G14.2` `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:41,52` — userId 与 tenantId 均来自请求头，客户端可伪造。design 注明为 SSO 模拟，生产前须替换。
  片段范围：`src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:40-58`

```java
L40|    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
L41|        String userId = request.getHeader(HEADER_USER_ID);
L42|        if (userId == null || userId.isBlank()) {
L43|            logger.warn("login check failed, uri: {}", request.getRequestURI());
L44|            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
L45|            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
L46|            response.setCharacterEncoding("UTF-8");
L47|            ApiResponse<Void> errorResponse = ApiResponse.error("AUTH_001", "未登录");
L48|            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
L49|            return false;
L50|        }
L51|
L52|        String tenantId = request.getHeader(HEADER_TENANT_ID);
L53|        if (tenantId == null) {
L54|            tenantId = CommonConstants.DEFAULT_TENANT_ID;
L55|        }
L56|
L57|        LoginContextHolder.set(new LoginContext(userId, tenantId));
L58|        return true;
L59|    }
```

### P2-1: A3.4 方法签名超 120 字符

- **P2** `A3.4` `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:40` — preHandle 方法签名 133 字符（HandlerInterceptor 接口方法）。
  片段范围：`src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:39-42`

```java
L39|    @Override
L40|    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
L41|        String userId = request.getHeader(HEADER_USER_ID);
L42|        if (userId == null || userId.isBlank()) {
```

### P2-2: A3.4 方法签名超 120 字符

- **P2** `A3.4` `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:62` — afterCompletion 方法签名 137 字符（HandlerInterceptor 接口方法）。
  片段范围：`src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:61-64`

```java
L61|    @Override
L62|    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
L63|        LoginContextHolder.clear();
L64|    }
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `G17.1` `src/main/java/com/todoapp/todo/api/controller/TodoController.java:35` — 将功能开关 `@Value` 改为 `@RefreshScope` 动态配置或接入配置中心，支持运行时切换不需重启
- [ ] **P0** `G16.1` `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:36-77` — 为 createTodo 核心链路引入 Micrometer 指标埋点（调用次数 Counter、成功率、处理耗时 Timer/P99），满足 design §6.5/§7.1 监控要求

### P1

- [ ] **P1** `S9.1` `src/main/resources/application.yml:9-10` — 将数据库凭证改为环境变量（如 `${DB_USERNAME}` / `${DB_PASSWORD}`）或配置中心获取
- [ ] **P1** `S9.2` `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:64-65,75` — 对日志中的 creator 做脱敏处理（复用 `LoginContext.maskUserId` 逻辑或提取为公共工具方法），满足 design §6.4.3.2
- [ ] **P1** `S8.1`/`G14.2` `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:41,52` — 生产部署前接入真实 SSO/IAM 鉴权，替换请求头模拟方案；或在网关层校验请求头来源可信性

### P2（可选）

- [ ] **P2** `A3.4` `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java:40,62` — 对超长方法签名折行处理，使行宽 ≤ 120 字符
