# 技术设计文档

## 1. 后端详细设计

### 1.1 项目结构

```
test/
├── main.go                 # 程序入口
├── go.mod                  # Go 模块定义
├── go.sum                  # 依赖校验
├── config/
│   └── config.go           # 配置管理
├── handlers/
│   ├── hello.go            # HelloWorld 处理器
│   ├── hash.go             # 哈希算法处理器
│   ├── bubble_sort.go      # 冒泡排序处理器
│   ├── track.go            # 埋点处理器
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

### 1.2 核心代码示例

#### main.go
```go
package main

import (
    "log"
    "test/config"
    "test/database"
    "test/handlers"
)

func main() {
    // 初始化配置
    cfg := config.Load()
    
    // 初始化数据库
    database.Init(cfg.DatabaseURL)
    
    // 创建 Gin 路由
    r := gin.Default()
    
    // 注册路由
    api := r.Group("/api")
    {
        api.GET("/hello", handlers.HelloHandler)
        api.POST("/hash", handlers.HashHandler)
        api.POST("/bubble-sort", handlers.BubbleSortHandler)
        api.POST("/track", handlers.TrackHandler)
        api.GET("/analytics", handlers.AnalyticsHandler)
        api.GET("/export", handlers.ExportHandler)
    }
    
    // 启动服务
    log.Fatal(r.Run(":8080"))
}
```

#### handlers/hello.go
```go
package handlers

import (
    "net/http"
    "test/utils"
    "github.com/gin-gonic/gin"
)

func HelloHandler(c *gin.Context) {
    name := c.DefaultQuery("name", "World")
    
    // 记录埋点
    go recordTrack("/api/hello", c)
    
    utils.Success(c, gin.H{
        "message": fmt.Sprintf("Hello, %s!", name),
    })
}

func recordTrack(endpoint string, c *gin.Context) {
    callerId := c.GetHeader("X-Caller-ID")
    if callerId == "" {
        callerId = "anonymous"
    }
    
    record := models.TrackingRecord{
        Endpoint:          endpoint,
        CallerID:          callerId,
        CallerType:        c.GetHeader("X-Caller-Type"),
        CallerLevel:       c.GetHeader("X-Caller-Level"),
        CallerDepartment:  c.GetHeader("X-Caller-Department"),
    }
    
    database.DB.Create(&record)
}
```

#### handlers/hash.go
```go
package handlers

import (
    "crypto/md5"
    "crypto/sha256"
    "crypto/sha512"
    "encoding/hex"
    "fmt"
    "net/http"
    "test/services"
    "test/utils"
    "github.com/gin-gonic/gin"
)

type HashRequest struct {
    Algorithm string `json:"algorithm" binding:"required"`
    Data      string `json:"data" binding:"required"`
}

type HashResponse struct {
    Algorithm string `json:"algorithm"`
    Original  string `json:"original"`
    Hash      string `json:"hash"`
}

func HashHandler(c *gin.Context) {
    var req HashRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        utils.Error(c, http.StatusBadRequest, "Invalid request")
        return
    }
    
    hash, err := services.CalculateHash(req.Algorithm, req.Data)
    if err != nil {
        utils.Error(c, http.StatusBadRequest, err.Error())
        return
    }
    
    // 记录埋点
    go recordTrack("/api/hash", c)
    
    utils.Success(c, HashResponse{
        Algorithm: req.Algorithm,
        Original:  req.Data,
        Hash:      hash,
    })
}
```

#### services/hash_service.go
```go
package services

import (
    "crypto/md5"
    "crypto/sha256"
    "crypto/sha512"
    "encoding/hex"
    "fmt"
)

func CalculateHash(algorithm, data string) (string, error) {
    var hash []byte
    
    switch algorithm {
    case "md5":
        h := md5.Sum([]byte(data))
        hash = h[:]
    case "sha256":
        h := sha256.Sum256([]byte(data))
        hash = h[:]
    case "sha512":
        h := sha512.Sum512([]byte(data))
        hash = h[:]
    default:
        return "", fmt.Errorf("unsupported algorithm: %s", algorithm)
    }
    
    return hex.EncodeToString(hash), nil
}
```

#### handlers/bubble_sort.go
```go
package handlers

import (
    "net/http"
    "test/services"
    "test/utils"
    "github.com/gin-gonic/gin"
)

type SortRequest struct {
    Numbers []int `json:"numbers" binding:"required"`
}

type SortResponse struct {
    Original []int `json:"original"`
    Sorted   []int `json:"sorted"`
    Steps    int   `json:"steps"`
}

