package config

import (
	"os"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestLoad_DefaultValues(t *testing.T) {
	cfg := Load()
	assert.Equal(t, "development", cfg.AppEnv)
	assert.Equal(t, "8080", cfg.AppPort)
	assert.Equal(t, ":memory:", cfg.DatabaseURL)
	assert.Equal(t, "default-secret-change-in-production", cfg.JWTSecret)
	assert.Equal(t, 2, cfg.JWTExpireHours)
	assert.Equal(t, 168, cfg.JWTRememberExpireHours)
}

func TestLoad_CustomValues(t *testing.T) {
	os.Setenv("APP_ENV", "production")
	os.Setenv("APP_PORT", "3000")
	os.Setenv("JWT_SECRET", "my-secret")
	os.Setenv("JWT_EXPIRE_HOURS", "4")
	defer func() {
		os.Unsetenv("APP_ENV")
		os.Unsetenv("APP_PORT")
		os.Unsetenv("JWT_SECRET")
		os.Unsetenv("JWT_EXPIRE_HOURS")
	}()

	cfg := Load()
	assert.Equal(t, "production", cfg.AppEnv)
	assert.Equal(t, "3000", cfg.AppPort)
	assert.Equal(t, "my-secret", cfg.JWTSecret)
	assert.Equal(t, 4, cfg.JWTExpireHours)
}

func TestIsProduction(t *testing.T) {
	cfg := &Config{AppEnv: "production"}
	assert.True(t, cfg.IsProduction())

	cfg.AppEnv = "development"
	assert.False(t, cfg.IsProduction())
}
