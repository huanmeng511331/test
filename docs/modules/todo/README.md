# todo 模块

## 1. 模块职责

提供日常待办事项的新增（创建）能力，最小闭环仅「创建」。

## 2. 关键类

| 类 | 类型 | 说明 |
|----|------|------|
| `TodoItemDO` | 数据对象 | 对应表 `todo_item` |
| `TodoItemCreateRequest` | 请求对象 | 新增待办事项入参（名称+描述） |
| `TodoItemVO` | 展示对象 | 返回视图，id 使用 String 防精度丢失 |
| `TodoItemMapper` | 数据访问 | MyBatis Mapper，提供 `insert` |
| `TodoItemService` / `TodoItemServiceImpl` | 业务服务 | 新增业务逻辑，接口与实现分离 |
| `TodoItemController` | 控制器 | 对外 HTTP 接口 |
| `GlobalExceptionHandler` | 异常处理 | 统一错误响应 |
| `BusinessException` | 业务异常 | 业务语义异常 |
| `ApiResult<T>` | 统一响应 | API 返回结构 |

## 3. 依赖关系

`TodoItemController` → `TodoItemService` → `TodoItemMapper` → MySQL

## 4. API 接口列表

| 方法 | 路径 | 说明 | 请求体 |
|------|------|------|--------|
| POST | `/todo_items` | 新增待办事项 | `{"name": "事项名称", "description": "事项描述(可选)"}` |

### 成功响应

```json
{
  "code": 200,
  "errorCode": null,
  "errorMessage": null,
  "data": {
    "id": "1001",
    "name": "写周报",
    "description": "整理本周工作进展"
  }
}
```

### 错误响应（参数校验失败示例）

```json
{
  "code": 500,
  "errorCode": "A0001",
  "errorMessage": "事项名称不能为空",
  "data": null
}
```

## 5. 数据表

表名 `todo_item`，DDL 见 `src/main/resources/schema.sql`。