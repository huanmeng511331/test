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
