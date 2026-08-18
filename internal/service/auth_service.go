package service

import (
	"fmt"
	"net/http"
	"time"

	"golang.org/x/crypto/bcrypt"

	"login-system/internal/models"
	"login-system/internal/repository"
	"login-system/internal/session"
	"login-system/pkg/crypto"
)

var fakeHash string

func init() {
	fakeHashBytes, err := bcrypt.GenerateFromPassword([]byte("fake"), bcrypt.DefaultCost)
	if err != nil {
		panic(fmt.Sprintf("failed to generate fake hash: %v", err))
	}
	fakeHash = string(fakeHashBytes)
}

// AuthService handles authentication logic.
type AuthService struct {
	userRepo       repository.UserRepository
	sessionManager *session.Manager
	passwordHasher crypto.PasswordHasher
	secureCookie   bool
}

// NewAuthService creates a new AuthService.
func NewAuthService(
	userRepo repository.UserRepository,
	sessionManager *session.Manager,
	passwordHasher crypto.PasswordHasher,
	secureCookie bool,
) *AuthService {
	return &AuthService{
		userRepo:       userRepo,
		sessionManager: sessionManager,
		passwordHasher: passwordHasher,
		secureCookie:   secureCookie,
	}
}

// LoginResult contains the result of a login attempt.
type LoginResult struct {
	Success    bool
	Message    string
	User       *models.User
	Token      string
	StatusCode int
}

// Login attempts to authenticate a user.
func (s *AuthService) Login(account, password string, rememberMe bool, ip, userAgent string) *LoginResult {
	user, err := s.userRepo.GetByAccount(account)
	if err != nil {
		// Perform fake hash comparison to prevent timing attacks
		_ = s.passwordHasher.Verify(password, fakeHash)
		return &LoginResult{
			Success:    false,
			Message:    "账号或密码错误",
			StatusCode: http.StatusUnauthorized,
		}
	}

	// If user doesn't exist, do fake hash comparison to prevent timing attacks
	if user == nil {
		_ = s.passwordHasher.Verify(password, fakeHash)
		return &LoginResult{
			Success:    false,
			Message:    "账号或密码错误",
			StatusCode: http.StatusUnauthorized,
		}
	}

	// Verify password
	if err := s.passwordHasher.Verify(password, user.PasswordHash); err != nil {
		return &LoginResult{
			Success:    false,
			Message:    "账号或密码错误",
			StatusCode: http.StatusUnauthorized,
		}
	}

	// Check if user is disabled
	if user.Status == models.UserStatusDisabled {
		return &LoginResult{
			Success:    false,
			Message:    "账号已被禁用",
			StatusCode: http.StatusForbidden,
		}
	}

	// Create session
	_, token, err := s.sessionManager.CreateSession(user.ID, ip, userAgent, rememberMe)
	if err != nil {
		return &LoginResult{
			Success:    false,
			Message:    "登录失败，请重试",
			StatusCode: http.StatusInternalServerError,
		}
	}

	return &LoginResult{
		Success:    true,
		Message:    "登录成功",
		User:       user,
		Token:      token,
		StatusCode: http.StatusOK,
	}
}

// Logout logs out a user by token.
func (s *AuthService) Logout(token string) error {
	return s.sessionManager.DeleteSession(token)
}

// ValidateSession validates a session token.
func (s *AuthService) ValidateSession(token string) (*models.Session, error) {
	return s.sessionManager.ValidateSession(token)
}

// Register registers a new user (for testing purposes).
func (s *AuthService) Register(account, password string) (*models.User, error) {
	hash, err := s.passwordHasher.Hash(password)
	if err != nil {
		return nil, fmt.Errorf("hash password: %w", err)
	}

	user := &models.User{
		Account:      account,
		PasswordHash: hash,
		Status:       models.UserStatusActive,
		CreatedAt:    time.Now(),
		UpdatedAt:    time.Now(),
	}

	if err := s.userRepo.Create(user); err != nil {
		return nil, fmt.Errorf("create user: %w", err)
	}

	return user, nil
}

// UpdateUser updates a user.
func (s *AuthService) UpdateUser(user *models.User) error {
	return s.userRepo.Update(user)
}
