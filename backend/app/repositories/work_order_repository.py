"""工单与工单记录仓储，SQL 全部使用参数绑定。"""

from __future__ import annotations

import sqlite3
from typing import Any

LIST_COLUMNS = (
    "id, order_no, device_name, device_code, fault_level, status, reporter, assignee, created_at, updated_at"
)
ORDER_COLUMNS = (
    "id, order_no, device_name, device_code, fault_description, fault_level, status, "
    "reporter, assignee, solution, created_at, updated_at"
)
LOG_COLUMNS = "id, log_type, from_status, to_status, assignee, operator, content, created_at"
UPDATABLE_COLUMNS = {"status", "assignee", "solution", "updated_at"}


def _row_to_dict(row: sqlite3.Row | None) -> dict[str, Any] | None:
    return dict(row) if row is not None else None


class WorkOrderRepository:
    """工单主表与记录表的读写方法。"""

    def insert_order(self, connection: sqlite3.Connection, order: dict[str, Any]) -> int:
        cursor = connection.execute(
            """
            INSERT INTO work_orders (
                order_no, device_name, device_code, fault_description, fault_level, status,
                reporter, assignee, solution, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            (
                order["order_no"],
                order["device_name"],
                order["device_code"],
                order["fault_description"],
                order["fault_level"],
                order["status"],
                order["reporter"],
                order.get("assignee"),
                order.get("solution"),
                order["created_at"],
                order["updated_at"],
            ),
        )
        return int(cursor.lastrowid)

    def update_order_if_status(
        self,
        connection: sqlite3.Connection,
        order_id: int,
        expected_status: str,
        fields: dict[str, Any],
    ) -> int:
        assignments: list[str] = []
        values: list[Any] = []
        for column, value in fields.items():
            if column not in UPDATABLE_COLUMNS:
                raise ValueError(f"不允许更新的字段：{column}")
            assignments.append(f"{column} = ?")
            values.append(value)
        if not assignments:
            raise ValueError("更新字段不能为空")
        values.extend([order_id, expected_status])
        cursor = connection.execute(
            f"UPDATE work_orders SET {', '.join(assignments)} WHERE id = ? AND status = ?",
            values,
        )
        return int(cursor.rowcount)

    def find_by_id(self, connection: sqlite3.Connection, order_id: int) -> dict[str, Any] | None:
        row = connection.execute(
            f"SELECT {ORDER_COLUMNS} FROM work_orders WHERE id = ?",
            (order_id,),
        ).fetchone()
        return _row_to_dict(row)

    def find_page(
        self,
        connection: sqlite3.Connection,
        status: str | None,
        fault_level: str | None,
        offset: int,
        limit: int,
    ) -> list[dict[str, Any]]:
        where_sql, params = self._build_filter(status, fault_level)
        rows = connection.execute(
            f"""
            SELECT {LIST_COLUMNS} FROM work_orders
            {where_sql}
            ORDER BY created_at DESC, id DESC
            LIMIT ? OFFSET ?
            """,
            (*params, limit, offset),
        ).fetchall()
        return [dict(row) for row in rows]

    def count_by_filter(
        self,
        connection: sqlite3.Connection,
        status: str | None,
        fault_level: str | None,
    ) -> int:
        where_sql, params = self._build_filter(status, fault_level)
        row = connection.execute(
            f"SELECT COUNT(*) AS total FROM work_orders {where_sql}",
            params,
        ).fetchone()
        return int(row["total"])

    @staticmethod
    def _build_filter(status: str | None, fault_level: str | None) -> tuple[str, list[Any]]:
        clauses: list[str] = []
        params: list[Any] = []
        if status:
            clauses.append("status = ?")
            params.append(status)
        if fault_level:
            clauses.append("fault_level = ?")
            params.append(fault_level)
        where_sql = f"WHERE {' AND '.join(clauses)}" if clauses else ""
        return where_sql, params

    def find_logs_by_order_id(
        self,
        connection: sqlite3.Connection,
        order_id: int,
    ) -> list[dict[str, Any]]:
        rows = connection.execute(
            f"""
            SELECT {LOG_COLUMNS} FROM work_order_logs
            WHERE work_order_id = ?
            ORDER BY created_at DESC, id DESC
            """,
            (order_id,),
        ).fetchall()
        return [dict(row) for row in rows]

    def insert_log(self, connection: sqlite3.Connection, log: dict[str, Any]) -> int:
        cursor = connection.execute(
            """
            INSERT INTO work_order_logs (
                work_order_id, log_type, from_status, to_status, assignee, operator, content, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """,
            (
                log["work_order_id"],
                log["log_type"],
                log.get("from_status"),
                log.get("to_status"),
                log.get("assignee"),
                log["operator"],
                log.get("content"),
                log["created_at"],
            ),
        )
        return int(cursor.lastrowid)

    def max_order_no_by_prefix(self, connection: sqlite3.Connection, prefix: str) -> str | None:
        row = connection.execute(
            "SELECT MAX(order_no) AS max_no FROM work_orders WHERE order_no LIKE ?",
            (f"{prefix}%",),
        ).fetchone()
        return row["max_no"] if row and row["max_no"] else None

    def count_all(self, connection: sqlite3.Connection) -> int:
        row = connection.execute("SELECT COUNT(*) AS total FROM work_orders").fetchone()
        return int(row["total"])

    def count_group_by_status(self, connection: sqlite3.Connection) -> dict[str, int]:
        rows = connection.execute(
            "SELECT status, COUNT(*) AS total FROM work_orders GROUP BY status"
        ).fetchall()
        return {row["status"]: int(row["total"]) for row in rows}

    def count_group_by_fault_level(self, connection: sqlite3.Connection) -> dict[str, int]:
        rows = connection.execute(
            "SELECT fault_level, COUNT(*) AS total FROM work_orders GROUP BY fault_level"
        ).fetchall()
        return {row["fault_level"]: int(row["total"]) for row in rows}

    def count_logs(self, connection: sqlite3.Connection) -> int:
        row = connection.execute("SELECT COUNT(*) AS total FROM work_order_logs").fetchone()
        return int(row["total"])

    def exists_seed_data(self, connection: sqlite3.Connection) -> bool:
        row = connection.execute("SELECT 1 FROM work_orders LIMIT 1").fetchone()
        return row is not None
