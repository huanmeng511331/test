# Code Review Report

> **Change** `todo 新增待办事项（最小闭环）` · **分支/Commit** `AI/task-DEV-c1f01881-…` / `9128e79` · **日期** `2026-10-27` · **审查者** AI
>
> 已先运行 `scan-all-rules.sh`（Src 目录），命中仅 `[P1] M016` ×6，其余要点并入 §5。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+1229 / -0`（21 个文件全为新增） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoApplication | src/main/java/com/example/todo/TodoApplication.java | 启动类 |
| ErrorCodeEnum | src/main/java/com/example/todo/common/constant/ErrorCodeEnum.java | 错误码枚举 |
| TodoConstants | src/main/java/com/example/todo/common/constant/TodoConstants.java | 模块常量 |
| BusinessException | src/main/java/com/example/todo/common/exception/BusinessException.java | 业务异常 |
| GlobalExceptionHandler | src/main/java/com/example/todo/common/exception/GlobalExceptionHandler.java | 全局异常处理 |
| ApiResponse | src/main/java/com/example/todo/common/result/ApiResponse.java | 统一响应体 |
| TodoItemController | src/main/java/com/example/todo/controller/TodoItemController.java | 控制器 |
| TodoItemMapper | src/main/java/com/example/todo/dao/mapper/TodoItemMapper.java | Mapper 接口 |
| TodoItemDO | src/main/java/com/example/todo/model/do/TodoItemDO.java | 数据对象 |
| TodoItemService | src/main/java/com/example/todo/service/TodoItemService.java | 服务接口 |
| TodoItemRequest | src/main/java/com/example/todo/service/dto/TodoItemRequest.java | 请求 DTO |
| TodoItemVO | src/main/java/com/example/todo/service/dto/TodoItemVO.java | 视图对象 |
| TodoItemServiceImpl | src/main/java/com/example/todo/service/impl/TodoItemServiceImpl.java | 服务实现 |
| TodoItemServiceImplTest | src/test/java/com/example/todo/service/impl/TodoItemServiceImplTest.java | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 4 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /todos 持久化并返回结果 | ✅ | 需求「核心功能：新增待办事项」 | Controller:43 → ServiceImpl:53→59 → Mapper.xml:14-18 | insert（useGeneratedKeys）→ selectById 回查 → VO，链路完整 |

### REQ-2: 事项名称和描述

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name 必填、长度 ≤100；description ≤500 | ✅ | 需求「任务信息：事项名称和描述」 | TodoItemRequest.java:19-27 | `@NotBlank` + `@Size` 与 DB `VARCHAR(100)/(500)` 一致 |

### REQ-3: 最小闭环「仅创建」

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 仅提供创建接口 | ✅ | 需求「最小闭环：仅创建」 | TodoItemController.java:42-46 | 仅一个 `@PostMapping`，无更新/删除/列表 |

### REQ-4: 统一响应契约

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| ApiResponse + id 以 String 返回 | ✅ | docs/ARCHITECTURE.md §7 | TodoItemVO.java:16；ServiceImpl:74 | 避免前端 Long 精度丢失 |

> 未发现功能性不符（无 P0）。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明 |
|------|------|
| ❌ | A2.4 import 顺序：`TodoItemServiceImplTest.java:19`（`ExtendWith` 应排在 `org.mockito.*` 之前） |
| ✅ | 其余 A1–A7：命名、缩进、Javadoc、`@Override`、常量风格均符合 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | reliability-checklist.md G1–G17 | ❌ | P1 | `G16.2`（业务异常未记日志）、`G14.3`（时区） |
| 安全 | security-checklist.md S1–S10 | ❌ | P1 | `S9.1`（明文凭据） |
| Bug 模式 | bug-pattern-checklist.md B/M/I | ❌ | P1 | 预扫 `scan-all-rules.sh` 命中 `M016` ×6；其余 51 条无命中 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | customized-checklist.md U* | ✅ | — | U1.1 示例项满足（`@Valid`）；其余未启用自定义规则 |

---

## 7. 结论

- **合并建议**：修复后合并（无 P0 阻塞，P1/P2 为安全隐患与可维护性优化）
- **P0**：无。
- **P1**：
  1. `M016`/`G14.3` — `LocalDateTime.now()` 未显式时区（ServiceImpl:50-51、Test:68-69、99-100）
  2. `G16.2` — 系统侧业务异常抛出未记录日志（GlobalExceptionHandler:48-52、ServiceImpl:53-62）
  3. `S9.1` — application.yml 明文数据库凭据 `root/root`（application.yml:7-8）
- **P2**：
  1. `A2.4` — 测试类 import 顺序（TodoItemServiceImplTest.java:19）
  2. 空 `setUp()` 方法（TodoItemServiceImplTest.java:46-49）
  3. `gmtCreate/gmtModified` 冗余赋值、未随 insert 持久化（ServiceImpl:50-51）
  4. `S8.1` — POST /todos 未接入鉴权（TodoItemController.java:42-46）
- **一句话**：功能闭环完整、分层与安全基础（`#{}`、`@Valid`、String 化 id）到位，主要风险集中在时区、异常可观测与明文凭据三项。

