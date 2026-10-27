# Code Review Report

> **Change** `todo(新增待办事项)` · **分支/Commit** `AI/task-DEV-c1f01881-…` / `fb53469` · **日期** `2026-10-27` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**。已先运行 `scan-all-rules.sh`（52/222 条，命中 1 条 P1），并 LLM 完成其余核销。问题均含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+942 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TodoApplication | src/main/java/com/dtaz/todo/TodoApplication.java | 启动类 |
| ErrorCodeEnum | src/main/java/com/dtaz/todo/common/constant/ErrorCodeEnum.java | 错误码 |
| TodoConstants | src/main/java/com/dtaz/todo/common/constant/TodoConstants.java | 常量 |
| BusinessException | src/main/java/com/dtaz/todo/common/exception/BusinessException.java | 业务异常 |
| GlobalExceptionHandler | src/main/java/com/dtaz/todo/common/exception/GlobalExceptionHandler.java | 统一异常 |
| ApiResponse | src/main/java/com/dtaz/todo/common/model/ApiResponse.java | 统一响应 |
| TodoController | src/main/java/com/dtaz/todo/controller/TodoController.java | REST 控制器 |
| TodoMapper | src/main/java/com/dtaz/todo/dao/mapper/TodoMapper.java | MyBatis Mapper |
| TodoRequest | src/main/java/com/dtaz/todo/model/dto/TodoRequest.java | 请求体 |
| TodoDO | src/main/java/com/dtaz/todo/model/entity/TodoDO.java | 数据对象 |
| TodoVO | src/main/java/com/dtaz/todo/model/vo/TodoVO.java | 视图对象 |
| TodoService | src/main/java/com/dtaz/todo/service/TodoService.java | 服务契约 |
| TodoServiceImpl | src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java | 业务实现 |
| TodoServiceImplTest | src/test/java/com/dtaz/todo/service/impl/TodoServiceImplTest.java | 单元测试 |

非 Java（本次一并审阅、不计入 14）：`pom.xml`、`application.yml`（主/测试）、`db/schema.sql`、`mapper/TodoMapper.xml`、`docs/*`、`.agents/*/impl.md`。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 2 | 4 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /todos 创建并返回 VO | ✅ | 需求「核心功能：新增待办事项」；README「POST /todos 新增待办事项」 | TodoController.java:32-36；TodoServiceImpl.java:34-51；TodoMapper.xml:14-18 | 链路过完整：Controller→Service→Mapper→实体，insert 后回读 generated key 返回 VO |

### REQ-2: 事项字段（名称 + 描述）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name/description 落库并返回 | ✅ | 需求「任务信息：事项名称和描述」 | TodoRequest.java:13-19；schema.sql:4-5；TodoMapper.xml:16-17 | 字段齐全，长度约束 50/500 与常量一致 |

### REQ-3: 名称必填校验

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name 空/空白/null 拒绝 | ✅ | README「业务校验（名称非空）」；ARCHITECTURE「@Valid + Jakarta Validation 双重校验」 | TodoRequest.java:13（@NotBlank）+ TodoServiceImpl.java:53-60（防御校验） | 双层校验，异常统一转为 ApiResponse |

### REQ-4: 最小闭环（仅创建）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 仅暴露创建端点 | ✅ | 需求「最小闭环：仅创建」 | TodoController.java:32 仅 @PostMapping | 无多余端点，符合闭环 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A2.4 `java.time.LocalDateTime` 未按字典序置于非静态 import 组内（排在 `org.*` 之后）：`TodoServiceImpl.java:15`（P2） |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | 最高 P1 | G14.4 时区未显式指定（P1/P2）；G11.2 长度边界未覆盖（P2）；G16.2 日志无 traceId（P2） |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | 最高 P1 | S8.1 POST /todos 无认证/授权（P1，内部用户范围；公网暴露则 P0） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | 最高 P1 | 预扫命中 M016（P1）；其余无 P0 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | — | 仅 U1.1 示例项（Controller `@Valid`）已满足；其余 U 项未定义 → N/A |

---

## 7. 结论

- **合并建议**：修复后合并（无 P0，建议先修复 P1）
- **P0**：无
- **P1/P2**：
  1. **P1** `M016`/`G14.4` `TodoServiceImpl.java:40` — `LocalDateTime.now()` 未显式时区
  2. **P1** `S8.1` `TodoController.java:32` — `POST /todos` 无认证/授权与归属校验
  3. **P2** `G11.2` — 单测未覆盖 name=50/description=500 长度边界与超长拒绝
  4. **P2** `G16.2` — 异常日志缺少 traceId/bizId 上下文
  5. **P2** `A2.4` `TodoServiceImpl.java:15` — import 未按字典序
