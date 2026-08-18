package models

import (
	"time"

	"gorm.io/gorm"
)

// User represents a user account in the system.
type User struct {
	ID               uint           `json:"id" gorm:"primaryKey"`
	Username         string         `json:"username" gorm:"uniqueIndex;size:50;not null"`
	Email            string         `json:"email" gorm:"uniqueIndex;size:100;not null"`
	PasswordHash     string         `json:"-" gorm:"size:255;not null"`
	DisplayName      string         `json:"display_name" gorm:"size:100"`
	Status           string         `json:"status" gorm:"size:20;default:'active'"`
	FailedLoginCount int            `json:"-" gorm:"default:0"`
	LockedUntil      *time.Time     `json:"-"`
	LastLoginAt      *time.Time     `json:"last_login_at"`
	CreatedAt        time.Time      `json:"created_at"`
	UpdatedAt        time.Time      `json:"updated_at"`
	DeletedAt        gorm.DeletedAt `json:"-" gorm:"index"`
}

// TableName returns the table name for User.
func (User) TableName() string {
	return "users"
}

// IsActive returns true if the user status is active.
func (u *User) IsActive() bool {
	return u.Status == "active"
}

// IsDisabled returns true if the user status is disabled.
func (u *User) IsDisabled() bool {
	return u.Status == "disabled"
}

// IsLocked returns true if the user is currently locked.
func (u *User) IsLocked() bool {
	if u.LockedUntil == nil {
		return false
	}
	return time.Now().Before(*u.LockedUntil)
}
