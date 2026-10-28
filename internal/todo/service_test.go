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
