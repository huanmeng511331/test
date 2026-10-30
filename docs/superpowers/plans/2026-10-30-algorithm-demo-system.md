# 算法演示系统实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现前后端分离的算法演示系统，包含三个基础算法接口（HelloWorld、哈希算法、冒泡排序）、数据导出功能和调用统计可视化报表

**Architecture:** 后端使用 Go + Gin + SQLite 提供 RESTful API，前端使用 React 18 + TypeScript + Ant Design + ECharts 构建三 Tab 页面和统计报表页面，通过 Axios 进行跨库通信

**Tech Stack:** 
- 后端：Go 1.21, Gin, GORM, SQLite, Excelize
- 前端：React 18, TypeScript, Vite, Ant Design, ECharts, Axios, React Router

## Global Constraints

- 后端端口：8080
- 前端端口：5173
- 数据库：SQLite（文件路径由配置决定）
- 跨域：后端需配置 CORS 允许前端访问
- 埋点异步：使用 goroutine 异步记录，不影响主业务响应
- 导出格式：仅支持 Excel (.xlsx)
- 调用者信息通过 HTTP Header 传递：X-Caller-ID, X-Caller-Type, X-Caller-Level, X-Caller-Department

---

### Task 1: 后端 - HelloWorld 接口实现

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/handlers/hello.go`
- Modify: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/utils/response.go`

**Interfaces:**
- Consumes: Gin Context, utils.Success/Error
- Produces: HelloHandler 函数，返回 JSON `{message: "Hello, {name}!"}`

- [ ] **Step 1: 创建统一响应工具函数**

```go
package utils

import (
	"net/http"
	"github.com/gin-gonic/gin"
)

type Response struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data,omitempty"`
}

func Success(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, Response{
		Code:    0,
		Message: "success",
		Data:    data,
	})
}

func Error(c *gin.Context, code int, message string) {
	c.JSON(code, Response{
		Code:    code,
		Message: message,
	})
}
```

- [ ] **Step 2: 运行测试验证 utils 包编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build ./utils`
Expected: 无错误输出

- [ ] **Step 3: 创建 HelloWorld 处理器**

```go
package handlers

import (
	"fmt"
	"test/models"
	"test/database"
	"test/utils"
	"github.com/gin-gonic/gin"
)

func HelloHandler(c *gin.Context) {
	name := c.DefaultQuery("name", "World")
	
	// 异步记录埋点
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

- [ ] **Step 4: 验证 main.go 已注册路由**

确认 main.go 中已有 `api.GET("/hello", handlers.HelloHandler)`

- [ ] **Step 5: 编译验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 生成 server 可执行文件，无编译错误

---

### Task 2: 后端 - 哈希算法接口实现

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/services/hash_service.go`
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/handlers/hash.go`

**Interfaces:**
- Consumes: services.CalculateHash(algorithm, data string) (string, error)
- Produces: HashHandler 函数，接收 JSON `{algorithm, data}`，返回 `{algorithm, original, hash}`

- [ ] **Step 1: 创建哈希服务**

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

- [ ] **Step 2: 创建哈希处理器**

```go
package handlers

import (
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
	
	// 异步记录埋点
	go recordTrack("/api/hash", c)
	
	utils.Success(c, HashResponse{
		Algorithm: req.Algorithm,
		Original:  req.Data,
		Hash:      hash,
	})
}
```

- [ ] **Step 3: 验证 main.go 已注册路由**

确认 main.go 中已有 `api.POST("/hash", handlers.HashHandler)`

- [ ] **Step 4: 编译验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 无编译错误

---

### Task 3: 后端 - 冒泡排序接口实现

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/services/sort_service.go`
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/handlers/bubble_sort.go`

**Interfaces:**
- Consumes: services.BubbleSort(arr []int) ([]int, int)
- Produces: BubbleSortHandler 函数，接收 JSON `{numbers: []int}`，返回 `{original, sorted, steps}`

- [ ] **Step 1: 创建排序服务**

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

- [ ] **Step 2: 创建冒泡排序处理器**

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
	
	// 异步记录埋点
	go recordTrack("/api/bubble-sort", c)
	
	utils.Success(c, SortResponse{
		Original: req.Numbers,
		Sorted:   sorted,
		Steps:    steps,
	})
}
```

- [ ] **Step 3: 验证 main.go 已注册路由**

确认 main.go 中已有 `api.POST("/bubble-sort", handlers.BubbleSortHandler)`

- [ ] **Step 4: 编译验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 无编译错误

---

### Task 4: 后端 - 埋点记录与查询接口

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/models/tracking_record.go`
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/services/analytics_service.go`
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/handlers/track.go`
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/handlers/analytics.go`
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/database/db.go`

**Interfaces:**
- Consumes: database.DB (GORM instance), models.TrackingRecord
- Produces: TrackHandler (POST /api/track), AnalyticsHandler (GET /api/analytics?dimension=type|level|department&startDate=&endDate=&endpoint=)

- [ ] **Step 1: 创建数据库连接模块**

```go
package database

