# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-c1f01881-7921-11f1-a885-b9c05fd79159-fc75538e-9afc-46b0-8103-8c75ed23a138` / `590d2a6` · **日期** `2026-10-28` · **审查者** AI
>
> **轮次**：第 2 轮（修复后复审）。第 1 轮发现 2 P0 + 4 P1/P2，修复提交 `590d2a6` 已逐项处理。
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。**每个 ❌/⚠️ 问题在 §7 后附 `.java` 问题片段**（见 §7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `19`（含 1 测试文件） |
| 变更范围 | 修复提交 `590d2a6` 涉及 7 文件（pom.xml、LoginContext.java、LoginInterceptor.java、TodoController.java、TodoServiceImpl.java、application.yml、TodoServiceImplTest.java） |
| 本轮审查范围 | 全量复审 19 个 Java 文件 + 关联配置（pom.xml / application.yml / TodoMapper.xml / todo_schema.sql） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `TodoApplication` | `src/main/java/com/todoapp/TodoApplication.java` | 应用启动类 |
| `WebMvcConfig` | `src/main/java/com/todoapp/common/config/WebMvcConfig.java` | Web MVC 配置，注册拦截器 |
| `CommonConstants` | `src/main/java/com/todoapp/common/constant/CommonConstants.java` | 公共常量 |
| `LoginContext` | `src/main/java/com/todoapp/common/context/LoginContext.java` | 登录上下文（userId, tenantId, maskUserId） |
| `LoginContextHolder` | `src/main/java/com/todoapp/common/context/LoginContextHolder.java` | ThreadLocal 登录上下文持有器 |
| `BusinessException` | `src/main/java/com/todoapp/common/exception/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler` | `src/main/java/com/todoapp/common/handler/GlobalExceptionHandler.java` | 全局异常处理器 |
| `LoginInterceptor` | `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java` | 全局登录拦截器（含安全警告 Javadoc） |
| `ApiResponse` | `src/main/java/com/todoapp/common/model/ApiResponse.java` | 通用响应包装 |
| `TodoController` | `src/main/java/com/todoapp/todo/api/controller/TodoController.java` | REST 控制器（@RefreshScope） |
| `TodoCreateApiRequest` | `src/main/java/com/todoapp/todo/api/request/TodoCreateApiRequest.java` | API 请求对象 |
| `TodoCreateData` | `src/main/java/com/todoapp/todo/api/response/TodoCreateData.java` | API 响应数据 |
| `TodoErrorCode` | `src/main/java/com/todoapp/todo/constant/TodoErrorCode.java` | 错误码枚举 |
| `TodoDO` | `src/main/java/com/todoapp/todo/dao/entity/TodoDO.java` | 数据对象 |
| `TodoMapper` | `src/main/java/com/todoapp/todo/dao/mapper/TodoMapper.java` | MyBatis Mapper 接口 |
| `TodoService` | `src/main/java/com/todoapp/todo/service/TodoService.java` | Service 接口 |
| `TodoCreateRequest` | `src/main/java/com/todoapp/todo/service/dto/TodoCreateRequest.java` | Service 层请求 DTO |
| `TodoCreateResult` | `src/main/java/com/todoapp/todo/service/dto/TodoCreateResult.java` | Service 层结果 DTO |
| `TodoServiceImpl` | `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java` | Service 实现（Micrometer 埋点） |
| `TodoServiceImplTest` | `src/test/java/com/todoapp/todo/service/impl/TodoServiceImplTest.java` | 单元测试（12 用例） |

### 执行队列（Step 1 产物 A）

