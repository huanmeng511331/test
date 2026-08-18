package handler

import (
	"encoding/json"
	"net/http"
	"strings"

	"login-system/internal/dto"
	"login-system/internal/ratelimit"
	"login-system/internal/service"
	"login-system/internal/session"
)

// AuthHandler handles authentication HTTP requests.
type AuthHandler struct {
	authService  *service.AuthService
	rateLimiter  *ratelimit.RateLimiter
	secureCookie bool
}

// NewAuthHandler creates a new AuthHandler.
func NewAuthHandler(authService *service.AuthService, rateLimiter *ratelimit.RateLimiter, secureCookie bool) *AuthHandler {
	return &AuthHandler{
		authService:  authService,
		rateLimiter:  rateLimiter,
		secureCookie: secureCookie,
	}
}

// Login handles login requests.
func (h *AuthHandler) Login(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")

	var req dto.LoginRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		w.WriteHeader(http.StatusBadRequest)
		_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": 40001, "message": "请求参数错误"})
		return
	}

	// Validate input
	if strings.TrimSpace(req.Account) == "" || strings.TrimSpace(req.Password) == "" {
		w.WriteHeader(http.StatusBadRequest)
		_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": 40002, "message": "账号和密码不能为空"})
		return
	}
	if len(req.Account) < 3 || len(req.Account) > 64 {
		w.WriteHeader(http.StatusBadRequest)
		_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": 40003, "message": "账号长度必须在3-64个字符之间"})
		return
	}
	if len(req.Password) < 8 || len(req.Password) > 128 {
		w.WriteHeader(http.StatusBadRequest)
		_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": 40004, "message": "密码长度必须在8-128个字符之间"})
		return
	}

	// Check rate limit (account-based)
	locked, _ := h.rateLimiter.IsLocked(req.Account)
	if locked {
		w.WriteHeader(http.StatusTooManyRequests)
		_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": 42901, "message": "操作过于频繁，请稍后再试"})
		return
	}

	// Get real IP securely (prevents X-Forwarded-For forgery)
	ip := ratelimit.GetClientIP(r)

	result := h.authService.Login(req.Account, req.Password, req.RememberMe, ip, r.UserAgent())

	if !result.Success {
		if result.StatusCode == http.StatusUnauthorized {
			h.rateLimiter.RecordFailure(req.Account)
		}
		w.WriteHeader(result.StatusCode)
		_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": result.StatusCode*100 + 1, "message": result.Message})
		return
	}

	// Reset rate limit on success
	h.rateLimiter.Reset(req.Account)

	// Set cookie
	maxAge := 2 * 60 * 60 // 2 hours
	if req.RememberMe {
		maxAge = 30 * 24 * 60 * 60 // 30 days
	}
	session.SetCookie(w, result.Token, maxAge, h.secureCookie)

	w.WriteHeader(result.StatusCode)
	_ = json.NewEncoder(w).Encode(dto.NewLoginResponse(result.User))
}

// Logout handles logout requests.
func (h *AuthHandler) Logout(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")

	cookie, err := r.Cookie("session_id")
	if err == nil && cookie.Value != "" {
		_ = h.authService.Logout(cookie.Value)
	}

	session.ClearCookie(w, h.secureCookie)

	w.WriteHeader(http.StatusOK)
	_ = json.NewEncoder(w).Encode(map[string]interface{}{"code": 0, "message": "退出成功"})
}
