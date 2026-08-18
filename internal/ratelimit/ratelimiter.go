package ratelimit

import (
	"sync"
	"time"
)

// RateLimiter tracks login failures and rate limits.
type RateLimiter struct {
	mu       sync.RWMutex
	attempts map[string]*attemptInfo
}

type attemptInfo struct {
	count     int
	lastFail  time.Time
	lockedUntil *time.Time
}

// NewRateLimiter creates a new RateLimiter.
func NewRateLimiter() *RateLimiter {
	return &RateLimiter{
		attempts: make(map[string]*attemptInfo),
	}
}

const staleThreshold = 15 * time.Minute

// cleanup removes stale entries to prevent unbounded memory growth.
func (r *RateLimiter) cleanup() {
	cutoff := time.Now().Add(-staleThreshold)
	for key, info := range r.attempts {
		if info.lastFail.Before(cutoff) && (info.lockedUntil == nil || info.lockedUntil.Before(cutoff)) {
			delete(r.attempts, key)
		}
	}
}

// RecordFailure records a failed login attempt for a key.
func (r *RateLimiter) RecordFailure(key string) {
	r.mu.Lock()
	defer r.mu.Unlock()

	r.cleanup()

	info, exists := r.attempts[key]
	if !exists {
		info = &attemptInfo{}
		r.attempts[key] = info
	}
	info.count++
	info.lastFail = time.Now()
}

// IsLocked checks if a key is currently locked due to too many failures.
func (r *RateLimiter) IsLocked(key string) (bool, time.Duration) {
	r.mu.Lock()
	defer r.mu.Unlock()

	r.cleanup()

	info, exists := r.attempts[key]
	if !exists {
		return false, 0
	}

	// If locked, check if lock has expired
	if info.lockedUntil != nil && time.Now().Before(*info.lockedUntil) {
		return true, info.lockedUntil.Sub(time.Now())
	}

	// Check if 5 failures within last 15 minutes
	if info.count >= 5 && time.Since(info.lastFail) < 15*time.Minute {
		// Lock for 15 minutes from last failure
		lockedUntil := info.lastFail.Add(15 * time.Minute)
		info.lockedUntil = &lockedUntil
		return true, lockedUntil.Sub(time.Now())
	}

	return false, 0
}

// Reset clears the failure count for a key (called on successful login).
func (r *RateLimiter) Reset(key string) {
	r.mu.Lock()
	defer r.mu.Unlock()
	delete(r.attempts, key)
}

// IPRateLimiter limits requests per IP.
type IPRateLimiter struct {
	mu       sync.RWMutex
	requests map[string][]time.Time
	window   time.Duration
	limit    int
}

// NewIPRateLimiter creates a new IPRateLimiter.
func NewIPRateLimiter(limit int, window time.Duration) *IPRateLimiter {
	if limit <= 0 {
		limit = 10
	}
	if window <= 0 {
		window = time.Minute
	}
	return &IPRateLimiter{
		requests: make(map[string][]time.Time),
		window:   window,
		limit:    limit,
	}
}

// Allow checks if a request from an IP is allowed.
func (r *IPRateLimiter) Allow(ip string) bool {
	r.mu.Lock()
	defer r.mu.Unlock()

	now := time.Now()
	cutoff := now.Add(-r.window)

	// Clean old entries
	var valid []time.Time
	for _, t := range r.requests[ip] {
		if t.After(cutoff) {
			valid = append(valid, t)
		}
	}
	valid = append(valid, now)
	r.requests[ip] = valid

	return len(valid) <= r.limit
}