import (
	"log"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var DB *gorm.DB

func Init(databaseURL string) {
	var err error
	DB, err = gorm.Open(sqlite.Open(databaseURL), &gorm.Config{})
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}
	log.Println("Database connected successfully")
}
```

- [ ] **Step 2: 创建埋点记录模型**

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

- [ ] **Step 3: 创建埋点记录处理器**

```go
package handlers

import (
	"net/http"
	"test/models"
	"test/database"
	"test/utils"
	"github.com/gin-gonic/gin"
)

type TrackRequest struct {
	Endpoint         string `json:"endpoint" binding:"required"`
	CallerID         string `json:"callerId" binding:"required"`
	CallerType       string `json:"callerType"`
	CallerLevel      string `json:"callerLevel"`
	CallerDepartment string `json:"callerDepartment"`
}

func TrackHandler(c *gin.Context) {
	var req TrackRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.Error(c, http.StatusBadRequest, "Invalid request")
		return
	}
	
	record := models.TrackingRecord{
		Endpoint:         req.Endpoint,
		CallerID:         req.CallerID,
		CallerType:       req.CallerType,
		CallerLevel:      req.CallerLevel,
		CallerDepartment: req.CallerDepartment,
	}
	
	if err := database.DB.Create(&record).Error; err != nil {
		utils.Error(c, http.StatusInternalServerError, err.Error())
		return
	}
	
	utils.Success(c, gin.H{
		"success":  true,
		"recordId": record.ID,
	})
}
```

- [ ] **Step 4: 创建统计分析服务**

```go
package services

