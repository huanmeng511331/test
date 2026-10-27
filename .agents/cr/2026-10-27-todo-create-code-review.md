# Code Review Report

> **Change** `todo-create` · **Commit** `36cb0d4` · **日期** `2026-10-27` · **审查者** AI

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+820 / -0`（commit 36cb0d4，17 文件；3 个非 Java 资源/文档跳过） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `TodoController` | `src/main/java/com/alipay/todo/api/controller/TodoController.java` | REST 入口（`POST /todos`） |
| `TodoCreateRequest` | `src/main/java/com/alipay/todo/api/request/TodoCreateRequest.java` | 请求 DTO + 校验注解 |
| `TodoConstants` | `src/main/java/com/alipay/todo/common/constant/TodoConstants.java` | 长度边界常量 |
| `TodoErrorCodeEnum` | `src/main/java/com/alipay/todo/common/exception/TodoErrorCodeEnum.java` | 错误码 |
| `TodoException` | `src/main/java/com/alipay/todo/common/exception/TodoException.java` | 业务异常 |
| `ApiResponse` | `src/main/java/com/alipay/todo/common/model/ApiResponse.java` | 统一响应体 |
| `TodoDO` | `src/main/java/com/alipay/todo/dao/entity/TodoDO.java` | 数据对象 |
| `TodoMapper` | `src/main/java/com/alipay/todo/dao/mapper/TodoMapper.java` | DAO 接口 |
| `TodoVO` | `src/main/java/com/alipay/todo/model/vo/TodoVO.java` | 视图对象 |
| `TodoService` | `src/main/java/com/alipay/todo/service/TodoService.java` | 服务接口 |
| `TodoServiceImpl` | `src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java` | 服务实现 |
| `TodoServiceImplTest` | `src/test/java/com/alipay/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

非 Java 跳过：`TodoMapper.xml`、`todo_item.sql`、3 个 `.md` 文档。

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 5 | 3 |

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项（含描述可空）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 合法 `{name, description}` 落库并返回含 id 的 VO | ✅ | 需求「核心功能：新增待办事项；任务信息：事项名称和描述」 | `TodoServiceImpl.java:30-42`、`TodoMapper.xml:14-18`、测试 `:44-66` | 全链路完整，`useGeneratedKeys` 回填 id |
| description 为 null | ✅ | impl.md「should_createTodo_when_descriptionIsNull」 | `TodoServiceImpl.java:52`、测试 `:69-83` | 允许空描述 |

### REQ-2: 输入校验

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 名称为 null/空白 | ✅ | impl.md `should_throwException_when_nameIsNull/Blank` | `TodoServiceImpl.java:46-48`、测试 `:86-99` | 抛 `TODO_NAME_BLANK` |
| 名称/描述超长 | ✅ | impl.md 超长用例 | `TodoServiceImpl.java:49-54`、测试 `:101-119` | 抛对应超长错误码 |

### REQ-3: 最小闭环仅创建

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 仅暴露创建能力 | ✅ | 需求「最小闭环：仅创建」 | `TodoMapper.java:16` 仅 `insert`；`TodoController.java:32` 仅 `@PostMapping` | 无查询/更新/删除 |

### REQ-4: 统一错误响应

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 错误码+提示封装 | ✅ | impl.md「统一响应体/错误码」 | `ApiResponse.java:28-35`、`TodoException.java:12-23` | 结构完备 |

## 4. Step 3 — 可读性检查

| 结果 | 说明 |
|------|------|
| ⚠️ | 整体达标；1 项 P2：`TodoController.java:3-14` import 顺序未按 ASCII 字典序（A2.4），建议调整（例如 `api.request` → `api` 内部 → `common.exception` → `common.model` → `model.vo` → `service`）。A1/A3–A7 均 ✅，脚本 A* 无命中 |

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1/P2 | ⚠️ P1 G2.1 无幂等；⚠️ P1 G14.3 默认时区；⚠️ P1 G16.1 无埋点；⚠️ P2 G11.2 缺 insert 返回 0 用例；⚠️ P2 G16.3 无日志 |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1/P2 | ⚠️ P1 S8.1 未鉴权；⚠️ P2 S8.3 自增主键可预测；SQL 预编译 S1.1 ✅ |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I | ❌ | P1 | ❌ P1 M016 `LocalDateTime.now()`（脚本命中，`TodoServiceImpl.java:33`）；其余 119 条无命中/N/A |

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | — | 仅示例项 U1.1（`TodoController.java:33` 已用 `@Valid`）；其余 U* 未启用，标 `N/A(未启用自定义规则)` |

