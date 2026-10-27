# todo 模块

## 1. 模块职责

内部待办事项记录：核心功能为「新增待办事项」，任务信息包含「事项名称」与「事项描述」。当前最小闭环仅创建。

## 2. 关键类

| 类 | 说明 |
|----|------|
| TodoController | REST 开放接口层，暴露 POST /todos |
| TodoService | 业务服务接口 |
| TodoServiceImpl | 业务服务实现，含入参校验与落库 |
| TodoItemMapper | MyBatis 数据访问接口 |
| TodoItemDO | 数据对象，对应表 todo_item |
| TodoCreateRequest | 新增待办请求对象（DTO） |
| TodoItemVO | 待办事项展示对象（VO） |
| ApiResponse | 统一响应结构 |
| BusinessException / ErrorCodeEnum | 业务异常与错误码 |
| GlobalExceptionHandler | 全局异常处理 |

## 3. 依赖关系

`TodoController` → `TodoService`(接口) → `TodoServiceImpl` → `TodoItemMapper` → MySQL(todo_item)

## 4. API 接口列表

| 方法 | 路径 | 说明 | 请求 | 响应 |
|------|------|------|------|------|
| POST | /todos | 新增待办事项 | `{ "itemName": "...", "description": "..." }` | `ApiResponse<TodoItemVO>` |

### 请求示例

```json
{
  "itemName": "编写周报",
  "description": "整理本周工作产出"
}
```

### 校验规则

- `itemName` 必填，长度 ≤ 200，首尾空白会被 trim
- `description` 可选，长度 ≤ 1000，为 null 时按空串存储

## 5. 数据表

见 `src/main/resources/sql/todo_item.sql`（表名 `todo_item`，必备字段 id/gmt_create/gmt_modified）。