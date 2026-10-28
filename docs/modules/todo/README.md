# 待办事项模块（todo）

> 模块文档 | 创建日期: 2026-10-28 | 作者: AiWork

## 模块职责

接收待办创建请求，校验事项名称与描述，填充登录用户信息后持久化待办记录。

## 关键类

| 类名 | 类型 | 职责 |
|------|------|------|
| TodoController | Controller | REST 接口入口，登录态获取 + 功能开关 + 参数校验 |
| TodoService | Service 接口 | 定义 createTodo 方法 |
| TodoServiceImpl | Service 实现 | 业务规则校验（R01~R03）+ 持久化 |
| TodoMapper | Mapper | MyBatis 数据访问，insert |
| TodoDO | Entity | 数据对象，映射 todo 表 |
| TodoCreateApiRequest | API Request | title + description，带 @Valid 校验 |
| TodoCreateData | API Response | 返回新建待办 ID |
| TodoCreateRequest | Service DTO | 含 creator / tenantId（来源登录上下文） |
| TodoCreateResult | Service DTO | 返回新建待办 ID |
| TodoErrorCode | Enum | 模块错误码（TODO_001~004） |

## 依赖关系

- 依赖数据库 MySQL（通过 MyBatis TodoMapper）
- 依赖公共层 LoginContextHolder（获取登录用户信息）
- 依赖公共层 BusinessException / ApiResponse

## API 接口列表

| 编号 | 接口名称 | 方法 | 路径 | 说明 |
|------|----------|------|------|------|
| W01 | 新增待办事项 | POST | /api/todo/create | 录入事项名称与描述，返回新建 ID |

## 业务规则

| 规则 | 描述 | 错误码 |
|------|------|--------|
| R01 | title 非空且非纯空白 | TODO_001 |
| R02 | title 长度 ≤ 100 | TODO_002 |
| R03 | description 长度 ≤ 1000 | TODO_002 |
| R04 | creator/tenant_id 取登录上下文，禁前端传入 | AUTH_001（全局拦截器） |

## 表结构

todo 表：id / title / description / creator / tenant_id / gmt_create / gmt_modified，索引 idx_todo_tenant_creator。
