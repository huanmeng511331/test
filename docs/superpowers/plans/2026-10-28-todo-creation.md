# 对话式待办事项创建 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个命令行对话式应用，允许内部用户通过交互式对话创建待办事项（事项名称 + 描述），并将待办事项存储在内存中。

**Architecture:** 采用分层架构：领域模型（`internal/todo/model.go`）→ 存储层（`internal/todo/store.go`，内存实现）→ 业务层（`internal/todo/service.go`，封装创建逻辑）→ 入口层（`cmd/todo/main.go`，负责命令行对话交互与输入输出）。无外部依赖，纯标准库实现。

**Tech Stack:** Go 1.22+, 标准库 (`bufio`, `fmt`, `os`, `strings`, `time`)

## Global Constraints

- 目标用户：内部用户
- 最小闭环：仅实现新增待办事项（Create）
- 数据存储：内存存储（当前不要求持久化）
- 交互方式：命令行对话式（用户输入名称和描述）
- 无外部依赖，仅使用 Go 标准库
- 遵循 TDD：先写测试，再写实现
- 频繁提交，每个 Task 完成后单独 commit

---

## File Structure

| 文件 | 职责 |
|---|---|
| `go.mod` | Go 模块定义，模块名 `todo-app` |
| `internal/todo/model.go` | 定义 `TodoItem` 领域模型（ID、Name、Description、CreatedAt） |
| `internal/todo/store.go` | 定义 `Store` 接口及 `InMemoryStore` 实现，提供增查方法 |
| `internal/todo/service.go` | 定义 `Service` 结构体，封装创建待办事项的业务逻辑 |
| `cmd/todo/main.go` | CLI 入口，使用 `bufio.Scanner` 进行对话式交互，调用 Service 创建待办事项 |
| `internal/todo/service_test.go` | `Service.Create` 的单元测试 |
| `internal/todo/store_test.go` | `InMemoryStore` 的单元测试 |

---

### Task 1: 初始化 Go 模块与项目结构

**Files:**
- Create: `go.mod`
- Create: `internal/todo/model.go`
- Create: `internal/todo/store.go`

**Interfaces:**
- Produces: `todo.TodoItem` struct, `todo.Store` interface, `todo.InMemoryStore`

- [ ] **Step 1: 初始化 Go 模块**

```bash
go mod init todo-app
```

- [ ] **Step 2: 创建领域模型**

`internal/todo/model.go`:
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

- [ ] **Step 3: 创建存储接口与内存实现**

`internal/todo/store.go`:
```go
package todo

import (
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

- [ ] **Step 4: 验证模块初始化**

Run: `go mod tidy && go build ./...`
Expected: 成功编译，无报错

- [ ] **Step 5: Commit**

```bash
git add go.mod internal/todo/model.go internal/todo/store.go
git commit -m "feat(todo): initialize Go module, add TodoItem model and in-memory store"
```

---

### Task 2: 实现存储层测试与业务服务层（TDD）

**Files:**
- Create: `internal/todo/store_test.go`
- Create: `internal/todo/service.go`
- Create: `internal/todo/service_test.go`

**Interfaces:**
- Consumes: `todo.TodoItem`, `todo.Store`, `todo.InMemoryStore`
- Produces: `todo.Service` with `CreateTodo(name, description string) (*TodoItem, error)`

- [ ] **Step 1: 编写存储层测试**

`internal/todo/store_test.go`:
```go
package todo

import (
	"testing"
)

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

- [ ] **Step 2: 运行存储测试，确认失败或编译错误**

Run: `go test ./internal/todo -v -run TestInMemoryStore`
Expected: 存储层测试通过（Task 1 已提供实现）

- [ ] **Step 3: 编写 Service 层（先写测试）**

