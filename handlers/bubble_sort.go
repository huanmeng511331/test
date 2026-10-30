package handlers

import (
	"net/http"
	"test/services"
	"test/utils"

	"github.com/gin-gonic/gin"
)

type SortRequest struct {
	Numbers []int `json:"numbers" binding:"required"`
}

type SortResponse struct {
	Original []int `json:"original"`
	Sorted   []int `json:"sorted"`
	Steps    int   `json:"steps"`
}

func BubbleSortHandler(c *gin.Context) {
	var req SortRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.Error(c, http.StatusBadRequest, "Invalid request: "+err.Error())
		return
	}

	sorted, steps := services.BubbleSort(req.Numbers)

	// 记录埋点
	go recordTrack("/api/bubble-sort", c)

	utils.Success(c, SortResponse{
		Original: req.Numbers,
		Sorted:   sorted,
		Steps:    steps,
	})
}
