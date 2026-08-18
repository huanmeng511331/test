package session

import (
	"crypto/rand"
	"encoding/hex"
	"fmt"
	"net/http"
	"time"

	"login-system/internal/models"
	"login-system/internal/repository"
)

// Manager manages user sessions.
type Manager struct {
	sessionRepo repository.SessionRepository
	maxSessions int
}

// NewManager creates a new session manager.
func NewManager(sessionRepo repository.SessionRepository, maxSessions int) *Manager {
	if maxSessions <= 0 {
		maxSessions = 3
	}
	return &Manager{
		sessionRepo: sessionRepo,
		maxSessions: maxSessions,
	}
}

// CreateSession creates a new session for a user.
func (m *Manager) CreateSession(userID int64, ip, userAgent string, rememberMe bool) (*models.Session, string, error) {
	// Clean up expired sessions
	_ = m.sessionRepo.DeleteExpired()

	// Check max sessions
	count, err := m.sessionRepo.CountByUserID(userID)
	if err != nil {
		return nil, "", fmt.Errorf("count sessions: %w", err)
	}
	if count >= int64(m.maxSessions) {
		// Remove oldest sessions
		if err := m.sessionRepo.DeleteOldestByUserID(userID, m.maxSessions-1); err != nil {
			return nil, "", fmt.Errorf("delete oldest sessions: %w", err)
		}
	}

	token, err := generateToken()
	if err != nil {
		return nil, "", fmt.Errorf("generate token: %w", err)
	}

	expiresAt := time.Now().Add(2 * time.Hour)
	if rememberMe {
		expiresAt = time.Now().Add(30 * 24 * time.Hour)
	}

	session := &models.Session{
		UserID:    userID,
		Token:     token,
		ExpiresAt: expiresAt,
		IP:        ip,
		UserAgent: userAgent,
	}

	if err := m.sessionRepo.Create(session); err != nil {
		return nil, "", fmt.Errorf("create session: %w", err)
	}

	return session, token, nil
}

// ValidateSession validates a session token.
func (m *Manager) ValidateSession(token string) (*models.Session, error) {
	if token == "" {
		return nil, fmt.Errorf("empty token")
	}

	session, err := m.sessionRepo.GetByToken(token)
	if err != nil {
		return nil, fmt.Errorf("get session: %w", err)
	}
	if session == nil {
		return nil, fmt.Errorf("session not found")
	}
	if session.IsExpired() {
		_ = m.sessionRepo.DeleteByToken(token)
		return nil, fmt.Errorf("session expired")
	}
	return session, nil
}

// DeleteSession deletes a session.
func (m *Manager) DeleteSession(token string) error {
	return m.sessionRepo.DeleteByToken(token)
}

// SetCookie sets the session cookie on the response.
func SetCookie(w http.ResponseWriter, token string, maxAge int, secure bool) {
	http.SetCookie(w, &http.Cookie{
		Name:     "session_id",
		Value:    token,
		Path:     "/",
		HttpOnly: true,
		Secure:   secure,
		SameSite: http.SameSiteLaxMode,
		MaxAge:   maxAge,
	})
}

// ClearCookie clears the session cookie.
func ClearCookie(w http.ResponseWriter, secure bool) {
	http.SetCookie(w, &http.Cookie{
		Name:     "session_id",
		Value:    "",
		Path:     "/",
		HttpOnly: true,
		Secure:   secure,
		SameSite: http.SameSiteLaxMode,
		MaxAge:   0,
		Expires:  time.Unix(0, 0),
	})
}

func generateToken() (string, error) {
	b := make([]byte, 32)
	if _, err := rand.Read(b); err != nil {
		return "", err
	}
	return hex.EncodeToString(b), nil
}
