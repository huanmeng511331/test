package auth

import (
	"sync"
	"time"
)

// FailedAttemptTracker tracks failed login attempts and lockouts.
type FailedAttemptTracker struct {
	mu       sync.RWMutex
	attempts map[string]*attemptInfo
}

type attemptInfo struct {
	count       int
	lockedUntil *time.Time
	lastFailed  time.Time
}

// NewFailedAttemptTracker creates a new FailedAttemptTracker with periodic cleanup.
func NewFailedAttemptTracker() *FailedAttemptTracker {
	t := &FailedAttemptTracker{
		attempts: make(map[string]*attemptInfo),
	}
	go t.cleanupLoop()
	return t
}

// cleanupLoop periodically removes expired lockout entries to prevent unbounded memory growth.
func (t *FailedAttemptTracker) cleanupLoop() {
	ticker := time.NewTicker(10 * time.Minute)
	defer ticker.Stop()
	for range ticker.C {
		t.cleanup()
	}
}

// cleanup removes entries that are no longer locked and have expired.
func (t *FailedAttemptTracker) cleanup() {
	t.mu.Lock()
	defer t.mu.Unlock()
	now := time.Now()
	for key, info := range t.attempts {
		if info.lockedUntil != nil && now.After(*info.lockedUntil) {
			delete(t.attempts, key)
		} else if info.count == 0 && now.Sub(info.lastFailed) > 10*time.Minute {
			delete(t.attempts, key)
		}
	}
}

// RecordFailedAttempt records a failed login attempt for the given key.
// Returns the current number of failed attempts.
func (t *FailedAttemptTracker) RecordFailedAttempt(key string) int {
	t.mu.Lock()
	defer t.mu.Unlock()

	info, exists := t.attempts[key]
	if !exists {
		info = &attemptInfo{}
		t.attempts[key] = info
	}
	info.count++
	info.lastFailed = time.Now()
	return info.count
}

// ResetFailedAttempts resets the failed attempt count for the given key.
func (t *FailedAttemptTracker) ResetFailedAttempts(key string) {
	t.mu.Lock()
	defer t.mu.Unlock()
	delete(t.attempts, key)
}

// IsLocked checks if the given key is currently locked.
func (t *FailedAttemptTracker) IsLocked(key string) bool {
	t.mu.RLock()
	defer t.mu.RUnlock()

	info, exists := t.attempts[key]
	if !exists || info.lockedUntil == nil {
		return false
	}
	return time.Now().Before(*info.lockedUntil)
}

// Lock locks the given key for the specified duration.
func (t *FailedAttemptTracker) Lock(key string, duration time.Duration) {
	t.mu.Lock()
	defer t.mu.Unlock()

	info, exists := t.attempts[key]
	if !exists {
		info = &attemptInfo{}
		t.attempts[key] = info
	}
	lockedUntil := time.Now().Add(duration)
	info.lockedUntil = &lockedUntil
}

// GetLockoutRemaining returns the remaining lockout duration.
func (t *FailedAttemptTracker) GetLockoutRemaining(key string) time.Duration {
	t.mu.RLock()
	defer t.mu.RUnlock()

	info, exists := t.attempts[key]
	if !exists || info.lockedUntil == nil {
		return 0
	}
	remaining := info.lockedUntil.Sub(time.Now())
	if remaining < 0 {
		return 0
	}
	return remaining
}
