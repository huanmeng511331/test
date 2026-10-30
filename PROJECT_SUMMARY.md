# 项目总体说明

## 项目概述

本项目是一个前后端分离的算法演示系统，包含以下核心功能：
1. 三个基础算法接口（HelloWorld、哈希算法、冒泡排序）
2. 前端展示页面（Tab 切换展示不同算法执行结果）
3. 数据导出功能（支持导出为 Excel 文件）
4. 埋点统计和可视化报表（折线图、饼图、柱状图）

## 仓库结构

### 后端仓库：test
**路径**: `test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d`

**技术栈**: Go 1.21+ + Gin + GORM + SQLite

**目录结构**:
```
test/
├── main.go                 # 程序入口
├── go.mod                  # Go 模块定义
├── config/
│   └── config.go           # 配置管理
├── handlers/               # HTTP 处理器
│   ├── hello.go            # HelloWorld 处理器
│   ├── hash.go             # 哈希算法处理器
│   ├── bubble_sort.go      # 冒泡排序处理器
│   ├── track.go            # 埋点处理器
│   ├── analytics.go        # 统计分析处理器
│   └── export.go           # 导出处理器
├── models/
│   └── tracking_record.go  # 埋点记录模型
├── services/               # 业务服务层
│   ├── hash_service.go     # 哈希服务
│   ├── sort_service.go     # 排序服务
│   └── analytics_service.go# 统计分析服务
├── database/
│   └── db.go               # 数据库连接和初始化
└── utils/
    └── response.go         # 统一响应格式
```

**API 接口**:
- `GET /api/hello?name=xxx` - HelloWorld 接口
- `POST /api/hash` - 哈希算法接口
- `POST /api/bubble-sort` - 冒泡排序接口
- `POST /api/track` - 埋点记录接口
- `GET /api/analytics?dimension=type` - 统计分析接口
- `GET /api/export?type=all` - 导出接口

**运行方式**:
```bash
cd test
go mod tidy
go run main.go
```

服务将在 `http://localhost:8080` 启动

---

### 前端仓库：huanmeng511331-project
**路径**: `huanmeng511331-project-agent-6f6adf8a`

**技术栈**: React 18 + TypeScript + Vite + Ant Design + ECharts

**目录结构**:
```
huanmeng511331-project/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
├── src/
│   ├── main.tsx              # 应用入口
│   ├── App.tsx               # 主组件
│   ├── components/           # 可复用组件
│   │   ├── HelloWorldTab.tsx
│   │   ├── HashTab.tsx
│   │   ├── BubbleSortTab.tsx
│   │   └── ExportButton.tsx
│   ├── pages/                # 页面组件
│   │   ├── HomePage.tsx
│   │   └── AnalyticsPage.tsx
│   ├── services/             # API 服务层
│   │   └── api.ts
│   ├── types/                # TypeScript 类型定义
│   │   └── index.ts
│   └── styles/               # 全局样式
│       └── global.css
└── public/                   # 静态资源
```

**运行方式**:
```bash
cd huanmeng511331-project
npm install
npm run dev
```

前端将在 `http://localhost:5173` 启动

---

## 快速开始

### 1. 启动后端
```bash
cd test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d
go mod tidy
go run main.go
```

### 2. 启动前端（新终端）
```bash
cd huanmeng511331-project-agent-6f6adf8a
npm install
npm run dev
```

### 3. 访问应用
打开浏览器访问 `http://localhost:5173`

---

## 功能演示

### 1. HelloWorld 功能
- 在 HelloWorld Tab 输入名称（可选）
- 点击"获取消息"按钮
- 查看返回的问候消息

### 2. 哈希算法功能
- 选择哈希算法（MD5/SHA256/SHA512）
- 输入要哈希的数据
- 点击"计算哈希"按钮
- 查看哈希结果

### 3. 冒泡排序功能
- 输入逗号分隔的数字数组
- 点击"执行排序"按钮
- 查看原始数组、排序结果和比较次数

### 4. 数据导出
- 点击"导出当前页结果"下拉按钮
- 选择要导出的数据类型
- 浏览器将自动下载 Excel 文件

### 5. 统计报表
- 点击"查看统计报表"按钮
- 选择统计维度（人员类型/层级/部门）
- 选择时间范围（可选）
- 点击"查询"按钮
- 查看折线图、饼图和柱状图

---

## 技术亮点

### 后端
- **Gin 框架**: 高性能 HTTP Web 框架
- **GORM**: Go 语言 ORM 库，简化数据库操作
- **SQLite**: 轻量级嵌入式数据库，无需额外部署
- **Excelize**: Excel 文件读写库，支持导出功能
- **CORS 支持**: 跨域资源共享配置
- **异步埋点**: 使用 goroutine 异步记录调用信息

### 前端
- **React 18**: 最新的 React 版本，支持并发特性
- **TypeScript**: 类型安全，提高代码质量
- **Vite**: 快速的构建工具，开发体验优秀
- **Ant Design**: 企业级 UI 组件库
- **ECharts**: 强大的图表库，支持多种图表类型
- **Axios**: HTTP 客户端，支持请求拦截器
- **React Router**: 声明式路由管理

---

## 注意事项

1. **Go 环境**: 后端需要安装 Go 1.21+
2. **Node.js 环境**: 前端需要安装 Node.js 16+
3. **端口占用**: 后端使用 8080 端口，前端使用 5173 端口
4. **数据库文件**: 首次运行后端会自动创建 `tracking.db` 文件
5. **依赖安装**: 确保先运行 `go mod tidy` 和 `npm install`

---

## 后续扩展

- 用户认证与授权
- 更多统计算法（热力图、漏斗图等）
- 实时数据统计（WebSocket）
- 更多导出格式（PDF、CSV）
- 性能优化和压力测试