`internal/todo/service_test.go`:
```go
package todo

import (
	"testing"
)

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

- [ ] **Step 4: 运行 Service 测试，确认失败**

Run: `go test ./internal/todo -v -run TestService_CreateTodo`
Expected: FAIL — `NewService` 或 `CreateTodo` 未定义

- [ ] **Step 5: 实现 Service 层**

`internal/todo/service.go`:
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

- [ ] **Step 6: 运行 Service 测试，确认通过**

Run: `go test ./internal/todo -v -run TestService_CreateTodo`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add internal/todo/store_test.go internal/todo/service.go internal/todo/service_test.go
git commit -m "feat(todo): add store tests and service layer for creating todos"
```

---

### Task 3: 实现命令行对话式入口

**Files:**
- Create: `cmd/todo/main.go`

**Interfaces:**
- Consumes: `todo.Service`, `todo.NewService`, `todo.NewInMemoryStore`
- Produces: 可执行 CLI 程序 `cmd/todo/main.go`

- [ ] **Step 1: 编写 CLI 入口（无测试，纯集成验证）**

`cmd/todo/main.go`:
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

- [ ] **Step 2: 编译验证**

Run: `go build ./cmd/todo`
Expected: 成功编译，生成 `todo` 可执行文件

- [ ] **Step 3: Commit**

```bash
git add cmd/todo/main.go
git commit -m "feat(cli): add interactive command-line todo creation entrypoint"
```

---

### Task 4: 端到端验证

**Files:**
- None (验证现有产物)

- [ ] **Step 1: 运行全部单元测试**

Run: `go test ./... -v`
Expected: 所有测试通过

- [ ] **Step 2: 端到端验证 CLI**

Run:
```bash
echo -e "Buy groceries\nBuy milk and eggs" | ./todo
```

Expected output:
```
=== 待办事项创建助手 ===
请输入待办事项信息（输入空行结束）

事项名称: 事项描述: 
✅ 待办事项已创建！
ID: 1
名称: Buy groceries
描述: Buy milk and eggs
创建时间: <current time>
```

- [ ] **Step 3: Commit（如验证通过，标记完成）**

```bash
git add -A
git commit -m "test(e2e): verify interactive todo creation end-to-end" || echo "nothing to commit"
```

---

## Self-Review

### 1. Spec Coverage

| 需求点 | 对应 Task | 状态 |
|---|---|---|
| 对话创建任务 | Task 3 (`cmd/todo/main.go` 交互式 CLI) | ✅ |
| 新增待办事项 | Task 2 (`Service.CreateTodo`) | ✅ |
| 事项名称 | Task 3 (读取并传入 `name`) | ✅ |
| 事项描述 | Task 3 (读取并传入 `description`) | ✅ |
| 内部用户 | Task 3 (简单 CLI，无认证) | ✅ |
| 最小闭环仅创建 | 无 Update/Delete/List 逻辑 | ✅ |

**Gap:** None.

### 2. Placeholder Scan

- 无 "TBD", "TODO", "implement later" — ✅
- 无 "Add appropriate error handling" 等模糊描述 — ✅
- 每个代码步骤均提供完整代码 — ✅
- 无未定义的类型或方法引用 — ✅

### 3. Type Consistency

- `TodoItem` 字段：`ID string`, `Name string`, `Description string`, `CreatedAt time.Time` — 全计划一致 ✅
- `Store.Create(name, description string) (*TodoItem, error)` — 全计划一致 ✅
- `Service.CreateTodo(name, description string) (*TodoItem, error)` — 全计划一致 ✅
- `NewInMemoryStore() *InMemoryStore` — 全计划一致 ✅
- `NewService(store Store) *Service` — 全计划一致 ✅

---

## Execution Handoff

**Plan complete and saved to `docs/superpowers/plans/2026-10-28-todo-creation.md`.**

**Two execution options:**

1. **Subagent-Driven (recommended)** — Dispatch a fresh subagent per task, review between tasks, fast iteration.
2. **Inline Execution** — Execute tasks in this session using `executing-plans`, batch execution with checkpoints for review.
