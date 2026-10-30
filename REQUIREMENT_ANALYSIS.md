# 需求分析文档

## 1. 项目概述

本项目是一个前后端分离的应用系统，提供基础算法演示、数据导出和调用统计分析功能。

### 1.1 核心功能
- 三个基础算法接口：HelloWorld、哈希算法、冒泡排序
- 前端展示页面：Tab 切换展示不同算法执行结果
- 数据导出：支持导出各页面展示结果为 Excel 文件
- 埋点统计：记录接口调用情况，提供多维度可视化报表

## 2. 技术架构

### 2.1 后端（test 仓库）
- **语言**: Go 1.21+
- **Web 框架**: Gin
- **数据库**: SQLite（轻量级，无需额外部署）
- **ORM**: GORM
- **Excel 导出**: Excelize

### 2.2 前端（huanmeng511331-project 仓库）
- **框架**: React 18 + TypeScript
- **构建工具**: Vite
- **UI 组件库**: Ant Design
- **图表库**: ECharts for React
- **HTTP 客户端**: Axios

## 3. 功能模块详细设计

### 3.1 后端接口设计

#### 3.1.1 业务接口

**HelloWorld 接口**
- 路径: `GET /api/hello`
- 参数: `name` (可选，默认 "World")
- 返回: `{"message": "Hello, {name}!"}`

**哈希算法接口**
- 路径: `POST /api/hash`
- 请求体: 
  ```json
  {
    "algorithm": "md5|sha256|sha512",
    "data": "string"
  }
  ```
- 返回: 
  ```json
  {
    "algorithm": "sha256",
    "original": "hello",
    "hash": "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
  }
  ```

**冒泡排序接口**
- 路径: `POST /api/bubble-sort`
- 请求体: 
  ```json
  {
    "numbers": [5, 3, 8, 1, 9, 2]
  }
  ```
- 返回: 
  ```json
  {
    "original": [5, 3, 8, 1, 9, 2],
    "sorted": [1, 2, 3, 5, 8, 9],
    "steps": 15
  }
  ```

#### 3.1.2 埋点接口

**记录调用**
- 路径: `POST /api/track`
- 请求体: 
  ```json
  {
    "endpoint": "/api/hello",
    "callerId": "user001",
    "callerType": "admin|normal",
    "callerLevel": "junior|middle|senior",
    "callerDepartment": "tech|product|ops"
  }
  ```
- 返回: `{"success": true, "recordId": 123}`

**查询统计数据**
- 路径: `GET /api/analytics`
- 参数: 
  - `dimension`: type|level|department（统计维度）
  - `startDate`: 起始日期（可选）
  - `endDate`: 结束日期（可选）
  - `endpoint`: 接口路径（可选，筛选特定接口）
- 返回: 
  ```json
  {
    "dimension": "type",
    "data": [
      {"label": "admin", "count": 150},
      {"label": "normal", "count": 320}
    ],
    "total": 470
  }
  ```

#### 3.1.3 导出接口

**导出数据**
- 路径: `GET /api/export`
- 参数: 
  - `type`: hello|hash|sort|all（导出类型）
  - `format`: xlsx（固定为 Excel）
- 返回: Excel 文件下载

### 3.2 前端页面设计

#### 3.2.1 主页面结构

```
┌─────────────────────────────────────┐
│  Header: 算法演示系统                │
├─────────────────────────────────────┤
│  Tabs: [HelloWorld] [哈希算法] [冒泡排序] │
├─────────────────────────────────────┤
│                                     │
│  Tab Content:                       │
│  - HelloWorld: 输入框 + 显示结果     │
│  - 哈希算法: 算法选择 + 输入 + 结果  │
│  - 冒泡排序: 数组输入 + 排序过程展示  │
│                                     │
├─────────────────────────────────────┤
│  [导出当前页结果] [查看统计报表]      │
└─────────────────────────────────────┘
```

#### 3.2.2 统计报表页面

```
┌─────────────────────────────────────┐
│  Header: 调用统计报表                │
├─────────────────────────────────────┤
│  筛选条件:                           │
│  - 时间范围: [开始日期] ~ [结束日期]  │
│  - 维度: [人员类型▼] [人员层级▼]      │
│  - 接口: [全部▼]                     │
├─────────────────────────────────────┤
│                                     │
│  图表区域:                           │
│  ┌──────────┐ ┌──────────┐         │
│  │ 折线图    │ │ 饼图      │         │
│  │ (趋势)   │ │ (占比)    │         │
│  └──────────┘ └──────────┘         │
│  ┌──────────────────────┐          │
│  │ 柱状图 (对比)         │          │
│  └──────────────────────┘          │
│                                     │
├─────────────────────────────────────┤
│  [返回主页]                          │
└─────────────────────────────────────┘
```

## 4. 数据库设计

### 4.1 埋点记录表 (tracking_records)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | INTEGER | 主键，自增 |
| endpoint | VARCHAR(50) | 接口路径 |
| caller_id | VARCHAR(100) | 调用者ID |
| caller_type | VARCHAR(50) | 人员类型 |
| caller_level | VARCHAR(50) | 人员层级 |
| caller_department | VARCHAR(100) | 人员部门 |
| created_at | TIMESTAMP | 创建时间 |

### 4.2 索引设计

- `idx_endpoint`: 加速按接口查询
- `idx_created_at`: 加速按时间范围查询
- `idx_caller_type`: 加速按人员类型统计
- `idx_caller_level`: 加速按人员层级统计
- `idx_caller_department`: 加速按部门统计

## 5. 实施计划

### Phase 1: 后端开发
1. 初始化 Go 项目结构
2. 实现三个业务接口
3. 集成 GORM 和 SQLite
4. 实现埋点记录和查询接口
5. 实现 Excel 导出功能

### Phase 2: 前端开发
1. 初始化 React + TypeScript 项目
2. 安装依赖（Ant Design, ECharts, Axios）
3. 实现主页面（三个 Tab）
4. 实现统计报表页面
5. 实现导出功能

### Phase 3: 联调测试
1. 前后端接口联调
2. 功能测试
3. 性能优化

## 6. 风险与注意事项

1. **并发安全**: 埋点记录需要处理并发写入，考虑使用数据库事务
2. **数据安全**: 导出功能需要权限控制（本期暂不实现）
3. **性能考虑**: 大量数据导出时需要考虑内存占用
4. **前端兼容性**: ECharts 需要确保浏览器兼容性

## 7. 后续扩展

- 用户认证与授权
- 更多统计算法（热力图、漏斗图等）
- 实时数据统计（WebSocket）
- 更多导出格式（PDF、CSV）
