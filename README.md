# Login System

A secure user login subsystem built with Go, implementing authentication with password hashing, session management, rate limiting, and cookie-based session persistence.

## Features

- **Account/Password Authentication**: Secure login with username and password
- **Password Hashing**: bcrypt with configurable cost
- **Session Management**: Cookie-based sessions with configurable expiration
- **Rate Limiting**: IP-based and account-based rate limiting to prevent brute force attacks
- **Security**: HttpOnly, Secure, SameSite cookies; timing-attack resistant login flow
- **Protected Endpoints**: Middleware-based authentication for protected routes

## API Endpoints

### POST /api/v1/auth/login

Login with account and password.

**Request:**
```json
{
  "account": "user@example.com",
  "password": "YourPass123!",
  "remember_me": false
}
```

**Response (Success):**
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "user_id": "1",
    "nickname": "user@example.com",
    "created_at": "..."
  }
}
```

**Response (Failure):**
```json
{
  "code": 40101,
  "message": "账号或密码错误"
}
```

### POST /api/v1/auth/logout

Logout and clear session cookie.

**Response:**
```json
{
  "code": 0,
  "message": "退出成功"
}
```

### GET /api/v1/me

Protected endpoint example (requires valid session).

## Running the Server

```bash
# Set the port (default: 8080)
export PORT=8080

# Run the server
go run cmd/server/main.go
```

## Running Tests

```bash
go test ./tests/integration/ -v
```

## Project Structure

```
.
├── cmd/server/main.go              # Entry point
├── internal/
│   ├── db/                         # Database (in-memory placeholder)
│   ├── dto/                        # Request/Response DTOs
│   ├── handler/                    # HTTP handlers
│   ├── middleware/                 # Auth and rate limit middleware
│   ├── models/                     # Domain models
│   ├── ratelimit/                  # Rate limiting implementation
│   ├── repository/                 # Data access layer (in-memory)
│   ├── service/                    # Business logic
│   └── session/                    # Session management
├── migrations/                     # Database migrations
├── pkg/crypto/                     # Password hashing
└── tests/integration/              # Integration tests
```

## Architecture

The system is designed with clean architecture principles:

- **Handlers**: HTTP request/response handling
- **Services**: Business logic and authentication flow
- **Repositories**: Data persistence abstraction
- **Middleware**: Cross-cutting concerns (auth, rate limiting)
- **Session Manager**: Cookie and session lifecycle management

## Security Considerations

- Passwords are hashed with bcrypt (configurable cost)
- Login responses are timing-attack resistant (fake hash comparison for non-existent accounts)
- Sessions use HttpOnly, Secure, and SameSite=Lax cookies
- Rate limiting prevents brute force attacks on login endpoint
- Account enumeration is prevented by returning identical messages for invalid accounts and invalid passwords

## Production Recommendations

- Use a persistent database (PostgreSQL/MySQL) instead of in-memory storage
- Enable HTTPS in production
- Set `Secure` cookie flag to true in production
- Implement CAPTCHA for repeated failed login attempts
- Add logging and monitoring
