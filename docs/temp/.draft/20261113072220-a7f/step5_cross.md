### 4.5 跨模块调用链视图

本期跨模块协作仅两条链路：①前端页面经 oneapi 进入工单管理模块，写操作后由前端再次调用统计模块刷新数字；②启动期运行支撑模块向工单管理模块的表写入种子数据。被调用接口均已在对应模块中定义（W01–W05、S01–S03、I01）。

#### 4.5.1 演示闭环链路（创建 → 分派 → 完成 → 关闭 → 统计刷新）

```mermaid
sequenceDiagram
    participant UI as web-console · 页面
    participant Ctrl as work-order-app · 路由层
    participant WoSvc as work-order-app · WorkOrderService
    participant StatSvc as work-order-app · StatisticsService
    participant Repo as work-order-app · WorkOrderRepository
    participant DB as SQLite

    UI->>+Ctrl: web-console.WorkOrderApi.createWorkOrder(payload)（W02 / F02）
    Ctrl->>+WoSvc: work-order-app.WorkOrderService.createWorkOrder(request)
    WoSvc->>+Repo: work-order-app.WorkOrderRepository.insertOrder(order)
    Repo->>+DB: INSERT work_orders
    DB-->>-Repo: id
    Repo-->>-WoSvc: id
    WoSvc->>+Repo: work-order-app.WorkOrderRepository.insertLog(createLog)
    Repo->>DB: INSERT work_order_logs
    WoSvc-->>-Ctrl: 201 工单对象
    Ctrl-->>-UI: 跳转详情页

    UI->>+Ctrl: web-console.WorkOrderApi.updateStatus(id, 分派+处理中)（W04 / F04）
    Ctrl->>+WoSvc: work-order-app.WorkOrderService.applyTransition(id, command)
    WoSvc->>+Repo: work-order-app.WorkOrderRepository.updateOrderIfStatus(id, 待处理, fields)
    Repo->>DB: UPDATE ... WHERE id = ? AND status = ?
    WoSvc->>+Repo: work-order-app.WorkOrderRepository.insertLog(statusLog)
    Repo->>DB: INSERT work_order_logs
    WoSvc-->>-Ctrl: 更新后工单
    Ctrl-->>-UI: 状态标签变为处理中

    UI->>+Ctrl: web-console.WorkOrderApi.updateStatus(id, 完成/关闭)（W04 / F04）
    Ctrl->>+WoSvc: work-order-app.WorkOrderService.applyTransition(id, command)
    WoSvc->>+Repo: work-order-app.WorkOrderRepository.updateOrderIfStatus(id, 当前状态, fields)
    Repo->>DB: UPDATE ... WHERE id = ? AND status = ?
    WoSvc->>+Repo: work-order-app.WorkOrderRepository.insertLog(logs)
    Repo->>DB: INSERT work_order_logs
    WoSvc-->>-Ctrl: 更新后工单
    Ctrl-->>-UI: 操作成功提示

    UI->>+Ctrl: web-console.StatisticsApi.getStatistics()（W05 / F05）
    Ctrl->>+StatSvc: work-order-app.StatisticsService.aggregate()（S03）
    StatSvc->>+Repo: work-order-app.WorkOrderRepository.countAll()
    Repo->>DB: SELECT COUNT(*)
    StatSvc->>+Repo: work-order-app.WorkOrderRepository.countGroupByStatus()
    Repo->>DB: SELECT status, COUNT(*) GROUP BY status
    StatSvc->>+Repo: work-order-app.WorkOrderRepository.countGroupByFaultLevel()
    Repo->>DB: SELECT fault_level, COUNT(*) GROUP BY fault_level
    StatSvc-->>-Ctrl: 统计结果
    Ctrl-->>-UI: 4 张卡片 + 等级分布刷新
```

说明：统计模块不订阅写事件、不做异步刷新；统计页每次进入/刷新时重新调用 W05，保证数字实时（AC-20、AC-21）。写操作与统计读取之间无强一致要求，页面刷新顺序由用户操作保证。

#### 4.5.2 启动初始化链路

```mermaid
sequenceDiagram
    participant Boot as work-order-app · 启动事件
    participant Init as work-order-app · DemoDataInitializer
    participant Schema as work-order-app · SchemaManager
    participant Repo as work-order-app · WorkOrderRepository
    participant DB as SQLite

    Boot->>+Init: work-order-app.DemoDataInitializer.initialize()（I01 / F06）
    Init->>+Schema: work-order-app.SchemaManager.createSchema()
    Schema->>DB: 建表与索引（不存在才创建）
    Schema-->>-Init: schemaCreated
    Init->>+Repo: work-order-app.WorkOrderRepository.existsSeedData()
    Repo->>DB: SELECT 1 FROM work_orders LIMIT 1
    Repo-->>-Init: exists
    alt 无数据且种子开启
        Init->>+Repo: work-order-app.WorkOrderRepository.insertOrder(order) × 6
        Repo->>DB: INSERT work_orders（事务）
        Init->>+Repo: work-order-app.WorkOrderRepository.insertLog(log) × 21
        Repo->>DB: INSERT work_order_logs（同事务）
        Init->>DB: COMMIT
    else 已有数据
        Init->>Init: work-order-app.DemoDataInitializer.skip(reason)
    end
    Init-->>-Boot: SeedResult + 汇总日志
```

说明：初始化只调用工单管理模块的仓储写方法，不经过业务校验与状态机（种子为固定合法数据），避免启动期依赖 HTTP 层；被调用方法与表结构均已在 4.2 定义。
