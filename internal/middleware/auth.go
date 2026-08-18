package middleware

import (
	"strings"

	"github.com/gin-gonic/gin"
	"login-system/internal/auth"
	"login-system/internal/config"
	"login-system/internal/utils"
)

// AuthMiddleware returns a gin middleware that validates JWT tokens.
func AuthMiddleware(cfg *config.Config) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			utils.Unauthorized(c)
			c.Abort()
			return
		}

		parts := strings.SplitN(authHeader, " ", 2)
		if len(parts) != 2 || strings.ToLower(parts[0]) != "bearer" {
			utils.Unauthorized(c)
			c.Abort()
			return
		}

		tokenString := parts[1]
		claims, err := auth.ValidateToken(tokenString, cfg)
		if err != nil {
			utils.Unauthorized(c)
			c.Abort()
			return
		}

		// Check if token has been blacklisted (logged out)
		if TokenBlacklist != nil && TokenBlacklist.IsBlacklisted(tokenString) {
			utils.Unauthorized(c)
			c.Abort()
			return
		}

		c.Set("userID", claims.UserID)
		c.Next()
	}
}

// TokenBlacklist is the shared token blacklist instance used across the application.
// It is set by the application setup code.
var TokenBlacklist *auth.TokenBlacklist

// SetTokenBlacklist sets the global token blacklist instance for the middleware.
func SetTokenBlacklist(bl *auth.TokenBlacklist) {
	TokenBlacklist = bl
}
