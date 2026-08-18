package auth

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"login-system/internal/config"
)

func TestGenerateAndValidateToken(t *testing.T) {
	cfg := &config.Config{
		JWTSecret:              "test-secret",
		JWTExpireHours:         2,
		JWTRememberExpireHours: 168,
	}

	token, expiresAt, err := GenerateToken("user-123", false, cfg)
	require.NoError(t, err)
	require.NotEmpty(t, token)
	assert.True(t, expiresAt.After(time.Now()))

	claims, err := ValidateToken(token, cfg)
	require.NoError(t, err)
	assert.Equal(t, "user-123", claims.UserID)
}

func TestValidateToken_Invalid(t *testing.T) {
	cfg := &config.Config{JWTSecret: "test-secret"}

	_, err := ValidateToken("invalid.token.here", cfg)
	assert.Error(t, err)
}

func TestValidateToken_Expired(t *testing.T) {
	cfg := &config.Config{
		JWTSecret:      "test-secret",
		JWTExpireHours: -1,
	}

	token, _, err := GenerateToken("user-123", false, cfg)
	require.NoError(t, err)

	_, err = ValidateToken(token, cfg)
	assert.Error(t, err)
}

func TestGenerateToken_RememberMe(t *testing.T) {
	cfg := &config.Config{
		JWTSecret:              "test-secret",
		JWTExpireHours:         2,
		JWTRememberExpireHours: 168,
	}

	_, expiresAt, err := GenerateToken("user-123", true, cfg)
	require.NoError(t, err)
	assert.True(t, expiresAt.After(time.Now().Add(24*time.Hour)))
}
