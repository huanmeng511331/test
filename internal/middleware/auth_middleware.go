package middleware

import (
	"context"
	"net/http"

	"login-system/internal/service"
)

// contextKey is a type for context keys.
type contextKey string

const (
	// ContextKeyUserID is the context key for user ID.
	ContextKeyUserID contextKey = "user_id"
	// ContextKeySession is the context key for session.
	ContextKeySession contextKey = "session"
)

// AuthMiddleware creates an authentication middleware.
func AuthMiddleware(authService *service.AuthService) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			w.Header().Set("Content-Type", "application/json")

			cookie, err := r.Cookie("session_id")
			if err != nil || cookie.Value == "" {
				w.WriteHeader(http.StatusUnauthorized)
				w.Write([]byte(`{"code":40101,"message":"登录已过期，请重新登录"}`))
				return
			}

			session, err := authService.ValidateSession(cookie.Value)
			if err != nil {
				w.WriteHeader(http.StatusUnauthorized)
				w.Write([]byte(`{"code":40101,"message":"登录已过期，请重新登录"}`))
				return
			}

			ctx := context.WithValue(r.Context(), ContextKeyUserID, session.UserID)
			ctx = context.WithValue(ctx, ContextKeySession, session)
			next.ServeHTTP(w, r.WithContext(ctx))
		})
	}
}
