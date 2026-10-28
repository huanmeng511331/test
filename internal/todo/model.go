package todo

import "time"

// TodoItem represents a single todo task.
type TodoItem struct {
	ID          string
	Name        string
	Description string
	CreatedAt   time.Time
}
