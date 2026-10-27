# Code Review Report

> **Change** 编码实现 (stage: coding) · **分支/Commit** `AI/task-DEV-…` / `aa1fc34` · **日期** `2026-10-27` · **审查者** AI
>
> 说明：本报告为「代码评审」节点只读审查产物，未修改任何源码；§8 为后续修复任务清单。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+947 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `TodoApplication` | `src/main/java/com/dtazziboot/todo/TodoApplication.java` | 启动类 + MapperScan |
| `TodoConstants` | `src/main/java/com/dtazziboot/todo/common/constant/TodoConstants.java` | 常量 |
| `TodoErrorCodeEnum` | `src/main/java/com/dtazziboot/todo/common/enums/TodoErrorCodeEnum.java` | 错误码 |
| `BusinessException` | `src/main/java/com/dtazziboot/todo/common/exception/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler` | `src/main/java/com/dtazziboot/todo/common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `ApiResponse` | `src/main/java/com/dtazziboot/todo/common/model/ApiResponse.java` | 统一响应 |
| `TodoController` | `src/main/java/com/dtazziboot/todo/controller/TodoController.java` | HTTP 接口 |
| `TodoMapper` | `src/main/java/com/dtazziboot/todo/dao/mapper/TodoMapper.java` | DAO 接口 |
| `TodoCreateRequest` | `src/main/java/com/dtazziboot/todo/model/dto/TodoCreateRequest.java` | 请求 DTO |
| `TodoVO` | `src/main/java/com/dtazziboot/todo/model/dto/TodoVO.java` | 视图对象 |
| `TodoDO` | `src/main/java/com/dtazziboot/todo/model/entity/TodoDO.java` | 数据对象 |
| `TodoService` | `src/main/java/com/dtazziboot/todo/service/TodoService.java` | 服务接口 |
| `TodoServiceImpl` | `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java` | 服务实现 |
| `TodoServiceImplTest` | `src/test/java/com/dtazziboot/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

> 非 Java 文件（7 个）标跳过：`impl.md`、`ARCHITECTURE.md`、`modules/todo/README.md`、`pom.xml`、`application.yml`、`schema.sql`、`TodoMapper.xml`（S1/G15 在 §5 核销）。需人工验证：`mvn compile` / `mvn test`（环境无 JDK/Maven，本次静态审查）。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 合法 name+description，When POST /todos，Then 落库并返回新记录 | ✅ | 需求「核心功能：新增待办事项」；README「POST /todos」 | `TodoController.java:26-30` → `TodoServiceImpl.java:48` → `TodoMapper.xml:14-18` | 链路完整，insert 使用 useGeneratedKeys 回填 id |
| 落库失败时给出可辨识异常 | ✅ | impl.md「异常处理」 | `TodoServiceImpl.java:49-52`；测试 111-127 | rows<=0 抛 SYSTEM_ERROR |

### REQ-2: 任务信息 = 名称 + 描述，名称必填

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name 空/空白拒绝，description 可空 | ✅ | 需求「事项名称和描述」；README 请求示例 | `TodoCreateRequest.java:12-16`；`TodoServiceImpl.java:37-39`；测试 68-108 | 注解 + service 双重校验 |

### REQ-3: 返回统一响应含视图

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| code=200/message=success/data{id,name,description,createTime} | ✅ | README「响应示例」；impl.md「统一 ApiResponse」 | `ApiResponse.java:22-28`；`TodoServiceImpl.java:58-66`；测试 60-63 | createTime 格式 yyyy-MM-dd HH:mm:ss |

### REQ-4: 表结构含必备三字段

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| todo_item 含 id/gmt_create/gmt_modified | ✅ | ARCHITECTURE「必备字段」 | `schema.sql:1-9`；`TodoDO.java:13-21` | DDL 含主键、DEFAULT/ON UPDATE 时间戳 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | **A2.4（P2）** import 未按 ASCII 字典序：`TodoCreateRequest.java:3-5`（jakarta 在 com 前）、`TodoServiceImpl.java:3-16`（`exception`/`enums` 逆序；`java.*` 置于 `org.*` 后）、`TodoServiceImplTest.java:3-4`（`exception`/`enums` 逆序）。无 Tab、无通配 import、行宽、命名、Javadoc 均合规。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1/P2 | G10.1（P1）错误码缺失；G11.2（P2）边界用例不足；G14.3（P1）时区 |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P2 | S8.1（P2）无鉴权；其余 SQL 注入/命令执行/密钥等已扫无命中 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ❌ | P1 | 预扫命中 M016（P1）`LocalDateTime.now()`；脚本其余 151 条 LLM 核销无命中 |

