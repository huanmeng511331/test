package main

import (
	"log"
	"net/http"
	"os"
	"time"

	"login-system/internal/handler"
	"login-system/internal/middleware"
	"login-system/internal/ratelimit"
	"login-system/internal/repository"
	"login-system/internal/service"
	"login-system/internal/session"
	cryptopkg "login-system/pkg/crypto"
)

func main() {
	// Initialize repositories (in-memory)
	userRepo := repository.NewMemoryUserRepository()
	sessionRepo := repository.NewMemorySessionRepository()

	// Initialize services
	hasher := cryptopkg.NewSHA256Hasher(16)
	sessionManager := session.NewManager(sessionRepo, 3)
	authService := service.NewAuthService(userRepo, sessionManager, hasher, false)

	// Initialize rate limiters
	failureLimiter := ratelimit.NewRateLimiter()
	ipLimiter := ratelimit.NewIPRateLimiter(10, time.Minute)

	// Initialize handlers
	authHandler := handler.NewAuthHandler(authService, failureLimiter, false)

	// Setup router using standard library
	mux := http.NewServeMux()

	// Public routes
	mux.HandleFunc("/api/v1/auth/login", authHandler.Login)

	// Protected routes with middleware
	protected := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		switch r.URL.Path {
		case "/api/v1/auth/logout":
			authHandler.Logout(w, r)
		case "/api/v1/me":
			w.Header().Set("Content-Type", "application/json")
			w.Write([]byte(`{"code":0,"message":"ok","data":{"message":"protected resource"}}`))
		default:
			w.WriteHeader(http.StatusNotFound)
		}
	})

	// Wrap protected with middleware
	authMiddleware := middleware.AuthMiddleware(authService)
	rateLimitMiddleware := middleware.RateLimitMiddleware(failureLimiter, ipLimiter)
	protectedWithMiddleware := authMiddleware(rateLimitMiddleware(protected))

	mux.Handle("/api/v1/", protectedWithMiddleware)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	log.Printf("Server starting on port %s", port)
	if err := http.ListenAndServe(":"+port, mux); err != nil {
		log.Fatalf("Server failed: %v", err)
	}
}
