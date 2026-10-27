package com.dtazza.todo.common.exception;

/**
 * 业务异常，携带错误码与用户提示信息。
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    private final String userTip;

    public BusinessException(String errorCode, String message) {
        this(errorCode, message, message);
    }

    public BusinessException(String errorCode, String message, String userTip) {
        super(message);
        this.errorCode = errorCode;
        this.userTip = userTip;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserTip() {
        return userTip;
    }
}
