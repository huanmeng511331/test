# todo 模块

## 模块职责

待办事项模块，提供待办事项的创建能力（当前最小闭环仅「新增待办事项」），面向内部用户记录日常待办。

## 关键类

| 类型 | 类 | 说明 |
|---|---|---|
| 控制器 | `TodoController` | `POST /todos` 新增待办事项 |
| 服务接口 | `TodoService` | 新增待办事项业务契约 |
| 服务实现 | `TodoServiceImpl` | 校验、落库、VO 转换 |
| 数据访问 | `TodoMapper` | MyBatis 插入接口 |
| 数据对象 | `TodoDO` | 数据表 `todo` 映射对象 |
| 请求对象 | `TodoRequest` | 入参结构（name、description） |
| 展示对象 | `TodoVO` | 出参结构 |

## 依赖关系

- `TodoController` → `TodoService` → `TodoServiceImpl` → `TodoMapper`。
- 公共层依赖：`ApiResponse`、`BusinessException`、`TodoConstants`、`GlobalExceptionHandler`。

## 数据表

表名：`todo`

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键，自增 |
| name | VARCHAR(100) | 待办事项名称，非空 |
| description | VARCHAR(500) | 待办事项描述，可空 |
| gmt_create | DATETIME | 创建时间 |
| gmt_modified | DATETIME | 更新时间 |

DDL 见 `src/main/resources/sql/schema.sql`。

## API 接口列表

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/todos` | 新增待办事项 |

请求体：

```json
{
  "name": "写周报",
  "description": "整理本周工作进展"
}
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 10001,
    "name": "写周报",
    "description": "整理本周工作进展",
    "gmtCreate": "2026-10-27 10:00:00"
  }
}
```