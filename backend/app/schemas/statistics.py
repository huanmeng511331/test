"""统计响应模型。"""

from __future__ import annotations

from pydantic import BaseModel


class StatisticsOut(BaseModel):
    total: int
    by_status: dict[str, int]
    by_fault_level: dict[str, int]