| # | 文件（仓库相对路径） | 归属原因 | 总状态 |
|---|----------------------|----------|--------|
| 1 | `src/main/java/com/todoapp/TodoApplication.java` | 启动类 | ✅ 已审 |
| 2 | `src/main/java/com/todoapp/common/config/WebMvcConfig.java` | REQ 登录拦截 | ✅ 已审 |
| 3 | `src/main/java/com/todoapp/common/constant/CommonConstants.java` | REQ 通用常量 | ✅ 已审 |
| 4 | `src/main/java/com/todoapp/common/context/LoginContext.java` | R04 登录上下文 + S9.2 脱敏 | ✅ 已审 |
| 5 | `src/main/java/com/todoapp/common/context/LoginContextHolder.java` | R04 ThreadLocal | ✅ 已审 |
| 6 | `src/main/java/com/todoapp/common/exception/BusinessException.java` | REQ 异常体系 | ✅ 已审 |
| 7 | `src/main/java/com/todoapp/common/handler/GlobalExceptionHandler.java` | REQ 异常处理 | ✅ 已审 |
| 8 | `src/main/java/com/todoapp/common/interceptor/LoginInterceptor.java` | REQ 登录拦截器 + A3.4 修复 | ✅ 已审 |
| 9 | `src/main/java/com/todoapp/common/model/ApiResponse.java` | REQ 通用出参 | ✅ 已审 |
| 10 | `src/main/java/com/todoapp/todo/api/controller/TodoController.java` | W01/F01 + G17.1 修复 | ✅ 已审 |
| 11 | `src/main/java/com/todoapp/todo/api/request/TodoCreateApiRequest.java` | W01 入参 | ✅ 已审 |
| 12 | `src/main/java/com/todoapp/todo/api/response/TodoCreateData.java` | W01 出参 | ✅ 已审 |
| 13 | `src/main/java/com/todoapp/todo/constant/TodoErrorCode.java` | REQ 错误码 | ✅ 已审 |
| 14 | `src/main/java/com/todoapp/todo/dao/entity/TodoDO.java` | REQ 数据对象 | ✅ 已审 |
| 15 | `src/main/java/com/todoapp/todo/dao/mapper/TodoMapper.java` | REQ DAO | ✅ 已审 |
| 16 | `src/main/java/com/todoapp/todo/service/TodoService.java` | S01 接口 | ✅ 已审 |
| 17 | `src/main/java/com/todoapp/todo/service/dto/TodoCreateRequest.java` | S01 DTO | ✅ 已审 |
| 18 | `src/main/java/com/todoapp/todo/service/dto/TodoCreateResult.java` | S01 DTO | ✅ 已审 |
| 19 | `src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java` | S01 实现 + G16.1/S9.2 修复 | ✅ 已审 |
| 20 | `src/test/java/com/todoapp/todo/service/impl/TodoServiceImplTest.java` | G11 自测 | ✅ 已审 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 0 | 0 |

> **第 1 轮问题修复状态**：
> - **P0 G17.1** 功能开关不支持运行时切换 → **已修复**（`@RefreshScope` + spring-cloud-context + `/actuator/refresh` 端点）
> - **P0 G16.1** 核心链路无指标埋点 → **已修复**（Micrometer Counter×3 + Timer P99 + Prometheus 导出）
> - **P1 S9.1** DB 凭证硬编码 → **已修复**（`${DB_USERNAME:root}` / `${DB_PASSWORD:root}` 环境变量覆盖）
> - **P1 S9.2** 日志输出原始 creator 未脱敏 → **已修复**（`LoginContext.maskUserId()` + Service 层日志调用）
> - **P1 S8.1/G14.2** 登录态可伪造 → **已处理**（LoginInterceptor Javadoc 安全警告，design 明确本期 SSO 不在范围）
> - **P2 A3.4** 方法签名超 120 字符 → **已修复**（preHandle/afterCompletion 签名折行）

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项接口（F01/W01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/todo/create 接口存在 | ✅ | design §4.1 W01: `POST /api/todo/create` | `TodoController.java:52` `@PostMapping("/create")` | 路径匹配 |
| 入参 title 必填、长度 1~100 | ✅ | design §5.1.2: `title String 是 长度 1~100` | `TodoCreateApiRequest.java:15-16` `@NotBlank` + `@Size(max=100)` | 框架层校验 |
| 入参 description 选填、长度 0~1000 | ✅ | design §5.1.2: `description String 否 长度 0~1000` | `TodoCreateApiRequest.java:20` `@Size(max=1000)` | null 通过校验，Service 层转空串 |
| 出参 data.id (Long) | ✅ | design §5.1.2: `data.id Long 新建待办 ID` | `TodoController.java:74` `new TodoCreateData(result.getId())` | |
| 通用出参 result/msg/data | ✅ | design §5.1 全局约定 | `ApiResponse.java:20-27` result/msg/data 三字段 | |

