package auth

import (
	"sync"
	"time"
)

// TokenBlacklistEntry represents a blacklisted token with its expiration time.
type TokenBlacklistEntry struct {
	ExpiresAt time.Time
}

// TokenBlacklist manages revoked tokens with automatic cleanup.
type TokenBlacklist struct {
	mu       sync.RWMutex
	entries  map[string]TokenBlacklistEntry
	cleanupInterval time.Duration
}

// NewTokenBlacklist creates a new TokenBlacklist with the given cleanup interval.
func NewTokenBlacklist(cleanupInterval time.Duration) *TokenBlacklist {
	bl := &TokenBlacklist{
		entries:         make(map[string]TokenBlacklistEntry),
		cleanupInterval: cleanupInterval,
	}
	go bl.cleanupLoop()
	return bl
}

// Add adds a token to the blacklist with the given expiration time.
func (bl *TokenBlacklist) Add(token string, expiresAt time.Time) {
	bl.mu.Lock()
	defer bl.mu.Unlock()
	bl.entries[token] = TokenBlacklistEntry{ExpiresAt: expiresAt}
}

// IsBlacklisted checks if a token is in the blacklist.
func (bl *TokenBlacklist) IsBlacklisted(token string) bool {
	bl.mu.RLock()
	defer bl.mu.RUnlock()
	entry, exists := bl.entries[token]
	if !exists {
		return false
	}
	// If the entry has expired, consider it not blacklisted
	if time.Now().After(entry.ExpiresAt) {
		return false
	}
	return true
}

// cleanup removes expired entries from the blacklist.
func (bl *TokenBlacklist) cleanup() {
	bl.mu.Lock()
	defer bl.mu.Unlock()
	now := time.Now()
	for token, entry := range bl.entries {
		if now.After(entry.ExpiresAt) {
			delete(bl.entries, token)
		}
	}
}

// cleanupLoop runs a periodic cleanup of expired entries.
func (bl *TokenBlacklist) cleanupLoop() {
	ticker := time.NewTicker(bl.cleanupInterval)
	defer ticker.Stop()
	for range ticker.C {
		bl.cleanup()
	}
}
