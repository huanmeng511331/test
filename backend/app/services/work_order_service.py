"""工单查询、创建与状态流转的唯一业务实现。"""

from __future__ import annotations

import logging
import sqlite3
from dataclasses import dataclass
from datetime import date, datetime
from typing import Any

from app.config import DEFAULT_OPERATOR
from app.database import Database
from app.errors import (
    AssigneeRequiredError,
    InvalidTransitionError,
    OrderClosedError,
    OrderNoConflictError,
    OrderNotFoundError,
    SolutionRequiredError,
)
from app.models.constants import (
    LEVELS,
    MAX_ORDER_SEQ,
    ORDER_NO_PREFIX,
    ORDER_NO_RETRY_TIMES,
    ORDER_NO_SEQ_LENGTH,
    STATUS_CLOSED,
    STATUS_COMPLETED,
    STATUS_PENDING,
    STATUS_PROCESSING,
    STATUSES,
)
from app.repositories.work_order_repository import WorkOrderRepository
from app.schemas.work_order import WorkOrderCreate, WorkOrderUpdate

logger = logging.getLogger("work_order.service")

LEGAL_TRANSITIONS: dict[str, str] = {
    STATUS_PENDING: STATUS_PROCESSING,
    STATUS_PROCESSING: STATUS_COMPLETED,
    STATUS_COMPLETED: STATUS_CLOSED,
}


def now_text() -> str:
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")


@dataclass
class TransitionPlan:
    """一次状态流转计算出的最终字段与待写记录。"""

    fields: dict[str, Any]
    status_log: dict[str, Any] | None
    handling_log: dict[str, Any] | None


