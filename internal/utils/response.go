package utils

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

// Response represents a standard JSON response.
type Response struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Data    interface{} `json:"data,omitempty"`
}

// Success returns a successful JSON response.
func Success(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, Response{
		Code:    0,
		Message: "success",
		Data:    data,
	})
}

// Error returns an error JSON response.
func Error(c *gin.Context, code int, message string) {
	c.JSON(http.StatusOK, Response{
		Code:    code,
		Message: message,
	})
}

// BadRequest returns a 400 bad request response.
func BadRequest(c *gin.Context, message string) {
	c.JSON(http.StatusBadRequest, Response{
		Code:    400,
		Message: message,
	})
}

// Unauthorized returns a 401 unauthorized response.
func Unauthorized(c *gin.Context) {
	c.JSON(http.StatusUnauthorized, Response{
		Code:    401,
		Message: "unauthorized",
	})
}

// Forbidden returns a 403 forbidden response.
func Forbidden(c *gin.Context) {
	c.JSON(http.StatusForbidden, Response{
		Code:    403,
		Message: "forbidden",
	})
}

// TooManyRequests returns a 429 too many requests response.
func TooManyRequests(c *gin.Context) {
	c.JSON(http.StatusTooManyRequests, Response{
		Code:    429,
		Message: "too many requests",
	})
}
