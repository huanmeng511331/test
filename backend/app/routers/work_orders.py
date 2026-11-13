"""工单管理接口。"""

from __future__ import annotations

from fastapi import APIRouter, Depends, HTTPException, Path, Query, Response

from app.database import Database
from app.routers.dependencies import get_database
from app.schemas.work_order import (
    WorkOrderCreate,
    WorkOrderDetailOut,
    WorkOrderListOut,
    WorkOrderOut,
    WorkOrderUpdate,
)
from app.services.work_order_service import WorkOrderService

router = APIRouter(prefix="/api/work-orders", tags=["工单管理"])


def _service(database: Database) -> WorkOrderService:
    return WorkOrderService(database)


def _query_error(field: str, message: str) -> HTTPException:
    return HTTPException(
        status_code=422,
        detail=[{"loc": ["query", field], "msg": message, "type": "value_error"}],
    )


@router.get(
    "",
    response_model=WorkOrderListOut,
    summary="查询工单列表",
    description="按状态、故障等级筛选，并按创建时间倒序分页返回。",
)
def list_work_orders(
    status: str | None = Query(None, description="待处理/处理中/已完成/已关闭"),
    fault_level: str | None = Query(None, description="高/中/低"),
    page: int = Query(1, ge=1, description="页码，从 1 开始"),
    page_size: int = Query(10, ge=1, le=100, description="每页条数，1–100"),
    database: Database = Depends(get_database),
) -> dict:
    service = _service(database)
    try:
        return service.list_work_orders(status, fault_level, page, page_size)
    except ValueError as exc:
        field = "status" if "状态" in str(exc) else "fault_level"
        raise _query_error(field, str(exc)) from exc


@router.post(
    "",
    response_model=WorkOrderOut,
    status_code=201,
    summary="创建工单",
    description="校验字段并生成当日唯一工单编号，创建后状态固定为待处理。",
)
def create_work_order(
    payload: WorkOrderCreate,
    response: Response,
    database: Database = Depends(get_database),
) -> dict:
    order = _service(database).create_work_order(payload)
    response.headers["Location"] = f"/api/work-orders/{order['id']}"
    return order


@router.get(
    "/{order_id}",
    response_model=WorkOrderDetailOut,
    summary="查询工单详情",
    description="返回工单完整信息与状态流转、处理记录，记录按时间倒序。",
)
def get_work_order(
    order_id: int = Path(..., ge=1, description="工单主键"),
    database: Database = Depends(get_database),
) -> dict:
    return _service(database).get_work_order_detail(order_id)


@router.patch(
    "/{order_id}/status",
    response_model=WorkOrderOut,
    summary="更新工单状态/人员/处理信息",
    description="分派、开始处理、追加处理说明、完成与关闭的统一入口，按当前库中状态校验。",
)
def update_work_order_status(
    payload: WorkOrderUpdate,
    order_id: int = Path(..., ge=1, description="工单主键"),
    database: Database = Depends(get_database),
) -> dict:
    return _service(database).apply_transition(order_id, payload)
