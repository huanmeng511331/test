"""统计聚合服务。"""

from __future__ import annotations

from typing import Any

from app.database import Database
from app.models.constants import LEVELS, STATUSES
from app.repositories.work_order_repository import WorkOrderRepository


class StatisticsService:
    """在单一读快照内聚合状态与等级分布。"""

    def __init__(self, database: Database) -> None:
        self.database = database
        self.repository = WorkOrderRepository()

    def aggregate(self) -> dict[str, Any]:
        with self.database.transaction(immediate=False) as connection:
            total = self.repository.count_all(connection)
            status_counts = self.repository.count_group_by_status(connection)
            level_counts = self.repository.count_group_by_fault_level(connection)
        by_status = {status: int(status_counts.get(status, 0)) for status in STATUSES}
        by_fault_level = {level: int(level_counts.get(level, 0)) for level in LEVELS}
        return {"total": total, "by_status": by_status, "by_fault_level": by_fault_level}
