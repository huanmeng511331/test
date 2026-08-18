package migrate

// Run is a no-op for in-memory mode.
func Run(database interface{}, migrationsDir string) error {
	return nil
}
