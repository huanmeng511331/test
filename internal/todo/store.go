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
