package db_test

import (
	"testing"

	"login-system/internal/db"
	"login-system/internal/migrate"
)

func TestMigrationsAndSchema(t *testing.T) {
	// Use a temporary database file
	tmpFile := t.TempDir() + "/test.db"

	// Open database
	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	// Run migrations
	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("failed to run migrations: %v", err)
	}

	// Verify users table exists
	tables, err := database.Query("SELECT name FROM sqlite_master WHERE type='table' AND name='users'")
	if err != nil {
		t.Fatalf("failed to query tables: %v", err)
	}
	if len(tables) == 0 {
		t.Fatal("users table not found after migration")
	}

	// Verify sessions table exists
	tables, err = database.Query("SELECT name FROM sqlite_master WHERE type='table' AND name='sessions'")
	if err != nil {
		t.Fatalf("failed to query tables: %v", err)
	}
	if len(tables) == 0 {
		t.Fatal("sessions table not found after migration")
	}

	// Verify schema_migrations table exists
	tables, err = database.Query("SELECT name FROM sqlite_master WHERE type='table' AND name='schema_migrations'")
	if err != nil {
		t.Fatalf("failed to query tables: %v", err)
	}
	if len(tables) == 0 {
		t.Fatal("schema_migrations table not found")
	}

	// Verify users table columns
	columns, err := database.Query("PRAGMA table_info(users)")
	if err != nil {
		t.Fatalf("failed to get users table info: %v", err)
	}
	colMap := make(map[string]bool)
	for _, col := range columns {
		colMap[col["name"]] = true
	}

	expectedUserCols := []string{"id", "account", "password_hash", "status", "created_at", "updated_at"}
	for _, col := range expectedUserCols {
		if !colMap[col] {
			t.Errorf("users table missing column: %s", col)
		}
	}

	// Verify sessions table columns
	columns, err = database.Query("PRAGMA table_info(sessions)")
	if err != nil {
		t.Fatalf("failed to get sessions table info: %v", err)
	}
	colMap = make(map[string]bool)
	for _, col := range columns {
		colMap[col["name"]] = true
	}

	expectedSessionCols := []string{"id", "user_id", "token", "expires_at", "ip", "user_agent", "created_at"}
	for _, col := range expectedSessionCols {
		if !colMap[col] {
			t.Errorf("sessions table missing column: %s", col)
		}
	}
}

func TestUsersCRUD(t *testing.T) {
	tmpFile := t.TempDir() + "/test.db"

	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("failed to run migrations: %v", err)
	}

	// Insert a user
	_, err = database.Exec("INSERT INTO users (account, password_hash, status) VALUES ('testuser', 'hashedpassword123', 1)")
	if err != nil {
		t.Fatalf("failed to insert user: %v", err)
	}

	// Query the user
	row, err := database.QueryRow("SELECT id, account, password_hash, status FROM users WHERE account = 'testuser'")
	if err != nil {
		t.Fatalf("failed to query user: %v", err)
	}
	if row == nil {
		t.Fatal("user not found")
	}
	if row["account"] != "testuser" {
		t.Errorf("expected account 'testuser', got '%s'", row["account"])
	}
	if row["password_hash"] != "hashedpassword123" {
		t.Errorf("expected password_hash 'hashedpassword123', got '%s'", row["password_hash"])
	}
	if row["status"] != "1" {
		t.Errorf("expected status '1', got '%s'", row["status"])
	}

	// Test unique constraint on account
	_, err = database.Exec("INSERT INTO users (account, password_hash, status) VALUES ('testuser', 'anotherhash', 1)")
	if err == nil {
		t.Fatal("expected error for duplicate account, got nil")
	}

	// Query all users
	rows, err := database.Query("SELECT COUNT(*) as cnt FROM users")
	if err != nil {
		t.Fatalf("failed to count users: %v", err)
	}
	if len(rows) != 1 || rows[0]["cnt"] != "1" {
		t.Errorf("expected 1 user, got %v", rows)
	}
}