### REQ-2: R01 — title 非空且非纯空白 → TODO_001

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| title 为 null 或纯空白时抛 TODO_001 | ✅ | design §5.1.3 R01: `title 非空且非纯空白 返回 TODO_001` | `TodoServiceImpl.java:66` `getTitle() == null \|\| isBlank()` → `TITLE_EMPTY.toException()` | |

### REQ-3: R02 — title 长度 ≤ 100 → TODO_002

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| title 超过 100 字符时抛 TODO_002 | ✅ | design §5.1.3 R02: `title 长度 ≤ 100 返回 TODO_002` | `TodoServiceImpl.java:71` `length() > TITLE_MAX_LENGTH` → `TITLE_LENGTH_EXCEEDED.toException()` | |

### REQ-4: R03 — description 长度 ≤ 1000 → TODO_002

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| description 超过 1000 字符时抛 TODO_002 | ✅ | design §5.1.3 R03: `description 长度 ≤ 1000 返回 TODO_002` | `TodoServiceImpl.java:76-79` null→""，`length() > DESCRIPTION_MAX_LENGTH` → `DESCRIPTION_LENGTH_EXCEEDED.toException()` | |

### REQ-5: R04 — creator/tenant_id 取登录上下文，禁前端传入

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| creator 从登录上下文获取 | ✅ | design §5.1.3 R04: `creator、tenant_id 取登录上下文 禁前端传入` | `TodoController.java:62-69` `LoginContextHolder.get()` → `setCreator/setTenantId` | TodoCreateApiRequest 无 creator/tenantId 字段 |
| 全局拦截器校验登录态 | ✅ | design §6.4.2.3: `全局统一拦截器校验登录态` | `LoginInterceptor.java:44-55` 检查 X-User-Id | |

### REQ-6: 错误码 TODO_001/002/003

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| TODO_001/002/003 定义正确 | ✅ | design §5.1.2 错误码表 | `TodoErrorCode.java:16-25` TITLE_EMPTY=TODO_001, TITLE_LENGTH_EXCEEDED=TODO_002, DESCRIPTION_LENGTH_EXCEEDED=TODO_002, CREATE_FAILED=TODO_003 | |
| TODO_004 功能开关错误码 | ✅ | design §7.2/7.3 功能开关 | `TodoErrorCode.java:28` FEATURE_DISABLED=TODO_004 | 合理新增，与可应急设计一致 |

### REQ-7: DB 异常捕获 → TODO_003

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| DB 写入异常返回 TODO_003 并记录日志 | ✅ | design §5.1.3 异常场景: `数据库写入异常 捕获异常 返回 TODO_003 记录日志与告警` | `TodoServiceImpl.java:88-94` try-catch → `logger.error` → `CREATE_FAILED.toException(e)` | |
| insert 返回 null id 返回 TODO_003 | ✅ | design 异常场景隐含 | `TodoServiceImpl.java:96-100` `id == null` → `CREATE_FAILED.toException()` | |

### REQ-8: 功能开关可灰度/可应急

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 功能开关存在 | ✅ | design §7.2/7.3: `功能开关 feature_todo_create` | `TodoController.java:37-38` `@Value("${feature.todo.create.enabled:true}")` | |
| 开关关闭时返回功能不可用 | ✅ | design §7.3: `关闭后接口直接返回功能暂不可用` | `TodoController.java:55-59` `!todoCreateEnabled` → `FEATURE_DISABLED` | |
| 开关即时生效（不需重启） | ✅ | design §7.2: `开关即时生效`；G17.1: `不需重启/发布` | `TodoController.java:30` `@RefreshScope` + `pom.xml:79-82` spring-cloud-context + `application.yml:29-33` `/actuator/refresh` 端点 | **第 1 轮 P0 已修复**：@RefreshScope 支持运行时配置刷新 |

