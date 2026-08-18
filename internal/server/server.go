package server

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"

	"login-system/internal/auth"
	"login-system/internal/config"
	"login-system/internal/handlers"
	"login-system/internal/middleware"
)

// Server represents the HTTP server with Gin engine.
type Server struct {
	Engine *gin.Engine
	config *config.Config
	db     *gorm.DB
	http   *http.Server
}

// New creates a new Server instance with the given configuration and database connection.
func New(cfg *config.Config, database *gorm.DB) *Server {
	// Set Gin mode based on environment
	if cfg.IsProduction() {
		gin.SetMode(gin.ReleaseMode)
	} else {
		gin.SetMode(gin.DebugMode)
	}

	engine := gin.New()
	engine.Use(gin.Logger(), gin.Recovery())

	// Initialize shared token blacklist with periodic cleanup
	tokenBlacklist := auth.NewTokenBlacklist(10 * time.Minute)
	handlers.TokenBlacklist = tokenBlacklist
	middleware.SetTokenBlacklist(tokenBlacklist)

	// Health check endpoint
	engine.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status": "ok",
			"env":    cfg.AppEnv,
		})
	})

	// Auth routes
	api := engine.Group("/api/v1")
	api.POST("/auth/login", middleware.IPRateLimitMiddleware(middleware.RateLimitConfig{
		MaxRequests: 20,
		Window:      10 * time.Minute,
	}), handlers.LoginHandler(cfg))
	api.POST("/auth/logout", middleware.AuthMiddleware(cfg), handlers.LogoutHandler(cfg))

	return &Server{
		Engine: engine,
		config: cfg,
		db:     database,
	}
}

// Run starts the HTTP server and blocks until shutdown.
func (s *Server) Run(addr string) error {
	if addr == "" {
		addr = ":" + s.config.AppPort
	}

	s.http = &http.Server{
		Addr:         addr,
		Handler:      s.Engine,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 15 * time.Second,
		IdleTimeout:  60 * time.Second,
	}

	fmt.Printf("Server starting on %s (env: %s)\n", addr, s.config.AppEnv)

	// Graceful shutdown
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		<-quit
		fmt.Println("\nShutting down server...")

		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()

		if err := s.http.Shutdown(ctx); err != nil {
			fmt.Printf("Server forced to shutdown: %v\n", err)
		}
	}()

	if err := s.http.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		return fmt.Errorf("server failed: %w", err)
	}

	return nil
}