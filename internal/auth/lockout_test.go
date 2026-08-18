package auth

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

func TestFailedAttemptTracker(t *testing.T) {
	tracker := NewFailedAttemptTracker()
	key := "test-user"

	// Record failed attempts
	count := tracker.RecordFailedAttempt(key)
	assert.Equal(t, 1, count)

	count = tracker.RecordFailedAttempt(key)
	assert.Equal(t, 2, count)

	// Should not be locked yet
	assert.False(t, tracker.IsLocked(key))

	// Lock the key
	tracker.Lock(key, 30*time.Minute)
	assert.True(t, tracker.IsLocked(key))

	// Remaining should be positive
	remaining := tracker.GetLockoutRemaining(key)
	assert.True(t, remaining > 0)

	// Reset failed attempts
	tracker.ResetFailedAttempts(key)
	assert.False(t, tracker.IsLocked(key))
	assert.Equal(t, time.Duration(0), tracker.GetLockoutRemaining(key))
}

func TestFailedAttemptTracker_UnknownKey(t *testing.T) {
	tracker := NewFailedAttemptTracker()
	assert.False(t, tracker.IsLocked("unknown"))
	assert.Equal(t, time.Duration(0), tracker.GetLockoutRemaining("unknown"))
}
