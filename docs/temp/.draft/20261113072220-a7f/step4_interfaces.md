## 4. 功能模块与接口设计

### 4.0 模块—接口映射

本期接口分为三类：oneapi（供前端页面调用，前缀 `/api`）、内部接口（后端应用内 Service 层方法，供其他模块或本模块接口复用）、任务接口（应用启动期触发）。无对外 OpenAPI，无外部集成接口。

| 接口编号 | 接口名称 | 类型 | 方法/触发方式 | 路径/方法签名 | 所属模块 | 对应功能点 |
|---|---|---|---|---|---|---|
| W01 | 查询工单列表 | oneapi | GET | `/api/work-orders`（query：`status`、`fault_level`、`page`、`page_size`） | 工单管理模块 | F01 |
| W02 | 创建工单 | oneapi | POST | `/api/work-orders`（body：设备与故障字段） | 工单管理模块 | F02 |
| W03 | 查询工单详情 | oneapi | GET | `/api/work-orders/{id}`（path：整型 `id`） | 工单管理模块 | F03 |
| W04 | 更新工单状态/人员/处理信息 | oneapi | PATCH | `/api/work-orders/{id}/status`（body：`status`、`assignee`、`solution`、`note`、`operator`） | 工单管理模块 | F04（复用 W03 刷新详情） |
| W05 | 查询工单统计 | oneapi | GET | `/api/work-orders/statistics` | 工单统计模块 | F05 |
| S01 | 工单编号生成 | 内部接口 | 方法调用 | `work-order-app.WorkOrderService.generateOrderNo(businessDate)` | 工单管理模块 | F02 |
| S02 | 工单状态流转校验与执行 | 内部接口 | 方法调用 | `work-order-app.WorkOrderService.applyTransition(orderId, command)` | 工单管理模块 | F04 |
| S03 | 统计聚合查询 | 内部接口 | 方法调用 | `work-order-app.StatisticsService.aggregate()` | 工单统计模块 | F05 |
| I01 | 启动初始化：建表与演示数据种子 | 任务接口 | 应用启动事件 | `work-order-app.DemoDataInitializer.initialize()` | 演示数据初始化与运行支撑模块 | F06 |

**OpenAPI 说明**：本项不适用。需求与 PRD 均未涉及对外系统对接，本期不暴露 `/openapi`。

**集成接口说明**：本项不适用。无外部系统或依赖服务，无 RPC/HTTP 外部契约需要设计。

**映射完整性核对**：F01→W01；F02→W02、S01；F03→W03；F04→W04、S02（并复用 W03 刷新）；F05→W05、S03；F06→I01。每个功能点至少映射 1 个接口，无遗漏。