func BubbleSortHandler(c *gin.Context) {
    var req SortRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        utils.Error(c, http.StatusBadRequest, "Invalid request")
        return
    }
    
    sorted, steps := services.BubbleSort(req.Numbers)
    
    // 记录埋点
    go recordTrack("/api/bubble-sort", c)
    
    utils.Success(c, SortResponse{
        Original: req.Numbers,
        Sorted:   sorted,
        Steps:    steps,
    })
}
```

#### services/sort_service.go
```go
package services

func BubbleSort(arr []int) ([]int, int) {
    result := make([]int, len(arr))
    copy(result, arr)
    
    steps := 0
    n := len(result)
    
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-i-1; j++ {
            steps++
            if result[j] > result[j+1] {
                result[j], result[j+1] = result[j+1], result[j]
                swapped = true
            }
        }
        if !swapped {
            break
        }
    }
    
    return result, steps
}
```

#### models/tracking_record.go
```go
package models

import "time"

type TrackingRecord struct {
    ID               uint      `gorm:"primaryKey" json:"id"`
    Endpoint         string    `gorm:"size:50;index" json:"endpoint"`
    CallerID         string    `gorm:"size:100" json:"callerId"`
    CallerType       string    `gorm:"size:50;index" json:"callerType"`
    CallerLevel      string    `gorm:"size:50;index" json:"callerLevel"`
    CallerDepartment string    `gorm:"size:100;index" json:"callerDepartment"`
    CreatedAt        time.Time `gorm:"index" json:"createdAt"`
}
```

#### handlers/analytics.go
```go
package handlers

import (
    "net/http"
    "test/services"
    "test/utils"
    "github.com/gin-gonic/gin"
)

func AnalyticsHandler(c *gin.Context) {
    dimension := c.Query("dimension")
    startDate := c.Query("startDate")
    endDate := c.Query("endDate")
    endpoint := c.Query("endpoint")
    
    if dimension == "" {
        utils.Error(c, http.StatusBadRequest, "dimension parameter is required")
        return
    }
    
    data, total, err := services.GetAnalytics(dimension, startDate, endDate, endpoint)
    if err != nil {
        utils.Error(c, http.StatusInternalServerError, err.Error())
        return
    }
    
    utils.Success(c, gin.H{
        "dimension": dimension,
        "data":      data,
        "total":     total,
    })
}
```

#### services/analytics_service.go
```go
package services

import (
    "test/database"
    "test/models"
)

type AnalyticsData struct {
    Label string `json:"label"`
    Count int64  `json:"count"`
}

func GetAnalytics(dimension, startDate, endDate, endpoint string) ([]AnalyticsData, int64, error) {
    query := database.DB.Model(&models.TrackingRecord{})
    
    if startDate != "" {
        query = query.Where("created_at >= ?", startDate)
    }
    if endDate != "" {
        query = query.Where("created_at <= ?", endDate)
    }
    if endpoint != "" {
        query = query.Where("endpoint = ?", endpoint)
    }
    
    var results []AnalyticsData
    var total int64
    
    switch dimension {
    case "type":
        query.Select("caller_type as label, COUNT(*) as count").
            Group("caller_type").
            Find(&results)
    case "level":
        query.Select("caller_level as label, COUNT(*) as count").
            Group("caller_level").
            Find(&results)
    case "department":
        query.Select("caller_department as label, COUNT(*) as count").
            Group("caller_department").
            Find(&results)
    default:
        return nil, 0, fmt.Errorf("invalid dimension: %s", dimension)
    }
    
    database.DB.Model(&models.TrackingRecord{}).Count(&total)
    
    return results, total, nil
}
```

#### handlers/export.go
```go
package handlers

import (
    "fmt"
    "net/http"
    "test/database"
    "test/models"
    "github.com/gin-gonic/gin"
    "github.com/xuri/excelize/v2"
)

func ExportHandler(c *gin.Context) {
    exportType := c.Query("type")
    if exportType == "" {
        exportType = "all"
    }
    
    f := excelize.NewFile()
    defer f.Close()
    
    switch exportType {
    case "hello":
        exportHelloData(f)
    case "hash":
        exportHashData(f)
    case "sort":
        exportSortData(f)
    case "all":
        exportHelloData(f)
        exportHashData(f)
        exportSortData(f)
    default:
        c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid export type"})
        return
    }
    
    // 设置响应头
    filename := fmt.Sprintf("export_%s.xlsx", exportType)
    c.Header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    c.Header("Content-Disposition", fmt.Sprintf("attachment; filename=%s", filename))
    
    if err := f.Write(c.Writer); err != nil {
        c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
        return
    }
}

