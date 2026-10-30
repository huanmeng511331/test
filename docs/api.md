# 固定资产配置 API 文档

## 概述

固定资产配置服务提供固定资产的全生命周期管理，包括资产创建、查询、更新、删除和折旧计算。

Base URL: `http://localhost:8080`

## API 端点

### 创建资产

`POST /api/assets`

请求体：
```json
{
  "asset_no": "A-001",
  "name": "办公电脑",
  "category": "IT设备",
  "original_value": 500000,
  "salvage_value": 50000,
  "useful_life_months": 60,
  "acquisition_date": "2026-01-01T00:00:00Z",
  "method": "straight_line"
}
```

响应：`201 Created`
```json
{
  "id": "a1b2c3d4e5f6a7b8",
  "asset_no": "A-001",
  "name": "办公电脑",
  "category": "IT设备",
  "original_value": 500000,
  "salvage_value": 50000,
  "useful_life_months": 60,
  "acquisition_date": "2026-01-01T00:00:00Z",
  "method": "straight_line",
  "status": "active",
  "net_value": 500000,
  "accumulated_depreciation": 0,
  "created_at": "2026-10-30T...",
  "updated_at": "2026-10-30T..."
}
```

### 列出所有资产

`GET /api/assets`

可选查询参数：`category` — 按类别筛选

响应：`200 OK`，返回资产数组。

### 获取单个资产

`GET /api/assets/{id}`

响应：`200 OK`，返回单个资产对象。未找到返回 `404`。

### 更新资产

`PUT /api/assets/{id}`

请求体：完整的资产对象（包含更新后的字段）

响应：`200 OK`，返回更新后的资产对象。

### 删除资产

`DELETE /api/assets/{id}`

响应：`204 No Content`。未找到返回 `404`。

### 应用折旧

`POST /api/assets/{id}/depreciate`

请求体：
```json
{
  "months_elapsed": 12
}
```

响应：`200 OK`，返回更新后的资产对象（含累计折旧和净值）。

## 数据模型

### 金额单位

所有金额字段使用 **分** 作为单位（int64 类型），避免浮点精度问题。

### 折旧方法

| 值 | 说明 |
|---|---|
| `straight_line` | 直线法：(原值 - 残值) / 使用月数 |
| `double_declining` | 双倍余额递减法：净值 × (2 / 使用月数) |

### 资产状态

| 值 | 说明 |
|---|---|
| `active` | 在用 |
| `depreciating` | 折旧中 |
| `disposed` | 已处置 |

## 错误响应

```json
{
  "error": "error message"
}
```

| HTTP 状态码 | 说明 |
|---|---|
| 400 | 请求参数校验失败 |
| 404 | 资产未找到 |
| 405 | 方法不允许 |
| 409 | 资产编号冲突 / 资产已处置 |
| 500 | 内部错误 |
