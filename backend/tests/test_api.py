"""工单接口端到端测试（使用临时 SQLite 文件）。"""

from __future__ import annotations

import sqlite3
import tempfile
import unittest
from pathlib import Path

from fastapi.testclient import TestClient

from app.main import create_app


class WorkOrderApiTestCase(unittest.TestCase):
    def setUp(self) -> None:
        self.temp_dir = tempfile.TemporaryDirectory()
        self.db_path = Path(self.temp_dir.name) / "test.db"
        self.app = create_app(db_path=self.db_path, seed_enabled=True)
        self.client = TestClient(self.app)
        self.client.__enter__()

    def tearDown(self) -> None:
        self.client.__exit__(None, None, None)
        self.temp_dir.cleanup()

    def create_order(self, **overrides) -> dict:
        payload = {
            "device_name": "压缩机 C-01",
            "device_code": "EQ-C-01",
            "fault_description": "运行中振动明显增大，伴随周期性异响。",
            "fault_level": "高",
            "reporter": "张伟",
        }
        payload.update(overrides)
        response = self.client.post("/api/work-orders", json=payload)
        self.assertEqual(201, response.status_code, response.text)
        return response.json()

    def test_seed_statistics(self) -> None:
        response = self.client.get("/api/work-orders/statistics")
        self.assertEqual(200, response.status_code, response.text)
        self.assertEqual(
            {
                "total": 6,
                "by_status": {"待处理": 1, "处理中": 2, "已完成": 1, "已关闭": 2},
                "by_fault_level": {"高": 2, "中": 2, "低": 2},
            },
            response.json(),
        )
        with sqlite3.connect(self.db_path) as connection:
            log_count = connection.execute("SELECT COUNT(*) FROM work_order_logs").fetchone()[0]
        self.assertEqual(21, log_count)

    def test_list_default_order_and_filters(self) -> None:
        response = self.client.get("/api/work-orders")
        self.assertEqual(200, response.status_code)
        body = response.json()
        self.assertEqual(6, body["total"])
        self.assertEqual("WO202611130002", body["items"][0]["order_no"])
        self.assertIsNone(body["items"][0]["assignee"])

        high = self.client.get("/api/work-orders", params={"fault_level": "高"}).json()
        self.assertEqual(2, high["total"])
        self.assertTrue(all(item["fault_level"] == "高" for item in high["items"]))

        combined = self.client.get(
            "/api/work-orders",
            params={"fault_level": "高", "status": "处理中"},
        ).json()
        self.assertEqual(2, combined["total"])

        empty = self.client.get(
            "/api/work-orders",
            params={"status": "待处理", "fault_level": "低"},
        ).json()
        self.assertEqual(0, empty["total"])
        self.assertEqual([], empty["items"])

    def test_invalid_filters_return_422(self) -> None:
        self.assertEqual(
            422,
            self.client.get("/api/work-orders", params={"status": "未知"}).status_code,
        )
        self.assertEqual(
            422,
            self.client.get("/api/work-orders", params={"fault_level": "紧急"}).status_code,
        )
        self.assertEqual(
            422,
            self.client.get("/api/work-orders", params={"page": 0}).status_code,
        )
        self.assertEqual(
            422,
            self.client.get("/api/work-orders", params={"page_size": 101}).status_code,
        )

    def test_create_order_generates_number_and_log(self) -> None:
        order = self.create_order()
        self.assertRegex(order["order_no"], r"^WO\d{8}\d{4}$")
        self.assertEqual("待处理", order["status"])
        self.assertIsNone(order["assignee"])
        self.assertIsNone(order["solution"])
        self.assertEqual(order["created_at"], order["updated_at"])
        self.assertEqual(7, self.client.get("/api/work-orders/statistics").json()["total"])

        detail = self.client.get(f"/api/work-orders/{order['id']}").json()
        self.assertEqual(1, len(detail["logs"]))
        self.assertEqual("创建工单", detail["logs"][0]["content"])
        self.assertIsNone(detail["logs"][0]["from_status"])
        self.assertEqual("待处理", detail["logs"][0]["to_status"])

    def test_create_validation(self) -> None:
        response = self.client.post(
            "/api/work-orders",
            json={
                "device_name": "压缩机 C-01",
                "device_code": "EQ-C-01",
                "fault_level": "高",
                "reporter": "张伟",
            },
        )
        self.assertEqual(422, response.status_code)

        response = self.client.post(
            "/api/work-orders",
            json={
                "device_name": "压缩机 C-01",
                "device_code": "EQ C 01",
                "fault_description": "异响",
                "fault_level": "高",
                "reporter": "张伟",
            },
        )
        self.assertEqual(422, response.status_code)

        response = self.client.post(
            "/api/work-orders",
            json={
                "device_name": "压缩机 C-01",
                "device_code": "EQ-C-01",
                "fault_description": "异响",
                "fault_level": "紧急",
                "reporter": "张伟",
            },
        )
        self.assertEqual(422, response.status_code)

    def test_full_transition_flow_and_closed_guard(self) -> None:
        order = self.create_order()
        order_id = order["id"]

        jump = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "已关闭"},
        )
        self.assertEqual(400, jump.status_code)
        self.assertEqual("INVALID_TRANSITION", jump.json()["detail"]["code"])

        assigned = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"assignee": "李强"},
        )
        self.assertEqual(200, assigned.status_code, assigned.text)
        self.assertEqual("待处理", assigned.json()["status"])
        self.assertEqual("李强", assigned.json()["assignee"])

        started = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "处理中"},
        )
        self.assertEqual(200, started.status_code, started.text)
        self.assertEqual("处理中", started.json()["status"])

        noted = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"note": "已到达现场"},
        )
        self.assertEqual(200, noted.status_code, noted.text)
        self.assertEqual("处理中", noted.json()["status"])

        missing_solution = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "已完成"},
        )
        self.assertEqual(400, missing_solution.status_code)
        self.assertEqual("SOLUTION_REQUIRED", missing_solution.json()["detail"]["code"])

        completed = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "已完成", "solution": "更换轴承并复检", "note": "处理完成"},
        )
        self.assertEqual(200, completed.status_code, completed.text)
        self.assertEqual("已完成", completed.json()["status"])
        self.assertEqual("更换轴承并复检", completed.json()["solution"])

        note_after_completed = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"note": "补充说明"},
        )
        self.assertEqual(400, note_after_completed.status_code)

        closed = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "已关闭"},
        )
        self.assertEqual(200, closed.status_code, closed.text)
        self.assertEqual("已关闭", closed.json()["status"])

        blocked = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "处理中"},
        )
        self.assertEqual(400, blocked.status_code)
        self.assertEqual("ORDER_CLOSED", blocked.json()["detail"]["code"])

        detail = self.client.get(f"/api/work-orders/{order_id}").json()
        self.assertTrue(all(log["operator"] for log in detail["logs"]))
        status_logs = [log for log in detail["logs"] if log["log_type"] == "status"]
        self.assertEqual("已关闭", status_logs[0]["to_status"])
        self.assertEqual(7, len(detail["logs"]))

    def test_assignee_required_and_reassign(self) -> None:
        order = self.create_order()
        order_id = order["id"]
        no_assignee = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "处理中"},
        )
        self.assertEqual(400, no_assignee.status_code)
        self.assertEqual("ASSIGNEE_REQUIRED", no_assignee.json()["detail"]["code"])

        self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"assignee": "李强", "status": "处理中"},
        )
        reassigned = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"assignee": "王敏", "note": "交接班说明"},
        )
        self.assertEqual(200, reassigned.status_code, reassigned.text)
        self.assertEqual("处理中", reassigned.json()["status"])
        self.assertEqual("王敏", reassigned.json()["assignee"])
        status_logs = [
            log
            for log in self.client.get(f"/api/work-orders/{order_id}").json()["logs"]
            if log["log_type"] == "status"
        ]
        self.assertIn("原维修人员：李强", status_logs[0]["content"])
        handling_logs = [
            log
            for log in self.client.get(f"/api/work-orders/{order_id}").json()["logs"]
            if log["log_type"] == "handling"
        ]
        self.assertEqual("交接班说明", handling_logs[0]["content"])
        self.assertEqual("王敏", handling_logs[0]["assignee"])

    def test_not_found_and_statistics_route_priority(self) -> None:
        missing = self.client.get("/api/work-orders/9999")
        self.assertEqual(404, missing.status_code)
        self.assertEqual("ORDER_NOT_FOUND", missing.json()["detail"]["code"])

        statistics = self.client.get("/api/work-orders/statistics")
        self.assertEqual(200, statistics.status_code)
        self.assertIn("by_status", statistics.json())

    def test_cors_allows_frontend_origin(self) -> None:
        response = self.client.options(
            "/api/work-orders",
            headers={
                "Origin": "http://localhost:5173",
                "Access-Control-Request-Method": "PATCH",
                "Access-Control-Request-Headers": "Content-Type",
            },
        )
        self.assertEqual(200, response.status_code)
        self.assertEqual(
            "http://localhost:5173",
            response.headers["access-control-allow-origin"],
        )

    def test_seed_is_idempotent_and_can_be_disabled(self) -> None:
        second_app = create_app(db_path=self.db_path, seed_enabled=True)
        with TestClient(second_app) as second_client:
            stats = second_client.get("/api/work-orders/statistics").json()
            self.assertEqual(6, stats["total"])
        with sqlite3.connect(self.db_path) as connection:
            self.assertEqual(
                21,
                connection.execute("SELECT COUNT(*) FROM work_order_logs").fetchone()[0],
            )

        disabled_db = Path(self.temp_dir.name) / "empty.db"
        disabled_app = create_app(db_path=disabled_db, seed_enabled=False)
        with TestClient(disabled_app) as disabled_client:
            stats = disabled_client.get("/api/work-orders/statistics").json()
            self.assertEqual(0, stats["total"])
            self.assertEqual({"待处理": 0, "处理中": 0, "已完成": 0, "已关闭": 0}, stats["by_status"])

    def test_invalid_update_payloads(self) -> None:
        order = self.create_order()
        order_id = order["id"]
        empty = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"operator": "张伟"},
        )
        self.assertEqual(422, empty.status_code)

        solution_without_complete = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"status": "处理中", "assignee": "李强", "solution": "方案"},
        )
        self.assertEqual(422, solution_without_complete.status_code)

        unknown_assignee = self.client.patch(
            f"/api/work-orders/{order_id}/status",
            json={"assignee": "张三"},
        )
        self.assertEqual(422, unknown_assignee.status_code)


if __name__ == "__main__":
    unittest.main()
