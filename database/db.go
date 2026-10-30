package database

import (
	"log"

	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var DB *gorm.DB

func Init(databaseURL string) {
	var err error
	DB, err = gorm.Open(sqlite.Open(databaseURL), &gorm.Config{})
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}

	// 启用 WAL 模式以提高并发性能
	DB.Exec("PRAGMA journal_mode=WAL")

	log.Println("Database connected successfully")
}
