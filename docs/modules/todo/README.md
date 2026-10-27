# todo 模块

## 职责

内部待办事项管理，当前最小闭环：新增待办事项。

## 关键类

| 类 | 职责 |
|----|------|
| TodoController | 新增待办事项 REST 接口（POST /todos） |
| TodoServiceImpl | 待办事项创建业务编排与参数规范化 |
| TodoMapper | 待办事项数据访问（INSERT） |
| TodoDO / TodoVO | 待办事项数据对象 / 展示对象 |
| CreateTodoRequest | 新增待办事项请求与参数校验 |

## 依赖

- service → dao（数据访问）
- service → common（异常、响应模型）
- api → service、common

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /todos | 新增待办事项（body: title 必填、description 选填） |
