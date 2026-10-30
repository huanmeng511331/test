package handlers

import (
	"net/http"
	"test/database"
	"test/models"
	"test/utils"

	"github.com/gin-gonic/gin"
)

type TrackRequest struct {
	Endpoint         string `json:"endpoint" binding:"required"`
	CallerID         string `json:"callerId"`
	CallerType       string `json:"callerType"`
	CallerLevel      string `json:"callerLevel"`
	CallerDepartment string `json:"callerDepartment"`
}

func TrackHandler(c *gin.Context) {
	var req TrackRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.Error(c, http.StatusBadRequest, "Invalid request: "+err.Error())
		return
	}

	record := models.TrackingRecord{
		Endpoint:         req.Endpoint,
		CallerID:         req.CallerID,
		CallerType:       req.CallerType,
		CallerLevel:      req.CallerLevel,
		CallerDepartment: req.CallerDepartment,
	}

	if err := database.DB.Create(&record).Error; err != nil {
		utils.Error(c, http.StatusInternalServerError, "Failed to record tracking")
		return
	}

	utils.Success(c, gin.H{
		"success":  true,
		"recordId": record.ID,
	})
}
