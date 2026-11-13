"""演示数据初始化（幂等）。"""

from __future__ import annotations

import logging
from typing import Any

from app.database import Database
from app.models.constants import LEVELS, STATUSES
from app.repositories.work_order_repository import WorkOrderRepository

logger = logging.getLogger("work_order.bootstrap")

SEED_ORDERS: tuple[dict[str, Any], ...] = (
    {
        "order_no": "WO202611130002",
        "device_name": "空压机 A-03",
        "device_code": "EQ-A-03",
        "fault_description": "空压机运行压力波动，排气温度偏高。",
        "fault_level": "中",
        "status": "待处理",
        "reporter": "张伟",
        "assignee": None,
        "solution": None,
        "created_at": "2026-11-13 09:12:00",
        "updated_at": "2026-11-13 09:12:00",
    },
    {
        "order_no": "WO202611130001",
        "device_name": "冷却水泵 P-11",
        "device_code": "EQ-P-11",
        "fault_description": "运行中振动明显增大，伴随周期性异响。",
        "fault_level": "高",
        "status": "处理中",
        "reporter": "张伟",
        "assignee": "李强",
        "solution": None,
        "created_at": "2026-11-13 08:40:00",
        "updated_at": "2026-11-13 09:05:00",
    },
    {
        "order_no": "WO202611120002",
        "device_name": "压缩机 C-01",
        "device_code": "EQ-C-01",
        "fault_description": "运行中振动明显增大，伴随周期性异响。",
        "fault_level": "高",
        "status": "处理中",
        "reporter": "张伟",
        "assignee": "王敏",
        "solution": None,
        "created_at": "2026-11-12 16:25:00",
        "updated_at": "2026-11-13 08:40:00",
    },
    {
        "order_no": "WO202611120001",
        "device_name": "传送带 M-07",
        "device_code": "EQ-M-07",
        "fault_description": "传送带运行跑偏，张紧机构出现松动。",
        "fault_level": "低",
        "status": "已完成",
        "reporter": "张伟",
        "assignee": "赵磊",
        "solution": "重新张紧输送带并复检运行状态",
        "created_at": "2026-11-12 11:03:00",
        "updated_at": "2026-11-12 13:40:00",
    },
    {
        "order_no": "WO202611110002",
        "device_name": "注塑机 I-02",
        "device_code": "EQ-I-02",
        "fault_description": "注塑机温控异常，产品出现明显缩水。",
        "fault_level": "中",
        "status": "已关闭",
        "reporter": "张伟",
        "assignee": "陈晨",
        "solution": "更换模具温控模块并复检，运行正常。",
        "created_at": "2026-11-11 15:47:00",
        "updated_at": "2026-11-11 18:05:00",
    },
    {
        "order_no": "WO202611110001",
        "device_name": "包装机 B-05",
        "device_code": "EQ-B-05",
        "fault_description": "包装封口不牢，热封刀温度偏低。",
        "fault_level": "低",
        "status": "已关闭",
        "reporter": "张伟",
        "assignee": "陈晨",
        "solution": "调整热封刀温度并更换加热管，封口质量恢复。",
        "created_at": "2026-11-11 10:22:00",
        "updated_at": "2026-11-11 16:30:00",
    },
)


def _log(
    log_type: str,
    from_status: str | None,
    to_status: str | None,
    assignee: str | None,
    operator: str,
    content: str,
    created_at: str,
) -> dict[str, Any]:
    return {
        "log_type": log_type,
        "from_status": from_status,
        "to_status": to_status,
        "assignee": assignee,
        "operator": operator,
        "content": content,
        "created_at": created_at,
    }


