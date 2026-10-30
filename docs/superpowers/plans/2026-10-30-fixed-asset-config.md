# 固定资产配置功能 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在现有 Go 项目中实现固定资产配置功能，支持资产类目的增删改查、折旧方法配置和资产卡片初始化。

**Architecture:** 采用分层架构——模型层定义资产实体和枚举，仓库层提供内存持久化（后续可替换为数据库），服务层封装业务逻辑（CRUD + 折旧计算），HTTP handler 层暴露 RESTful API。所有层通过接口解耦，便于测试和替换实现。

**Tech Stack:** Go 1.21+（标准库 `net/http` + `encoding/json`），表驱动测试，无外部依赖。

## Global Constraints

- Go 版本：1.21+（使用标准库，不引入第三方框架）
- 模块路径：`fixedasset`（新建 `go.mod`）
- JSON API 遵循 RESTful 风格，字段命名使用 `snake_case`
- 所有金额字段使用 `int64` 类型（单位：分），避免浮点精度问题
- 折旧方法枚举值：`straight_line`（直线法）、`double_declining`（双倍余额递减法）
- 资产状态枚举值：`active`（在用）、`depreciating`（折旧中）、`disposed`（已处置）
- 测试覆盖：每个公开函数必须有对应的表驱动测试

---

## File Structure

| 文件路径 | 职责 |
|---|---|
| `go.mod` | Go 模块定义，模块名 `fixedasset` |
| `model/asset.go` | 固定资产实体、折旧方法枚举、资产状态枚举的定义 |
| `model/asset_test.go` | 模型层单元测试（枚举校验、JSON 序列化） |
| `repository/repository.go` | `AssetRepository` 接口定义 + 内存实现 `InMemoryAssetRepository` |
| `repository/repository_test.go` | 仓库层测试（CRUD 操作、并发安全、边界条件） |
| `service/depreciation.go` | 折旧计算引擎：直线法和双倍余额递减法 |
| `service/depreciation_test.go` | 折旧计算测试（表驱动，含边界值） |
| `service/asset_service.go` | `AssetService` 业务逻辑层，组合 Repository + Depreciation |
| `service/asset_service_test.go` | 服务层测试（创建、更新、删除、查询、折旧应用） |
| `handler/asset_handler.go` | HTTP handler，注册路由，JSON 请求/响应处理 |
| `handler/asset_handler_test.go` | HTTP 层测试（使用 `httptest`，覆盖各 endpoint） |
| `main.go` | 程序入口，组装各层依赖，启动 HTTP 服务（端口 8080） |

**跨仓依赖说明：** `huanmeng511331-project` 仓库当前为空仓库，不参与本功能实现。所有代码在 `test` 仓库中实现。若后续需要前端配置界面，将在 `huanmeng511331-project` 中独立开发，通过 HTTP API 与本服务交互。

---

### Task 1: 项目初始化与模型定义

**Files:**
- Create: `go.mod`
- Create: `model/asset.go`
- Create: `model/asset_test.go`

**Interfaces:**
- Consumes: 无（基础任务）
- Produces: `FixedAsset` struct、`DepreciationMethod` 类型、`AssetStatus` 类型、`NewFixedAsset()` 构造函数

- [ ] **Step 1: 创建 go.mod**

```
module fixedasset

go 1.21
```

- [ ] **Step 2: 编写模型层失败测试**

```go
// model/asset_test.go
package model

import (
	"testing"
	"time"
)

func TestDepreciationMethod_IsValid(t *testing.T) {
	tests := []struct {
		name    string
		method  DepreciationMethod
		wantValid bool
	}{
		{"straight_line is valid", DepreciationStraightLine, true},
		{"double_declining is valid", DepreciationDoubleDeclining, true},
		{"empty is invalid", DepreciationMethod(""), false},
		{"unknown is invalid", DepreciationMethod("sum_of_years"), false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := tt.method.IsValid(); got != tt.wantValid {
				t.Errorf("DepreciationMethod.IsValid() = %v, want %v", got, tt.wantValid)
			}
		})
	}
}

func TestAssetStatus_IsValid(t *testing.T) {
	tests := []struct {
		name    string
		status  AssetStatus
		wantValid bool
	}{
		{"active is valid", StatusActive, true},
		{"depreciating is valid", StatusDepreciating, true},
		{"disposed is valid", StatusDisposed, true},
		{"empty is invalid", AssetStatus(""), false},
		{"unknown is invalid", AssetStatus("lost"), false},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := tt.status.IsValid(); got != tt.wantValid {
				t.Errorf("AssetStatus.IsValid() = %v, want %v", got, tt.wantValid)
			}
		})
	}
}

func TestNewFixedAsset(t *testing.T) {
	acquisitionDate := time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC)
	asset := NewFixedAsset(
		"A-001",
		"办公电脑",
		"IT设备",
		500000, // 5000.00 元 = 500000 分
		60,     // 使用年限 60 个月
		acquisitionDate,
		DepreciationStraightLine,
		50000,  // 残值 500.00 元 = 50000 分
	)

	if asset.ID == "" {
		t.Error("NewFixedAsset should generate a non-empty ID")
	}
	if asset.AssetNo != "A-001" {
		t.Errorf("AssetNo = %q, want %q", asset.AssetNo, "A-001")
	}
	if asset.Status != StatusActive {
		t.Errorf("Status = %q, want %q", asset.Status, StatusActive)
	}
	if asset.NetValue != asset.OriginalValue {
		t.Errorf("NetValue should equal OriginalValue on creation, got %d, want %d", asset.NetValue, asset.OriginalValue)
	}
}
```

- [ ] **Step 3: 运行测试验证失败**

Run: `go test ./model/ -v`
Expected: FAIL — 编译错误，`DepreciationMethod`、`AssetStatus`、`FixedAsset` 未定义

- [ ] **Step 4: 编写模型层实现**

```go
// model/asset.go
package model

import (
	"time"

	"crypto/rand"
	"encoding/hex"
)

// DepreciationMethod 折旧方法枚举
type DepreciationMethod string

const (
	DepreciationStraightLine   DepreciationMethod = "straight_line"
	DepreciationDoubleDeclining DepreciationMethod = "double_declining"
)

func (m DepreciationMethod) IsValid() bool {
	switch m {
	case DepreciationStraightLine, DepreciationDoubleDeclining:
		return true
	}
	return false
}

// AssetStatus 资产状态枚举
type AssetStatus string

const (
	StatusActive       AssetStatus = "active"
	StatusDepreciating AssetStatus = "depreciating"
	StatusDisposed     AssetStatus = "disposed"
)

func (s AssetStatus) IsValid() bool {
	switch s {
	case StatusActive, StatusDepreciating, StatusDisposed:
		return true
	}
	return false
}

// FixedAsset 固定资产实体
type FixedAsset struct {
	ID              string             `json:"id"`
	AssetNo         string             `json:"asset_no"`          // 资产编号
	Name            string             `json:"name"`               // 资产名称
	Category        string             `json:"category"`           // 资产类别
	OriginalValue   int64              `json:"original_value"`     // 原值（单位：分）
	SalvageValue    int64              `json:"salvage_value"`      // 残值（单位：分）
	UsefulLifeMonths int                `json:"useful_life_months"` // 使用年限（月）
	AcquisitionDate time.Time          `json:"acquisition_date"`   // 取得日期
	Method          DepreciationMethod `json:"method"`             // 折旧方法
	Status          AssetStatus        `json:"status"`             // 资产状态
	NetValue        int64              `json:"net_value"`          // 净值（单位：分）
	AccumulatedDep  int64              `json:"accumulated_depreciation"` // 累计折旧（单位：分）
	CreatedAt       time.Time          `json:"created_at"`
	UpdatedAt       time.Time          `json:"updated_at"`
}

// NewFixedAsset 创建一个新的固定资产，自动生成 ID 并初始化状态
func NewFixedAsset(
	assetNo string,
	name string,
	category string,
	originalValue int64,
	usefulLifeMonths int,
	acquisitionDate time.Time,
	method DepreciationMethod,
	salvageValue int64,
) *FixedAsset {
	now := time.Now()
	return &FixedAsset{
		ID:               generateID(),
		AssetNo:          assetNo,
		Name:             name,
		Category:         category,
		OriginalValue:    originalValue,
		SalvageValue:     salvageValue,
		UsefulLifeMonths: usefulLifeMonths,
		AcquisitionDate:  acquisitionDate,
		Method:           method,
		Status:           StatusActive,
		NetValue:         originalValue,
		AccumulatedDep:   0,
		CreatedAt:        now,
		UpdatedAt:        now,
	}
}

// generateID 生成一个 16 字符的随机 hex ID
func generateID() string {
	b := make([]byte, 8)
	_, _ = rand.Read(b)
	return hex.EncodeToString(b)
}
```