---

## 7.1 问题片段（必填）

- **P1** `M016/G14.3` `src/main/java/com/example/todo/service/impl/TodoItemServiceImpl.java:50-51` — 未显式时区；且该值未参与 insert（见 P2-3）。
  片段范围：`TodoItemServiceImpl.java:50-51`

```java
L50|        todoItemDO.setGmtCreate(LocalDateTime.now());
L51|        todoItemDO.setGmtModified(LocalDateTime.now());
```

（关联测试同类问题：`TodoItemServiceImplTest.java:68-69`、`99-100`）

- **P1** `G16.2` `src/main/java/com/example/todo/common/exception/GlobalExceptionHandler.java:48-52` — 系统侧（B 开头错误码）业务异常未记录日志，插入失败/回查为空的故障无排查线索。
  片段范围：`GlobalExceptionHandler.java:48-52`

```java
L48|    @ExceptionHandler(BusinessException.class)
L49|    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
L50|        ApiResponse<Void> body = ApiResponse.error(e.getErrorCode(), e.getMessage(), e.getUserTip());
L51|        return ResponseEntity.status(resolveStatus(e.getErrorCode())).body(body);
L52|    }
```

- **P1** `S9.1` `src/main/resources/application.yml:6-8` — 明文数据库凭据。N/A(非 Java)。
  片段范围：`application.yml:6-8`

```yaml
L6|    url: jdbc:mysql://localhost:3306/todo?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
L7|    username: root
L8|    password: root
```

- **P2** `A2.4` `src/test/java/com/example/todo/service/impl/TodoItemServiceImplTest.java:16-19` — import ASCII 字典序错误。
  片段范围：`TodoItemServiceImplTest.java:16-19`

```java
L16|import org.mockito.InjectMocks;
L17|import org.mockito.Mock;
L18|import org.mockito.junit.jupiter.MockitoExtension;
L19|import org.junit.jupiter.api.extension.ExtendWith;
```

- **P2** 空 `setUp()` `src/test/java/com/example/todo/service/impl/TodoItemServiceImplTest.java:46-49` — 无内容的 `@BeforeEach`，可删除以避免噪音。
  片段范围：`TodoItemServiceImplTest.java:46-49`

```java
L46|    @BeforeEach
L47|    void setUp() {
L48|        // 通用初始化，Mockito 注解已由 MockitoExtension 处理
L49|    }
```

- **P2** 冗余赋值 `src/main/java/com/example/todo/service/impl/TodoItemServiceImpl.java:50-51` — insert SQL 仅写 `(name, description)`，`gmtCreate/gmtModified` 实际取自 DB `DEFAULT CURRENT_TIMESTAMP`，此处两行赋值不参与持久化（片段见上方 M016 条目）。

- **P2** `S8.1` `src/main/java/com/example/todo/controller/TodoItemController.java:42-46` — 写入接口未接入鉴权（内部用户 + 最小闭环，可接受但需确认）。
  片段范围：`TodoItemController.java:42-46`

```java
L42|    @PostMapping
L43|    public ApiResponse<TodoItemVO> createTodoItem(@Valid @RequestBody TodoItemRequest request) {
L44|        TodoItemVO vo = todoItemService.createTodoItem(request);
L45|        return ApiResponse.success(vo);
L46|    }
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/example/todo/service/impl/TodoItemServiceImpl.java:50-51` — 改为显式时区（如 `LocalDateTime.now(ZoneOffset.UTC)`）或直接删除冗余赋值并依赖 DB `DEFAULT CURRENT_TIMESTAMP`；同步修正测试 `TodoItemServiceImplTest.java:68-69,99-100`。
- [ ] **P1** `src/main/java/com/example/todo/common/exception/GlobalExceptionHandler.java:48-52` — 对系统侧（错误码 B 开头）的 `BusinessException` 补充 ERROR 日志，包含 errorCode 与上下文。
- [ ] **P1** `src/main/resources/application.yml:6-8` — 数据库凭据改为环境变量/`${DB_PASSWORD}` 占位，移除明文 `root/root`。

### P2（可选）

- [ ] **P2** `src/test/java/com/example/todo/service/impl/TodoItemServiceImplTest.java:19` — 调整 `ExtendWith` import 至 `org.junit.jupiter.api.extension` 组，符合 ASCII 字典序。
- [ ] **P2** `src/test/java/com/example/todo/service/impl/TodoItemServiceImplTest.java:46-49` — 删除空 `setUp()` 方法。
- [ ] **P2** `src/main/java/com/example/todo/service/impl/TodoItemServiceImpl.java:50-51` — 移除不参与持久化的 `gmtCreate/gmtModified` 赋值（与 P1 同源）。
- [ ] **P2** `src/main/java/com/example/todo/controller/TodoItemController.java:42-46` — 明确是否需为 `/todos` 写入接口补充鉴权（内部用户场景确认）。