class WorkOrderService:
    """工单全生命周期服务。"""

    def __init__(self, database: Database) -> None:
        self.database = database
        self.repository = WorkOrderRepository()

    def list_work_orders(
        self,
        status: str | None,
        fault_level: str | None,
        page: int,
        page_size: int,
    ) -> dict[str, Any]:
        normalized_status = self._normalize_filter(status, STATUSES, "状态")
        normalized_level = self._normalize_filter(fault_level, LEVELS, "故障等级")
        offset = (page - 1) * page_size
        with self.database.read() as connection:
            total = self.repository.count_by_filter(connection, normalized_status, normalized_level)
            items = self.repository.find_page(
                connection,
                normalized_status,
                normalized_level,
                offset,
                page_size,
            )
        return {"total": total, "page": page, "page_size": page_size, "items": items}

    def create_work_order(self, payload: WorkOrderCreate) -> dict[str, Any]:
        operator = payload.operator or DEFAULT_OPERATOR
        for attempt in range(ORDER_NO_RETRY_TIMES + 1):
            try:
                with self.database.transaction() as connection:
                    timestamp = now_text()
                    order_no = self.generate_order_no(connection, date.today())
                    order_id = self.repository.insert_order(
                        connection,
                        {
                            "order_no": order_no,
                            "device_name": payload.device_name,
                            "device_code": payload.device_code,
                            "fault_description": payload.fault_description,
                            "fault_level": payload.fault_level,
                            "status": STATUS_PENDING,
                            "reporter": payload.reporter,
                            "assignee": None,
                            "solution": None,
                            "created_at": timestamp,
                            "updated_at": timestamp,
                        },
                    )
                    self.repository.insert_log(
                        connection,
                        {
                            "work_order_id": order_id,
                            "log_type": "status",
                            "from_status": None,
                            "to_status": STATUS_PENDING,
                            "assignee": None,
                            "operator": operator,
                            "content": "创建工单",
                            "created_at": timestamp,
                        },
                    )
                    order = self.repository.find_by_id(connection, order_id)
                    logger.info("创建工单成功 order_no=%s operator=%s", order_no, operator)
                    return order or {}
            except sqlite3.IntegrityError as exc:
                if "order_no" not in str(exc) or attempt >= ORDER_NO_RETRY_TIMES:
                    logger.warning("工单编号冲突且重试失败 order_no=%s", "unknown")
                    raise OrderNoConflictError() from exc
                logger.warning("工单编号冲突，重新生成后重试第 %s 次", attempt + 1)
        raise OrderNoConflictError()

    def get_work_order_detail(self, order_id: int) -> dict[str, Any]:
        with self.database.read() as connection:
            order = self.repository.find_by_id(connection, order_id)
            if order is None:
                raise OrderNotFoundError()
            order["logs"] = self.repository.find_logs_by_order_id(connection, order_id)
            return order

    def apply_transition(self, order_id: int, command: WorkOrderUpdate) -> dict[str, Any]:
        operator = command.operator or DEFAULT_OPERATOR
        with self.database.transaction() as connection:
            order = self.repository.find_by_id(connection, order_id)
            if order is None:
                raise OrderNotFoundError()
            if order["status"] == STATUS_CLOSED:
                raise OrderClosedError()
            plan = self._build_plan(order, command)
            affected = self.repository.update_order_if_status(
                connection,
                order_id,
                order["status"],
                plan.fields,
            )
            if affected == 0:
                latest = self.repository.find_by_id(connection, order_id)
                if latest is not None and latest["status"] == STATUS_CLOSED:
                    raise OrderClosedError()
                raise InvalidTransitionError("当前状态已变化，请刷新后重试")
            if plan.handling_log is not None:
                self.repository.insert_log(connection, plan.handling_log)
            if plan.status_log is not None:
                self.repository.insert_log(connection, plan.status_log)
            updated = self.repository.find_by_id(connection, order_id)
            logger.info(
                "工单状态更新成功 order_no=%s from=%s to=%s operator=%s",
                order["order_no"],
                order["status"],
                plan.fields.get("status", order["status"]),
                operator,
            )
            return updated or {}

    def generate_order_no(self, connection: sqlite3.Connection, business_date: date) -> str:
        prefix = f"{ORDER_NO_PREFIX}{business_date.strftime('%Y%m%d')}"
        max_order_no = self.repository.max_order_no_by_prefix(connection, prefix)
        next_seq = 1
        if max_order_no:
            suffix = max_order_no[len(prefix) :]
            if len(suffix) == ORDER_NO_SEQ_LENGTH and suffix.isdigit():
                next_seq = int(suffix) + 1
        if next_seq > MAX_ORDER_SEQ:
            raise OrderNoConflictError("当日工单流水已用尽")
        return f"{prefix}{next_seq:0{ORDER_NO_SEQ_LENGTH}d}"

    def _build_plan(self, order: dict[str, Any], command: WorkOrderUpdate) -> TransitionPlan:
        current = order["status"]
        timestamp = now_text()
        fields: dict[str, Any] = {"updated_at": timestamp}
        status_log: dict[str, Any] | None = None
        handling_log: dict[str, Any] | None = None
        target = command.status

        if target is not None:
            if target == STATUS_PENDING:
                raise InvalidTransitionError("不允许将工单状态改为待处理")
            if target == current:
                raise InvalidTransitionError("目标状态与当前状态相同，请勿重复提交")
            if LEGAL_TRANSITIONS.get(current) != target:
                raise InvalidTransitionError(
                    f"不允许从“{current}”流转到“{target}”，请刷新后重试"
                )
            if command.note is not None and current != STATUS_PROCESSING:
                raise InvalidTransitionError("仅处理中的工单可以填写处理说明")
            if target == STATUS_PROCESSING:
                assignee = command.assignee or order["assignee"]
                if not assignee:
                    raise AssigneeRequiredError()
                fields["status"] = target
                fields["assignee"] = assignee
                if command.assignee is not None:
                    content = f"分派并开始处理，维修人员：{assignee}"
                else:
                    content = "开始处理"
                status_log = self._status_log(
                    order, current, target, assignee, command.operator, content, timestamp
                )
            elif target == STATUS_COMPLETED:
                if command.solution is None:
                    raise SolutionRequiredError()
                fields["status"] = target
                fields["solution"] = command.solution
                if command.assignee is not None:
                    fields["assignee"] = command.assignee
                    assignee = command.assignee
                else:
                    assignee = order["assignee"]
                status_log = self._status_log(
                    order, current, target, assignee, command.operator, "完成工单", timestamp
                )
            elif target == STATUS_CLOSED:
                if command.assignee is not None:
                    fields["assignee"] = command.assignee
                assignee = command.assignee or order["assignee"]
                fields["status"] = target
                status_log = self._status_log(
                    order, current, target, assignee, command.operator, "关闭工单", timestamp
                )
            if command.note is not None:
                handling_log = self._handling_log(
                    order, command.note, command.operator, timestamp, assignee
                )
            return TransitionPlan(fields=fields, status_log=status_log, handling_log=handling_log)

        if command.solution is not None:
            raise InvalidTransitionError("处理方案仅允许在完成工单时提交")
        if command.note is not None and current != STATUS_PROCESSING:
            raise InvalidTransitionError("仅处理中的工单可以填写处理说明")

        assignee = command.assignee
        has_change = assignee is not None or command.note is not None
        if not has_change:
            raise InvalidTransitionError("请至少提交一项有效变更")
        if assignee is not None:
            if current not in (STATUS_PENDING, STATUS_PROCESSING):
                raise InvalidTransitionError(
                    f"当前状态“{current}”不允许分派维修人员"
                )
            fields["assignee"] = assignee
            previous = order["assignee"]
            if previous and previous != assignee:
                content = f"分派工单，维修人员：{assignee}（原维修人员：{previous}）"
            else:
                content = f"分派工单，维修人员：{assignee}"
            status_log = self._status_log(
                order, current, current, assignee, command.operator, content, timestamp
            )
        if command.note is not None:
            handling_log = self._handling_log(
                order,
                command.note,
                command.operator,
                timestamp,
                assignee or order["assignee"],
            )
        return TransitionPlan(fields=fields, status_log=status_log, handling_log=handling_log)

    @staticmethod
    def _status_log(
        order: dict[str, Any],
        from_status: str,
        to_status: str,
        assignee: str | None,
        operator: str | None,
        content: str,
        timestamp: str,
    ) -> dict[str, Any]:
        return {
            "work_order_id": order["id"],
            "log_type": "status",
            "from_status": from_status,
            "to_status": to_status,
            "assignee": assignee,
            "operator": operator or DEFAULT_OPERATOR,
            "content": content,
            "created_at": timestamp,
        }

    @staticmethod
    def _handling_log(
        order: dict[str, Any],
        note: str,
        operator: str | None,
        timestamp: str,
        assignee: str | None,
    ) -> dict[str, Any]:
        return {
            "work_order_id": order["id"],
            "log_type": "handling",
            "from_status": None,
            "to_status": None,
            "assignee": assignee,
            "operator": operator or DEFAULT_OPERATOR,
            "content": note,
            "created_at": timestamp,
        }

    @staticmethod
    def _normalize_filter(value: str | None, allowed: tuple[str, ...], label: str) -> str | None:
        if value is None:
            return None
        stripped = value.strip()
        if not stripped:
            return None
        if stripped not in allowed:
            raise ValueError(f"{label}取值不合法")
        return stripped
