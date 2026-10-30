package config

import "os"

type Config struct {
	ServerPort  string
	DatabaseURL string
}

func Load() *Config {
	port := os.Getenv("SERVER_PORT")
	if port == "" {
		port = "8080"
	}

	dbURL := os.Getenv("DATABASE_URL")
	if dbURL == "" {
		dbURL = "tracking.db"
	}

	return &Config{
		ServerPort:  port,
		DatabaseURL: dbURL,
	}
}