import (
	"fmt"
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

- [ ] **Step 5: 创建统计分析处理器**

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

- [ ] **Step 6: 验证 main.go 已注册路由和数据库迁移**

确认 main.go 中已有：
- `database.Init(cfg.DatabaseURL)`
- `database.DB.AutoMigrate(&models.TrackingRecord{})`
- `api.POST("/track", handlers.TrackHandler)`
- `api.GET("/analytics", handlers.AnalyticsHandler)`

- [ ] **Step 7: 编译验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 无编译错误

---

### Task 5: 后端 - Excel 导出接口

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/handlers/export.go`

**Interfaces:**
- Consumes: database.DB, models.TrackingRecord, excelize library
- Produces: ExportHandler (GET /api/export?type=hello|hash|sort|all)，返回 Excel 文件流

- [ ] **Step 1: 创建导出处理器**

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
	index, _ := f.NewSheet(sheet)
	f.SetActiveSheet(index)
	
	// 设置表头
	f.SetCellValue(sheet, "A1", "调用时间")
	f.SetCellValue(sheet, "B1", "调用者ID")
	f.SetCellValue(sheet, "C1", "参数")
	
	// 查询数据
	var records []models.TrackingRecord
	database.DB.Where("endpoint = ?", "/api/hello").Find(&records)
	
	for i, record := range records {
		row := i + 2
		f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt.Format("2006-01-02 15:04:05"))
		f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
		f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "name=World")
	}
	
	// 删除默认 Sheet1
	f.DeleteSheet("Sheet1")
}

func exportHashData(f *excelize.File) {
	sheet := "HashAlgorithm"
	index, _ := f.NewSheet(sheet)
	
	f.SetCellValue(sheet, "A1", "调用时间")
	f.SetCellValue(sheet, "B1", "调用者ID")
	f.SetCellValue(sheet, "C1", "算法")
	f.SetCellValue(sheet, "D1", "原始数据")
	
	var records []models.TrackingRecord
	database.DB.Where("endpoint = ?", "/api/hash").Find(&records)
	
	for i, record := range records {
		row := i + 2
		f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt.Format("2006-01-02 15:04:05"))
		f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
		f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "N/A")
		f.SetCellValue(sheet, fmt.Sprintf("D%d", row), "N/A")
	}
}

func exportSortData(f *excelize.File) {
	sheet := "BubbleSort"
	index, _ := f.NewSheet(sheet)
	
	f.SetCellValue(sheet, "A1", "调用时间")
	f.SetCellValue(sheet, "B1", "调用者ID")
	f.SetCellValue(sheet, "C1", "原始数组")
	
	var records []models.TrackingRecord
	database.DB.Where("endpoint = ?", "/api/bubble-sort").Find(&records)
	
	for i, record := range records {
		row := i + 2
		f.SetCellValue(sheet, fmt.Sprintf("A%d", row), record.CreatedAt.Format("2006-01-02 15:04:05"))
		f.SetCellValue(sheet, fmt.Sprintf("B%d", row), record.CallerID)
		f.SetCellValue(sheet, fmt.Sprintf("C%d", row), "N/A")
	}
}
```

- [ ] **Step 2: 验证 main.go 已注册路由**

确认 main.go 中已有 `api.GET("/export", handlers.ExportHandler)`

- [ ] **Step 3: 编译验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 无编译错误

---

### Task 6: 后端 - 配置管理

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/config/config.go`

**Interfaces:**
- Produces: config.Load() 返回 Config 结构体，包含 ServerPort 和 DatabaseURL

- [ ] **Step 1: 创建配置管理模块**

```go
package config

import "os"

type Config struct {
	ServerPort  string
	DatabaseURL string
}

func Load() *Config {
	serverPort := os.Getenv("SERVER_PORT")
	if serverPort == "" {
		serverPort = "8080"
	}
	
	databaseURL := os.Getenv("DATABASE_URL")
	if databaseURL == "" {
		databaseURL = "tracking.db"
	}
	
	return &Config{
		ServerPort:  serverPort,
		DatabaseURL: databaseURL,
	}
}
```

- [ ] **Step 2: 验证 main.go 已使用配置**

确认 main.go 中已有 `cfg := config.Load()` 和 `database.Init(cfg.DatabaseURL)`

- [ ] **Step 3: 编译验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 无编译错误

---

### Task 7: 前端 - 项目初始化与依赖安装

**Files:**
- Modify: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/package.json` (已存在，确认依赖完整)

**Interfaces:**
- Consumes: npm/yarn package manager
- Produces: node_modules 目录，所有依赖已安装

- [ ] **Step 1: 确认 package.json 依赖完整**

确认包含：
```json
{
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

- [ ] **Step 2: 安装依赖**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npm install`
Expected: 成功安装所有依赖，生成 node_modules 目录

- [ ] **Step 3: 验证 TypeScript 配置**

确认 tsconfig.json 和 tsconfig.node.json 存在且配置正确

---

### Task 8: 前端 - TypeScript 类型定义

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/types/index.ts`

**Interfaces:**
- Produces: HashRequest, HashResponse, SortRequest, SortResponse, TrackRequest, AnalyticsData, AnalyticsResponse 等类型定义

- [ ] **Step 1: 创建类型定义文件**

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

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 9: 前端 - API 服务层

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/services/api.ts`

**Interfaces:**
- Consumes: axios, localStorage (调用者信息)
- Produces: helloApi, hashApi, sortApi, trackApi, analyticsApi, exportApi 对象

- [ ] **Step 1: 创建 API 服务模块**

```typescript
import axios from 'axios';
import type { HashRequest, HashResponse, SortRequest, SortResponse, TrackRequest, AnalyticsResponse } from '../types';

const apiClient = axios.create({
  baseURL: '/api',
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
    window.location.href = `/api/export?type=${type}`;
  },
};
```

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 10: 前端 - HelloWorld Tab 组件

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/components/HelloWorldTab.tsx`

**Interfaces:**
- Consumes: helloApi.getMessage(name?)
- Produces: HelloWorldTab React 组件，包含输入框、按钮和结果展示

- [ ] **Step 1: 创建 HelloWorld Tab 组件**

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

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 11: 前端 - 哈希算法 Tab 组件

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/components/HashTab.tsx`

**Interfaces:**
- Consumes: hashApi.calculate(data: HashRequest)
- Produces: HashTab React 组件，包含算法选择、输入框和结果展示

- [ ] **Step 1: 创建哈希算法 Tab 组件**

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

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 12: 前端 - 冒泡排序 Tab 组件

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/components/BubbleSortTab.tsx`

**Interfaces:**
- Consumes: sortApi.bubbleSort(data: SortRequest)
- Produces: BubbleSortTab React 组件，包含数组输入、排序按钮和结果展示

- [ ] **Step 1: 创建冒泡排序 Tab 组件**

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

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 13: 前端 - 导出按钮组件

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/components/ExportButton.tsx`

**Interfaces:**
- Consumes: exportApi.download(type)
- Produces: ExportButton React 组件，支持下拉选择导出类型

- [ ] **Step 1: 创建导出按钮组件**

```tsx
import React from 'react';
import { Button, Dropdown, MenuProps } from 'antd';
import { exportApi } from '../services/api';

const ExportButton: React.FC = () => {
  const items: MenuProps['items'] = [
    {
      key: 'hello',
      label: 'HelloWorld 数据',
      onClick: () => exportApi.download('hello'),
    },
    {
      key: 'hash',
      label: '哈希算法数据',
      onClick: () => exportApi.download('hash'),
    },
    {
      key: 'sort',
      label: '冒泡排序数据',
      onClick: () => exportApi.download('sort'),
    },
    {
      key: 'all',
      label: '全部数据',
      onClick: () => exportApi.download('all'),
    },
  ];

  return (
    <Dropdown menu={{ items }}>
      <Button>导出当前页结果</Button>
    </Dropdown>
  );
};

export default ExportButton;
```

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 14: 前端 - 主页组件

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/pages/HomePage.tsx`

**Interfaces:**
- Consumes: HelloWorldTab, HashTab, BubbleSortTab, ExportButton 组件
- Produces: HomePage React 组件，包含三 Tab 和导航按钮

- [ ] **Step 1: 创建主页组件**

```tsx
import React from 'react';
import { Tabs, Button, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import HelloWorldTab from '../components/HelloWorldTab';
import HashTab from '../components/HashTab';
import BubbleSortTab from '../components/BubbleSortTab';
import ExportButton from '../components/ExportButton';

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div style={{ padding: 24 }}>
      <h1>算法演示系统</h1>
      <Tabs defaultActiveKey="1">
        <Tabs.TabPane tab="HelloWorld" key="1">
          <HelloWorldTab />
        </Tabs.TabPane>
        <Tabs.TabPane tab="哈希算法" key="2">
          <HashTab />
        </Tabs.TabPane>
        <Tabs.TabPane tab="冒泡排序" key="3">
          <BubbleSortTab />
        </Tabs.TabPane>
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

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 15: 前端 - 统计报表页面

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/pages/AnalyticsPage.tsx`

**Interfaces:**
- Consumes: analyticsApi.getStats(params), ReactECharts
- Produces: AnalyticsPage React 组件，包含筛选条件和三种图表

- [ ] **Step 1: 创建统计报表页面**

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

- [ ] **Step 2: 验证类型编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npx tsc --noEmit`
Expected: 无类型错误

---

### Task 16: 前端 - 全局样式

**Files:**
- Create: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a/src/styles/global.css`

**Interfaces:**
- Produces: 全局 CSS 样式文件

- [ ] **Step 1: 创建全局样式文件**

```css
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
}

#root {
  min-height: 100vh;
}
```

- [ ] **Step 2: 验证 main.tsx 已导入样式**

确认 main.tsx 中已有 `import './styles/global.css';`

---

### Task 17: 后端 - 依赖管理与编译

**Files:**
- Modify: `/root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d/go.mod` (已存在)

**Interfaces:**
- Produces: go.sum 文件，所有依赖已下载

- [ ] **Step 1: 下载依赖**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go mod tidy`
Expected: 生成 go.sum 文件，无错误