func exportHelloData(f *excelize.File) {
    sheet := "HelloWorld"
    f.SetSheetName("Sheet1", sheet)
    
    // 设置表头
    f.SetCellValue(sheet, "A1", "调用时间")
    f.SetCellValue(sheet, "B1", "调用者ID")
    f.SetCellValue(sheet, "C1", "参数")
    
    // 查询数据（这里简化处理，实际应该从日志或专门的结果表中查询）
    var records []models.TrackingRecord
    database.DB.Where("endpoint = ?", "/api/hello").Find(&records)
    
    for i, record := range records {
        row := i + 2
        f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt)
        f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
        f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "name=World")
    }
}

func exportHashData(f *excelize.File) {
    // 类似实现
}

func exportSortData(f *excelize.File) {
    // 类似实现
}
```

### 1.3 依赖管理

**go.mod**
```go
module test

go 1.21

require (
    github.com/gin-gonic/gin v1.9.1
    gorm.io/gorm v1.25.5
    gorm.io/driver/sqlite v1.5.4
    github.com/xuri/excelize/v2 v2.8.0
)
```

## 2. 前端详细设计

### 2.1 项目结构

```
huanmeng511331-project/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── components/
│   │   ├── Header.tsx
│   │   ├── TabPanel.tsx
│   │   ├── HelloWorldTab.tsx
│   │   ├── HashTab.tsx
│   │   ├── BubbleSortTab.tsx
│   │   └── ExportButton.tsx
│   ├── pages/
│   │   ├── HomePage.tsx
│   │   └── AnalyticsPage.tsx
│   ├── services/
│   │   └── api.ts
│   ├── types/
│   │   └── index.ts
│   └── styles/
│       └── global.css
└── public/
```

### 2.2 核心组件示例

#### types/index.ts
```typescript
export interface HashRequest {
  algorithm: 'md5' | 'sha256' | 'sha512';
  data: string;
}

export interface HashResponse {
  algorithm: string;
  original: string;
  hash: string;
}

export interface SortRequest {
  numbers: number[];
}

export interface SortResponse {
  original: number[];
  sorted: number[];
  steps: number;
}

export interface TrackRequest {
  endpoint: string;
  callerId: string;
  callerType?: string;
  callerLevel?: string;
  callerDepartment?: string;
}

export interface AnalyticsData {
  label: string;
  count: number;
}

export interface AnalyticsResponse {
  dimension: string;
  data: AnalyticsData[];
  total: number;
}
```

#### services/api.ts
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 添加请求拦截器，自动添加调用者信息
apiClient.interceptors.request.use((config) => {
  config.headers['X-Caller-ID'] = localStorage.getItem('callerId') || 'anonymous';
  config.headers['X-Caller-Type'] = localStorage.getItem('callerType') || 'normal';
  config.headers['X-Caller-Level'] = localStorage.getItem('callerLevel') || 'junior';
  config.headers['X-Caller-Department'] = localStorage.getItem('callerDepartment') || 'tech';
  return config;
});

export const helloApi = {
  getMessage: (name?: string) => 
    apiClient.get('/hello', { params: { name } }),
};

export const hashApi = {
  calculate: (data: HashRequest) => 
    apiClient.post<HashResponse>('/hash', data),
};

export const sortApi = {
  bubbleSort: (data: SortRequest) => 
    apiClient.post<SortResponse>('/bubble-sort', data),
};

export const trackApi = {
  record: (data: TrackRequest) => 
    apiClient.post('/track', data),
};

export const analyticsApi = {
  getStats: (params: {
    dimension: string;
    startDate?: string;
    endDate?: string;
    endpoint?: string;
  }) => 
    apiClient.get<AnalyticsResponse>('/analytics', { params }),
};

export const exportApi = {
  download: (type: string) => {
    window.location.href = `http://localhost:8080/api/export?type=${type}`;
  },
};
```

#### components/HelloWorldTab.tsx
```tsx
import React, { useState } from 'react';
import { Input, Button, Card, message } from 'antd';
import { helloApi } from '../services/api';

const HelloWorldTab: React.FC = () => {
  const [name, setName] = useState('');
  const [result, setResult] = useState('');

  const handleGetMessage = async () => {
    try {
      const response = await helloApi.getMessage(name || undefined);
      setResult(response.data.message);
      message.success('获取成功');
    } catch (error) {
      message.error('获取失败');
    }
  };

  return (
    <Card title="HelloWorld 演示">
      <Input
        placeholder="输入名称（默认为 World）"
        value={name}
        onChange={(e) => setName(e.target.value)}
        style={{ marginBottom: 16 }}
      />
      <Button type="primary" onClick={handleGetMessage}>
        获取消息
      </Button>
      {result && (
        <div style={{ marginTop: 16, padding: 16, background: '#f0f0f0' }}>
          <strong>结果：</strong>{result}
        </div>
      )}
    </Card>
  );
};

