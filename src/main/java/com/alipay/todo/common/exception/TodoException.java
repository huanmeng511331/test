package com.alipay.todo.common.exception;

/**
 * 待办事项业务异常。
 *
 * <p>携带业务错误码，供全局异常处理器统一转换为 HTTP 响应。</p>
 */
public class TodoException extends RuntimeException {

    private final String errorCode;

    public TodoException(TodoErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserTip() {
        return getMessage();
    }
}
