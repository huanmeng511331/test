# 后端服务说明

## 项目结构

```
test/
├── main.go                 # 程序入口
├── go.mod                  # Go 模块定义
├── config/
│   └── config.go           # 配置管理
├── handlers/
│   ├── hello.go            # HelloWorld 处理器
│   ├── hash.go             # 哈希算法处理器
│   ├── bubble_sort.go      # 冒泡排序处理器
│   ├── track.go            # 埋点处理器
│   ├── analytics.go        # 统计分析处理器
│   └── export.go           # 导出处理器
├── models/
│   └── tracking_record.go  # 埋点记录模型
├── services/
│   ├── hash_service.go     # 哈希服务
│   ├── sort_service.go     # 排序服务
│   └── analytics_service.go# 统计分析服务
├── database/
│   └── db.go               # 数据库连接和初始化
└── utils/
    └── response.go         # 统一响应格式
```

## API 接口文档

### 1. HelloWorld 接口
- **路径**: `GET /api/hello`
- **参数**: `name` (可选，默认 "World")
- **返回**: `{"success": true, "data": {"message": "Hello, World!"}}`

### 2. 哈希算法接口
- **路径**: `POST /api/hash`
- **请求体**: 
  ```json
  {
    "algorithm": "sha256",
    "data": "hello"
  }
  ```
- **返回**: 
  ```json
  {
    "success": true,
    "data": {
      "algorithm": "sha256",
      "original": "hello",
      "hash": "2cf24dba..."
    }
  }
  ```

### 3. 冒泡排序接口
- **路径**: `POST /api/bubble-sort`
- **请求体**: 
  ```json
  {
    "numbers": [5, 3, 8, 1, 9]
  }
  ```
- **返回**: 
  ```json
  {
    "success": true,
    "data": {
      "original": [5, 3, 8, 1, 9],
      "sorted": [1, 3, 5, 8, 9],
      "steps": 10
    }
  }
  ```

### 4. 埋点记录接口
- **路径**: `POST /api/track`
- **请求体**: 
  ```json
  {
    "endpoint": "/api/hello",
    "callerId": "user001",
    "callerType": "admin",
    "callerLevel": "senior",
    "callerDepartment": "tech"
  }
  ```

### 5. 统计分析接口
- **路径**: `GET /api/analytics?dimension=type&startDate=2024-01-01&endDate=2024-12-31`
- **参数**: 
  - `dimension`: type|level|department（必填）
  - `startDate`: 起始日期（可选）
  - `endDate`: 结束日期（可选）
  - `endpoint`: 接口路径（可选）

### 6. 导出接口
- **路径**: `GET /api/export?type=all`
- **参数**: `type`: hello|hash|sort|all（默认 all）
- **返回**: Excel 文件下载

## 运行说明

### 前置条件
- Go 1.21+
- SQLite（内置支持，无需额外安装）

### 安装依赖
```bash
cd test
go mod tidy
```

### 运行服务
```bash
go run main.go
```

服务将在 `http://localhost:8080` 启动

### 环境变量
- `SERVER_PORT`: 服务器端口（默认 8080）
- `DATABASE_URL`: 数据库文件路径（默认 tracking.db）

## 测试示例

### 测试 HelloWorld
```bash
curl http://localhost:8080/api/hello?name=Alice
```

### 测试哈希算法
```bash
curl -X POST http://localhost:8080/api/hash \
  -H "Content-Type: application/json" \
  -d '{"algorithm":"sha256","data":"hello"}'
```

### 测试冒泡排序
```bash
curl -X POST http://localhost:8080/api/bubble-sort \
  -H "Content-Type: application/json" \
  -d '{"numbers":[5,3,8,1,9]}'
```

### 测试埋点
```bash
curl -X POST http://localhost:8080/api/track \
  -H "Content-Type: application/json" \
  -d '{
    "endpoint":"/api/hello",
    "callerId":"user001",
    "callerType":"admin",
    "callerLevel":"senior",
    "callerDepartment":"tech"
  }'
```

### 测试统计查询
```bash
curl "http://localhost:8080/api/analytics?dimension=type"
```

### 测试导出
```bash
curl -o export.xlsx "http://localhost:8080/api/export?type=all"
```
