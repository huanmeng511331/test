package repository

import (
	"sort"
	"sync"
	"time"

	"login-system/internal/models"
)

// SessionRepository defines the interface for session data access.
type SessionRepository interface {
	Create(session *models.Session) error
	GetByToken(token string) (*models.Session, error)
	DeleteByToken(token string) error
	DeleteExpired() error
	CountByUserID(userID int64) (int64, error)
	DeleteOldestByUserID(userID int64, limit int) error
}

// MemorySessionRepository implements SessionRepository in memory.
type MemorySessionRepository struct {
	mu       sync.RWMutex
	sessions map[string]*models.Session
	idSeq    int64
}

// NewMemorySessionRepository creates a new MemorySessionRepository.
func NewMemorySessionRepository() *MemorySessionRepository {
	return &MemorySessionRepository{
		sessions: make(map[string]*models.Session),
		idSeq:    1,
	}
}

// Create inserts a new session.
func (r *MemorySessionRepository) Create(session *models.Session) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	session.ID = r.idSeq
	r.idSeq++
	session.CreatedAt = time.Now()

	copySession := *session
	r.sessions[session.Token] = &copySession
	return nil
}

// GetByToken retrieves a session by token.
func (r *MemorySessionRepository) GetByToken(token string) (*models.Session, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	session, exists := r.sessions[token]
	if !exists {
		return nil, nil
	}
	copySession := *session
	return &copySession, nil
}

// DeleteByToken deletes a session by token.
func (r *MemorySessionRepository) DeleteByToken(token string) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	delete(r.sessions, token)
	return nil
}

// DeleteExpired removes all expired sessions.
func (r *MemorySessionRepository) DeleteExpired() error {
	r.mu.Lock()
	defer r.mu.Unlock()

	now := time.Now()
	for token, session := range r.sessions {
		if now.After(session.ExpiresAt) {
			delete(r.sessions, token)
		}
	}
	return nil
}

// CountByUserID counts sessions for a user.
func (r *MemorySessionRepository) CountByUserID(userID int64) (int64, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	var count int64
	for _, session := range r.sessions {
		if session.UserID == userID {
			count++
		}
	}
	return count, nil
}

// DeleteOldestByUserID deletes oldest sessions for a user beyond a limit.
func (r *MemorySessionRepository) DeleteOldestByUserID(userID int64, limit int) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	var userSessions []*models.Session
	for _, session := range r.sessions {
		if session.UserID == userID {
			userSessions = append(userSessions, session)
		}
	}

	if len(userSessions) <= limit {
		return nil
	}

	// Sort by created_at ascending (oldest first)
	sort.Slice(userSessions, func(i, j int) bool {
		return userSessions[i].CreatedAt.Before(userSessions[j].CreatedAt)
	})

	// Delete oldest sessions beyond the limit
	for i := 0; i < len(userSessions)-limit; i++ {
		delete(r.sessions, userSessions[i].Token)
	}

	return nil
}