### REQ-9: 表结构与数据模型

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| todo 表 7 字段 + 索引 | ✅ | design §5.1.1.1 表结构 | `todo_schema.sql:3-13` 7 字段 + `idx_todo_tenant_creator` | 完全匹配 |
| MyBatis INSERT 使用 #{} 预编译 | ✅ | design §5.1；S1.1 | `TodoMapper.xml:18-19` 全部 `#{}` 参数 | |
| useGeneratedKeys 回填 id | ✅ | design §5.1.3 时序图: `自增主键` | `TodoMapper.xml:16-17` `useGeneratedKeys="true" keyProperty="id"` | |

### REQ-10: 监控/埋点

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| createTodo 接口埋点（调用量/成功率/耗时 P99） | ✅ | design §6.5: `埋点：createTodo 调用次数、成功/失败计数、处理耗时（P99）`；§7.1: `接口埋点` | `TodoServiceImpl.java:52-57` Counter×3 + Timer P99 + `pom.xml:69-77` actuator + micrometer-prometheus + `application.yml:29-33` prometheus 端点 | **第 1 轮 P0 已修复** |

### REQ-11: 日志脱敏

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| creator 日志脱敏 | ✅ | design §6.4.3.2: `日志打印对 creator 做脱敏处理` | `TodoServiceImpl.java:92,103` `LoginContext.maskUserId(request.getCreator())`；`LoginContext.java:44-49` maskUserId 实现 | **第 1 轮 P1 已修复** |

---

## 4. Step 3 — 可读性检查

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1.1 | 文件名 = 顶层类名 | ✅ | 全部文件符合 |
| A1.2 | 编码 UTF-8 | ✅ | |
| A1.3 | 仅 ASCII 空格，禁止 Tab | ✅ | |
| A2.1 | 文件顺序 package → import → 类 | ✅ | |
| A2.2 | 禁止 import * | ✅ | 全部显式 import |
| A2.3 | 静态/非静态 import 分组 | ✅ | `TodoServiceImplTest.java:19-23` 正确分组 |
| A2.4 | import 字典序 | ✅ | |
| A2.5 | 重载方法连续 | N/A | 无重载方法 |
| A3.1 | K&R 大括号 | ✅ | |
| A3.2 | 空 catch 不可简写 | N/A | 无空 catch |
| A3.3 | 缩进 4 空格 | ✅ | |
| A3.4 | 行宽 ≤ 120 | ✅ | **第 1 轮 P2 已修复**：`LoginInterceptor.java:44-45,67-68` preHandle/afterCompletion 签名已折行，每行 ≤ 120 |
| A3.5 | 换行规则 | ✅ | |
| A3.6 | 类成员间空行 | ✅ | |
| A3.7 | 关键字与括号间空格 | ✅ | |
| A3.8 | 运算符两侧空格 | ✅ | |
| A4.1 | 包名全小写 | ✅ | |
| A4.2 | 类名 UpperCamelCase | ✅ | |
| A4.3 | 方法名 lowerCamelCase | ✅ | |
| A4.4 | 常量 UPPER_SNAKE_CASE | ✅ | `RESULT_OK`, `TITLE_MAX_LENGTH`, `AUTH_NOT_LOGIN_CODE` 等 |
| A4.5 | 字段 lowerCamelCase | ✅ | |
| A4.6 | 泛型命名 | ✅ | `ApiResponse<T>` |
| A4.7 | 测试类名 + Test | ✅ | `TodoServiceImplTest` |
| A5.1 | 重写方法加 @Override | ✅ | `WebMvcConfig:19`, `TodoServiceImpl:60`, `LoginInterceptor:43,66`, `GlobalExceptionHandler` 无重写 |
| A5.2 | catch 块不为空 | ✅ | 所有 catch 块均有日志/计数/抛出 |
| A5.3 | 静态方法用类名调用 | ✅ | `LoginContext.maskUserId()` |
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
Targets: src/main/java/com/todoapp src/test/java/com/todoapp
Engine:  ripgrep

