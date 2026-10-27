# todo-service 架构文档

## 1. 模块概览

`todo-service` 是内部待办事项记录服务，当前最小闭环为「仅创建待办事项」。

| 模块 | 职责 |
|------|------|
| todo | 待办事项的新增（最小闭环：仅创建） |

## 2. 分层架构

遵循 MVC 分层，调用关系：开放接口层 → Service 层 → DAO 层。

```
com.antdigital.todo
├── api
│   └── controller        # 开放接口层（REST Controller）
├── service
│   └── impl              # 业务服务层
├── dao
│   └── mapper            # 数据访问层（MyBatis）
├── model
│   ├── do                # 数据对象
│   ├── dto               # 请求传输对象
│   └── vo                # 展示对象
└── common
    ├── enums             # 错误码枚举
    ├── exception         # 业务异常与全局异常处理
    └── response          # 统一响应结构
```

## 3. 关键类与依赖关系

- `TodoController` → `TodoService` → `TodoServiceImpl` → `TodoItemMapper` → MySQL(todo_item)
- `TodoCreateRequest`（DTO）→ `TodoItemDO`（数据对象）→ `TodoItemVO`（展示对象）

## 4. 技术栈

- Java 17、Spring Boot 3.2.5
- MyBatis（Spring Boot Starter 3.0.3，SQL 参数化 #{ }，禁止 ${ }）
- MySQL（运行时）/ H2（本地演示与测试兜底）

## 5. 约束

- 表必备三字段：id、gmt_create、gmt_modified
- 单表命名小写 + 下划线，见 `src/main/resources/sql/todo_item.sql`
- JSON key 使用 lowerCamelCase，超长整数以 String 返回（VO 的 id 为 String）