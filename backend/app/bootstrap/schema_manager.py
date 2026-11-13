"""建表与索引初始化。"""

from __future__ import annotations

from app.database import Database

SCHEMA_STATEMENTS = (
    """
    CREATE TABLE IF NOT EXISTS work_orders (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        order_no TEXT(16) NOT NULL,
        device_name TEXT(50) NOT NULL,
        device_code TEXT(32) NOT NULL,
        fault_description TEXT(500) NOT NULL,
        fault_level TEXT(2) NOT NULL,
        status TEXT(4) NOT NULL DEFAULT '待处理',
        reporter TEXT(20) NOT NULL,
        assignee TEXT(20) NULL,
        solution TEXT(500) NULL,
        created_at TEXT(19) NOT NULL,
        updated_at TEXT(19) NOT NULL
    )
    """,
    "CREATE UNIQUE INDEX IF NOT EXISTS uk_work_order_order_no ON work_orders(order_no)",
    "CREATE INDEX IF NOT EXISTS idx_work_order_status ON work_orders(status)",
    "CREATE INDEX IF NOT EXISTS idx_work_order_level ON work_orders(fault_level)",
    "CREATE INDEX IF NOT EXISTS idx_work_order_created ON work_orders(created_at)",
    """
    CREATE TABLE IF NOT EXISTS work_order_logs (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        work_order_id INTEGER NOT NULL,
        log_type TEXT(8) NOT NULL,
        from_status TEXT(4) NULL,
        to_status TEXT(4) NULL,
        assignee TEXT(20) NULL,
        operator TEXT(20) NOT NULL,
        content TEXT(500) NULL,
        created_at TEXT(19) NOT NULL
    )
    """,
    "CREATE INDEX IF NOT EXISTS idx_wo_log_order_created ON work_order_logs(work_order_id, created_at)",
)


class SchemaManager:
    """以“不存在才创建”的方式初始化表结构。"""

    def __init__(self, database: Database) -> None:
        self.database = database

    def create_schema(self) -> bool:
        with self.database.read() as connection:
            existing_tables = {
                row["name"]
                for row in connection.execute(
                    "SELECT name FROM sqlite_master WHERE type = 'table' "
                    "AND name IN ('work_orders', 'work_order_logs')"
                ).fetchall()
            }
            created = len(existing_tables) < 2
            for statement in SCHEMA_STATEMENTS:
                connection.execute(statement)
            connection.commit()
        return created
