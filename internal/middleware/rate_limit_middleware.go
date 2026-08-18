package middleware

import (
	"net/http"
	"strings"

	"login-system/internal/ratelimit"
)

// RateLimitMiddleware creates a rate limiting middleware for login endpoints.
func RateLimitMiddleware(rateLimiter *ratelimit.RateLimiter, ipLimiter *ratelimit.IPRateLimiter) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			// Only apply to login endpoint
			if !strings.HasSuffix(r.URL.Path, "/login") {
				next.ServeHTTP(w, r)
				return
			}

			// Get real IP securely (prevents X-Forwarded-For forgery)
			ip := ratelimit.GetClientIP(r)

			// Check IP rate limit
			if !ipLimiter.Allow(ip) {
				w.Header().Set("Content-Type", "application/json")
				w.WriteHeader(http.StatusTooManyRequests)
				w.Write([]byte(`{"code":42901,"message":"操作过于频繁，请稍后再试"}`))
				return
			}

			next.ServeHTTP(w, r)
		})
	}
}
