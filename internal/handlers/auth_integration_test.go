package handlers

import (
	"bytes"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"login-system/internal/config"
	"login-system/internal/middleware"
	"login-system/internal/testutil"
	"login-system/internal/utils"
)

func TestLoginIntegration_FullFlow(t *testing.T) {
	err := testutil.SetupTestDB()
	require.NoError(t, err)
	defer testutil.TeardownTestDB()

	cfg := &config.Config{
		JWTSecret:              "test-secret-key-for-jwt-generation",
		JWTExpireHours:         2,
		JWTRememberExpireHours: 168,
	}

	gin.SetMode(gin.TestMode)
	router := gin.New()
	router.POST("/api/v1/auth/login", LoginHandler(cfg))
	router.POST("/api/v1/auth/logout", middleware.AuthMiddleware(cfg), LogoutHandler(cfg))

	// Create test user
	_, err = testutil.CreateTestUser("integrationuser", "SecureP@ssw0rd")
	require.NoError(t, err)

	// Step 1: Login
	body, _ := json.Marshal(map[string]interface{}{
		"username": "integrationuser",
		"password": "SecureP@ssw0rd",
	})

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	require.Equal(t, http.StatusOK, w.Code)

	var loginResp utils.Response
	err = json.Unmarshal(w.Body.Bytes(), &loginResp)
	require.NoError(t, err)
	require.Equal(t, 0, loginResp.Code)

	loginData, ok := loginResp.Data.(map[string]interface{})
	require.True(t, ok)
	token, ok := loginData["token"].(string)
	require.True(t, ok)
	require.NotEmpty(t, token)

	// Step 2: Logout
	w2 := httptest.NewRecorder()
	req2, _ := http.NewRequest("POST", "/api/v1/auth/logout", nil)
	req2.Header.Set("Authorization", "Bearer "+token)
	router.ServeHTTP(w2, req2)

	assert.Equal(t, http.StatusOK, w2.Code)
}

func TestLoginIntegration_Lockout(t *testing.T) {
	err := testutil.SetupTestDB()
	require.NoError(t, err)
	defer testutil.TeardownTestDB()

	cfg := &config.Config{
		JWTSecret:              "test-secret-key-for-jwt-generation",
		JWTExpireHours:         2,
		JWTRememberExpireHours: 168,
	}

	gin.SetMode(gin.TestMode)
	router := gin.New()
	router.POST("/api/v1/auth/login", LoginHandler(cfg))

	// Create test user
	_, err = testutil.CreateTestUser("lockoutuser", "SecureP@ssw0rd")
	require.NoError(t, err)

	// Make 5 failed login attempts
	for i := 0; i < 5; i++ {
		body, _ := json.Marshal(map[string]interface{}{
			"username": "lockoutuser",
			"password": "WrongPassword",
		})

		w := httptest.NewRecorder()
		req, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(body))
		req.Header.Set("Content-Type", "application/json")
		router.ServeHTTP(w, req)
	}

	// 6th attempt should be locked
	body, _ := json.Marshal(map[string]interface{}{
		"username": "lockoutuser",
		"password": "SecureP@ssw0rd",
	})

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var resp utils.Response
	err = json.Unmarshal(w.Body.Bytes(), &resp)
	require.NoError(t, err)
	assert.Equal(t, 10003, resp.Code)
}