## 7. 结论

- **合并建议**：修复后合并（无 P0 阻塞）
- **P0**：无
- **P1/P2**：
  1. P1 M016 / G14.3 —— 默认时区时间戳（`TodoServiceImpl.java:33`）
  2. P1 G2.1 —— 创建接口无幂等保护
  3. P1 S8.1 —— Controller 未接入鉴权/授权
  4. P1 G16.1 —— 核心链路无监控埋点
  5. P2 G16.3 —— 无日志输出
  6. P2 A2.4 —— import 顺序未按字典序（`TodoController.java:3-14`）
  7. P2 S8.3 —— 自增主键可预测
  8. P2 G11.2 —— 缺 insert 返回 0 行失败路径用例
- **一句话**：最小闭环功能完整、分层清晰、校验与单测到位，无 P0 阻塞；主要在幂等、时区、鉴权、可观测性等健壮性层面有 5 项 P1 待补强。

## 7.1 问题片段（必填）

- **P1** `M016 / G14.3` `TodoServiceImpl.java:33` — `LocalDateTime.now()` 依赖系统默认时区，服务端时间与 DB `CURRENT_TIMESTAMP` 可能不一致，跨区部署产生漂移。
  片段范围：`src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java:29-40`

```java
L28|    @Override
L29|    @Transactional(rollbackFor = Exception.class)
L30|    public TodoVO createTodo(String name, String description) {
L31|        validate(name, description);
L32|
L33|        LocalDateTime now = LocalDateTime.now();  // 问题：默认系统时区
L34|        TodoDO todoDO = new TodoDO();
L35|        todoDO.setName(name);
L36|        todoDO.setDescription(description);
L37|        todoDO.setGmtCreate(now);
L38|        todoDO.setGmtModified(now);
L39|
L40|        todoMapper.insert(todoDO);
```

- **P1** `G2.1` `TodoServiceImpl.java:30-40` — 写接口无幂等键（无业务单号/去重表/唯一约束），重复提交或重试会产生重复记录。
  片段范围：`src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java:30-42`

```java
L30|    public TodoVO createTodo(String name, String description) {
L31|        validate(name, description);
L32|
L33|        LocalDateTime now = LocalDateTime.now();
L34|        TodoDO todoDO = new TodoDO();
L35|        todoDO.setName(name);
L36|        todoDO.setDescription(description);
L37|        todoDO.setGmtCreate(now);
L38|        todoDO.setGmtModified(now);
L39|
L40|        todoMapper.insert(todoDO);   // 问题：无幂等拦截，重复请求重复落库
L41|
L42|        return toVO(todoDO);
```

- **P1** `S8.1` `TodoController.java:32-36` — Controller 未接入任何鉴权/授权，接口对任意请求开放。
  片段范围：`src/main/java/com/alipay/todo/api/controller/TodoController.java:29-36`

```java
L29|    /**
L30|     * 新增待办事项。
L31|     */
L32|    @PostMapping
L33|    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
L34|        TodoVO vo = todoService.createTodo(request.getName(), request.getDescription());
L35|        return ApiResponse.success(vo);
L36|    }
```

- **P1** `G16.1` `TodoController.java:32-36` — 核心创建链路无成功率/耗时/调用量埋点，无异常日志。
  片段范围：`src/main/java/com/alipay/todo/api/controller/TodoController.java:29-36`

