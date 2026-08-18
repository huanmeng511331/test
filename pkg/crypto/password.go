package crypto

import (
	"fmt"

	"golang.org/x/crypto/bcrypt"
)

// PasswordHasher defines the interface for password hashing.
type PasswordHasher interface {
	Hash(password string) (string, error)
	Verify(password, hash string) error
}

// BcryptHasher implements PasswordHasher using bcrypt.
type BcryptHasher struct {
	Cost int
}

// NewBcryptHasher creates a new BcryptHasher with the given cost.
// If cost is 0, it defaults to bcrypt.DefaultCost (10).
func NewBcryptHasher(cost int) *BcryptHasher {
	if cost <= 0 {
		cost = bcrypt.DefaultCost
	}
	return &BcryptHasher{Cost: cost}
}

// Hash hashes a password using bcrypt.
func (h *BcryptHasher) Hash(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), h.Cost)
	if err != nil {
		return "", fmt.Errorf("generate bcrypt hash: %w", err)
	}
	return string(bytes), nil
}

// Verify checks if a password matches a bcrypt hash.
func (h *BcryptHasher) Verify(password, hash string) error {
	return bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
}
