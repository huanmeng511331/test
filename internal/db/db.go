package db

/*
#cgo LDFLAGS: -lsqlite3

#include <stdlib.h>

// SQLite3 constants
#define SQLITE_OK          0
#define SQLITE_ROW         100
#define SQLITE_DONE        101
#define SQLITE_INTEGER     1
#define SQLITE_FLOAT       2
#define SQLITE_TEXT        3
#define SQLITE_NULL        5

// SQLite3 opaque types
typedef struct sqlite3 sqlite3;
typedef struct sqlite3_stmt sqlite3_stmt;

// SQLite3 function declarations
extern int sqlite3_open(const char *filename, sqlite3 **ppDb);
extern int sqlite3_close(sqlite3 *db);
extern const char *sqlite3_errmsg(sqlite3 *db);
extern int sqlite3_exec(sqlite3 *db, const char *sql,
    int (*callback)(void*,int,char**,char**), void *arg, char **errmsg);
extern int sqlite3_prepare_v2(sqlite3 *db, const char *sql, int nByte,
    sqlite3_stmt **ppStmt, const char **pzTail);
extern int sqlite3_finalize(sqlite3_stmt *pStmt);
extern int sqlite3_step(sqlite3_stmt *stmt);
extern int sqlite3_column_count(sqlite3_stmt *pStmt);
extern const char *sqlite3_column_name(sqlite3_stmt *stmt, int N);
extern const unsigned char *sqlite3_column_text(sqlite3_stmt *stmt, int iCol);
extern int sqlite3_bind_int(sqlite3_stmt *stmt, int idx, int value);
extern int sqlite3_bind_int64(sqlite3_stmt *stmt, int idx, long long value);
extern int sqlite3_bind_double(sqlite3_stmt *stmt, int idx, double value);
extern int sqlite3_bind_text(sqlite3_stmt *stmt, int idx, const char *value,
    int n, void (*dtor)(void*));
extern int sqlite3_bind_null(sqlite3_stmt *stmt, int idx);
extern int sqlite3_changes(sqlite3 *db);
extern void sqlite3_free(void *ptr);
typedef long long sqlite3_int64;
*/
import "C"
import (
	"fmt"
	"os"
	"path/filepath"
	"unsafe"
)

// DB wraps a SQLite3 database connection.
type DB struct {
	handle *C.sqlite3
}

// Open opens a SQLite database at the given path.
// If the directory for the database file does not exist, it will be created.
func Open(dsn string) (*DB, error) {
	// Ensure the directory for the database file exists
	dir := filepath.Dir(dsn)
	if dir != "." {
		if err := os.MkdirAll(dir, 0755); err != nil {
			return nil, fmt.Errorf("create database directory: %w", err)
		}
	}

	cPath := C.CString(dsn)
	defer C.free(unsafe.Pointer(cPath))

	var handle *C.sqlite3
	rc := C.sqlite3_open(cPath, &handle)
	if rc != C.SQLITE_OK {
		errMsg := C.GoString(C.sqlite3_errmsg(handle))
		C.sqlite3_close(handle)
		return nil, fmt.Errorf("open sqlite database: %s", errMsg)
	}

	db := &DB{handle: handle}

	// Enable WAL mode for better concurrent performance
	if _, err := db.Exec("PRAGMA journal_mode=WAL"); err != nil {
		db.Close()
		return nil, fmt.Errorf("enable WAL mode: %w", err)
	}

	// Enable foreign keys enforcement
	if _, err := db.Exec("PRAGMA foreign_keys=ON"); err != nil {
		db.Close()
		return nil, fmt.Errorf("enable foreign keys: %w", err)
	}

	return db, nil
}

// Close closes the database connection.
func (db *DB) Close() error {
	if db.handle != nil {
		C.sqlite3_close(db.handle)
		db.handle = nil
	}
	return nil
}

