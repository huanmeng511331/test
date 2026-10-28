# 对话创建任务 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现一个对话式命令行工具，帮助内部用户通过交互输入记录日常待办事项，最小闭环为仅支持新增（创建）操作。

**Architecture:** 采用经典三层架构——Model 定义领域数据结构，Store 负责持久化（内存实现），Service 封装业务逻辑，main.go 提供对话式 CLI 入口。存储层通过接口抽象，便于后续替换为真实数据库。

**Tech Stack:** Go 1.23.2, 标准库 (`bufio`, `fmt`, `os`, `strings`, `sync`, `time`)

## Global Constraints

- Go 版本：>= 1.23.2
- 存储：首期为内存实现（`InMemoryStore`），线程安全
- 输入验证：事项名称不能为空
- 输出格式：中文提示，包含 ID、名称、描述、创建时间
- 测试：每个导出函数均需单元测试覆盖
- 代码规范：遵循 Go 官方 formatting 标准（`gofmt`）

---

## File Structure

```
.
├── cmd/todo/main.go              # CLI 入口，对话式交互
├── internal/todo/
│   ├── model.go                  # TodoItem 领域模型
│   ├── store.go                  # Store 接口 + InMemoryStore 实现
│   ├── store_test.go             # Store 层单元测试
│   ├── service.go                # Service 业务逻辑层
│   └── service_test.go           # Service 层单元测试
└── go.mod                        # module: todo-app
```

---

### Task 1: 定义领域模型 TodoItem

**Files:**
- Create: `internal/todo/model.go`

**Interfaces:**
- Produces: `type TodoItem struct { ID string; Name string; Description string; CreatedAt time.Time }`

- [ ] **Step 1: 创建 model 文件**

```go
package todo

import "time"

// TodoItem represents a single todo task.
type TodoItem struct {
	ID          string
	Name        string
	Description string
	CreatedAt   time.Time
}
```

- [ ] **Step 2: 验证编译通过**

Run: `go build ./internal/todo`
Expected: 无报错，编译通过

- [ ] **Step 3: Commit**

```bash
git add internal/todo/model.go
git commit -m "feat: add TodoItem domain model"
```

---

### Task 2: 实现存储层 Store 接口与 InMemoryStore

**Files:**
- Create: `internal/todo/store.go`
- Create: `internal/todo/store_test.go`

**Interfaces:**
- Consumes: `TodoItem` (from Task 1)
- Produces: `type Store interface { Create(name, description string) (*TodoItem, error); GetAll() []*TodoItem }`
- Produces: `func NewInMemoryStore() *InMemoryStore`

- [ ] **Step 1: 编写失败测试**

```go
package todo

import "testing"

func TestInMemoryStore_Create(t *testing.T) {
	store := NewInMemoryStore()
	item, err := store.Create("Buy milk", "Buy 2 liters of milk")
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if item == nil {
		t.Fatal("expected item, got nil")
	}
	if item.Name != "Buy milk" {
		t.Errorf("expected name 'Buy milk', got %q", item.Name)
	}
	if item.Description != "Buy 2 liters of milk" {
		t.Errorf("expected description 'Buy 2 liters of milk', got %q", item.Description)
	}
	if item.ID == "" {
		t.Error("expected non-empty ID")
	}
	if item.CreatedAt.IsZero() {
		t.Error("expected CreatedAt to be set")
	}
}

func TestInMemoryStore_GetAll(t *testing.T) {
	store := NewInMemoryStore()
	store.Create("Task 1", "Description 1")
	store.Create("Task 2", "Description 2")
	items := store.GetAll()
	if len(items) != 2 {
		t.Fatalf("expected 2 items, got %d", len(items))
	}
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `go test ./internal/todo -run TestInMemoryStore -v`
Expected: FAIL - `NewInMemoryStore` 未定义 / `Create` 未定义

- [ ] **Step 3: 实现最小代码**

```go
package todo

import (
	"fmt"
	"sync"
	"time"
)

// Store defines the persistence interface for todo items.
type Store interface {
	Create(name, description string) (*TodoItem, error)
	GetAll() []*TodoItem
}

// InMemoryStore stores todo items in memory.
type InMemoryStore struct {
	mu     sync.RWMutex
	items  []*TodoItem
	nextID int
}

// NewInMemoryStore creates a new InMemoryStore.
func NewInMemoryStore() *InMemoryStore {
	return &InMemoryStore{
		items:  make([]*TodoItem, 0),
		nextID: 1,
	}
}

// Create adds a new todo item.
func (s *InMemoryStore) Create(name, description string) (*TodoItem, error) {
	s.mu.Lock()
	defer s.mu.Unlock()
	item := &TodoItem{
		ID:          fmt.Sprintf("%d", s.nextID),
		Name:        name,
		Description: description,
		CreatedAt:   time.Now(),
	}
	s.items = append(s.items, item)
	s.nextID++
	return item, nil
}

