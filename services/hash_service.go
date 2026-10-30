package services

import (
	"crypto/md5"
	"crypto/sha256"
	"crypto/sha512"
	"encoding/hex"
	"fmt"
)

func CalculateHash(algorithm, data string) (string, error) {
	var hash []byte

	switch algorithm {
	case "md5":
		h := md5.Sum([]byte(data))
		hash = h[:]
	case "sha256":
		h := sha256.Sum256([]byte(data))
		hash = h[:]
	case "sha512":
		h := sha512.Sum512([]byte(data))
		hash = h[:]
	default:
		return "", fmt.Errorf("unsupported algorithm: %s", algorithm)
	}

	return hex.EncodeToString(hash), nil
}
