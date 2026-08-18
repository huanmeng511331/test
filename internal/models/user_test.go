package models

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestUser_IsActive(t *testing.T) {
	u := &User{Status: "active"}
	assert.True(t, u.IsActive())

	u.Status = "disabled"
	assert.False(t, u.IsActive())
}

func TestUser_IsDisabled(t *testing.T) {
	u := &User{Status: "disabled"}
	assert.True(t, u.IsDisabled())

	u.Status = "active"
	assert.False(t, u.IsDisabled())
}

func TestUser_IsLocked(t *testing.T) {
	u := &User{}
	assert.False(t, u.IsLocked())

	future := time.Now().Add(time.Hour)
	u.LockedUntil = &future
	assert.True(t, u.IsLocked())

	past := time.Now().Add(-time.Hour)
	u.LockedUntil = &past
	assert.False(t, u.IsLocked())
}

func TestUser_TableName(t *testing.T) {
	u := User{}
	assert.Equal(t, "users", u.TableName())
}
