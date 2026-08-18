package ratelimit

import (
	"net"
	"net/http"
	"strings"
	"sync"
	"time"
)

// TrustedProxies holds the list of trusted proxy IP addresses.
// When empty (default), X-Forwarded-For is ignored for security.
var TrustedProxies []string

const (
	judgeWindow  = 15 * time.Minute
	lockDuration = 15 * time.Minute
	staleThreshold = 30 * time.Minute
)

// RateLimiter tracks login failures and rate limits.
type RateLimiter struct {
	mu       sync.RWMutex
	attempts map[string]*attemptInfo
	stopCh   chan struct{}
}

type attemptInfo struct {
	count       int
	lastFail    time.Time
	lockedUntil *time.Time
}

// NewRateLimiter creates a new RateLimiter.
func NewRateLimiter() *RateLimiter {
	r := &RateLimiter{
		attempts: make(map[string]*attemptInfo),
		stopCh:   make(chan struct{}),
	}

	// Start background cleanup goroutine to avoid blocking writes.
	go r.backgroundCleanup()

	return r
}

// Stop stops the background cleanup goroutine.
func (r *RateLimiter) Stop() {
	close(r.stopCh)
}

func (r *RateLimiter) backgroundCleanup() {
	ticker := time.NewTicker(5 * time.Minute)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			r.mu.Lock()
			r.cleanup()
			r.mu.Unlock()
		case <-r.stopCh:
			return
		}
	}
}

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

	info, exists := r.attempts[key]
	if !exists {
		return false, 0
	}

	// If locked, check if lock has expired
	if info.lockedUntil != nil {
		if time.Now().Before(*info.lockedUntil) {
			return true, info.lockedUntil.Sub(time.Now())
		}
		// Lock expired: reset count and clear lockedUntil for a fresh start
		info.count = 0
		info.lockedUntil = nil
	}

	// Check if 5 failures within judge window
	if info.count >= 5 && time.Since(info.lastFail) < judgeWindow {
		// Lock for lockDuration from last failure
		lockedUntil := info.lastFail.Add(lockDuration)
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

	// Reject if limit exceeded; do NOT append now for rejected requests
	if len(valid) >= r.limit {
		return false
	}

	valid = append(valid, now)
	r.requests[ip] = valid

	return true
}

// GetClientIP extracts the client IP from the request.
// By default, it uses RemoteAddr and ignores X-Forwarded-For
// unless TrustedProxies is configured.
func GetClientIP(r *http.Request) string {
	ip := r.RemoteAddr
	// Strip port if present
	if host, _, err := net.SplitHostPort(ip); err == nil {
		ip = host
	}

	fwd := r.Header.Get("X-Forwarded-For")
	if fwd == "" || len(TrustedProxies) == 0 {
		return ip
	}

	// Check if RemoteAddr is from a trusted proxy
	if !isTrusted(ip) {
		return ip
	}

	// Iterate from right (closest to server) to left, find first non-trusted IP
	parts := strings.Split(fwd, ",")
	for i := len(parts) - 1; i >= 0; i-- {
		candidate := strings.TrimSpace(parts[i])
		if candidate == "" {
			continue
		}
		if !isTrusted(candidate) {
			return candidate
		}
	}

	return ip
}

func isTrusted(ip string) bool {
	for _, trusted := range TrustedProxies {
		if strings.TrimSpace(trusted) == ip {
			return true
		}
	}
	return false
}