- **一句话**：功能闭环完整、分层清晰、分页/注入/事务等风险面在本最小闭环内均不涉及，无 P0；需补齐时区显式声明与接口鉴权两项 P1 后合入。

---

## 7.1 问题片段（必填）

- **P1** `M016` / `G14.4` `src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:40` — `LocalDateTime.now()` 依赖系统默认时区，跨时区部署或同一应用误跨区时时间点不一致。  
  片段范围：`src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:37-41`

```java
L37|        TodoDO todo = new TodoDO();
L38|        todo.setName(request.getName().trim());
L39|        todo.setDescription(trimToEmpty(request.getDescription()));
L40|        todo.setGmtCreate(LocalDateTime.now());
L41|        todo.setGmtModified(todo.getGmtCreate());
```

- **P1** `S8.1` `src/main/java/com/dtaz/todo/controller/TodoController.java:32-36` — 创建接口无认证/授权与归属校验；内部用户范围下可接受，若暴露公网应加鉴权。  
  片段范围：`src/main/java/com/dtaz/todo/controller/TodoController.java:26-36`

```java
L26|    /**
L27|     * 新增待办事项。
L28|     *
L29|     * @param request 新增请求
L30|     * @return 创建后的待办事项
L31|     */
L32|    @PostMapping
L33|    public ApiResponse<TodoVO> create(@Valid @RequestBody TodoRequest request) {
L34|        TodoVO vo = todoService.create(request);
L35|        return ApiResponse.success(vo);
L36|    }
```

- **P2** `G11.2` `src/test/java/com/dtaz/todo/service/impl/TodoServiceImplTest.java:49-116` — 未覆盖 name/description 长度边界与超长拒绝。  
  片段范围：`src/test/java/com/dtaz/todo/service/impl/TodoServiceImplTest.java:85-104`

```java
L85|    @Test
L86|    void should_throwException_when_requestIsNull() {
L87|        // Act & Assert
L88|        assertThatThrownBy(() -> todoService.create(null))
L89|                .isInstanceOf(BusinessException.class)
L90|                .hasMessageContaining("请求不能为空");
L91|        verify(todoMapper, never()).insert(any(TodoDO.class));
L92|    }
L93|
L94|    @Test
L95|    void should_throwException_when_nameIsBlank() {
L96|        // Arrange
L97|        TodoRequest request = buildRequest("   ", TEST_DESCRIPTION);
L98|
L99|        // Act & Assert
L100|        assertThatThrownBy(() -> todoService.create(request))
L101|                .isInstanceOf(BusinessException.class)
L102|                .hasMessageContaining("事项名称不能为空");
L103|        verify(todoMapper, never()).insert(any(TodoDO.class));
L104|    }
```

- **P2** `G16.2` `src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:45` — 异常日志缺少 traceId/bizId 等可追溯上下文。  
  片段范围：`src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:43-47`

```java
L43|        int rows = todoMapper.insert(todo);
L44|        if (rows != INSERT_SUCCESS_ROWS) {
L45|            LOGGER.error("新增待办事项失败, name: {}, affectedRows: {}", todo.getName(), rows);
L46|            throw new BusinessException(ErrorCodeEnum.BUSINESS_ERROR.getCode(), "新增待办事项失败");
L47|        }
```

- **P2** `A2.4` `src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:15` — `java.time.LocalDateTime` 未按 ASCII 字典序置于 import 组内。  
  片段范围：`src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:11-16`

```java
L11|import org.springframework.stereotype.Service;
L12|import org.springframework.util.StringUtils;
L13|
L14|
L15|import java.time.LocalDateTime;
```

---

## 8. 修复任务列表

> 供后续改代码逐项执行与核销；无 P0 项。

### P0

- 无

### P1

- [ ] **P1** `src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:40` — 将 `LocalDateTime.now()` 改为显式时区（如 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))` 或统一 `Instant` 存库），并在测试 TODO 中固化期望
- [ ] **P1** `src/main/java/com/dtaz/todo/controller/TodoController.java:32` — 接入内部鉴权与资源归属校验；若确认仅限内网且无归属需求，请以设计文档显式声明豁免原因

### P2（可选）

- [ ] **P2** `src/test/java/com/dtaz/todo/service/impl/TodoServiceImplTest.java` — 增加 name=50、description=500 边界及超长拒绝的用例
- [ ] **P2** `src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:45` — 异常日志补充 traceId/bizId 上下文
- [ ] **P2** `src/main/java/com/dtaz/todo/service/impl/TodoServiceImpl.java:15` — 按 ASCII 字典序重排 import（`java.time.LocalDateTime` 置于 `org.*` 之前）