```java
L29|    /**
L30|     * 新增待办事项。
L31|     */
L32|    @PostMapping
L33|    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
L34|        TodoVO vo = todoService.createTodo(request.getName(), request.getDescription());
L35|        return ApiResponse.success(vo);
L36|    }
```

- **P2** `G16.3` `TodoServiceImpl.java:30-43` — 全链路无任何日志，业务失败无法以 WARN 记录、成功无 INFO。
  片段范围：`src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java:28-43`

```java
L28|    @Override
L29|    @Transactional(rollbackFor = Exception.class)
L30|    public TodoVO createTodo(String name, String description) {
L31|        validate(name, description);
L32|
L33|        LocalDateTime now = LocalDateTime.now();
L34|        TodoDO todoDO = new TodoDO();
L35|        todoDO.setName(name);
L36|        todoDO.setDescription(description);
L37|        todoDO.setGmtCreate(now);
L38|        todoDO.setGmtModified(now);
L39|
L40|        todoMapper.insert(todoDO);
L41|
L42|        return toVO(todoDO);
L43|    }
```

- **P2** `A2.4` `TodoController.java:3-14` — import 顺序未按完整引入名 ASCII 字典序排列。
  片段范围：`src/main/java/com/alipay/todo/api/controller/TodoController.java:3-14`

```java
L3 |import com.alipay.todo.api.request.TodoCreateRequest;
L4 |import com.alipay.todo.common.model.ApiResponse;
L5 |import com.alipay.todo.common.exception.TodoException;
L6 |import com.alipay.todo.model.vo.TodoVO;
L7 |import com.alipay.todo.service.TodoService;
L8 |
L9 |import jakarta.validation.Valid;
L10|
L11|import org.springframework.web.bind.annotation.PostMapping;
L12|import org.springframework.web.bind.annotation.RequestBody;
L13|import org.springframework.web.bind.annotation.RequestMapping;
L14|import org.springframework.web.bind.annotation.RestController;
```

- **P2** `S8.3 / G11.2` `TodoMapper.xml:14-18` — 自增主键可预测（S8.3）；另缺「insert 返回 0 行」的失败路径单测（G11.2）。
  片段范围：`src/main/resources/mapper/TodoMapper.xml:14-18`（非 Java，G11.2 对应测试文件）

```xml
L14|    <insert id="insert" parameterType="com.alipay.todo.dao.entity.TodoDO"
L15|            useGeneratedKeys="true" keyProperty="id">
L16|        INSERT INTO todo_item (name, description, gmt_create, gmt_modified)
L17|        VALUES (#{name}, #{description}, #{gmtCreate}, #{gmtModified})
L18|    </insert>
```

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java:33` — 用 `Instant.now()`/UTC 或显式 `ZoneId` 替代 `LocalDateTime.now()`，或将时间交由 DB `CURRENT_TIMESTAMP` 生成。
- [ ] **P1** `src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java:30-40` — 为创建接口增加幂等键（业务单号 + 去重表/唯一约束），防重复提交落库。
- [ ] **P1** `src/main/java/com/alipay/todo/api/controller/TodoController.java:32-36` — 接入鉴权/授权（如登录态校验或内部网关鉴权）。
- [ ] **P1** `src/main/java/com/alipay/todo/api/controller/TodoController.java:32-36` — 为核心创建链路补充成功率/耗时/调用量埋点。

### P2（可选）

- [ ] **P2** `src/main/java/com/alipay/todo/service/impl/TodoServiceImpl.java:28-43` — 补充业务日志（成功 INFO、失败 WARN，含可追溯上下文）。
- [ ] **P2** `src/main/java/com/alipay/todo/api/controller/TodoController.java:3-14` — 按 ASCII 字典序调整 import 顺序。
- [ ] **P2** `src/main/java/com/alipay/todo/api/controller/TodoController.java:32` — 评估主键可预测风险（内部系统可降级，必要时改用 UUID/雪花 id）。
- [ ] **P2** `src/test/java/com/alipay/todo/service/impl/TodoServiceImplTest.java` — 补充 insert 返回 0 行时的失败路径用例。