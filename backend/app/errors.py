"""统一业务异常定义，对应 PRD 4.7 错误码。"""

from __future__ import annotations


class BusinessError(Exception):
    """可映射为 HTTP 响应的业务异常。"""

    status_code = 400
    code = "INTERNAL_ERROR"

    def __init__(self, message: str) -> None:
        super().__init__(message)
        self.message = message


class OrderNotFoundError(BusinessError):
    status_code = 404
    code = "ORDER_NOT_FOUND"

    def __init__(self, message: str = "工单不存在或已被删除") -> None:
        super().__init__(message)


class OrderClosedError(BusinessError):
    status_code = 400
    code = "ORDER_CLOSED"

    def __init__(self, message: str = "工单已关闭，不可修改") -> None:
        super().__init__(message)


class InvalidTransitionError(BusinessError):
    status_code = 400
    code = "INVALID_TRANSITION"

    def __init__(self, message: str = "当前状态不允许该操作，请刷新后重试") -> None:
        super().__init__(message)


class AssigneeRequiredError(BusinessError):
    status_code = 400
    code = "ASSIGNEE_REQUIRED"

    def __init__(self, message: str = "请先分派维修人员") -> None:
        super().__init__(message)


class SolutionRequiredError(BusinessError):
    status_code = 400
    code = "SOLUTION_REQUIRED"

    def __init__(self, message: str = "请填写处理方案后再完成工单") -> None:
        super().__init__(message)


class OrderNoConflictError(BusinessError):
    status_code = 500
    code = "ORDER_NO_CONFLICT"

    def __init__(self, message: str = "工单编号生成冲突，请稍后重试") -> None:
        super().__init__(message)


class InternalError(BusinessError):
    status_code = 500
    code = "INTERNAL_ERROR"

    def __init__(self, message: str = "服务暂不可用，请稍后重试或检查后端服务") -> None:
        super().__init__(message)
