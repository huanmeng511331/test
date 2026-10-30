package handlers

import (
	"net/http"
	"test/services"
	"test/utils"

	"github.com/gin-gonic/gin"
)

type HashRequest struct {
	Algorithm string `json:"algorithm" binding:"required"`
	Data      string `json:"data" binding:"required"`
}

type HashResponse struct {
	Algorithm string `json:"algorithm"`
	Original  string `json:"original"`
	Hash      string `json:"hash"`
}

func HashHandler(c *gin.Context) {
	var req HashRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.Error(c, http.StatusBadRequest, "Invalid request: "+err.Error())
		return
	}

	hash, err := services.CalculateHash(req.Algorithm, req.Data)
	if err != nil {
		utils.Error(c, http.StatusBadRequest, err.Error())
		return
	}

	// 记录埋点
	go recordTrack("/api/hash", c)

	utils.Success(c, HashResponse{
		Algorithm: req.Algorithm,
		Original:  req.Data,
		Hash:      hash,
	})
}
