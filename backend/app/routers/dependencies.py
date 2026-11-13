"""路由依赖。"""

from __future__ import annotations

from fastapi import Request

from app.database import Database


def get_database(request: Request) -> Database:
    return request.app.state.database
