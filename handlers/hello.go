package handlers

import (
	"fmt"

	"test/database"
	"test/models"
	"test/utils"

	"github.com/gin-gonic/gin"
)

func HelloHandler(c *gin.Context) {
	name := c.DefaultQuery("name", "World")

	// 记录埋点
	go recordTrack("/api/hello", c)

	utils.Success(c, gin.H{
		"message": fmt.Sprintf("Hello, %s!", name),
	})
}

func recordTrack(endpoint string, c *gin.Context) {
	callerID := c.GetHeader("X-Caller-ID")
	if callerID == "" {
		callerID = "anonymous"
	}

	record := models.TrackingRecord{
		Endpoint:         endpoint,
		CallerID:         callerID,
		CallerType:       c.GetHeader("X-Caller-Type"),
		CallerLevel:      c.GetHeader("X-Caller-Level"),
		CallerDepartment: c.GetHeader("X-Caller-Department"),
	}

	database.DB.Create(&record)
}