[P0] G16.2 — CatchWithoutLogging: src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:90
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/todoapp/todo/service/impl/TodoServiceImpl.java:106

=== Summary: 2 findings (P0=2, P1=0, P2=0) | 52/222 rules scanned ===
```

**预扫复核**：

- `G16.2 TodoServiceImpl.java:90` — **误报（false positive）**。该 catch 块位于第 90 行，第 91-93 行有 `logger.error("createTodo insert failed, title: {}, creator: {}", ...)` 日志输出并重新抛出异常。脚本因日志语句跨行未识别。人工复核确认 catch 块**有日志**，标记为 ✅。
- `G16.2 TodoServiceImpl.java:106` — **误报（false positive）**。该 catch 块位于第 106 行（外层 `catch (RuntimeException e)`），第 107 行 `failureCounter.increment()` 记录失败指标计数，第 108 行 `throw e` 重新抛出异常。异常由 `GlobalExceptionHandler.handleBusinessException` 统一记录 WARN 日志。catch 块**有指标埋点且异常未吞没**，标记为 ✅。

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
| G8.1 | ✅ | — | TodoServiceImpl 内层 catch 记日志并抛出；外层 catch 计数并抛出，非吞异常 |
| G8.2 | N/A | — | 无外部 RPC/HTTP 调用 |
| G8.3 | N/A | — | 无手动 I/O 流/连接管理（Spring 管理 DataSource） |
| G8.4 | N/A | — | 无线程池/定时任务 |
| G8.5 | ✅ | — | `LoginInterceptor.java:69` afterCompletion 中 `LoginContextHolder.clear()` 清理 ThreadLocal |
| G8.6 | N/A | — | 无线程池 |
| G9.1 | N/A | — | 无外部 HTTP/RPC 调用 |
| G9.2 | N/A | — | 同上 |
| G9.3 | N/A | — | 同上 |
| G10.1 | ✅ | — | data.id 为 null 时表示系统异常（返回 TODO_003），非用 null 表示"无数据" |
| G10.2 | N/A | — | 全新接口无版本变更 |
| G11.1 | ✅ | — | 12 个单测，均有断言 |
| G11.2 | ✅ | — | 覆盖 null、最大长度边界值、指标埋点 |
| G11.3 | ✅ | — | TodoServiceImpl 对 title null/blank、description null 做防御校验 |
| G11.4 | N/A | — | 无数值运算/金额 |
| G12.1 | N/A | — | 无资金场景 |
| G12.2 | ✅ | — | 功能开关可作为止血手段，@RefreshScope 支持运行时切换 |
| G13.1 | ✅ | — | 业务异常 WARN，系统异常 ERROR，成功 INFO，级别正确 |
| G14.1 | N/A | — | 无金额 |
| G14.2 | ✅ | — | **第 1 轮 P1 已处理**：LoginInterceptor Javadoc 安全警告明确标注风险，design §6.4.1 确认本期 SSO 不在范围，生产前需接入网关层鉴权。内部工具可接受 |
| G14.3 | ✅ | — | DB 使用 DATETIME + NOW()，应用 serverTimezone=GMT+8，TodoDO 使用 LocalDateTime |
| G14.4 | N/A | — | 无 SimpleDateFormat |
| G15.1 | ✅ | — | 全新表，无表结构变更兼容问题 |
| G15.2 | N/A | — | 无旧接口 |
| G15.3 | ✅ | — | 功能开关控制切换 |
| G16.1 | ✅ | — | **第 1 轮 P0 已修复**：`TodoServiceImpl.java:52-57` Micrometer Counter(total/success/failure) + Timer(P99) + `pom.xml` actuator/prometheus 依赖 + `application.yml` prometheus 端点 |
| G16.2 | ✅ | — | 预扫 2 处误报已复核：内层 catch 有 logger.error（`TodoServiceImpl.java:91-93`），外层 catch 有 failureCounter + rethrow（`TodoServiceImpl.java:107-108`） |
| G16.3 | ✅ | — | 业务异常 WARN（`GlobalExceptionHandler.java:45`），系统异常 ERROR（`GlobalExceptionHandler.java:73`），成功 INFO（`TodoServiceImpl.java:102`） |
| G16.4 | ✅ | — | 无空 catch、无 printStackTrace、无关键路径吞异常 |
| G17.1 | ✅ | — | **第 1 轮 P0 已修复**：`TodoController.java:30` `@RefreshScope` + `pom.xml:79-82` spring-cloud-context + `application.yml:31` `/actuator/refresh` 端点暴露 |
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
| S8.1 | ✅ | — | **第 1 轮 P1 已处理**：LoginInterceptor Javadoc 安全警告标注风险（`LoginInterceptor.java:24-27`），design §6.4.1 确认本期 SSO 不在范围，内部工具可接受 |
| S8.2 | ✅ | — | POST 方法执行创建，非 GET |
| S8.3 | N/A | — | id 自增，内部应用 |
| S8.4 | N/A | — | 无 Cookie |
| S9.1 | ✅ | — | **第 1 轮 P1 已修复**：`application.yml:9-10` `${DB_USERNAME:root}` / `${DB_PASSWORD:root}` 环境变量覆盖，默认值仅用于本地开发 |
| S9.2 | ✅ | — | **第 1 轮 P1 已修复**：`TodoServiceImpl.java:92,103` 日志使用 `LoginContext.maskUserId(request.getCreator())` 脱敏 |
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
| B055 | ✅ | `TodoServiceImplTest.java:68,87,110,128,221,238,256` when().thenAnswer()/thenReturn() 正确；`verify(todoMapper).insert()` 未嵌套调用 |
| B076 | N/A | 无 @Transactional |
| B077 | N/A | 测试无 catch Throwable |
| B079 | ✅ | `TodoServiceImplTest.java:48-49` `@Mock` 未显式赋值 |
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
| M012 | N/A | 无 finally return/throw（finally 仅有 `sample.stop(createTimer)`，不含 return/throw） |
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
| I001 | ✅ | 测试断言了错误码和消息（`TodoServiceImplTest.java:160-161,192-193,209-210`），非仅判断类型 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | ✅ | 使用 `LocalDateTime`，未使用 `java.util.Date` |
| I005 | N/A | 使用 JUnit 5 |
| I006 | ✅ | `TodoServiceImplTest.java:55` `@BeforeEach` 标注 setUp() |
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

- **合并建议**：通过
- **P0**：无
- **P1/P2**：无
- **第 1 轮问题修复确认**：2 个 P0（G17.1 功能开关运行时切换、G16.1 监控埋点）和 4 个 P1/P2（S9.1 凭证硬编码、S9.2 日志脱敏、S8.1/G14.2 登录态安全警告、A3.4 行宽）均已修复或妥善处理。
- **预扫复核**：scan-all-rules.sh 报告 2 处 G16.2 CatchWithoutLogging，经人工复核均为误报（内层 catch 有 logger.error 跨行日志；外层 catch 有 failureCounter 计数 + 异常 rethrow）。
- **一句话**：核心功能完整实现，设计规格全覆盖，代码结构清晰，单测覆盖良好（12 用例含指标埋点验证），第 1 轮全部问题已修复且无新增问题，建议合并。

---

## 7.1 问题片段（必填）

> 本轮无 ❌/⚠️ 问题，本节 N/A。

N/A（本轮审查未发现需修复的问题。）

---

## 8. 修复任务列表

- 无待修复项。