export default HelloWorldTab;
```

#### components/HashTab.tsx
```tsx
import React, { useState } from 'react';
import { Input, Select, Button, Card, message } from 'antd';
import { hashApi } from '../services/api';
import type { HashRequest } from '../types';

const { Option } = Select;

const HashTab: React.FC = () => {
  const [algorithm, setAlgorithm] = useState<'md5' | 'sha256' | 'sha512'>('sha256');
  const [data, setData] = useState('');
  const [result, setResult] = useState<any>(null);

  const handleCalculate = async () => {
    if (!data) {
      message.warning('请输入数据');
      return;
    }

    try {
      const response = await hashApi.calculate({ algorithm, data });
      setResult(response.data);
      message.success('计算成功');
    } catch (error) {
      message.error('计算失败');
    }
  };

  return (
    <Card title="哈希算法演示">
      <Select
        value={algorithm}
        onChange={(value) => setAlgorithm(value)}
        style={{ width: 200, marginBottom: 16 }}
      >
        <Option value="md5">MD5</Option>
        <Option value="sha256">SHA256</Option>
        <Option value="sha512">SHA512</Option>
      </Select>
      <Input.TextArea
        placeholder="输入要哈希的数据"
        value={data}
        onChange={(e) => setData(e.target.value)}
        rows={4}
        style={{ marginBottom: 16 }}
      />
      <Button type="primary" onClick={handleCalculate}>
        计算哈希
      </Button>
      {result && (
        <div style={{ marginTop: 16, padding: 16, background: '#f0f0f0' }}>
          <div><strong>算法：</strong>{result.algorithm}</div>
          <div><strong>原始数据：</strong>{result.original}</div>
          <div><strong>哈希值：</strong>{result.hash}</div>
        </div>
      )}
    </Card>
  );
};

export default HashTab;
```

#### components/BubbleSortTab.tsx
```tsx
import React, { useState } from 'react';
import { Input, Button, Card, message, Tag } from 'antd';
import { sortApi } from '../services/api';

const BubbleSortTab: React.FC = () => {
  const [input, setInput] = useState('');
  const [result, setResult] = useState<any>(null);

  const handleSort = async () => {
    try {
      const numbers = input.split(',').map(n => parseInt(n.trim())).filter(n => !isNaN(n));
      
      if (numbers.length === 0) {
        message.warning('请输入有效的数字数组');
        return;
      }

      const response = await sortApi.bubbleSort({ numbers });
      setResult(response.data);
      message.success('排序成功');
    } catch (error) {
      message.error('排序失败');
    }
  };

  return (
    <Card title="冒泡排序演示">
      <Input
        placeholder="输入数字数组，用逗号分隔（如：5,3,8,1,9）"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        style={{ marginBottom: 16 }}
      />
      <Button type="primary" onClick={handleSort}>
        执行排序
      </Button>
      {result && (
        <div style={{ marginTop: 16, padding: 16, background: '#f0f0f0' }}>
          <div>
            <strong>原始数组：</strong>
            {result.original.map((n: number, i: number) => (
              <Tag key={i} color="blue">{n}</Tag>
            ))}
          </div>
          <div style={{ marginTop: 8 }}>
            <strong>排序结果：</strong>
            {result.sorted.map((n: number, i: number) => (
              <Tag key={i} color="green">{n}</Tag>
            ))}
          </div>
          <div style={{ marginTop: 8 }}>
            <strong>比较次数：</strong>{result.steps}
          </div>
        </div>
      )}
    </Card>
  );
};

export default BubbleSortTab;
```

#### pages/HomePage.tsx
```tsx
import React from 'react';
import { Tabs, Button, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import HelloWorldTab from '../components/HelloWorldTab';
import HashTab from '../components/HashTab';
import BubbleSortTab from '../components/BubbleSortTab';
import ExportButton from '../components/ExportButton';

const { TabPane } = Tabs;

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div style={{ padding: 24 }}>
      <h1>算法演示系统</h1>
      <Tabs defaultActiveKey="1">
        <TabPane tab="HelloWorld" key="1">
          <HelloWorldTab />
        </TabPane>
        <TabPane tab="哈希算法" key="2">
          <HashTab />
        </TabPane>
        <TabPane tab="冒泡排序" key="3">
          <BubbleSortTab />
        </TabPane>
      </Tabs>
      <Space style={{ marginTop: 24 }}>
        <ExportButton />
        <Button onClick={() => navigate('/analytics')}>
          查看统计报表
        </Button>
      </Space>
    </div>
  );
};

