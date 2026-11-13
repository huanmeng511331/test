# 设备故障智能工单助手

本机演示用的设备故障工单管理后台。现场人员创建故障工单，管理人员分派维修人员，维修人员记录处理过程并完成工单，最终关闭工单；列表与统计页展示工单处理情况。

## 技术栈

| 分层 | 选型 |
|---|---|
| 前端 | Vue 3 + Vite + Element Plus |
| 后端 | FastAPI + Pydantic |
| 数据库 | SQLite（单文件 `backend/demo.db`） |
| 接口 | JSON REST API，自动文档 `/docs` |

## 目录结构

```text
.
├── backend/
│   ├── app/
│   │   ├── main.py              # FastAPI 入口、CORS、异常映射、请求日志
│   │   ├── models/              # 枚举与常量
│   │   ├── schemas/             # 请求/响应模型
│   │   ├── routers/             # API 路由
│   │   ├── services/            # 状态流转与统计业务逻辑
│   │   ├── repositories/        # SQLite 数据访问
│   │   └── bootstrap/           # 建表与演示数据初始化
│   ├── tests/                   # 接口测试
│   ├── requirements.txt
│   └── demo.db                  # 首次启动自动生成
├── frontend/
│   ├── src/
│   │   ├── api/                 # 后端接口调用
│   │   ├── components/          # 新建、分派、完成弹窗
│   │   ├── views/               # 列表、详情、统计页
│   │   ├── router/              # 页面路由
│   │   └── utils/               # 标签颜色与格式化
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## 环境要求

- Python 3.10+
- Node.js 18+（推荐 20.x）与 npm 9+
- Chrome / Edge 最近两个大版本

## 启动步骤

### 1. 启动后端

```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate        # Windows：.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

- 接口文档：http://localhost:8000/docs
- 首次启动自动创建 `backend/demo.db`、两张表与 6 条演示工单（21 条记录）。
- 服务重启数据不丢失。

### 2. 启动前端

另开终端：

```bash
cd frontend
npm install
npm run dev
```

- 前端访问：http://localhost:5173
- 默认首页为工单列表，左侧菜单可切换工单统计。
- 端口被占用时前端会直接报错退出（`strictPort`），请释放 5173 或修改 `frontend/vite.config.js` 后同步调整后端 `CORS_ORIGINS`。

## 配置项

| 变量 | 默认值 | 说明 |
|---|---|---|
| `DB_PATH` | `backend/demo.db` | SQLite 文件路径 |
| `CORS_ORIGINS` | `http://localhost:5173,http://127.0.0.1:5173` | 允许的前端来源，逗号分隔 |
| `SEED_ENABLED` | `true` | 库为空时是否写入演示数据 |
| `VITE_API_BASE` | `http://localhost:8000/api` | 前端接口基础地址 |

## 接口清单

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/work-orders` | 查询工单列表，支持 `status`、`fault_level`、`page`、`page_size` |
| POST | `/api/work-orders` | 创建工单，返回 201 与新工单 |
| GET | `/api/work-orders/{id}` | 查询工单详情与记录 |
| PATCH | `/api/work-orders/{id}/status` | 分派、开始处理、处理说明、完成、关闭 |
| GET | `/api/work-orders/statistics` | 全量状态数量与故障等级分布 |

错误响应统一为 `{"detail": {"code": "...", "message": "..."}}`；参数校验失败为 FastAPI 422 结构。

## 工单状态规则

```text
待处理 --分派并开始处理/开始处理--> 处理中 --填写处理方案并完成--> 已完成 --关闭工单--> 已关闭
   └── 仅分派（不改变状态，可再次换人）       处理中可追加处理说明
```

- 状态不可回退、不可跳步，已关闭为终态，任何更新返回 400 `ORDER_CLOSED`。
- 进入「处理中」必须有维修人员；进入「已完成」必须填写处理方案。
- 维修人员为固定演示名单：李强、王敏、赵磊、陈晨。

## 演示数据

首次启动写入 6 条工单（2026-11-11 至 2026-11-13），统计口径：

- 状态：待处理 1、处理中 2、已完成 1、已关闭 2
- 等级：高 2、中 2、低 2
- 配套记录：21 条

重置演示数据：停止后端，删除 `backend/demo.db`，重新启动后端即可重建并写入种子数据。若需接入真实数据，可设置 `SEED_ENABLED=false` 后清空数据库再启动。

## 测试与构建

后端接口测试（使用临时数据库，不影响 `demo.db`）：

```bash
cd backend
source .venv/bin/activate
pip install -r requirements-dev.txt
python -m unittest discover -s tests -v
```

前端生产构建：

```bash
cd frontend
npm run build
```

## 验收演示路径

1. 工单列表点击「新建工单」，填写「压缩机振动异常」并提交，跳转详情页，状态为「待处理」。
2. 返回列表，故障等级筛选「高」，仅显示高等级工单。
3. 进入待处理工单详情，点击「分派」，选择维修人员并保留「立即开始处理」，状态变为「处理中」。
4. 在处理中详情填写处理说明并「填写处理方案并完成」，状态变为「已完成」；点击「关闭工单」后页面只读。
5. 打开「工单统计」，待处理 / 处理中 / 已完成 / 已关闭数量与等级分布随操作变化。

## 说明

本期为本机单用户演示应用，不包含登录鉴权、设备台账、附件上传与消息通知；所有状态流转校验均在后端 `services` 层完成。
