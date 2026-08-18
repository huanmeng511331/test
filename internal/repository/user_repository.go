package repository

import (
	"fmt"
	"sync"
	"time"

	"login-system/internal/models"
)

// UserRepository defines the interface for user data access.
type UserRepository interface {
	GetByAccount(account string) (*models.User, error)
	Create(user *models.User) error
	Update(user *models.User) error
}

// MemoryUserRepository implements UserRepository in memory.
type MemoryUserRepository struct {
	mu    sync.RWMutex
	users map[string]*models.User
	idSeq int64
}

// NewMemoryUserRepository creates a new MemoryUserRepository.
func NewMemoryUserRepository() *MemoryUserRepository {
	return &MemoryUserRepository{
		users: make(map[string]*models.User),
		idSeq: 1,
	}
}

// GetByAccount retrieves a user by their account.
func (r *MemoryUserRepository) GetByAccount(account string) (*models.User, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	user, exists := r.users[account]
	if !exists {
		return nil, nil
	}
	// Return a copy
	copyUser := *user
	return &copyUser, nil
}

// Create inserts a new user.
func (r *MemoryUserRepository) Create(user *models.User) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.users[user.Account]; exists {
		return fmt.Errorf("user already exists")
	}

	user.ID = r.idSeq
	r.idSeq++
	user.CreatedAt = time.Now()
	user.UpdatedAt = user.CreatedAt

	// Store a copy
	copyUser := *user
	r.users[user.Account] = &copyUser
	return nil
}

// Update updates a user.
func (r *MemoryUserRepository) Update(user *models.User) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.users[user.Account]; !exists {
		return fmt.Errorf("user not found")
	}

	user.UpdatedAt = time.Now()
	copyUser := *user
	r.users[user.Account] = &copyUser
	return nil
}
