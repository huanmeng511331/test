package middleware

import (
	"net/http"
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

		c.Set("userID", claims.UserID)
		c.Next()
	}
}