func TestSessionsCRUD(t *testing.T) {
	tmpFile := t.TempDir() + "/test.db"

	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("failed to run migrations: %v", err)
	}

	// First create a user
	_, err = database.Exec("INSERT INTO users (account, password_hash, status) VALUES ('testuser', 'hash', 1)")
	if err != nil {
		t.Fatalf("failed to insert user: %v", err)
	}

	// Get the user id
	userRow, err := database.QueryRow("SELECT id FROM users WHERE account = 'testuser'")
	if err != nil {
		t.Fatalf("failed to get user id: %v", err)
	}
	userID := userRow["id"]

	// Insert a session
	_, err = database.Exec("INSERT INTO sessions (user_id, token, expires_at, ip, user_agent) VALUES (" +
		userID + ", 'sessiontoken123', '2025-12-31 23:59:59', '127.0.0.1', 'test-agent')")
	if err != nil {
		t.Fatalf("failed to insert session: %v", err)
	}

	// Query the session
	session, err := database.QueryRow("SELECT id, user_id, token, expires_at, ip, user_agent FROM sessions WHERE token = 'sessiontoken123'")
	if err != nil {
		t.Fatalf("failed to query session: %v", err)
	}
	if session == nil {
		t.Fatal("session not found")
	}
	if session["user_id"] != userID {
		t.Errorf("expected user_id '%s', got '%s'", userID, session["user_id"])
	}
	if session["token"] != "sessiontoken123" {
		t.Errorf("expected token 'sessiontoken123', got '%s'", session["token"])
	}
	if session["ip"] != "127.0.0.1" {
		t.Errorf("expected ip '127.0.0.1', got '%s'", session["ip"])
	}
	if session["user_agent"] != "test-agent" {
		t.Errorf("expected user_agent 'test-agent', got '%s'", session["user_agent"])
	}
}

func TestForeignKeyConstraint(t *testing.T) {
	tmpFile := t.TempDir() + "/test.db"

	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("failed to run migrations: %v", err)
	}

	// Attempt to insert a session with a non-existent user_id
	_, err = database.Exec("INSERT INTO sessions (user_id, token, expires_at) VALUES (999, 'invalidtoken', '2025-12-31')")
	if err == nil {
		t.Fatal("expected foreign key error for non-existent user_id, got nil")
	}
}

func TestDownMigrations(t *testing.T) {
	tmpFile := t.TempDir() + "/test.db"

	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	// Run up migrations
	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("failed to run migrations: %v", err)
	}

	// Run down migrations manually
	_, err = database.Exec("DROP TABLE IF EXISTS sessions")
	if err != nil {
		t.Fatalf("failed to drop sessions table: %v", err)
	}
	_, err = database.Exec("DROP TABLE IF EXISTS users")
	if err != nil {
		t.Fatalf("failed to drop users table: %v", err)
	}

	// Verify tables are gone
	tables, err := database.Query("SELECT name FROM sqlite_master WHERE type='table' AND name IN ('users', 'sessions')")
	if err != nil {
		t.Fatalf("failed to query tables: %v", err)
	}
	if len(tables) != 0 {
		t.Fatal("tables still exist after down migration")
	}
}

func TestIdempotency(t *testing.T) {
	tmpFile := t.TempDir() + "/test.db"

	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	// Run migrations twice
	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("first migration run failed: %v", err)
	}
	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("second migration run failed: %v", err)
	}

	// Verify tables exist
	tables, err := database.Query("SELECT name FROM sqlite_master WHERE type='table' AND name='users'")
	if err != nil {
		t.Fatalf("failed to query tables: %v", err)
	}
	if len(tables) == 0 {
		t.Fatal("users table not found after second migration")
	}
}

func TestDefaultValues(t *testing.T) {
	tmpFile := t.TempDir() + "/test.db"

	database, err := db.Open(tmpFile)
	if err != nil {
		t.Fatalf("failed to open database: %v", err)
	}
	defer database.Close()

	if err := migrate.Run(database, "../migrations"); err != nil {
		t.Fatalf("failed to run migrations: %v", err)
	}

	// Insert user with minimal fields to test defaults
	_, err = database.Exec("INSERT INTO users (account, password_hash) VALUES ('defaulttest', 'hash')")
	if err != nil {
		t.Fatalf("failed to insert user: %v", err)
	}

	// Verify defaults
	row, err := database.QueryRow("SELECT status, created_at, updated_at FROM users WHERE account = 'defaulttest'")
	if err != nil {
		t.Fatalf("failed to query user: %v", err)
	}
	if row == nil {
		t.Fatal("user not found")
	}
	if row["status"] != "1" {
		t.Errorf("expected default status '1', got '%s'", row["status"])
	}
	if row["created_at"] == "" {
		t.Error("expected created_at to have a default value")
	}
	if row["updated_at"] == "" {
		t.Error("expected updated_at to have a default value")
	}
}