- [ ] **Step 2: 完整编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && go build -o server main.go`
Expected: 生成 server 可执行文件

---

### Task 18: 联调测试准备

**Files:**
- No file changes

**Interfaces:**
- Consumes: 后端 server, 前端 dev server
- Produces: 可运行的前后端服务

- [ ] **Step 1: 启动后端服务**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/test-AI-task-DEV-66a2f4f2-84c9-11f1-9849-d5c90ba1aaae-0a5aeae3-22bf-448c-98fd-93336a93624d && ./server`
Expected: 服务启动在 8080 端口，日志显示 "Server starting on port 8080"

- [ ] **Step 2: 启动前端开发服务器**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-6e101080-e19d-4261-a633-da605990ba50/worktree/huanmeng511331-project-agent-6f6adf8a && npm run dev`
Expected: 服务启动在 5173 端口

- [ ] **Step 3: 验证前端访问**

访问 http://localhost:5173 应能看到算法演示系统主页

---

## Self-Review

**1. Spec coverage:**
- ✅ HelloWorld 接口 (Task 1)
- ✅ 哈希算法接口 (Task 2)
- ✅ 冒泡排序接口 (Task 3)
- ✅ 埋点记录与查询 (Task 4)
- ✅ Excel 导出 (Task 5)
- ✅ 配置管理 (Task 6)
- ✅ 前端项目初始化 (Task 7)
- ✅ TypeScript 类型定义 (Task 8)
- ✅ API 服务层 (Task 9)
- ✅ HelloWorld Tab (Task 10)
- ✅ 哈希算法 Tab (Task 11)
- ✅ 冒泡排序 Tab (Task 12)
- ✅ 导出按钮 (Task 13)
- ✅ 主页组件 (Task 14)
- ✅ 统计报表页面 (Task 15)
- ✅ 全局样式 (Task 16)
- ✅ 依赖管理与编译 (Task 17)
- ✅ 联调测试准备 (Task 18)

**2. Placeholder scan:** 无 TBD/TODO/占位符

**3. Type consistency:** 
- 后端：所有 Handler 使用统一的 utils.Success/Error
- 前端：所有 API 调用使用定义的 TypeScript 类型
- 跨库：HTTP Header 名称一致 (X-Caller-ID 等)

Plan complete and saved to `docs/superpowers/plans/2026-10-30-algorithm-demo-system.md`. Two execution options:

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
