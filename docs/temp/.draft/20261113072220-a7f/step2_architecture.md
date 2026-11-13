## 2. 架构与模块

### 2.1 架构形态与分层

本期为**本机单实例、前后端分离的两进程应用**：前端 `web-console`（Vue 3 + Vite + Element Plus）通过 JSON REST 调用后端 `work-order-app`（FastAPI），后端独占访问 SQLite 单文件 `demo.db`。不存在微服务拆分、消息中间件、缓存与外部系统集成。

- 前端分层：交互层（5 个页面/弹窗）→ 前端支撑层（API 客户端、路由与菜单、公共格式化与标签色）。
- 后端分层：路由层（Controller）→ 业务层（Service，状态流转校验唯一入口）→ 仓储层（Repository，封装 SQLite 访问）→ 数据层（`work_orders`、`work_order_logs`）。
- 运行支撑：应用启动时完成建表与演示数据种子（幂等），并注册 CORS、统一异常处理与请求日志。

### 2.2 功能架构

```mermaid
graph TB
    subgraph webconsole[web-console 前端应用]
        subgraph interactionLayer[交互层]
            ListPage[工单列表页<br/>筛选 / 分页 / 查看]
            CreateDialog[新建工单弹窗<br/>录入与校验]
            DetailPage[工单详情页<br/>基本信息 / 流转记录 / 处理记录 / 操作栏]
            AssignDialog[分派弹窗<br/>选人员 / 立即开始处理]
            StatPage[工单统计页<br/>状态卡片 / 等级分布]
        end
        subgraph frontendSupport[前端支撑层]
            ApiClient[API 客户端<br/>统一请求与错误提示]
            RouterMenu[路由与菜单<br/>列表 / 统计]
            ViewHelper[展示辅助<br/>时间格式化 / 标签色]
        end
    end

    subgraph workorderapp[work-order-app 后端应用]
        subgraph coreServiceLayer[核心服务层]
            subgraph moduleWO[工单管理模块]
                WoList[工单列表与筛选 F01]
                WoCreate[新建工单与编号生成 F02]
                WoDetail[工单详情与记录查询 F03]
                WoFlow[分派 / 开始处理 / 完成 / 关闭 F04]
            end
            subgraph moduleStat[工单统计模块]
                StatCard[状态数量统计 F05]
                StatLevel[故障等级分布 F05]
            end
        end
        subgraph bootstrapLayer[运行支撑层]
            subgraph moduleBoot[演示数据初始化与运行支撑模块]
                SchemaInit[建表与索引初始化 F06]
                SeedData[演示数据种子 F06]
                RuntimeConf[CORS / 异常处理 / 请求日志 F06]
            end
        end
    end

    ListPage --> ApiClient
    CreateDialog --> ApiClient
    DetailPage --> ApiClient
    AssignDialog --> ApiClient
    StatPage --> ApiClient
    ApiClient --> RouterMenu
    ListPage --> ViewHelper
    DetailPage --> ViewHelper
    StatPage --> ViewHelper

    ApiClient -->|JSON oneapi| WoList
    ApiClient -->|JSON oneapi| WoCreate
    ApiClient -->|JSON oneapi| WoDetail
    ApiClient -->|JSON oneapi| WoFlow
    ApiClient -->|JSON oneapi| StatCard

    WoFlow --> WoDetail
    StatCard --> StatLevel
    StatCard --> WoList
    SeedData --> SchemaInit
    SeedData --> WoCreate
```

- **交互层说明**：仅承担录入、展示、按钮可用性与前端校验，不实现状态流转判定；所有数据来自后端接口。
- **核心服务层说明**：工单管理模块是唯一写入方，负责编号生成、状态机校验、记录追加与事务；工单统计模块只读聚合，复用工单管理模块的查询能力，不直接写库。
- **运行支撑层说明**：启动期完成表结构创建与演示数据初始化；运行期提供 CORS、统一异常映射与请求日志。该层不参与业务请求链路。

**模块清单**

| 模块 | 职责（一句话） | 包含的功能点 | 依赖的模块 |
|---|---|---|---|
| 工单管理模块（`work-order-app`） | 维护工单全生命周期：列表查询、创建与编号生成、详情与记录查询、分派与状态流转校验 | F01、F02、F03、F04 | 无（仅依赖 SQLite 数据层） |
| 工单统计模块（`work-order-app`） | 按全量工单聚合状态数量与故障等级分布 | F05 | 工单管理模块（复用其仓储只读查询：按状态计数、按等级计数、总数） |
| 演示数据初始化与运行支撑模块（`work-order-app`） | 启动时建表并幂等写入演示数据，提供 CORS、统一异常与请求日志 | F06 | 工单管理模块（复用其表结构定义与仓储写入/计数） |

