# todo 模块

## 职责

内部用户日常待办事项管理，当前最小闭环仅支持新增待办事项（事项名称 + 事项描述）。

## 关键类

| 类 | 职责 |
|----|------|
| TodoItemController | 新增待办事项 REST 接口 |
| TodoItemService / TodoItemServiceImpl | 待办事项业务编排与校验 |
| TodoItemMapper | 待办事项数据访问 |
| TodoItemDO | 待办事项数据对象 |
| TodoItemCreateRequest / TodoItemVO | 请求 / 视图对象 |

## 依赖

- 向下依赖：MySQL（jdbc:mysql）
- 框架：Spring MVC、MyBatis

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /todo_items | 新增待办事项 |

请求体示例：

```json
{
    "title": "完成周报",
    "description": "整理本周工作内容并提交"
}
```

成功响应示例：

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "title": "完成周报",
        "description": "整理本周工作内容并提交",
        "createTime": "2026-10-27T10:00:00"
    }
}
```
