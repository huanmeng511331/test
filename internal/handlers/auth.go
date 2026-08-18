package handlers

import (
	"net/http"
	"strings"
	"sync"
	"time"

	"github.com/gin-gonic/gin"
	"login-system/internal/auth"
	"login-system/internal/config"
	"login-system/internal/database"
	"login-system/internal/models"
	"login-system/internal/utils"
)

const (
	maxFailedAttempts = 5
	lockoutDuration   = 30 * time.Minute
)

var (
	lockoutTracker = auth.NewFailedAttemptTracker()
	tokenBlacklist = make(map[string]bool)
	blacklistMu    sync.RWMutex
)

// LoginRequest represents the login request body.
type LoginRequest struct {
	Username   string `json:"username" binding:"required"`
	Password   string `json:"password" binding:"required"`
	RememberMe bool   `json:"remember_me"`
	Captcha    string `json:"captcha,omitempty"`
}

// LoginResponse represents the login response.
type LoginResponse struct {
	Token    string    `json:"token"`
	ExpiresAt time.Time `json:"expires_at"`
	User     UserInfo  `json:"user"`
}

// UserInfo represents the user info in responses.
type UserInfo struct {
	ID          string `json:"id"`
	Username    string `json:"username"`
	DisplayName string `json:"display_name"`
}

// LoginHandler handles user login.
func LoginHandler(cfg *config.Config) gin.HandlerFunc {
	return func(c *gin.Context) {
		var req LoginRequest
		if err := c.ShouldBindJSON(&req); err != nil {
			utils.BadRequest(c, "invalid request")
			return
		}

		// Check if user is locked
		if lockoutTracker.IsLocked(req.Username) {
			remaining := lockoutTracker.GetLockoutRemaining(req.Username)
			utils.Error(c, 10003, "账号已被锁定，请 "+remaining.Round(time.Minute).String()+" 后重试")
			return
		}

		db := database.GetDB()
		if db == nil {
			utils.Error(c, 500, "internal server error")
			return
		}

		// Find user by username or email
		var user models.User
		result := db.Where("username = ? OR email = ?", req.Username, req.Username).First(&user)
		if result.Error != nil {
			// Account does not exist - record attempt and return generic error
			lockoutTracker.RecordFailedAttempt(req.Username)
			utils.Error(c, 10001, "账号或密码错误")
			return
		}

		// Check if account is disabled
		if user.IsDisabled() {
			utils.Error(c, 10002, "账号已被禁用，请联系管理员")
			return
		}

		// Verify password
		if err := utils.CheckPassword(req.Password, user.PasswordHash); err != nil {
			// Wrong password - record failed attempt
			count := lockoutTracker.RecordFailedAttempt(req.Username)
			if count >= maxFailedAttempts {
				lockoutTracker.Lock(req.Username, lockoutDuration)
			}
			utils.Error(c, 10001, "账号或密码错误")
			return
		}

		// Reset failed attempts on successful login
		lockoutTracker.ResetFailedAttempts(req.Username)

		// Update last login time
		now := time.Now()
		user.LastLoginAt = &now
		db.Save(&user)

		// Generate token
		token, expiresAt, err := auth.GenerateToken(user.Username, req.RememberMe, cfg)
		if err != nil {
			utils.Error(c, 500, "failed to generate token")
			return
		}

		utils.Success(c, LoginResponse{
			Token:     token,
			ExpiresAt: expiresAt,
			User: UserInfo{
				ID:          string(user.ID),
				Username:    user.Username,
				DisplayName: user.DisplayName,
			},
		})
	}
}

// LogoutHandler handles user logout.
func LogoutHandler(cfg *config.Config) gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			utils.Success(c, nil)
			return
		}

		parts := strings.SplitN(authHeader, " ", 2)
		if len(parts) == 2 && strings.ToLower(parts[0]) == "bearer" {
			blacklistMu.Lock()
			tokenBlacklist[parts[1]] = true
			blacklistMu.Unlock()
		}

		utils.Success(c, nil)
	}
}

// IsTokenBlacklisted checks if a token has been revoked.
func IsTokenBlacklisted(token string) bool {
	blacklistMu.RLock()
	defer blacklistMu.RUnlock()
	return tokenBlacklist[token]
}
