# todo 模块

## 职责

日常待办事项管理，最小闭环：新增待办事项（事项名称 + 事项描述）。

## 关键类

| 类 | 职责 |
|----|------|
| TodoController | `POST /todos` 新增待办事项，接收并校验请求 |
| TodoService / TodoServiceImpl | 业务校验（名称非空）、持久化编排、VO 转换 |
| TodoMapper | MyBatis 数据访问（insert / findById） |
| TodoDO / TodoVO / TodoRequest | 数据对象 / 视图对象 / 请求体 |
| GlobalExceptionHandler | 统一异常响应 |

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/todos` | 新增待办事项，请求体 `{name, description}` |

## 依赖

- 向下依赖：dao（数据访问）、common（错误码/异常/常量）、model（数据模型）
- 不得依赖其他业务模块