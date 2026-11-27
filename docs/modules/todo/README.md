# todo 模块(待办事项)

## 职责

待办事项的创建落库:接收新增待办请求(事项名称必填、描述选填),完成服务端参数校验(R01~R03)、
创建人注入(登录态)、租户与时间组装,单表插入 MySQL 并返回创建结果。
当前为最小闭环——仅"新增";查询/修改/删除/完成为后续迭代扩展点。

## 关键类

| 类 | 职责 |
|----|------|
| TodoItemController | W01 `POST /api/todo/items`:功能开关检查、R01~R03 参数校验、统一响应封装 |
| TodoItemService / TodoItemServiceImpl | S01 `createTodoItem`:实体组装(creator/tenantId/gmt*)、落库、结果回显 |
| TodoItemMapper | MyBatis-Plus BaseMapper,单表插入,自增主键回填 |
| TodoItemDO | 表 `todo_item` 数据对象 |
| CreateTodoItemRequest / TodoItemResult | 入参(name/description)与出参(id/name/creator/gmtCreate) |
| TodoProperties | `todo.create.enabled` 应急开关 |
| TodoErrorCodeConstants | TODO_001~TODO_004 错误码 |

## 依赖

- 向下依赖:common(登录态上下文、异常/响应)、数据存储 MySQL(表 `todo_item`)。
- 被依赖:无(叶子模块,禁止反向依赖)。

## API 接口列表

| 编号 | 方法 | 路径 | 入参 | 出参 data | 错误码 |
|------|------|------|------|-----------|--------|
| W01 | POST | /api/todo/items | name(必填,trim 1~64)、description(选填,≤500) | id、name、creator、gmtCreate(yyyy-MM-dd HH:mm:ss) | TODO_001/002/003、TODO_004(维护中)、AUTH_001、SYS_999 |

## 数据模型

表 `todo_item`:`id`(PK 自增)、`tenant_id`、`name`、`description`、`creator`、`modifier`、
`is_deleted`(预留)、`gmt_create`、`gmt_modified`;utf8mb4,无外键;DDL 见 `src/main/resources/db/init/todo_item.sql`。
扩展点:查询上线前补 `idx_todo_item_creator_gmt (creator, gmt_create)`;引入完成状态时加 `status` 字段。
