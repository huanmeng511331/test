"""应用配置：数据库路径、CORS 来源与种子开关。"""

from __future__ import annotations

import os
from pathlib import Path

BACKEND_DIR = Path(__file__).resolve().parent.parent
DEFAULT_DB_PATH = BACKEND_DIR / "demo.db"
DEFAULT_OPERATOR = "张伟"
DEFAULT_CORS_ORIGINS = ("http://localhost:5173", "http://127.0.0.1:5173")


def get_db_path() -> Path:
    """返回 SQLite 文件路径，允许通过 DB_PATH 覆盖。"""
    configured = os.getenv("DB_PATH")
    return Path(configured).expanduser() if configured else DEFAULT_DB_PATH


def get_cors_origins() -> list[str]:
    """返回允许跨域访问的前端来源，逗号分隔。"""
    configured = os.getenv("CORS_ORIGINS")
    if not configured:
        return list(DEFAULT_CORS_ORIGINS)
    return [origin.strip() for origin in configured.split(",") if origin.strip()]


def is_seed_enabled() -> bool:
    """默认开启演示数据种子，SEED_ENABLED=false 时关闭。"""
    return os.getenv("SEED_ENABLED", "true").strip().lower() not in {"0", "false", "no", "off"}
