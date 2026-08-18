package database

import (
	"fmt"
	"sync"

	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"

	"login-system/internal/models"
)

var (
	db   *gorm.DB
	once sync.Once
	mu   sync.RWMutex
)

// InitDB initializes the database connection using the provided DSN.
// If dsn is empty or ":memory:", it uses SQLite in-memory mode.
// It runs auto-migration for registered models.
func InitDB(dsn string) (*gorm.DB, error) {
	var initErr error

	once.Do(func() {
		if dsn == "" {
			dsn = ":memory:"
		}

		config := &gorm.Config{
			Logger: logger.Default.LogMode(logger.Warn),
		}

		if dsn == ":memory:" {
			config.Logger = logger.Default.LogMode(logger.Silent)
		}

		database, err := gorm.Open(sqlite.Open(dsn), config)
		if err != nil {
			initErr = fmt.Errorf("failed to connect to database: %w", err)
			return
		}

		mu.Lock()
		db = database
		mu.Unlock()

		// Auto-migrate models
		if err := database.AutoMigrate(&models.User{}); err != nil {
			initErr = fmt.Errorf("failed to auto migrate: %w", err)
			return
		}
	})

	if initErr != nil {
		return nil, initErr
	}

	return db, nil
}

// GetDB returns the current database instance.
// Returns nil if InitDB has not been called yet.
func GetDB() *gorm.DB {
	mu.RLock()
	defer mu.RUnlock()
	return db
}

// CloseDB closes the underlying database connection.
func CloseDB() error {
	mu.RLock()
	database := db
	mu.RUnlock()

	if database != nil {
		sqlDB, err := database.DB()
		if err != nil {
			return fmt.Errorf("failed to get underlying sql.DB: %w", err)
		}
		return sqlDB.Close()
	}
	return nil
}

// ResetTestDB resets the singleton for testing purposes only.
func ResetTestDB() {
	mu.Lock()
	defer mu.Unlock()
	if db != nil {
		sqlDB, err := db.DB()
		if err == nil {
			_ = sqlDB.Close()
		}
		db = nil
	}
	once = sync.Once{}
}