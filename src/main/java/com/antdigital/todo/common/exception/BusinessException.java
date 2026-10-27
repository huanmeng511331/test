package com.antdigital.todo.common.exception;

import com.antdigital.todo.common.enums.ErrorCodeEnum;

/**
 * 业务异常，携带业务错误码。
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private final String errorCode;

    public BusinessException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(ErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}