依赖方向：`工单统计模块 → 工单管理模块`、`演示数据初始化与运行支撑模块 → 工单管理模块`，工单管理模块不反向依赖任何业务模块，**无循环依赖**。

### 2.3 应用集成架构

本期无外部业务系统、无第三方 API、无消息/缓存中间件。集成点仅为本机三跳：浏览器 → 前端开发服务器 → 后端 API → SQLite 文件。

```mermaid
flowchart TB
    user[用户浏览器<br/>Chrome / Edge]

    subgraph app[本机应用]
        WebConsole[web-console<br/>Vite Dev Server :5173]
        ApiGw[FastAPI 路由层<br/>:8000 /api]
        CoreServices[核心服务层<br/>工单管理 + 工单统计]
        Bootstrap[运行支撑层<br/>初始化 / CORS / 日志]
    end

    subgraph middleware[数据层]
        DB[(SQLite 文件<br/>backend/demo.db)]
    end

    user -->|HTTP localhost:5173| WebConsole
    WebConsole -->|HTTP JSON oneapi :8000| ApiGw
    ApiGw -->|进程内调用| CoreServices
    Bootstrap -->|启动期建表与种子| CoreServices
    CoreServices -->|sqlite3 驱动 / 本地文件读写| DB
```

**集成关系说明**

| 调用方 | 被调用方 | 协议 | 接口类型 | 说明 |
|---|---|---|---|---|
| 用户浏览器 | web-console（Vite :5173） | HTTP | 页面资源 | 加载 Vue 单页应用 |
| web-console | work-order-app（FastAPI :8000） | HTTP + JSON | oneapi REST | 5 个业务接口，跨域需 CORS 允许 `http://localhost:5173` |
| work-order-app 核心服务层 | SQLite `demo.db` | 本地文件（sqlite3 驱动） | SQL | 单连接/短连接读写；无外键、无存储过程 |
| 运行支撑层 | 核心服务层 | 进程内方法调用 | 内部接口 | 启动时建表与演示数据种子，幂等 |
| 外部业务系统 | — | — | — | **本项不适用**：需求与 PRD 均未涉及外部系统对接，本期不设计 OpenAPI 与外部集成层 |

### 2.4 部署架构

```mermaid
graph TB
    subgraph host[本机开发/演示环境 单机]
        subgraph clientLayer[客户端]
            Browser[Chrome / Edge<br/>1280×720 及以上]
        end
        subgraph appLayer[应用层 两个进程]
            Vite[Vite Dev Server<br/>localhost:5173]
            Uvicorn[Uvicorn + FastAPI<br/>localhost:8000 单 worker]
        end
        subgraph dataLayer[数据层]
            DBFile[(backend/demo.db<br/>SQLite 单文件)]
            Logs[标准输出日志<br/>请求 / 异常 / 启动自检]
        end
        subgraph codeLayer[代码目录]
            FrontSrc[frontend/src]
            BackSrc[backend/app]
        end
    end

    Browser -->|HTTP| Vite
    Vite -->|HTTP JSON| Uvicorn
    Uvicorn --> DBFile
    Uvicorn --> Logs
    Vite --- FrontSrc
    Uvicorn --- BackSrc
```

**部署说明**

- **客户端层**：单一浏览器，无 CDN、无反向代理。
- **应用层**：`npm run dev` 启动 Vite（5173），`uvicorn app.main:app --reload --port 8000` 启动 FastAPI（8000，单 worker）。端口可按环境调整，README 必须写明最终地址。
- **数据层**：SQLite 单文件 `backend/demo.db`；首次启动自动建表，表为空时写入 6 条演示工单及配套记录；删除文件后重启可恢复。
- **单点说明**：FastAPI 单进程与 SQLite 单文件均为单点，属于本机演示交付形态（PRD O-6 明确不做生产部署与高可用），**本期不设计负载均衡与多副本**；风险控制为"数据文件可备份/可重建 + 启动自检日志"，不引入集群能力。
- **无定时任务、无后台批处理**：不涉及任务单点与并发调度问题。
