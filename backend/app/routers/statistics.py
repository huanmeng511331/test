"""工单统计接口。"""

from __future__ import annotations

from fastapi import APIRouter, Depends

from app.database import Database
from app.routers.dependencies import get_database
from app.schemas.statistics import StatisticsOut
from app.services.statistics_service import StatisticsService

router = APIRouter(prefix="/api/work-orders", tags=["工单统计"])


@router.get(
    "/statistics",
    response_model=StatisticsOut,
    summary="查询工单统计",
    description="按全量工单返回总数、状态数量与故障等级分布。",
)
def get_statistics(database: Database = Depends(get_database)) -> dict:
    return StatisticsService(database).aggregate()
