package models

import "time"

// UserStatus represents the status of a user account.
type UserStatus int

const (
	UserStatusActive   UserStatus = 1
	UserStatusDisabled UserStatus = 2
)

// User represents a user in the system.
type User struct {
	ID           int64      `json:"user_id"`
	Account      string     `json:"account"`
	PasswordHash string     `json:"-"`
	Status       UserStatus `json:"status"`
	CreatedAt    time.Time  `json:"created_at"`
	UpdatedAt    time.Time  `json:"updated_at"`
}
