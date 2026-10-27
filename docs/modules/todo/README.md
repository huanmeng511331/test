# todo 模块

## 模块职责

管理内部用户的日常待办事项。最小闭环当前仅覆盖「新增待办事项」，任务信息包含事项名称与事项描述。

## 关键类

| 类 | 职责 |
|----|------|
| `TodoController` | 对外 HTTP 接口，参数校验转发 |
| `TodoService` / `TodoServiceImpl` | 业务逻辑：校验、组装、落库、返回 |
| `TodoMapper` | MyBatis 数据访问 |
| `TodoDO` | 数据对象（对应 `todo_item` 表） |
| `TodoVO` | 视图对象（返回给前端） |
| `TodoCreateRequest` | 新增请求 DTO |

## 依赖关系

Controller → Service → Mapper → MySQL；依赖 Spring Boot 3 Web / Validation、MyBatis、MySQL driver。

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/todos` | 新增待办事项 |

请求示例：

```json
{
  "name": "买牛奶",
  "description": "下班后去超市购买"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "买牛奶",
    "description": "下班后去超市购买",
    "createTime": "2026-10-27 08:40:00"
  }
}
```

## 数据库

表 `todo_item`，DDL 见 `src/main/resources/db/schema.sql`。