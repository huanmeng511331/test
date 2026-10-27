# todo 模块

## 模块职责

记录内部用户的日常待办事项，当前最小闭环仅支持「新增待办事项」。

## 关键类

| 类 | 说明 |
|----|------|
| TodoApplication | 启动类 |
| TodoItemDO | 数据对象 |
| TodoItemVO | 视图对象 |
| TodoCreateRequest | 新增待办事项请求 DTO |
| TodoItemMapper | 数据访问接口 |
| TodoItemService / TodoItemServiceImpl | 业务服务接口与实现 |
| TodoController | 控制器 |
| ApiResponse | 统一响应结构 |
| TodoErrorCodeEnum | 错误码枚举 |
| BusinessException / GlobalExceptionHandler | 异常处理 |

## 依赖关系

- 依赖 `todo_item` 表（MySQL），DDL 见 `src/main/resources/db/schema.sql`。
- 不依赖其他业务模块。

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /todos | 新增待办事项 |