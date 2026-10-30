Hello World
511331
test

## 固定资产配置功能

本项目提供固定资产配置和管理功能，包括：
- 资产卡片管理（创建、查询、更新、删除）
- 折旧计算（直线法、双倍余额递减法）
- 资产分类管理

### 快速开始

```bash
go build -o fixedasset-server .
./fixedasset-server
```

服务启动后监听 `http://localhost:8080`。

### 运行测试

```bash
go test ./... -v
```

### API 文档

详见 [docs/api.md](docs/api.md)。

### 项目结构

```
.
├── main.go              # 程序入口
├── model/               # 数据模型
│   └── asset.go         # 固定资产实体与枚举
├── repository/          # 数据仓库层
│   └── repository.go    # 内存实现
├── service/             # 业务逻辑层
│   ├── asset_service.go # 资产服务
│   └── depreciation.go  # 折旧计算引擎
├── handler/             # HTTP 处理层
│   └── asset_handler.go # RESTful API
└── docs/
    └── api.md           # API 文档
```