export default HomePage;
```

#### pages/AnalyticsPage.tsx
```tsx
import React, { useState, useEffect } from 'react';
import { Card, Select, DatePicker, Button, Row, Col } from 'antd';
import { useNavigate } from 'react-router-dom';
import ReactECharts from 'echarts-for-react';
import { analyticsApi } from '../services/api';
import type { AnalyticsData } from '../types';

const { RangePicker } = DatePicker;
const { Option } = Select;

const AnalyticsPage: React.FC = () => {
  const navigate = useNavigate();
  const [dimension, setDimension] = useState('type');
  const [dateRange, setDateRange] = useState<any>(null);
  const [data, setData] = useState<AnalyticsData[]>([]);
  const [total, setTotal] = useState(0);

  const fetchAnalytics = async () => {
    const params: any = { dimension };
    if (dateRange) {
      params.startDate = dateRange[0].format('YYYY-MM-DD');
      params.endDate = dateRange[1].format('YYYY-MM-DD');
    }

    try {
      const response = await analyticsApi.getStats(params);
      setData(response.data.data);
      setTotal(response.data.total);
    } catch (error) {
      console.error('获取统计数据失败', error);
    }
  };

  useEffect(() => {
    fetchAnalytics();
  }, [dimension]);

  const getLineChartOption = () => ({
    title: { text: '调用趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map(d => d.label),
    },
    yAxis: { type: 'value' },
    series: [{
      name: '调用次数',
      type: 'line',
      data: data.map(d => d.count),
    }],
  });

  const getPieChartOption = () => ({
    title: { text: '占比分布' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: data.map(d => ({
        name: d.label,
        value: d.count,
      })),
    }],
  });

  const getBarChartOption = () => ({
    title: { text: '对比分析' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map(d => d.label),
    },
    yAxis: { type: 'value' },
    series: [{
      name: '调用次数',
      type: 'bar',
      data: data.map(d => d.count),
    }],
  });

  return (
    <div style={{ padding: 24 }}>
      <h1>调用统计报表</h1>
      <Card>
        <Space>
          <span>维度：</span>
          <Select value={dimension} onChange={setDimension} style={{ width: 120 }}>
            <Option value="type">人员类型</Option>
            <Option value="level">人员层级</Option>
            <Option value="department">人员部门</Option>
          </Select>
          <RangePicker onChange={setDateRange} />
          <Button type="primary" onClick={fetchAnalytics}>查询</Button>
          <Button onClick={() => navigate('/')}>返回主页</Button>
        </Space>
      </Card>

      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="折线图">
            <ReactECharts option={getLineChartOption()} style={{ height: 300 }} />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="饼图">
            <ReactECharts option={getPieChartOption()} style={{ height: 300 }} />
          </Card>
        </Col>
      </Row>

      <Row style={{ marginTop: 16 }}>
        <Col span={24}>
          <Card title="柱状图">
            <ReactECharts option={getBarChartOption()} style={{ height: 300 }} />
          </Card>
        </Col>
      </Row>

      <Card style={{ marginTop: 16 }}>
        <strong>总调用次数：</strong>{total}
      </Card>
    </div>
  );
};

export default AnalyticsPage;
```

#### package.json
```json
{
  "name": "huanmeng511331-project",
  "version": "1.0.0",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "antd": "^5.12.0",
    "axios": "^1.6.2",
    "echarts": "^5.4.3",
    "echarts-for-react": "^3.0.2"
  },
  "devDependencies": {
    "@types/react": "^18.2.43",
    "@types/react-dom": "^18.2.17",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.3.3",
    "vite": "^5.0.8"
  }
}
```

## 3. 部署说明

### 3.1 后端部署
```bash
cd test
go mod tidy
go build -o server main.go
./server
```

### 3.2 前端部署
```bash
cd huanmeng511331-project
npm install
npm run dev
```

### 3.3 访问地址
- 后端 API: http://localhost:8080
- 前端页面: http://localhost:5173

## 4. 注意事项

1. **跨域问题**: 开发环境下需要在后端配置 CORS
2. **数据库初始化**: 首次运行会自动创建 SQLite 数据库文件
3. **埋点异步**: 埋点记录使用 goroutine 异步执行，不影响主业务响应
4. **导出功能**: 目前仅支持 Excel 格式，可根据需要扩展
