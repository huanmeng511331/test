package dto

import (
	"fmt"
	"login-system/internal/models"
)

// LoginResponse represents a login response.
type LoginResponse struct {
	Code    int        `json:"code"`
	Message string     `json:"message"`
	Data    *LoginData `json:"data,omitempty"`
}

// LoginData contains user info in login response.
type LoginData struct {
	UserID    string `json:"user_id"`
	Nickname  string `json:"nickname"`
	CreatedAt string `json:"created_at"`
}

// LogoutResponse represents a logout response.
type LogoutResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

// NewLoginResponse creates a login response from a user model.
func NewLoginResponse(user *models.User) *LoginResponse {
	return &LoginResponse{
		Code:    0,
		Message: "登录成功",
		Data: &LoginData{
			UserID:    fmt.Sprintf("%d", user.ID),
			Nickname:  user.Account,
			CreatedAt: user.CreatedAt.String(),
		},
	}
}
