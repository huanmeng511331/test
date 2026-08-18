package testutil

import (
	"fmt"

	"login-system/internal/database"
	"login-system/internal/models"
	"login-system/internal/utils"
)

// SetupTestDB initializes a test database.
func SetupTestDB() error {
	_, err := database.InitDB(":memory:")
	if err != nil {
		return fmt.Errorf("failed to init test db: %w", err)
	}
	db := database.GetDB()
	if db == nil {
		return fmt.Errorf("database is nil after init")
	}
	return db.AutoMigrate(&models.User{})
}

// TeardownTestDB resets the test database.
func TeardownTestDB() {
	database.ResetTestDB()
}

// CreateTestUser creates a test user with the given credentials.
func CreateTestUser(username, password string) (*models.User, error) {
	hash, err := utils.HashPassword(password)
	if err != nil {
		return nil, fmt.Errorf("failed to hash password: %w", err)
	}

	user := &models.User{
		Username:     username,
		Email:        username + "@example.com",
		PasswordHash: hash,
		DisplayName:  username,
		Status:       "active",
	}

	db := database.GetDB()
	if db == nil {
		return nil, fmt.Errorf("database is nil")
	}

	if err := db.Create(user).Error; err != nil {
		return nil, fmt.Errorf("failed to create user: %w", err)
	}

	return user, nil
}
