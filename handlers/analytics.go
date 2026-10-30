package handlers

import (
	"net/http"
	"test/services"
	"test/utils"

	"github.com/gin-gonic/gin"
)

func AnalyticsHandler(c *gin.Context) {
	dimension := c.Query("dimension")
	startDate := c.Query("startDate")
	endDate := c.Query("endDate")
	endpoint := c.Query("endpoint")

	if dimension == "" {
		utils.Error(c, http.StatusBadRequest, "dimension parameter is required")
		return
	}

	data, total, err := services.GetAnalytics(dimension, startDate, endDate, endpoint)
	if err != nil {
		utils.Error(c, http.StatusInternalServerError, err.Error())
		return
	}

	utils.Success(c, gin.H{
		"dimension": dimension,
		"data":      data,
		"total":     total,
	})
}
