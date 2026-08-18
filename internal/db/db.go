package db

// DB is a placeholder for database connection.
// In this in-memory implementation, no actual DB connection is needed.
type DB struct{}

// Open is a no-op for in-memory mode.
func Open(dsn string) (*DB, error) {
	return &DB{}, nil
}