# 演示记录按工单编号分组，按时间正序写入。
SEED_LOGS: dict[str, tuple[dict[str, Any], ...]] = {
    "WO202611130002": (
        _log("status", None, "待处理", None, "张伟", "创建工单", "2026-11-13 09:12:00"),
    ),
    "WO202611130001": (
        _log("status", None, "待处理", None, "张伟", "创建工单", "2026-11-13 08:40:00"),
        _log("status", "待处理", "待处理", "李强", "张伟", "分派工单，维修人员：李强", "2026-11-13 08:52:00"),
        _log("status", "待处理", "处理中", "李强", "李强", "开始处理", "2026-11-13 09:05:00"),
    ),
    "WO202611120002": (
        _log("status", None, "待处理", None, "张伟", "创建工单", "2026-11-12 16:25:00"),
        _log("status", "待处理", "待处理", "王敏", "张伟", "分派工单，维修人员：王敏", "2026-11-12 17:10:00"),
        _log("status", "待处理", "处理中", "王敏", "王敏", "分派并开始处理，维修人员：王敏", "2026-11-13 08:40:00"),
        _log("handling", None, None, "王敏", "王敏", "已到达现场，初步判断为地脚螺栓松动。", "2026-11-13 08:52:00"),
    ),
    "WO202611120001": (
        _log("status", None, "待处理", None, "张伟", "创建工单", "2026-11-12 11:03:00"),
        _log("status", "待处理", "处理中", "赵磊", "赵磊", "开始处理", "2026-11-12 11:20:00"),
        _log("handling", None, None, "赵磊", "赵磊", "检查张紧机构松动，已重新张紧", "2026-11-12 11:20:00"),
        _log("status", "处理中", "已完成", "赵磊", "赵磊", "完成工单", "2026-11-12 13:40:00"),
    ),
    "WO202611110002": (
        _log("status", None, "待处理", None, "张伟", "创建工单", "2026-11-11 15:47:00"),
        _log("status", "待处理", "处理中", "陈晨", "陈晨", "开始处理", "2026-11-11 16:00:00"),
        _log("status", "处理中", "已完成", "陈晨", "陈晨", "完成工单", "2026-11-11 17:40:00"),
        _log("status", "已完成", "已关闭", "陈晨", "张伟", "关闭工单", "2026-11-11 18:05:00"),
    ),
    "WO202611110001": (
        _log("status", None, "待处理", None, "张伟", "创建工单", "2026-11-11 10:22:00"),
        _log("status", "待处理", "处理中", "陈晨", "陈晨", "开始处理", "2026-11-11 10:40:00"),
        _log("handling", None, None, "陈晨", "陈晨", "检查热封刀温度偏低", "2026-11-11 10:40:00"),
        _log("status", "处理中", "已完成", "陈晨", "陈晨", "完成工单", "2026-11-11 15:55:00"),
        _log("status", "已完成", "已关闭", "陈晨", "张伟", "关闭工单", "2026-11-11 16:30:00"),
    ),
}


class DemoDataInitializer:
    """建表并写入 6 条演示工单与 21 条记录。"""

    def __init__(self, database: Database, seed_enabled: bool = True) -> None:
        self.database = database
        self.seed_enabled = seed_enabled
        self.repository = WorkOrderRepository()

    def initialize(self) -> dict[str, Any]:
        from app.bootstrap.schema_manager import SchemaManager

        schema_created = SchemaManager(self.database).create_schema()
        result: dict[str, Any] = {
            "schema_created": schema_created,
            "seeded": False,
            "order_count": 0,
            "log_count": 0,
            "skipped_reason": None,
        }
        with self.database.read() as connection:
            if self.repository.exists_seed_data(connection):
                result["skipped_reason"] = "已有工单数据"
                self._log_summary(result)
                return result
        if not self.seed_enabled:
            result["skipped_reason"] = "种子开关关闭"
            self._log_summary(result)
            return result

        with self.database.transaction() as connection:
            order_ids: dict[str, int] = {}
            for order in SEED_ORDERS:
                order_ids[order["order_no"]] = self.repository.insert_order(connection, order)
            log_count = 0
            for order_no, logs in SEED_LOGS.items():
                order_id = order_ids[order_no]
                for log in logs:
                    self.repository.insert_log(
                        connection,
                        {**log, "work_order_id": order_id},
                    )
                    log_count += 1
            self._verify(connection, order_ids, log_count)
            result.update({"seeded": True, "order_count": len(order_ids), "log_count": log_count})
        self._log_summary(result)
        return result

    def _verify(
        self,
        connection,
        order_ids: dict[str, int],
        log_count: int,
    ) -> None:
        expected_status = {"待处理": 1, "处理中": 2, "已完成": 1, "已关闭": 2}
        expected_level = {"高": 2, "中": 2, "低": 2}
        status_counts = self.repository.count_group_by_status(connection)
        level_counts = self.repository.count_group_by_fault_level(connection)
        if any(status_counts.get(key, 0) != value for key, value in expected_status.items()):
            raise RuntimeError(f"演示数据状态口径校验失败：{status_counts}")
        if any(level_counts.get(key, 0) != value for key, value in expected_level.items()):
            raise RuntimeError(f"演示数据等级口径校验失败：{level_counts}")
        if log_count != 21 or self.repository.count_logs(connection) != 21:
            raise RuntimeError("演示记录条数校验失败，期望 21 条")
        for order_no, order_id in order_ids.items():
            order = self.repository.find_by_id(connection, order_id)
            logs = self.repository.find_logs_by_order_id(connection, order_id)
            status_logs = [log for log in logs if log["log_type"] == "status"]
            if not status_logs or status_logs[0]["to_status"] != order["status"]:
                raise RuntimeError(f"演示数据结构不自洽：{order_no}")
        if set(STATUSES) != set(expected_status) or set(LEVELS) != set(expected_level):
            raise RuntimeError("枚举常量与演示数据口径不一致")

    @staticmethod
    def _log_summary(result: dict[str, Any]) -> None:
        logger.info(
            "初始化完成 schema_created=%s seeded=%s orders=%s logs=%s skipped=%s",
            result["schema_created"],
            result["seeded"],
            result["order_count"],
            result["log_count"],
            result["skipped_reason"],
        )
