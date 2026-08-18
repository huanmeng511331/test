package utils

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestHashPasswordAndCheckPassword(t *testing.T) {
	password := "SecureP@ssw0rd"
	hash, err := HashPassword(password)
	require.NoError(t, err)
	require.NotEmpty(t, hash)

	// Correct password should verify
	err = CheckPassword(password, hash)
	assert.NoError(t, err)

	// Wrong password should fail
	err = CheckPassword("wrongpassword", hash)
	assert.Error(t, err)
}

func TestCheckPassword_WrongPassword(t *testing.T) {
	hash, _ := HashPassword("correct")
	err := CheckPassword("wrong", hash)
	assert.Error(t, err)
}

func TestIsValidPassword(t *testing.T) {
	assert.NoError(t, IsValidPassword("12345678"))
	assert.NoError(t, IsValidPassword("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))

	assert.Error(t, IsValidPassword("short"))
	assert.Error(t, IsValidPassword(""))
	assert.Error(t, IsValidPassword("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
}
