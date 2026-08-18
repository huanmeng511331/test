package migrate

import (
	"fmt"
	"os"
	"path/filepath"
	"sort"
	"strconv"
	"strings"

	"login-system/internal/db"
)

// Run executes all pending up-migrations from the given directory
// against the provided database. Migration files are expected to follow
// the pattern: <number>_<description>.up.sql and <number>_<description>.down.sql.
func Run(database *db.DB, migrationsDir string) error {
	entries, err := os.ReadDir(migrationsDir)
	if err != nil {
		return fmt.Errorf("read migrations directory: %w", err)
	}

	// Collect up-migration files
	var upFiles []string
	for _, e := range entries {
		if e.IsDir() {
			continue
		}
		if strings.HasSuffix(e.Name(), ".up.sql") {
			upFiles = append(upFiles, e.Name())
		}
	}

	sort.Strings(upFiles)

	// Create a migrations tracking table if not exists
	if _, err := database.Exec(`CREATE TABLE IF NOT EXISTS schema_migrations (
		version TEXT PRIMARY KEY,
		applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
	)`); err != nil {
		return fmt.Errorf("create schema_migrations table: %w", err)
	}

	for _, f := range upFiles {
		// Extract version from filename (e.g., "001" from "001_create_users_table.up.sql")
		version := strings.SplitN(f, "_", 2)[0]

		// Check if already applied
		row, err := database.QueryRow("SELECT COUNT(*) FROM schema_migrations WHERE version = '" + version + "'")
		if err != nil {
			return fmt.Errorf("check migration %s: %w", version, err)
		}
		if row != nil {
			count, _ := strconv.Atoi(row["COUNT(*)"])
			if count > 0 {
				continue
			}
		}

		// Read and execute migration
		content, err := os.ReadFile(filepath.Join(migrationsDir, f))
		if err != nil {
			return fmt.Errorf("read migration file %s: %w", f, err)
		}

		sql := string(content)
		if _, err := database.Exec(sql); err != nil {
			return fmt.Errorf("execute migration %s: %w", f, err)
		}

		// Record migration
		if _, err := database.Exec("INSERT INTO schema_migrations (version) VALUES ('" + version + "')"); err != nil {
			return fmt.Errorf("record migration %s: %w", version, err)
		}

		fmt.Printf("Applied migration: %s\n", f)
	}

	return nil
}