- [ ] **Step 5: 运行测试验证通过**

Run: `go test ./model/ -v`
Expected: PASS — 所有测试用例通过

- [ ] **Step 6: 提交**

```bash
git add go.mod model/asset.go model/asset_test.go
git commit -m "feat: init go module and define FixedAsset model with enums"
```

---

### Task 2: 仓库层 — 内存持久化实现

**Files:**
- Create: `repository/repository.go`
- Create: `repository/repository_test.go`

**Interfaces:**
- Consumes: `model.FixedAsset`、`model.AssetStatus`（来自 Task 1）
- Produces: `AssetRepository` 接口（`Create`、`GetByID`、`GetByAssetNo`、`Update`、`Delete`、`ListByCategory`、`ListAll`）、`InMemoryAssetRepository` 实现

- [ ] **Step 1: 编写仓库层失败测试**

```go
// repository/repository_test.go
package repository

import (
	"sync"
	"testing"
	"time"

	"fixedasset/model"
)

func newTestAsset(assetNo string) *model.FixedAsset {
	return model.NewFixedAsset(
		assetNo,
		"测试资产",
		"IT设备",
		100000,
		60,
		time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC),
		model.DepreciationStraightLine,
		10000,
	)
}

func TestInMemoryAssetRepository_Create(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-001")

	err := repo.Create(asset)
	if err != nil {
		t.Fatalf("Create() error = %v", err)
	}

	got, err := repo.GetByID(asset.ID)
	if err != nil {
		t.Fatalf("GetByID() error = %v", err)
	}
	if got.AssetNo != asset.AssetNo {
		t.Errorf("AssetNo = %q, want %q", got.AssetNo, asset.AssetNo)
	}
}

func TestInMemoryAssetRepository_Create_DuplicateAssetNo(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-001")

	_ = repo.Create(asset)
	err := repo.Create(newTestAsset("A-001"))
	if err == nil {
		t.Error("Create() with duplicate AssetNo should return error")
	}
}

func TestInMemoryAssetRepository_GetByID_NotFound(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	_, err := repo.GetByID("nonexistent")
	if err == nil {
		t.Error("GetByID() with nonexistent ID should return error")
	}
}

func TestInMemoryAssetRepository_GetByAssetNo(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-002")
	_ = repo.Create(asset)

	got, err := repo.GetByAssetNo("A-002")
	if err != nil {
		t.Fatalf("GetByAssetNo() error = %v", err)
	}
	if got.ID != asset.ID {
		t.Errorf("ID = %q, want %q", got.ID, asset.ID)
	}
}

func TestInMemoryAssetRepository_Update(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-003")
	_ = repo.Create(asset)

	asset.Status = model.StatusDepreciating
	asset.NetValue = 90000
	err := repo.Update(asset)
	if err != nil {
		t.Fatalf("Update() error = %v", err)
	}

	got, _ := repo.GetByID(asset.ID)
	if got.Status != model.StatusDepreciating {
		t.Errorf("Status = %q, want %q", got.Status, model.StatusDepreciating)
	}
	if got.NetValue != 90000 {
		t.Errorf("NetValue = %d, want %d", got.NetValue, 90000)
	}
}

func TestInMemoryAssetRepository_Delete(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	asset := newTestAsset("A-004")
	_ = repo.Create(asset)

	err := repo.Delete(asset.ID)
	if err != nil {
		t.Fatalf("Delete() error = %v", err)
	}

	_, err = repo.GetByID(asset.ID)
	if err == nil {
		t.Error("GetByID() after Delete should return error")
	}
}

func TestInMemoryAssetRepository_ListByCategory(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	_ = repo.Create(newTestAsset("A-005"))
	_ = repo.Create(newTestAsset("A-006"))

	// 修改第二个资产类别
	asset2, _ := repo.GetByAssetNo("A-006")
	asset2.Category = "办公家具"
	_ = repo.Update(asset2)

	got := repo.ListByCategory("IT设备")
	if len(got) != 1 {
		t.Errorf("ListByCategory() len = %d, want 1", len(got))
	}
}

func TestInMemoryAssetRepository_ListAll(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	_ = repo.Create(newTestAsset("A-007"))
	_ = repo.Create(newTestAsset("A-008"))

	got := repo.ListAll()
	if len(got) != 2 {
		t.Errorf("ListAll() len = %d, want 2", len(got))
	}
}

func TestInMemoryAssetRepository_ConcurrentSafe(t *testing.T) {
	repo := NewInMemoryAssetRepository()
	var wg sync.WaitGroup

	for i := 0; i < 100; i++ {
		wg.Add(1)
		go func(idx int) {
			defer wg.Done()
			asset := newTestAsset("A-CONC")
			asset.AssetNo = ""
			_ = repo.Create(asset)
		}(i)
	}
	wg.Wait()

	if len(repo.ListAll()) != 100 {
		t.Errorf("ListAll() len = %d, want 100", len(repo.ListAll()))
	}
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `go test ./repository/ -v`
Expected: FAIL — 编译错误，`AssetRepository`、`InMemoryAssetRepository` 未定义

- [ ] **Step 3: 编写仓库层实现**

```go
// repository/repository.go
package repository

import (
	"errors"
	"sync"

	"fixedasset/model"
)

// ErrAssetNotFound 资产未找到
var ErrAssetNotFound = errors.New("asset not found")

// ErrDuplicateAssetNo 资产编号重复
var ErrDuplicateAssetNo = errors.New("duplicate asset number")

// AssetRepository 资产仓库接口
type AssetRepository interface {
	Create(asset *model.FixedAsset) error
	GetByID(id string) (*model.FixedAsset, error)
	GetByAssetNo(assetNo string) (*model.FixedAsset, error)
	Update(asset *model.FixedAsset) error
	Delete(id string) error
	ListByCategory(category string) []*model.FixedAsset
	ListAll() []*model.FixedAsset
}

// InMemoryAssetRepository 内存实现的资产仓库
type InMemoryAssetRepository struct {
	mu       sync.RWMutex
	byID     map[string]*model.FixedAsset
	byAssetNo map[string]*model.FixedAsset
}