**预扫结果摘要**（scan-all-rules.sh，ripgrep，52/222 条）：仅 `[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:44`。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | — | U1.1（P1 示例项）`@Valid` 已使用；U2 为空，视作「未启用自定义规则」 |

---

## 7. 结论

- **合并建议**：修复后合并（无 P0 阻塞；3 个 P1、3 个 P2 建议修复）
- **P0**：无
- **P1**：
  1. `TodoServiceImpl.java:44` — `LocalDateTime.now()` 未显式时区（M016 / G14.3），与 DB `serverTimezone=UTC` 不一致；
  2. `ApiResponse.java:32` — `error()` 恒 `code=500`，参数校验失败与系统错误无法区分（G10.1）；
  3. `TodoController.java:26-30` / 认证缺失（S8.1，本列为 P2 观察项，接入鉴权后转必改）。
- **P2**：
  1. `TodoServiceImplTest.java` — 缺 name=100 / description=500 长度边界用例（G11.2）；
  2. `TodoController.java:26` — 无鉴权（S8.1，内部用户最小闭环暂可接受）；
  3. import 顺序未按 ASCII 字典序（A2.4：`TodoCreateRequest.java:3-5`、`TodoServiceImpl.java:3-16`、`TodoServiceImplTest.java:3-4`）。
- **一句话**：功能闭环完整、SQL 参数化与异常/日志规范到位，主要遗留为时区一致性、错误码语义与鉴权三个非阻塞项。

---

## 7.1 问题片段（必填）

- **P1** `M016` / `G14.3` `TodoServiceImpl.java:44` — 未显式指定时区，`LocalDateTime.now()` 取 JVM 默认时区，与数据源 `serverTimezone=UTC` 语义可能不一致。
  片段范围：`src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:40-46`

```java
L40|
L41|        TodoDO todoDO = new TodoDO();
L42|        todoDO.setName(request.getName());
L43|        todoDO.setDescription(request.getDescription());
L44|        LocalDateTime now = LocalDateTime.now(); // 问题：未显式指定时区
L45|        todoDO.setGmtCreate(now);
L46|        todoDO.setGmtModified(now);
```

- **P1** `G10.1` `ApiResponse.java:30-37` — `error()` 固定 `code=500`，无法区分 400 参数错误与 500 系统错误。
  片段范围：`src/main/java/com/dtazziboot/todo/common/model/ApiResponse.java:30-37`

```java
L30|    public static <T> ApiResponse<T> error(String errorCode, String message) {
L31|        ApiResponse<T> response = new ApiResponse<>();
L32|        response.setCode(500); // 问题：参数校验失败也被标记为 500
L33|        response.setErrorCode(errorCode);
L34|        response.setMessage(message);
L35|        response.setData(null);
L36|        return response;
L37|    }
```

- **P2** `S8.1` `TodoController.java:26-30` — 创建接口无鉴权，内部用户闭环可接受，对外前须接入。
  片段范围：`src/main/java/com/dtazziboot/todo/controller/TodoController.java:26-30`

```java
L26|    @PostMapping
L27|    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
L28|        TodoVO todoVO = todoService.createTodo(request);
L29|        return ApiResponse.success(todoVO);
L30|    }
```

- **P2** `A2.4` `TodoCreateRequest.java:3-5` — import 分组内未按 ASCII 字典序（`jakarta` 与 `com` 顺序颠倒）。
  片段范围：`src/main/java/com/dtazziboot/todo/model/dto/TodoCreateRequest.java:3-5`

```java
L3| import jakarta.validation.constraints.NotBlank;
L4| import jakarta.validation.constraints.Size;
L5| import com.dtazziboot.todo.common.constant.TodoConstants;
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/dtazziboot/todo/service/impl/TodoServiceImpl.java:44`（M016 / G14.3）— 使用 `LocalDateTime.now(ZoneOffset.UTC)` 或显式指定与 DB 一致的时区，消除时区语义不一致。
- [ ] **P1** `src/main/java/com/dtazziboot/todo/common/model/ApiResponse.java:32`（G10.1）— 为 `error()` 增加 HTTP 语义码（如参数错误 400、系统错误 500），或在 `GlobalExceptionHandler` 中区分返回码。

### P2

- [ ] **P2** `src/main/java/com/dtazziboot/todo/controller/TodoController.java:26`（S8.1）— 接入内部用户认证/鉴权；接入前保持最小闭环说明。
- [ ] **P2** `src/test/java/com/dtazziboot/todo/service/impl/TodoServiceImplTest.java`（G11.2）— 补充 name=100 与 description=500 长度边界测试用例。
- [ ] **P2** `A2.4` — 按 ASCII 字典序重排 import：`TodoCreateRequest.java:3-5`、`TodoServiceImpl.java:3-16`、`TodoServiceImplTest.java:3-4`。