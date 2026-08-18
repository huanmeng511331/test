package config

import (
	"os"
	"strconv"
)

// Config holds all configuration for the application.
type Config struct {
	AppEnv               string
	AppPort              string
	DatabaseURL          string
	JWTSecret            string
	JWTExpireHours       int
	JWTRememberExpireHours int
}

// Load reads configuration from environment variables with sensible defaults.
func Load() *Config {
	return &Config{
		AppEnv:                 getEnv("APP_ENV", "development"),
		AppPort:                getEnv("APP_PORT", "8080"),
		DatabaseURL:            getEnv("DATABASE_URL", ":memory:"),
		JWTSecret:              getEnv("JWT_SECRET", "default-secret-change-in-production"),
		JWTExpireHours:         getEnvAsInt("JWT_EXPIRE_HOURS", 2),
		JWTRememberExpireHours: getEnvAsInt("JWT_REMEMBER_EXPIRE_HOURS", 168),
	}
}

// IsProduction returns true if the application is running in production mode.
func (c *Config) IsProduction() bool {
	return c.AppEnv == "production"
}

// getEnv retrieves an environment variable or returns a default value.
func getEnv(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}

// getEnvAsInt retrieves an environment variable as an integer or returns a default value.
func getEnvAsInt(key string, defaultValue int) int {
	if value, exists := os.LookupEnv(key); exists {
		if intVal, err := strconv.Atoi(value); err == nil {
			return intVal
		}
	}
	return defaultValue
}