// NewInMemoryAssetRepository 创建内存仓库实例
func NewInMemoryAssetRepository() *InMemoryAssetRepository {
	return &InMemoryAssetRepository{
		byID:      make(map[string]*model.FixedAsset),
		byAssetNo: make(map[string]*model.FixedAsset),
	}
}

func (r *InMemoryAssetRepository) Create(asset *model.FixedAsset) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if asset.AssetNo != "" {
		if _, exists := r.byAssetNo[asset.AssetNo]; exists {
			return ErrDuplicateAssetNo
		}
	}

	stored := *asset
	r.byID[asset.ID] = &stored
	if asset.AssetNo != "" {
		r.byAssetNo[asset.AssetNo] = &stored
	}
	return nil
}

func (r *InMemoryAssetRepository) GetByID(id string) (*model.FixedAsset, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	asset, exists := r.byID[id]
	if !exists {
		return nil, ErrAssetNotFound
	}
	copy := *asset
	return &copy, nil
}

func (r *InMemoryAssetRepository) GetByAssetNo(assetNo string) (*model.FixedAsset, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	asset, exists := r.byAssetNo[assetNo]
	if !exists {
		return nil, ErrAssetNotFound
	}
	copy := *asset
	return &copy, nil
}

func (r *InMemoryAssetRepository) Update(asset *model.FixedAsset) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.byID[asset.ID]
	if !exists {
		return ErrAssetNotFound
	}

	// 如果 AssetNo 变更，更新索引
	if existing.AssetNo != asset.AssetNo {
		if existing.AssetNo != "" {
			delete(r.byAssetNo, existing.AssetNo)
		}
		if asset.AssetNo != "" {
			if _, dup := r.byAssetNo[asset.AssetNo]; dup {
				return ErrDuplicateAssetNo
			}
			r.byAssetNo[asset.AssetNo] = asset
		}
	}

	stored := *asset
	stored.UpdatedAt = asset.UpdatedAt
	r.byID[asset.ID] = &stored
	if asset.AssetNo != "" {
		r.byAssetNo[asset.AssetNo] = &stored
	}
	return nil
}

func (r *InMemoryAssetRepository) Delete(id string) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	asset, exists := r.byID[id]
	if !exists {
		return ErrAssetNotFound
	}

	delete(r.byID, id)
	if asset.AssetNo != "" {
		delete(r.byAssetNo, asset.AssetNo)
	}
	return nil
}

func (r *InMemoryAssetRepository) ListByCategory(category string) []*model.FixedAsset {
	r.mu.RLock()
	defer r.mu.RUnlock()

	var result []*model.FixedAsset
	for _, asset := range r.byID {
		if asset.Category == category {
			copy := *asset
			result = append(result, &copy)
		}
	}
	return result
}

