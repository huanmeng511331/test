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
