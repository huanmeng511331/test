# todo 模块

## 模块职责

提供待办事项的新增能力。用户通过接口提交事项名称与描述，系统持久化并返回创建结果。

## 关键类

| 类 | 层级 | 说明 |
|----|------|------|
| TodoItemController | controller | 待办事项 REST 控制器 |
| TodoItemService / TodoItemServiceImpl | service | 业务逻辑 |
| TodoItemMapper | dao | MyBatis 数据访问接口 |
| TodoItemMapper.xml | dao | SQL 映射文件 |
| TodoItemDO | model | 数据对象 |
| TodoItemRequest / TodoItemVO | service.dto | 请求 / 视图对象 |

## 依赖关系

```
TodoItemController → TodoItemService → TodoItemMapper → todo_item 表
```

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /todos | 新增待办事项 |

请求体：

```json
{
  "name": "事项名称",
  "description": "事项描述"
}
```

响应体（`ApiResponse<TodoItemVO>`）：

```json
{
  "errorCode": "00000",
  "errorMessage": "success",
  "userTip": null,
  "data": {
    "id": "1",
    "name": "事项名称",
    "description": "事项描述",
    "gmtCreate": "2026-10-27T10:00:00",
    "gmtModified": "2026-10-27T10:00:00"
  }
}
```

## 数据表

见 `src/main/resources/db/schema.sql`，表名 `todo_item`，必备字段 `id`、`gmt_create`、`gmt_modified`。