func (r *InMemoryAssetRepository) ListAll() []*model.FixedAsset {
	r.mu.RLock()
	defer r.mu.RUnlock()

	result := make([]*model.FixedAsset, 0, len(r.byID))
	for _, asset := range r.byID {
		copy := *asset
		result = append(result, &copy)
	}
	return result
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `go test ./repository/ -v`
Expected: PASS — 所有测试用例通过，包括并发安全测试

- [ ] **Step 5: 提交**

```bash
git add repository/repository.go repository/repository_test.go
git commit -m "feat: implement in-memory asset repository with CRUD and concurrency safety"
```

---

### Task 3: 折旧计算引擎

**Files:**
- Create: `service/depreciation.go`
- Create: `service/depreciation_test.go`

**Interfaces:**
- Consumes: `model.FixedAsset`、`model.DepreciationMethod`（来自 Task 1）
- Produces: `DepreciationCalculator` 接口、`calculateStraightLine()` 函数、`calculateDoubleDeclining()` 函数、`CalculateDepreciation()` 入口函数

- [ ] **Step 1: 编写折旧计算失败测试**

```go
// service/depreciation_test.go
package service

import (
	"testing"
	"time"

	"fixedasset/model"
)

func newDepreciationTestAsset(method model.DepreciationMethod) *model.FixedAsset {
	return &model.FixedAsset{
		ID:               "test-1",
		AssetNo:          "A-001",
		Name:             "测试设备",
		Category:         "IT设备",
		OriginalValue:    600000, // 6000.00 元
		SalvageValue:     60000,  // 600.00 元（残值率 10%）
		UsefulLifeMonths: 60,     // 5 年
		AcquisitionDate:  time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC),
		Method:           method,
		Status:           model.StatusActive,
		NetValue:         600000,
		AccumulatedDep:   0,
	}
}

func TestCalculateDepreciation_StraightLine(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationStraightLine)

	// 直线法：月折旧 = (原值 - 残值) / 使用月数 = (600000 - 60000) / 60 = 9000
	tests := []struct {
		name       string
		monthsElapsed int
		wantMonthly int64
		wantTotal   int64
		wantNet     int64
	}{
		{"1 month", 1, 9000, 9000, 591000},
		{"12 months", 12, 9000, 108000, 492000},
		{"60 months (full)", 60, 9000, 540000, 60000},
		{"61 months (capped)", 61, 9000, 540000, 60000},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			asset.AccumulatedDep = 0
			asset.NetValue = asset.OriginalValue
			monthly, total := CalculateDepreciation(asset, tt.monthsElapsed)
			if monthly != tt.wantMonthly {
				t.Errorf("monthly depreciation = %d, want %d", monthly, tt.wantMonthly)
			}
			if total != tt.wantTotal {
				t.Errorf("total depreciation = %d, want %d", total, tt.wantTotal)
			}
		})
	}
}

func TestCalculateDepreciation_DoubleDeclining(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationDoubleDeclining)

	// 双倍余额递减法：月折旧率 = 2 / 60 = 0.0333...
	// 第1月：600000 * (2/60) = 20000
	tests := []struct {
		name         string
		monthsElapsed int
		wantMonthly   int64
	}{
		{"month 1", 1, 20000},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			asset.AccumulatedDep = 0
			asset.NetValue = asset.OriginalValue
			monthly, _ := CalculateDepreciation(asset, tt.monthsElapsed)
			if monthly != tt.wantMonthly {
				t.Errorf("monthly depreciation = %d, want %d", monthly, tt.wantMonthly)
			}
		})
	}
}

func TestCalculateDepreciation_DoubleDeclining_NetNotBelowSalvage(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationDoubleDeclining)

	// 折旧 60 个月后净值不应低于残值
	asset.AccumulatedDep = 0
	asset.NetValue = asset.OriginalValue
	_, total := CalculateDepreciation(asset, 60)

	netValue := asset.OriginalValue - total
	if netValue < asset.SalvageValue {
		t.Errorf("net value %d should not be below salvage value %d", netValue, asset.SalvageValue)
	}
}

func TestCalculateDepreciation_ZeroUsefulLife(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationStraightLine)
	asset.UsefulLifeMonths = 0

	monthly, total := CalculateDepreciation(asset, 12)
	if monthly != 0 {
		t.Errorf("monthly depreciation with zero useful life = %d, want 0", monthly)
	}
	if total != 0 {
		t.Errorf("total depreciation with zero useful life = %d, want 0", total)
	}
}

func TestCalculateDepreciation_InvalidMethod(t *testing.T) {
	asset := newDepreciationTestAsset(model.DepreciationMethod("invalid"))
	monthly, total := CalculateDepreciation(asset, 12)
	if monthly != 0 || total != 0 {
		t.Error("invalid method should return zero depreciation")
	}
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `go test ./service/ -v`
Expected: FAIL — 编译错误，`CalculateDepreciation` 未定义

- [ ] **Step 3: 编写折旧计算实现**

```go
// service/depreciation.go
package service

import (
	"fixedasset/model"
)

// CalculateDepreciation 根据折旧方法和已使用月数计算月折旧额和累计折旧额
// 返回值: (月折旧额, 累计折旧额)，单位均为分
func CalculateDepreciation(asset *model.FixedAsset, monthsElapsed int) (monthlyDep int64, totalDep int64) {
	if asset.UsefulLifeMonths <= 0 || monthsElapsed <= 0 {
		return 0, 0
	}

	switch asset.Method {
	case model.DepreciationStraightLine:
		return calculateStraightLine(asset, monthsElapsed)
	case model.DepreciationDoubleDeclining:
		return calculateDoubleDeclining(asset, monthsElapsed)
	default:
		return 0, 0
	}
}

// calculateStraightLine 直线法折旧
// 月折旧额 = (原值 - 残值) / 使用月数
// 累计折旧不超过 (原值 - 残值)
func calculateStraightLine(asset *model.FixedAsset, monthsElapsed int) (monthlyDep int64, totalDep int64) {
	depreciableBase := asset.OriginalValue - asset.SalvageValue
	if depreciableBase <= 0 {
		return 0, 0
	}

	monthlyDep = depreciableBase / int64(asset.UsefulLifeMonths)

	effectiveMonths := monthsElapsed
	if effectiveMonths > asset.UsefulLifeMonths {
		effectiveMonths = asset.UsefulLifeMonths
	}

	totalDep = monthlyDep * int64(effectiveMonths)
	if totalDep > depreciableBase {
		totalDep = depreciableBase
	}

	return monthlyDep, totalDep
}

// calculateDoubleDeclining 双倍余额递减法
// 月折旧率 = 2 / 使用月数
// 每月折旧 = 当前净值 * 月折旧率
// 净值不低于残值
func calculateDoubleDeclining(asset *model.FixedAsset, monthsElapsed int) (monthlyDep int64, totalDep int64) {
	depreciableBase := asset.OriginalValue - asset.SalvageValue
	if depreciableBase <= 0 {
		return 0, 0
	}

	monthlyRate := float64(2) / float64(asset.UsefulLifeMonths)
	netValue := float64(asset.OriginalValue)

	effectiveMonths := monthsElapsed
	if effectiveMonths > asset.UsefulLifeMonths {
		effectiveMonths = asset.UsefulLifeMonths
	}

	// 第一个月的月折旧额（用于返回值）
	firstMonthly := int64(netValue * monthlyRate)

	var accumulated int64
	for i := 0; i < effectiveMonths; i++ {
		dep := int64(netValue * monthlyRate)
		netValue -= float64(dep)

		// 确保净值不低于残值
		if int64(netValue) < asset.SalvageValue {
			dep += int64(netValue) - asset.SalvageValue
			netValue = float64(asset.SalvageValue)
			accumulated += dep
			break
		}
		accumulated += dep
	}

	if accumulated > depreciableBase {
		accumulated = depreciableBase
	}

	return firstMonthly, accumulated
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `go test ./service/ -v -run TestCalculateDepreciation`
Expected: PASS — 所有折旧计算测试通过

- [ ] **Step 5: 提交**

```bash
git add service/depreciation.go service/depreciation_test.go
git commit -m "feat: implement depreciation engine with straight-line and double-declining methods"
```

---

### Task 4: 服务层 — 业务逻辑编排

**Files:**
- Create: `service/asset_service.go`
- Create: `service/asset_service_test.go`

**Interfaces:**
- Consumes: `model.FixedAsset`（Task 1）、`AssetRepository` 接口（Task 2）、`CalculateDepreciation` 函数（Task 3）
- Produces: `AssetService` struct、方法 `CreateAsset`、`GetAsset`、`GetByAssetNo`、`UpdateAsset`、`DeleteAsset`、`ListByCategory`、`ListAll`、`ApplyDepreciation`

- [ ] **Step 1: 编写服务层失败测试**

```go
// service/asset_service_test.go
package service

import (
	"testing"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
)

func newTestService() *AssetService {
	repo := repository.NewInMemoryAssetRepository()
	return NewAssetService(repo)
}

func newCreateInput() *CreateAssetInput {
	return &CreateAssetInput{
		AssetNo:          "A-001",
		Name:             "办公电脑",
		Category:         "IT设备",
		OriginalValue:    500000,
		SalvageValue:     50000,
		UsefulLifeMonths: 60,
		AcquisitionDate:  time.Date(2026, 1, 1, 0, 0, 0, 0, time.UTC),
		Method:           model.DepreciationStraightLine,
	}
}

func TestAssetService_CreateAsset(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()

	asset, err := svc.CreateAsset(input)
	if err != nil {
		t.Fatalf("CreateAsset() error = %v", err)
	}
	if asset.ID == "" {
		t.Error("CreateAsset() should return asset with non-empty ID")
	}
	if asset.Status != model.StatusActive {
		t.Errorf("Status = %q, want %q", asset.Status, model.StatusActive)
	}
}

func TestAssetService_CreateAsset_InvalidMethod(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()
	input.Method = model.DepreciationMethod("invalid")

	_, err := svc.CreateAsset(input)
	if err == nil {
		t.Error("CreateAsset() with invalid method should return error")
	}
}

func TestAssetService_CreateAsset_NegativeValue(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()
	input.OriginalValue = -100

	_, err := svc.CreateAsset(input)
	if err == nil {
		t.Error("CreateAsset() with negative original value should return error")
	}
}

func TestAssetService_CreateAsset_DuplicateAssetNo(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()

	_, _ = svc.CreateAsset(input)
	_, err := svc.CreateAsset(input)
	if err == nil {
		t.Error("CreateAsset() with duplicate AssetNo should return error")
	}
}

func TestAssetService_GetAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	got, err := svc.GetAsset(created.ID)
	if err != nil {
		t.Fatalf("GetAsset() error = %v", err)
	}
	if got.AssetNo != created.AssetNo {
		t.Errorf("AssetNo = %q, want %q", got.AssetNo, created.AssetNo)
	}
}

func TestAssetService_UpdateAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	created.Name = "更新后的电脑"
	err := svc.UpdateAsset(created)
	if err != nil {
		t.Fatalf("UpdateAsset() error = %v", err)
	}

	got, _ := svc.GetAsset(created.ID)
	if got.Name != "更新后的电脑" {
		t.Errorf("Name = %q, want %q", got.Name, "更新后的电脑")
	}
}

func TestAssetService_DeleteAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	err := svc.DeleteAsset(created.ID)
	if err != nil {
		t.Fatalf("DeleteAsset() error = %v", err)
	}

	_, err = svc.GetAsset(created.ID)
	if err == nil {
		t.Error("GetAsset() after delete should return error")
	}
}

func TestAssetService_ListAll(t *testing.T) {
	svc := newTestService()

	input1 := newCreateInput()
	input2 := newCreateInput()
	input2.AssetNo = "A-002"
	_, _ = svc.CreateAsset(input1)
	_, _ = svc.CreateAsset(input2)

	list := svc.ListAll()
	if len(list) != 2 {
		t.Errorf("ListAll() len = %d, want 2", len(list))
	}
}

func TestAssetService_ApplyDepreciation(t *testing.T) {
	svc := newTestService()
	input := newCreateInput()
	input.OriginalValue = 600000
	input.SalvageValue = 60000
	input.UsefulLifeMonths = 60

	created, _ := svc.CreateAsset(input)

	err := svc.ApplyDepreciation(created.ID, 12)
	if err != nil {
		t.Fatalf("ApplyDepreciation() error = %v", err)
	}

	got, _ := svc.GetAsset(created.ID)
	// 直线法：(600000 - 60000) / 60 * 12 = 108000
	if got.AccumulatedDep != 108000 {
		t.Errorf("AccumulatedDep = %d, want 108000", got.AccumulatedDep)
	}
	if got.NetValue != 492000 {
		t.Errorf("NetValue = %d, want 492000", got.NetValue)
	}
	if got.Status != model.StatusDepreciating {
		t.Errorf("Status = %q, want %q", got.Status, model.StatusDepreciating)
	}
}

func TestAssetService_ApplyDepreciation_DisposedAsset(t *testing.T) {
	svc := newTestService()
	created, _ := svc.CreateAsset(newCreateInput())

	created.Status = model.StatusDisposed
	_ = svc.UpdateAsset(created)

	err := svc.ApplyDepreciation(created.ID, 12)
	if err == nil {
		t.Error("ApplyDepreciation() on disposed asset should return error")
	}
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `go test ./service/ -v -run TestAssetService`
Expected: FAIL — 编译错误，`AssetService`、`CreateAssetInput` 未定义

- [ ] **Step 3: 编写服务层实现**

```go
// service/asset_service.go
package service

import (
	"errors"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
)

// ErrValidation 校验错误
var ErrValidation = errors.New("validation error")

// ErrAssetDisposed 资产已处置
var ErrAssetDisposed = errors.New("asset has been disposed")

// CreateAssetInput 创建资产的输入参数
type CreateAssetInput struct {
	AssetNo          string
	Name             string
	Category         string
	OriginalValue    int64
	SalvageValue     int64
	UsefulLifeMonths int
	AcquisitionDate  time.Time
	Method           model.DepreciationMethod
}

// AssetService 资产业务服务
type AssetService struct {
	repo repository.AssetRepository
}

// NewAssetService 创建资产服务实例
func NewAssetService(repo repository.AssetRepository) *AssetService {
	return &AssetService{repo: repo}
}

// CreateAsset 创建固定资产
func (s *AssetService) CreateAsset(input *CreateAssetInput) (*model.FixedAsset, error) {
	if err := validateCreateInput(input); err != nil {
		return nil, err
	}

	asset := model.NewFixedAsset(
		input.AssetNo,
		input.Name,
		input.Category,
		input.OriginalValue,
		input.UsefulLifeMonths,
		input.AcquisitionDate,
		input.Method,
		input.SalvageValue,
	)

	if err := s.repo.Create(asset); err != nil {
		return nil, err
	}

	return asset, nil
}

// GetAsset 根据 ID 获取资产
func (s *AssetService) GetAsset(id string) (*model.FixedAsset, error) {
	return s.repo.GetByID(id)
}

// GetByAssetNo 根据资产编号获取资产
func (s *AssetService) GetByAssetNo(assetNo string) (*model.FixedAsset, error) {
	return s.repo.GetByAssetNo(assetNo)
}

// UpdateAsset 更新资产
func (s *AssetService) UpdateAsset(asset *model.FixedAsset) error {
	asset.UpdatedAt = time.Now()
	return s.repo.Update(asset)
}

// DeleteAsset 删除资产
func (s *AssetService) DeleteAsset(id string) error {
	return s.repo.Delete(id)
}

// ListByCategory 按类别列出资产
func (s *AssetService) ListByCategory(category string) []*model.FixedAsset {
	return s.repo.ListByCategory(category)
}

// ListAll 列出所有资产
func (s *AssetService) ListAll() []*model.FixedAsset {
	return s.repo.ListAll()
}

// ApplyDepreciation 对指定资产应用折旧计算
func (s *AssetService) ApplyDepreciation(id string, monthsElapsed int) error {
	asset, err := s.repo.GetByID(id)
	if err != nil {
		return err
	}

	if asset.Status == model.StatusDisposed {
		return ErrAssetDisposed
	}

	_, totalDep := CalculateDepreciation(asset, monthsElapsed)

	asset.AccumulatedDep = totalDep
	asset.NetValue = asset.OriginalValue - totalDep
	asset.Status = model.StatusDepreciating
	asset.UpdatedAt = time.Now()

	return s.repo.Update(asset)
}

func validateCreateInput(input *CreateAssetInput) error {
	if input.AssetNo == "" {
		return ErrValidation
	}
	if input.Name == "" {
		return ErrValidation
	}
	if input.OriginalValue <= 0 {
		return ErrValidation
	}
	if input.SalvageValue < 0 {
		return ErrValidation
	}
	if input.SalvageValue >= input.OriginalValue {
		return ErrValidation
	}
	if input.UsefulLifeMonths <= 0 {
		return ErrValidation
	}
	if !input.Method.IsValid() {
		return ErrValidation
	}
	return nil
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `go test ./service/ -v`
Expected: PASS — 所有服务层测试通过（包括折旧应用测试）

- [ ] **Step 5: 提交**

```bash
git add service/asset_service.go service/asset_service_test.go
git commit -m "feat: implement asset service with CRUD, validation, and depreciation application"
```

---

### Task 5: HTTP Handler 层 — RESTful API

**Files:**
- Create: `handler/asset_handler.go`
- Create: `handler/asset_handler_test.go`

**Interfaces:**
- Consumes: `AssetService`（Task 4）及其所有公开方法
- Produces: `AssetHandler` struct、`RegisterRoutes(mux *http.ServeMux)` 方法、以下 API endpoints：
  - `POST /api/assets` — 创建资产
  - `GET /api/assets` — 列出所有资产
  - `GET /api/assets/{id}` — 获取单个资产
  - `PUT /api/assets/{id}` — 更新资产
  - `DELETE /api/assets/{id}` — 删除资产
  - `POST /api/assets/{id}/depreciate` — 应用折旧
  - `GET /api/assets?category={category}` — 按类别筛选

- [ ] **Step 1: 编写 HTTP handler 失败测试**

```go
// handler/asset_handler_test.go
package handler

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
	"fixedasset/service"
)

func setupHandler() (http.Handler, *service.AssetService) {
	repo := repository.NewInMemoryAssetRepository()
	svc := service.NewAssetService(repo)
	mux := http.NewServeMux()
	handler := NewAssetHandler(svc)
	handler.RegisterRoutes(mux)
	return mux, svc
}

func TestCreateAsset_API(t *testing.T) {
	mux, _ := setupHandler()

	body := map[string]interface{}{
		"asset_no":           "A-001",
		"name":               "办公电脑",
		"category":           "IT设备",
		"original_value":     500000,
		"salvage_value":      50000,
		"useful_life_months": 60,
		"acquisition_date":   "2026-01-01T00:00:00Z",
		"method":             "straight_line",
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("POST", "/api/assets", bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusCreated {
		t.Errorf("status = %d, want %d", w.Code, http.StatusCreated)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.AssetNo != "A-001" {
		t.Errorf("AssetNo = %q, want %q", resp.AssetNo, "A-001")
	}
	if resp.ID == "" {
		t.Error("ID should not be empty")
	}
}

func TestCreateAsset_API_ValidationError(t *testing.T) {
	mux, _ := setupHandler()

	body := map[string]interface{}{
		"asset_no":       "",
		"name":           "测试",
		"original_value": -100,
		"method":         "straight_line",
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("POST", "/api/assets", bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusBadRequest {
		t.Errorf("status = %d, want %d", w.Code, http.StatusBadRequest)
	}
}

func TestGetAsset_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo:          "A-002",
		Name:             "打印机",
		Category:         "IT设备",
		OriginalValue:    300000,
		SalvageValue:     30000,
		UsefulLifeMonths: 36,
		AcquisitionDate:  time.Now(),
		Method:           model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("GET", "/api/assets/"+asset.ID, nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.AssetNo != "A-002" {
		t.Errorf("AssetNo = %q, want %q", resp.AssetNo, "A-002")
	}
}

func TestGetAsset_API_NotFound(t *testing.T) {
	mux, _ := setupHandler()

	req := httptest.NewRequest("GET", "/api/assets/nonexistent", nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusNotFound {
		t.Errorf("status = %d, want %d", w.Code, http.StatusNotFound)
	}
}

func TestListAssets_API(t *testing.T) {
	mux, svc := setupHandler()

	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-003", Name: "设备1", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})
	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-004", Name: "设备2", Category: "办公家具",
		OriginalValue: 200000, SalvageValue: 20000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("GET", "/api/assets", nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp []*model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if len(resp) != 2 {
		t.Errorf("list length = %d, want 2", len(resp))
	}
}

func TestListAssetsByCategory_API(t *testing.T) {
	mux, svc := setupHandler()

	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-005", Name: "设备1", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})
	_, _ = svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-006", Name: "设备2", Category: "办公家具",
		OriginalValue: 200000, SalvageValue: 20000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("GET", "/api/assets?category=IT设备", nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp []*model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if len(resp) != 1 {
		t.Errorf("list length = %d, want 1", len(resp))
	}
}

func TestUpdateAsset_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-007", Name: "原始名称", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	body := model.FixedAsset{
		ID:               asset.ID,
		AssetNo:          asset.AssetNo,
		Name:             "更新名称",
		Category:         asset.Category,
		OriginalValue:    asset.OriginalValue,
		SalvageValue:     asset.SalvageValue,
		UsefulLifeMonths: asset.UsefulLifeMonths,
		AcquisitionDate:  asset.AcquisitionDate,
		Method:           asset.Method,
		Status:           asset.Status,
		NetValue:         asset.NetValue,
		AccumulatedDep:   asset.AccumulatedDep,
		CreatedAt:        asset.CreatedAt,
		UpdatedAt:        asset.UpdatedAt,
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("PUT", "/api/assets/"+asset.ID, bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.Name != "更新名称" {
		t.Errorf("Name = %q, want %q", resp.Name, "更新名称")
	}
}

func TestDeleteAsset_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-008", Name: "测试", Category: "IT设备",
		OriginalValue: 100000, SalvageValue: 10000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	req := httptest.NewRequest("DELETE", "/api/assets/"+asset.ID, nil)
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusNoContent {
		t.Errorf("status = %d, want %d", w.Code, http.StatusNoContent)
	}
}

func TestApplyDepreciation_API(t *testing.T) {
	mux, svc := setupHandler()

	asset, _ := svc.CreateAsset(&service.CreateAssetInput{
		AssetNo: "A-009", Name: "测试", Category: "IT设备",
		OriginalValue: 600000, SalvageValue: 60000, UsefulLifeMonths: 60,
		AcquisitionDate: time.Now(), Method: model.DepreciationStraightLine,
	})

	body := map[string]interface{}{
		"months_elapsed": 12,
	}
	jsonBody, _ := json.Marshal(body)

	req := httptest.NewRequest("POST", "/api/assets/"+asset.ID+"/depreciate", bytes.NewReader(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()

	mux.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("status = %d, want %d", w.Code, http.StatusOK)
	}

	var resp model.FixedAsset
	_ = json.NewDecoder(w.Body).Decode(&resp)
	if resp.AccumulatedDep != 108000 {
		t.Errorf("AccumulatedDep = %d, want 108000", resp.AccumulatedDep)
	}
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `go test ./handler/ -v`
Expected: FAIL — 编译错误，`AssetHandler`、`NewAssetHandler` 未定义

- [ ] **Step 3: 编写 HTTP handler 实现**

```go
// handler/asset_handler.go
package handler

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"
	"time"

	"fixedasset/model"
	"fixedasset/repository"
	"fixedasset/service"
)

// AssetHandler HTTP 请求处理器
type AssetHandler struct {
	svc *service.AssetService
}

// NewAssetHandler 创建 handler 实例
func NewAssetHandler(svc *service.AssetService) *AssetHandler {
	return &AssetHandler{svc: svc}
}

// RegisterRoutes 在 mux 上注册资产相关路由
func (h *AssetHandler) RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/api/assets", h.handleAssetsCollection)
	mux.HandleFunc("/api/assets/", h.handleAssetItem)
}

func (h *AssetHandler) handleAssetsCollection(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case http.MethodPost:
		h.createAsset(w, r)
	case http.MethodGet:
		h.listAssets(w, r)
	default:
		http.Error(w, `{"error":"method not allowed"}`, http.StatusMethodNotAllowed)
	}
}

