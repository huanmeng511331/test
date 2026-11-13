"""工单请求与响应模型。"""

from __future__ import annotations

import re

from pydantic import BaseModel, Field, field_validator, model_validator

from app.models.constants import DEMO_ASSIGNEES, LEVELS, STATUSES

DEVICE_CODE_PATTERN = re.compile(r"^[A-Za-z0-9_-]+$")
ANNOTATED_STATUSES = (*STATUSES,)


def _strip_required(value: str, field_label: str) -> str:
    stripped = value.strip()
    if not stripped:
        raise ValueError(f"{field_label}不能为空")
    return stripped


class WorkOrderCreate(BaseModel):
    device_name: str = Field(..., max_length=50, description="设备名称")
    device_code: str = Field(..., max_length=32, description="设备编号")
    fault_description: str = Field(..., max_length=500, description="故障现象")
    fault_level: str = Field(..., description="故障等级：高/中/低")
    reporter: str = Field(..., max_length=20, description="报修人")
    operator: str | None = Field(None, max_length=20, description="操作人，缺省张伟")

    @field_validator("device_name", "reporter", mode="before")
    @classmethod
    def validate_short_text(cls, value: object, info) -> str:
        if not isinstance(value, str):
            raise ValueError(f"{info.field_name} 必须为文本")
        label = "设备名称" if info.field_name == "device_name" else "报修人"
        return _strip_required(value, label)

    @field_validator("device_code", mode="before")
    @classmethod
    def validate_device_code(cls, value: object) -> str:
        if not isinstance(value, str):
            raise ValueError("设备编号必须为文本")
        stripped = _strip_required(value, "设备编号")
        if not DEVICE_CODE_PATTERN.match(stripped):
            raise ValueError("设备编号仅允许字母、数字、- 和 _")
        return stripped

    @field_validator("fault_description", mode="before")
    @classmethod
    def validate_fault_description(cls, value: object) -> str:
        if not isinstance(value, str):
            raise ValueError("故障现象必须为文本")
        return _strip_required(value, "故障现象")

    @field_validator("fault_level")
    @classmethod
    def validate_fault_level(cls, value: str) -> str:
        if value not in LEVELS:
            raise ValueError("故障等级必须为：高、中、低")
        return value

    @field_validator("operator", mode="before")
    @classmethod
    def validate_operator(cls, value: object) -> str | None:
        if value is None:
            return None
        if not isinstance(value, str):
            raise ValueError("操作人必须为文本")
        stripped = value.strip()
        if not stripped:
            raise ValueError("操作人不能为空")
        return stripped


class WorkOrderUpdate(BaseModel):
    status: str | None = Field(None, description="目标状态：处理中/已完成/已关闭")
    assignee: str | None = Field(None, max_length=20, description="维修人员")
    solution: str | None = Field(None, max_length=500, description="处理方案")
    note: str | None = Field(None, max_length=500, description="处理说明")
    operator: str | None = Field(None, max_length=20, description="操作人，缺省张伟")

    @field_validator("status")
    @classmethod
    def validate_status(cls, value: str | None) -> str | None:
        if value is None:
            return None
        if value not in ANNOTATED_STATUSES:
            raise ValueError("状态取值必须为：待处理、处理中、已完成、已关闭")
        return value

    @field_validator("assignee")
    @classmethod
    def validate_assignee(cls, value: str | None) -> str | None:
        if value is None:
            return None
        stripped = _strip_required(value, "维修人员")
        if stripped not in DEMO_ASSIGNEES:
            raise ValueError("维修人员必须从演示人员名单中选择")
        return stripped

    @field_validator("solution")
    @classmethod
    def validate_solution(cls, value: str | None) -> str | None:
        if value is None:
            return None
        return _strip_required(value, "处理方案")

    @field_validator("note")
    @classmethod
    def validate_note(cls, value: str | None) -> str | None:
        if value is None:
            return None
        return _strip_required(value, "处理说明")

    @field_validator("operator")
    @classmethod
    def validate_operator(cls, value: str | None) -> str | None:
        if value is None:
            return None
        return _strip_required(value, "操作人")

    @model_validator(mode="after")
    def validate_payload(self) -> "WorkOrderUpdate":
        if not any(value is not None for value in (self.status, self.assignee, self.solution, self.note)):
            raise ValueError("请至少提交状态、维修人员、处理方案或处理说明中的一项")
        if self.solution is not None and self.status != "已完成":
            raise ValueError("处理方案仅允许随“已完成”状态提交")
        if self.status is None and self.assignee is None and self.note is None:
            raise ValueError("请至少提交状态、维修人员或处理说明中的一项")
        return self


class WorkOrderLogOut(BaseModel):
    id: int
    log_type: str
    from_status: str | None
    to_status: str | None
    assignee: str | None
    operator: str
    content: str | None
    created_at: str


class WorkOrderOut(BaseModel):
    id: int
    order_no: str
    device_name: str
    device_code: str
    fault_description: str
    fault_level: str
    status: str
    reporter: str
    assignee: str | None
    solution: str | None
    created_at: str
    updated_at: str


class WorkOrderDetailOut(WorkOrderOut):
    logs: list[WorkOrderLogOut]


class WorkOrderListItemOut(BaseModel):
    id: int
    order_no: str
    device_name: str
    device_code: str
    fault_level: str
    status: str
    reporter: str
    assignee: str | None
    created_at: str
    updated_at: str


class WorkOrderListOut(BaseModel):
    total: int
    page: int
    page_size: int
    items: list[WorkOrderListItemOut]
