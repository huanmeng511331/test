package main

import (
	"log"

	"test/config"
	"test/database"
	"test/handlers"
	"test/models"

	"github.com/gin-gonic/gin"
)

func main() {
	// 初始化配置
	cfg := config.Load()

	// 初始化数据库
	database.Init(cfg.DatabaseURL)

	// 自动迁移数据库表
	database.DB.AutoMigrate(&models.TrackingRecord{})

	// 创建 Gin 路由
	r := gin.Default()

	// 配置 CORS
	r.Use(func(c *gin.Context) {
		c.Header("Access-Control-Allow-Origin", "*")
		c.Header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
		c.Header("Access-Control-Allow-Headers", "Content-Type, X-Caller-ID, X-Caller-Type, X-Caller-Level, X-Caller-Department")
		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}
		c.Next()
	})

	// 注册路由
	api := r.Group("/api")
	{
		api.GET("/hello", handlers.HelloHandler)
		api.POST("/hash", handlers.HashHandler)
		api.POST("/bubble-sort", handlers.BubbleSortHandler)
		api.POST("/track", handlers.TrackHandler)
		api.GET("/analytics", handlers.AnalyticsHandler)
		api.GET("/export", handlers.ExportHandler)
	}

	// 启动服务
	log.Printf("Server starting on port %s", cfg.ServerPort)
	log.Fatal(r.Run(":" + cfg.ServerPort))
}
