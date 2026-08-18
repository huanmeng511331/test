package middleware

import (
	"net"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
	"login-system/internal/utils"
)

// RateLimitConfig holds the rate limit configuration.
type RateLimitConfig struct {
	MaxRequests int
	Window      time.Duration
}

// ipBucket represents a token bucket for an IP address.
type ipBucket struct {
	count     int
	resetTime time.Time
	mu        sync.Mutex
}

// IPRateLimitMiddleware returns a gin middleware that limits requests per IP.
func IPRateLimitMiddleware(config RateLimitConfig) gin.HandlerFunc {
	buckets := make(map[string]*ipBucket)
	var mu sync.RWMutex

	// Start a background goroutine to clean up expired buckets periodically.
	go func() {
		ticker := time.NewTicker(config.Window)
		defer ticker.Stop()
		for range ticker.C {
			mu.Lock()
			now := time.Now()
			for ip, bucket := range buckets {
				if now.After(bucket.resetTime) {
					delete(buckets, ip)
				}
			}
			mu.Unlock()
		}
	}()

	return func(c *gin.Context) {
		ip := getClientIP(c)
		now := time.Now()

		mu.Lock()
		bucket, exists := buckets[ip]
		if !exists || now.After(bucket.resetTime) {
			bucket = &ipBucket{
				count:     0,
				resetTime: now.Add(config.Window),
			}
			buckets[ip] = bucket
		}
		mu.Unlock()

		bucket.mu.Lock()
		if now.After(bucket.resetTime) {
			bucket.count = 0
			bucket.resetTime = now.Add(config.Window)
		}
		bucket.count++
		currentCount := bucket.count
		bucket.mu.Unlock()

		if currentCount > config.MaxRequests {
			utils.TooManyRequests(c)
			c.Abort()
			return
		}

		c.Next()
	}
}

func getClientIP(c *gin.Context) string {
	ip := c.ClientIP()
	if ip == "" {
		ip, _, _ = net.SplitHostPort(c.Request.RemoteAddr)
	}
	return ip
}
