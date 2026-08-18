package main

import (
	"fmt"
	"os"

	"login-system/internal/config"
	"login-system/internal/database"
	"login-system/internal/server"
)

func main() {
	// Load configuration
	cfg := config.Load()

	// Initialize database
	databaseURL := cfg.DatabaseURL
	if databaseURL == ":memory:" {
		databaseURL = ":memory:"
	}
	db, err := database.InitDB(databaseURL)
	if err != nil {
		fmt.Printf("Failed to initialize database: %v\n", err)
		os.Exit(1)
	}

	// Create and run server
	srv := server.New(cfg, db)
	if err := srv.Run(":" + cfg.AppPort); err != nil {
		fmt.Printf("Server error: %v\n", err)
		os.Exit(1)
	}
}