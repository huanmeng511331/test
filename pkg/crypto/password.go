package crypto

import (
	"crypto/rand"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
)

// PasswordHasher defines the interface for password hashing.
type PasswordHasher interface {
	Hash(password string) (string, error)
	Verify(password, hash string) error
}

// SHA256Hasher implements PasswordHasher using SHA256 with salt.
// Note: In production, use bcrypt or Argon2. SHA256+salt is used here
// for environments where external dependencies are not available.
type SHA256Hasher struct {
	SaltLen int
}

// NewSHA256Hasher creates a new SHA256Hasher.
func NewSHA256Hasher(saltLen int) *SHA256Hasher {
	if saltLen <= 0 {
		saltLen = 16
	}
	return &SHA256Hasher{SaltLen: saltLen}
}

// Hash hashes a password using SHA256 with a random salt.
func (h *SHA256Hasher) Hash(password string) (string, error) {
	salt := make([]byte, h.SaltLen)
	if _, err := rand.Read(salt); err != nil {
		return "", fmt.Errorf("generate salt: %w", err)
	}
	saltHex := hex.EncodeToString(salt)

	hash := sha256.Sum256([]byte(saltHex + password))
	hashHex := hex.EncodeToString(hash[:])

	return saltHex + ":" + hashHex, nil
}

// Verify checks if a password matches a hash.
func (h *SHA256Hasher) Verify(password, hashStr string) error {
	parts := make([]string, 0, 2)
	for i := 0; i < len(hashStr); i++ {
		if hashStr[i] == ':' {
			parts = append(parts, hashStr[:i])
			parts = append(parts, hashStr[i+1:])
			break
		}
	}
	if len(parts) != 2 {
		return fmt.Errorf("invalid hash format")
	}

	salt := parts[0]
	hash := sha256.Sum256([]byte(salt + password))
	hashHex := hex.EncodeToString(hash[:])

	if hashHex != parts[1] {
		return fmt.Errorf("password mismatch")
	}
	return nil
}
