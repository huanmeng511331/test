"""FastAPI 应用入口：CORS、异常映射、请求日志与启动初始化。"""

from __future__ import annotations

import logging
import time
from contextlib import asynccontextmanager
from pathlib import Path
from typing import AsyncIterator

from fastapi import FastAPI, Request
from fastapi.encoders import jsonable_encoder
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.bootstrap.demo_data import DemoDataInitializer
from app.config import get_cors_origins, get_db_path, is_seed_enabled
from app.database import Database
from app.errors import BusinessError
from app.routers import statistics, work_orders

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s - %(message)s",
)
logger = logging.getLogger("work_order.app")


def create_app(
    db_path: str | Path | None = None,
    seed_enabled: bool | None = None,
) -> FastAPI:
    database = Database(db_path or get_db_path())
    seed = is_seed_enabled() if seed_enabled is None else seed_enabled

    @asynccontextmanager
    async def lifespan(app: FastAPI) -> AsyncIterator[None]:
        logger.info("启动初始化：数据库 %s", database.path)
        result = DemoDataInitializer(database, seed).initialize()
        logger.info("启动自检：%s", result)
        yield

    app = FastAPI(
        title="设备故障智能工单助手 API",
        version="1.0.0",
        description="设备故障工单的创建、分派、处理、关闭与统计接口。",
        lifespan=lifespan,
    )
    app.state.database = database

    app.add_middleware(
        CORSMiddleware,
        allow_origins=get_cors_origins(),
        allow_credentials=False,
        allow_methods=["GET", "POST", "PATCH", "OPTIONS"],
        allow_headers=["Content-Type"],
    )

    @app.middleware("http")
    async def log_requests(request: Request, call_next):
        started = time.perf_counter()
        try:
            response = await call_next(request)
        except Exception:
            logger.exception("请求异常 %s %s", request.method, request.url.path)
            raise
        elapsed_ms = (time.perf_counter() - started) * 1000
        logger.info(
            "请求 %s %s -> %s %.1fms",
            request.method,
            request.url.path,
            response.status_code,
            elapsed_ms,
        )
        return response

    @app.exception_handler(BusinessError)
    async def handle_business_error(request: Request, exc: BusinessError) -> JSONResponse:
        return JSONResponse(
            status_code=exc.status_code,
            content={"detail": {"code": exc.code, "message": exc.message}},
        )

    @app.exception_handler(RequestValidationError)
    async def handle_validation_error(
        request: Request,
        exc: RequestValidationError,
    ) -> JSONResponse:
        return JSONResponse(status_code=422, content={"detail": jsonable_encoder(exc.errors())})

    @app.exception_handler(Exception)
    async def handle_unexpected_error(request: Request, exc: Exception) -> JSONResponse:
        logger.exception("未预期异常 %s %s", request.method, request.url.path)
        return JSONResponse(
            status_code=500,
            content={
                "detail": {
                    "code": "INTERNAL_ERROR",
                    "message": "服务暂不可用，请稍后重试或检查后端服务",
                }
            },
        )

    # 静态路径 /statistics 必须先于 /{order_id} 注册，避免被当作 id 解析。
    app.include_router(statistics.router)
    app.include_router(work_orders.router)
    return app


app = create_app()
