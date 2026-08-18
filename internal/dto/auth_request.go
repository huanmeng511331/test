package dto

// LoginRequest represents a login request.
type LoginRequest struct {
	Account    string `json:"account"`
	Password   string `json:"password"`
	RememberMe bool   `json:"remember_me"`
	Captcha    string `json:"captcha,omitempty"`
}