// GetAll returns all stored todo items.
func (s *InMemoryStore) GetAll() []*TodoItem {
	s.mu.RLock()
	defer s.mu.RUnlock()
	result := make([]*TodoItem, len(s.items))
	copy(result, s.items)
	return result
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `go test ./internal/todo -run TestInMemoryStore -v`
Expected: PASS (2/2)

- [ ] **Step 5: Commit**

```bash
git add internal/todo/store.go internal/todo/store_test.go
git commit -m "feat: add Store interface and InMemoryStore implementation"
```

---

### Task 3: 实现业务 Service 层

**Files:**
- Create: `internal/todo/service.go`
- Create: `internal/todo/service_test.go`

**Interfaces:**
- Consumes: `Store` interface, `NewInMemoryStore()`, `TodoItem` (from Task 1-2)
- Produces: `func NewService(store Store) *Service`
- Produces: `func (s *Service) CreateTodo(name, description string) (*TodoItem, error)`

- [ ] **Step 1: 编写失败测试**

```go
package todo

import "testing"

func TestService_CreateTodo(t *testing.T) {
	store := NewInMemoryStore()
	svc := NewService(store)
	item, err := svc.CreateTodo("Review PR", "Review the todo creation PR")
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if item == nil {
		t.Fatal("expected item, got nil")
	}
	if item.Name != "Review PR" {
		t.Errorf("expected name 'Review PR', got %q", item.Name)
	}
	if item.Description != "Review the todo creation PR" {
		t.Errorf("expected description 'Review the todo creation PR', got %q", item.Description)
	}
	// Verify it was actually stored
	all := store.GetAll()
	if len(all) != 1 {
		t.Fatalf("expected 1 item in store, got %d", len(all))
	}
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `go test ./internal/todo -run TestService_CreateTodo -v`
Expected: FAIL - `NewService` 未定义

- [ ] **Step 3: 实现最小代码**

```go
package todo

// Service provides business logic for todo management.
type Service struct {
	store Store
}

// NewService creates a new Service with the given store.
func NewService(store Store) *Service {
	return &Service{store: store}
}

// CreateTodo creates a new todo item with the given name and description.
func (s *Service) CreateTodo(name, description string) (*TodoItem, error) {
	return s.store.Create(name, description)
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `go test ./internal/todo -run TestService_CreateTodo -v`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add internal/todo/service.go internal/todo/service_test.go
git commit -m "feat: add Service layer for todo creation"
```

---

### Task 4: 实现对话式 CLI 入口

**Files:**
- Create: `cmd/todo/main.go`

**Interfaces:**
- Consumes: `NewInMemoryStore()`, `NewService()`, `CreateTodo(name, description string) (*TodoItem, error)` (from Task 2-3)

- [ ] **Step 1: 编写 CLI 程序**

```go
package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"

	"todo-app/internal/todo"
)

func main() {
	store := todo.NewInMemoryStore()
	svc := todo.NewService(store)
	reader := bufio.NewReader(os.Stdin)

	fmt.Println("=== 待办事项创建助手 ===")
	fmt.Println("请输入待办事项信息（输入空行结束）")
	fmt.Println()

	fmt.Print("事项名称: ")
	name, err := reader.ReadString('\n')
	if err != nil {
		fmt.Fprintf(os.Stderr, "读取名称失败: %v\n", err)
		os.Exit(1)
	}
	name = strings.TrimSpace(name)
	if name == "" {
		fmt.Println("事项名称不能为空")
		os.Exit(1)
	}

	fmt.Print("事项描述: ")
	description, err := reader.ReadString('\n')
	if err != nil {
		fmt.Fprintf(os.Stderr, "读取描述失败: %v\n", err)
		os.Exit(1)
	}
	description = strings.TrimSpace(description)

	item, err := svc.CreateTodo(name, description)
	if err != nil {
		fmt.Fprintf(os.Stderr, "创建失败: %v\n", err)
		os.Exit(1)
	}

	fmt.Printf("\n✅ 待办事项已创建！\n")
	fmt.Printf("ID: %s\n", item.ID)
	fmt.Printf("名称: %s\n", item.Name)
	fmt.Printf("描述: %s\n", item.Description)
	fmt.Printf("创建时间: %s\n", item.CreatedAt.Format("2006-01-02 15:04:05"))
}
```

- [ ] **Step 2: 验证编译通过**

Run: `go build ./cmd/todo`
Expected: 无报错，生成可执行文件

- [ ] **Step 3: 运行验证**

Run: `echo -e "Buy milk\nBuy 2 liters" | ./todo`
Expected: 输出创建成功信息，包含 ID、名称、描述、创建时间

- [ ] **Step 4: Commit**

```bash
git add cmd/todo/main.go
git commit -m "feat: add interactive CLI for dialog-based todo creation"
```

---

## Self-Review

**1. Spec coverage:**
- ✅ 对话式创建待办事项 → Task 4
- ✅ 事项名称和描述输入 → Task 4
- ✅ 内部用户使用 → Task 4（命令行交互）
- ✅ 最小闭环（仅创建）→ Task 2-4 仅实现 Create 操作，无 Update/Delete/List

**2. Placeholder scan:**
- ✅ 无 TBD/TODO
- ✅ 无 "implement later"
- ✅ 每个 Task 均包含完整代码和命令

**3. Type consistency:**
- ✅ `CreateTodo(name, description string) (*TodoItem, error)` 签名在 Task 3 和 Task 4 中一致
- ✅ `Store.Create(name, description string) (*TodoItem, error)` 在 Task 2 中定义，Task 3 中使用
- ✅ `TodoItem` 结构体字段在 Task 1 定义，Task 2-4 中一致使用

---

## Execution Handoff

**Plan complete and saved to `docs/superpowers/plans/2026-10-28-dialog-todo-creation.md`.**

**Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints for review

**Which approach?**