func (h *AssetHandler) handleAssetItem(w http.ResponseWriter, r *http.Request) {
	path := strings.TrimPrefix(r.URL.Path, "/api/assets/")
	parts := strings.Split(path, "/")

	if len(parts) == 0 || parts[0] == "" {
		http.Error(w, `{"error":"asset id required"}`, http.StatusBadRequest)
		return
	}

	id := parts[0]

	// /api/assets/{id}/depreciate
	if len(parts) == 2 && parts[1] == "depreciate" {
		if r.Method != http.MethodPost {
			http.Error(w, `{"error":"method not allowed"}`, http.StatusMethodNotAllowed)
			return
		}
		h.applyDepreciation(w, r, id)
		return
	}

	// /api/assets/{id}
	switch r.Method {
	case http.MethodGet:
		h.getAsset(w, r, id)
	case http.MethodPut:
		h.updateAsset(w, r, id)
	case http.MethodDelete:
		h.deleteAsset(w, r, id)
	default:
		http.Error(w, `{"error":"method not allowed"}`, http.StatusMethodNotAllowed)
	}
}

func (h *AssetHandler) createAsset(w http.ResponseWriter, r *http.Request) {
	var input service.CreateAssetInput
	if err := json.NewDecoder(r.Body).Decode(&input); err != nil {
		writeJSONError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	if input.AcquisitionDate.IsZero() {
		input.AcquisitionDate = time.Now()
	}

	asset, err := h.svc.CreateAsset(&input)
	if err != nil {
		if errors.Is(err, service.ErrValidation) {
			writeJSONError(w, http.StatusBadRequest, err.Error())
			return
		}
		if errors.Is(err, repository.ErrDuplicateAssetNo) {
			writeJSONError(w, http.StatusConflict, err.Error())
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}

	writeJSON(w, http.StatusCreated, asset)
}

func (h *AssetHandler) getAsset(w http.ResponseWriter, r *http.Request, id string) {
	asset, err := h.svc.GetAsset(id)
	if err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}
	writeJSON(w, http.StatusOK, asset)
}

func (h *AssetHandler) listAssets(w http.ResponseWriter, r *http.Request) {
	category := r.URL.Query().Get("category")
	if category != "" {
		assets := h.svc.ListByCategory(category)
		writeJSON(w, http.StatusOK, assets)
		return
	}
	assets := h.svc.ListAll()
	writeJSON(w, http.StatusOK, assets)
}

func (h *AssetHandler) updateAsset(w http.ResponseWriter, r *http.Request, id string) {
	var asset model.FixedAsset
	if err := json.NewDecoder(r.Body).Decode(&asset); err != nil {
		writeJSONError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	asset.ID = id
	if err := h.svc.UpdateAsset(&asset); err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		if errors.Is(err, repository.ErrDuplicateAssetNo) {
			writeJSONError(w, http.StatusConflict, err.Error())
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}

	updated, _ := h.svc.GetAsset(id)
	writeJSON(w, http.StatusOK, updated)
}

func (h *AssetHandler) deleteAsset(w http.ResponseWriter, r *http.Request, id string) {
	if err := h.svc.DeleteAsset(id); err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

func (h *AssetHandler) applyDepreciation(w http.ResponseWriter, r *http.Request, id string) {
	var body struct {
		MonthsElapsed int `json:"months_elapsed"`
	}
	if err := json.NewDecoder(r.Body).Decode(&body); err != nil {
		writeJSONError(w, http.StatusBadRequest, "invalid JSON body")
		return
	}

	if err := h.svc.ApplyDepreciation(id, body.MonthsElapsed); err != nil {
		if errors.Is(err, repository.ErrAssetNotFound) {
			writeJSONError(w, http.StatusNotFound, "asset not found")
			return
		}
		if errors.Is(err, service.ErrAssetDisposed) {
			writeJSONError(w, http.StatusConflict, "asset has been disposed")
			return
		}
		writeJSONError(w, http.StatusInternalServerError, "internal error")
		return
	}

	asset, _ := h.svc.GetAsset(id)
	writeJSON(w, http.StatusOK, asset)
}

func writeJSON(w http.ResponseWriter, status int, data interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(data)
}

func writeJSONError(w http.ResponseWriter, status int, message string) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(map[string]string{"error": message})
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `go test ./handler/ -v`
Expected: PASS — 所有 HTTP endpoint 测试通过

- [ ] **Step 5: 提交**

```bash
git add handler/asset_handler.go handler/asset_handler_test.go
git commit -m "feat: implement RESTful HTTP handler with all asset CRUD and depreciation endpoints"
```

---

### Task 6: 主程序入口与集成测试

**Files:**
- Modify: `main.go`（注意：当前 `main.go` 不存在，根目录已有 `sort.go` 和 `review_test.go` 使用 `package main`，需将新的 main 逻辑放入独立文件）
- Create: `main_fixedasset.go`（同属 `package main`，使用 build tag 或函数名避免与 `review_test.go` 的 `main()` 冲突）
- Create: `integration_test.go`（`package main` 级别集成测试）

**Interfaces:**
- Consumes: `AssetHandler`（Task 5）、`AssetService`（Task 4）、`InMemoryAssetRepository`（Task 2）
- Produces: 可运行的 HTTP 服务入口（`runFixedAssetServer()` 函数），集成测试验证完整请求链路

> **注意：** 现有 `review_test.go` 的 `func main()` 会与新入口冲突。方案：新建 `app.go` 提供 `runFixedAssetServer(addr string)` 函数，在 `main.go` 中调用。但 `sort.go` 也在 `package main` 中且无 `main()` 函数，所以只需新建一个含 `func main()` 的文件。由于 `review_test.go` 是 `_test.go` 文件，其 `main()` 只在测试编译时生效，不影响正常构建。因此新建 `main.go` 是安全的。

- [ ] **Step 1: 编写集成测试失败测试**

```go
// integration_test.go
package main

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestIntegration_CreateAndGetAsset(t *testing.T) {
	server := httptest.NewServer(setupRouter())
	defer server.Close()

	// 创建资产
	createBody := map[string]interface{}{
		"asset_no":           "INT-001",
		"name":               "集成测试设备",
		"category":           "IT设备",
		"original_value":     600000,
		"salvage_value":      60000,
		"useful_life_months": 60,
		"acquisition_date":   "2026-01-01T00:00:00Z",
		"method":             "straight_line",
	}
	jsonBody, _ := json.Marshal(createBody)

	resp, err := http.Post(server.URL+"/api/assets", "application/json", bytes.NewReader(jsonBody))
	if err != nil {
		t.Fatalf("Create request failed: %v", err)
	}
	if resp.StatusCode != http.StatusCreated {
		t.Errorf("Create status = %d, want %d", resp.StatusCode, http.StatusCreated)
	}

	var created map[string]interface{}
	_ = json.NewDecoder(resp.Body).Decode(&created)
	resp.Body.Close()

	assetID := created["id"].(string)

	// 获取资产
	getResp, err := http.Get(server.URL + "/api/assets/" + assetID)
	if err != nil {
		t.Fatalf("Get request failed: %v", err)
	}
	if getResp.StatusCode != http.StatusOK {
		t.Errorf("Get status = %d, want %d", getResp.StatusCode, http.StatusOK)
	}

	var fetched map[string]interface{}
	_ = json.NewDecoder(getResp.Body).Decode(&fetched)
	getResp.Body.Close()

	if fetched["asset_no"] != "INT-001" {
		t.Errorf("asset_no = %v, want INT-001", fetched["asset_no"])
	}
}

func TestIntegration_CreateAndDepreciate(t *testing.T) {
	server := httptest.NewServer(setupRouter())
	defer server.Close()

	// 创建资产
	createBody := map[string]interface{}{
		"asset_no":           "INT-002",
		"name":               "折旧测试设备",
		"category":           "IT设备",
		"original_value":     600000,
		"salvage_value":      60000,
		"useful_life_months": 60,
		"acquisition_date":   "2026-01-01T00:00:00Z",
		"method":             "straight_line",
	}
	jsonBody, _ := json.Marshal(createBody)

	resp, _ := http.Post(server.URL+"/api/assets", "application/json", bytes.NewReader(jsonBody))
	var created map[string]interface{}
	_ = json.NewDecoder(resp.Body).Decode(&created)
	resp.Body.Close()

	assetID := created["id"].(string)

	// 应用折旧
	depBody := map[string]interface{}{
		"months_elapsed": 12,
	}
	depJSON, _ := json.Marshal(depBody)

	depResp, err := http.Post(server.URL+"/api/assets/"+assetID+"/depreciate", "application/json", bytes.NewReader(depJSON))
	if err != nil {
		t.Fatalf("Depreciate request failed: %v", err)
	}
	if depResp.StatusCode != http.StatusOK {
		t.Errorf("Depreciate status = %d, want %d", depResp.StatusCode, http.StatusOK)
	}

	var depreciated map[string]interface{}
	_ = json.NewDecoder(depResp.Body).Decode(&depreciated)
	depResp.Body.Close()

	accumulatedDep := int64(depreciated["accumulated_depreciation"].(float64))
	if accumulatedDep != 108000 {
		t.Errorf("accumulated_depreciation = %d, want 108000", accumulatedDep)
	}
}

func TestIntegration_ListAndDelete(t *testing.T) {
	server := httptest.NewServer(setupRouter())
	defer server.Close()

	// 创建两个资产
	for _, assetNo := range []string{"INT-003", "INT-004"} {
		body := map[string]interface{}{
			"asset_no": assetNo, "name": "设备", "category": "IT设备",
			"original_value": 100000, "salvage_value": 10000, "useful_life_months": 60,
			"acquisition_date": "2026-01-01T00:00:00Z", "method": "straight_line",
		}
		jsonBody, _ := json.Marshal(body)
		resp, _ := http.Post(server.URL+"/api/assets", "application/json", bytes.NewReader(jsonBody))
		resp.Body.Close()
	}

	// 列出
	listResp, _ := http.Get(server.URL + "/api/assets")
	var list []map[string]interface{}
	_ = json.NewDecoder(listResp.Body).Decode(&list)
	listResp.Body.Close()

	if len(list) != 2 {
		t.Errorf("list length = %d, want 2", len(list))
	}

	// 删除第一个
	firstID := list[0]["id"].(string)
	delReq, _ := http.NewRequest("DELETE", server.URL+"/api/assets/"+firstID, nil)
	delResp, err := http.DefaultClient.Do(delReq)
	if err != nil {
		t.Fatalf("Delete request failed: %v", err)
	}
	if delResp.StatusCode != http.StatusNoContent {
		t.Errorf("Delete status = %d, want %d", delResp.StatusCode, http.StatusNoContent)
	}
	delResp.Body.Close()

	// 验证删除后列表长度
	listResp2, _ := http.Get(server.URL + "/api/assets")
	var list2 []map[string]interface{}
	_ = json.NewDecoder(listResp2.Body).Decode(&list2)
	listResp2.Body.Close()

	if len(list2) != 1 {
		t.Errorf("list length after delete = %d, want 1", len(list2))
	}
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `go test ./... -v -run TestIntegration`
Expected: FAIL — 编译错误，`setupRouter` 未定义

- [ ] **Step 3: 编写 main.go 入口与 setupRouter**

```go
// main.go
package main

import (
	"fmt"
	"net/http"

	"fixedasset/handler"
	"fixedasset/repository"
	"fixedasset/service"
)

// setupRouter 组装所有依赖并返回 HTTP handler
func setupRouter() http.Handler {
	repo := repository.NewInMemoryAssetRepository()
	svc := service.NewAssetService(repo)
	assetHandler := handler.NewAssetHandler(svc)

	mux := http.NewServeMux()
	assetHandler.RegisterRoutes(mux)
	return mux
}

func main() {
	router := setupRouter()
	fmt.Println("Fixed Asset Config Service starting on :8080 ...")
	if err := http.ListenAndServe(":8080", router); err != nil {
		fmt.Printf("Server error: %v\n", err)
	}
}
```

> **重要说明：** 现有 `sort.go` 在 `package main` 中定义了 `Sort`、`IsSorted` 等函数但不含 `func main()`。`review_test.go` 虽然定义了 `func main()`，但它是 `_test.go` 文件，只在 `go test` 时编译，不影响 `go build`。因此新建 `main.go` 的 `func main()` 不会与正常构建冲突。但 `go test` 时会有两个 `func main()` 冲突（`review_test.go` 和 `integration_test.go` 同在 `package main`）。解决方案：将 `review_test.go` 中的 `func main()` 重命名为测试函数，或将其移至单独的 build tag 文件。在本计划中，建议在执行时将 `review_test.go` 的 `func main()` 改为 `func TestReviewExample(t *testing.T)`，以消除冲突。

- [ ] **Step 4: 解决 review_test.go 冲突**

修改 `review_test.go`，将 `func main()` 替换为测试函数：

```go
// review_test.go
package main

import (
	"fmt"
	"testing"
)

func TestReviewExample(t *testing.T) {
	fmt.Println("Hello, Code Review!")
	var x int = 10
	// 注意：原始代码有除零问题，这里改为安全输出
	if x > 0 {
		fmt.Println(x)
	}
}
```

- [ ] **Step 5: 运行全量测试**

Run: `go test ./... -v`
Expected: PASS — 所有包的测试通过，包括集成测试

- [ ] **Step 6: 验证服务可正常启动（冒烟测试）**

Run: `go build -o fixedasset-server . && timeout 3 ./fixedasset-server || true`
Expected: 输出 `Fixed Asset Config Service starting on :8080 ...`，3 秒后超时退出

- [ ] **Step 7: 提交**

```bash
git add main.go integration_test.go review_test.go
git commit -m "feat: add main entry point, integration tests, and fix review_test conflict"
```

---

### Task 7: API 文档与 README 更新

**Files:**
- Modify: `README.md`（追加 API 使用说明）
- Create: `docs/api.md`（API 接口文档）

**Interfaces:**
- Consumes: 所有 API endpoints 定义（Task 5）
- Produces: 完整的 API 文档和使用示例

- [ ] **Step 1: 创建 API 文档**

```markdown
<!-- docs/api.md -->
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
```

- [ ] **Step 2: 更新 README.md**

在现有 README.md 末尾追加：

```markdown

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
```

- [ ] **Step 3: 提交**

```bash
git add docs/api.md README.md
git commit -m "docs: add API documentation and update README with feature overview"
```

---

## Self-Review

### 1. Spec coverage

需求"实现固定资产配置功能"拆解为以下能力，逐一映射到 Task：

| 需求能力 | 覆盖 Task |
|---|---|
| 固定资产数据模型定义 | Task 1 |
| 资产持久化存储 | Task 2 |
| 折旧方法配置（直线法 + 双倍余额递减法） | Task 3 |
| 资产 CRUD 业务逻辑 | Task 4 |
| 资产折旧应用 | Task 4 (ApplyDepreciation) |
| RESTful API 暴露 | Task 5 |
| 服务启动与集成验证 | Task 6 |
| API 文档 | Task 7 |

**Gaps:** 无遗漏。所有需求能力均有对应 Task 覆盖。

### 2. Placeholder scan

- 全文无 "TBD" / "TODO" / "implement later" / "fill in details"
- 每个 code step 均包含完整代码
- 每个测试 step 包含完整测试代码
- 每个命令 step 包含完整命令和预期输出

### 3. Type consistency

| 符号 | 定义 Task | 使用 Task | 一致性 |
|---|---|---|---|
| `model.FixedAsset` | Task 1 | Task 2,3,4,5,6 | ✅ |
| `model.DepreciationMethod` | Task 1 | Task 3,4,5 | ✅ |
| `model.AssetStatus` | Task 1 | Task 2,4 | ✅ |
| `repository.AssetRepository` | Task 2 | Task 4 | ✅ |
| `repository.ErrAssetNotFound` | Task 2 | Task 4,5 | ✅ |
| `repository.ErrDuplicateAssetNo` | Task 2 | Task 4,5 | ✅ |
| `service.CalculateDepreciation` | Task 3 | Task 4 | ✅ |
| `service.AssetService` | Task 4 | Task 5,6 | ✅ |
| `service.CreateAssetInput` | Task 4 | Task 5 | ✅ |
| `service.ErrValidation` | Task 4 | Task 5 | ✅ |
| `service.ErrAssetDisposed` | Task 4 | Task 5 | ✅ |
| `handler.AssetHandler` | Task 5 | Task 6 | ✅ |
| `handler.NewAssetHandler` | Task 5 | Task 6 | ✅ |

所有类型和方法签名在跨 Task 引用时一致。

---

## 跨仓对齐点

| 对齐项 | test 仓库 | huanmeng511331-project 仓库 | 状态 |
|---|---|---|---|
| 技术栈 | Go 1.21+ | 空（GitLab 模板） | 无依赖 |
| 接口契约 | 提供 RESTful API (`:8080`) | 无 | 如后续需前端，通过 HTTP API 对接 |
| 数据格式 | JSON (`snake_case`) | N/A | 契约向后兼容 |
| 代码变更 | 7 个 Task 全部在此仓库 | 不涉及 | 路径隔离满足 |

**结论：** 本功能完全在 `test` 仓库实现，`huanmeng511331-project` 仓库不参与。跨仓无接口耦合风险。若未来 `huanmeng511331-project` 需要实现前端配置界面，可通过 HTTP API（Task 5 定义）与本服务对接，契约已向后兼容（新增字段/接口不影响现有调用方）。
