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
	"login-system/internal/database"
	"login-system/internal/testutil"
	"login-system/internal/utils"
)

func setupAuthTest(t *testing.T) (*gin.Engine, *config.Config) {
	t.Helper()
	err := testutil.SetupTestDB()
	require.NoError(t, err)
	t.Cleanup(testutil.TeardownTestDB)

	cfg := &config.Config{
		JWTSecret:              "test-secret-key-for-jwt-generation",
		JWTExpireHours:         2,
		JWTRememberExpireHours: 168,
	}

	gin.SetMode(gin.TestMode)
	router := gin.New()
	router.POST("/api/v1/auth/login", LoginHandler(cfg))

	return router, cfg
}

func TestLoginHandler_Success(t *testing.T) {
	router, _ := setupAuthTest(t)

	_, err := testutil.CreateTestUser("testuser", "SecureP@ssw0rd")
	require.NoError(t, err)

	body, _ := json.Marshal(map[string]interface{}{
		"username": "testuser",
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
	assert.Equal(t, 0, resp.Code)
	assert.Equal(t, "success", resp.Message)
}

func TestLoginHandler_WrongPassword(t *testing.T) {
	router, _ := setupAuthTest(t)

	_, err := testutil.CreateTestUser("testuser", "SecureP@ssw0rd")
	require.NoError(t, err)

	body, _ := json.Marshal(map[string]interface{}{
		"username": "testuser",
		"password": "WrongPassword",
	})

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var resp utils.Response
	err = json.Unmarshal(w.Body.Bytes(), &resp)
	require.NoError(t, err)
	assert.Equal(t, 10001, resp.Code)
	assert.Equal(t, "账号或密码错误", resp.Message)
}

func TestLoginHandler_AccountNotFound(t *testing.T) {
	router, _ := setupAuthTest(t)

	body, _ := json.Marshal(map[string]interface{}{
		"username": "nonexistent",
		"password": "SecureP@ssw0rd",
	})

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("POST", "/api/v1/auth/login", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	router.ServeHTTP(w, req)

	assert.Equal(t, http.StatusOK, w.Code)

	var resp utils.Response
	err := json.Unmarshal(w.Body.Bytes(), &resp)
	require.NoError(t, err)
	assert.Equal(t, 10001, resp.Code)
	assert.Equal(t, "账号或密码错误", resp.Message)
}

func TestLoginHandler_DisabledAccount(t *testing.T) {
	router, _ := setupAuthTest(t)

	user, err := testutil.CreateTestUser("disableduser", "SecureP@ssw0rd")
	require.NoError(t, err)

	db := database.GetDB()
	user.Status = "disabled"
	db.Save(user)

	body, _ := json.Marshal(map[string]interface{}{
		"username": "disableduser",
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
	assert.Equal(t, 10002, resp.Code)
	assert.Equal(t, "账号已被禁用，请联系管理员", resp.Message)
}
