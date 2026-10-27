# todo 模块

## 职责

内部用户的日常待办事项记录，最小闭环仅支持新增（创建）待办事项。

## 关键类

| 类 | 职责 |
|----|------|
| `TodoController` | 创建待办事项的 REST 入口（`POST /todos`） |
| `TodoService` / `TodoServiceImpl` | 创建待办事项的业务规则与校验 |
| `TodoMapper` | 待办事项数据访问接口 |
| `TodoDO` | 待办事项数据对象，对应表 `todo_item` |
| `TodoVO` | 待办事项视图对象 |
| `TodoCreateRequest` | 创建请求对象 |
| `TodoException` / `TodoErrorCodeEnum` | 业务异常与错误码 |

## 依赖

- 向下依赖：`dao`（数据访问）、`model`（VO）、`common`（常量/异常/响应体）。
- 不得依赖：框架以外的外部模块。

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/todos` | 新增待办事项，入参 `{ name, description }`，返回 `TodoVO` |