// Exec executes one or more SQL statements separated by semicolons.
// It returns the number of rows affected by the last statement.
func (db *DB) Exec(sql string) (int64, error) {
	cSQL := C.CString(sql)
	defer C.free(unsafe.Pointer(cSQL))

	var cErrMsg *C.char
	rc := C.sqlite3_exec(db.handle, cSQL, nil, nil, &cErrMsg)
	if rc != C.SQLITE_OK {
		errMsg := C.GoString(cErrMsg)
		C.sqlite3_free(unsafe.Pointer(cErrMsg))
		return 0, fmt.Errorf("exec sql: %s", errMsg)
	}

	return int64(C.sqlite3_changes(db.handle)), nil
}

// QueryRow executes a query and returns the first row as a map of column names to string values.
func (db *DB) QueryRow(query string, args ...interface{}) (map[string]string, error) {
	stmt, err := db.prepare(query)
	if err != nil {
		return nil, err
	}
	defer C.sqlite3_finalize(stmt)

	if err := db.bindArgs(stmt, args); err != nil {
		return nil, err
	}

	rc := C.sqlite3_step(stmt)
	if rc == C.SQLITE_ROW {
		return db.rowToMap(stmt), nil
	}
	if rc == C.SQLITE_DONE {
		return nil, nil
	}

	return nil, fmt.Errorf("query row: %s", C.GoString(C.sqlite3_errmsg(db.handle)))
}

// Query executes a query and returns all rows as a slice of maps.
func (db *DB) Query(query string, args ...interface{}) ([]map[string]string, error) {
	stmt, err := db.prepare(query)
	if err != nil {
		return nil, err
	}
	defer C.sqlite3_finalize(stmt)

	if err := db.bindArgs(stmt, args); err != nil {
		return nil, err
	}

	var rows []map[string]string
	for {
		rc := C.sqlite3_step(stmt)
		if rc == C.SQLITE_ROW {
			rows = append(rows, db.rowToMap(stmt))
		} else if rc == C.SQLITE_DONE {
			break
		} else {
			return nil, fmt.Errorf("query: %s", C.GoString(C.sqlite3_errmsg(db.handle)))
		}
	}

	return rows, nil
}

// prepare compiles a SQL statement.
func (db *DB) prepare(sql string) (*C.sqlite3_stmt, error) {
	cSQL := C.CString(sql)
	defer C.free(unsafe.Pointer(cSQL))

	var stmt *C.sqlite3_stmt
	rc := C.sqlite3_prepare_v2(db.handle, cSQL, -1, &stmt, nil)
	if rc != C.SQLITE_OK {
		return nil, fmt.Errorf("prepare: %s", C.GoString(C.sqlite3_errmsg(db.handle)))
	}

	return stmt, nil
}

// bindArgs binds Go arguments to a prepared statement.
func (db *DB) bindArgs(stmt *C.sqlite3_stmt, args []interface{}) error {
	for i, arg := range args {
		idx := C.int(i + 1) // SQLite uses 1-based indexing
		switch v := arg.(type) {
		case nil:
			C.sqlite3_bind_null(stmt, idx)
		case int:
			C.sqlite3_bind_int64(stmt, idx, C.sqlite3_int64(v))
		case int64:
			C.sqlite3_bind_int64(stmt, idx, C.sqlite3_int64(v))
		case float64:
			C.sqlite3_bind_double(stmt, idx, C.double(v))
		case string:
			cStr := C.CString(v)
			C.sqlite3_bind_text(stmt, idx, cStr, C.int(len(v)), (*[0]byte)(C.free))
		case bool:
			if v {
				C.sqlite3_bind_int(stmt, idx, 1)
			} else {
				C.sqlite3_bind_int(stmt, idx, 0)
			}
		default:
			return fmt.Errorf("unsupported type for bind arg %d: %T", i, arg)
		}
	}
	return nil
}

// rowToMap converts the current row of a prepared statement to a map.
func (db *DB) rowToMap(stmt *C.sqlite3_stmt) map[string]string {
	colCount := int(C.sqlite3_column_count(stmt))
	row := make(map[string]string, colCount)
	for i := 0; i < colCount; i++ {
		colName := C.GoString(C.sqlite3_column_name(stmt, C.int(i)))
		colText := C.sqlite3_column_text(stmt, C.int(i))
		if colText != nil {
			row[colName] = C.GoString((*C.char)(unsafe.Pointer(colText)))
		} else {
			row[colName] = ""
		}
	}
	return row
}