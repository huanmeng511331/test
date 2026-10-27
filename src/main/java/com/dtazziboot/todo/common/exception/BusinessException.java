package com.dtazziboot.todo.common.exception;

import com.dtazziboot.todo.common.enums.TodoErrorCodeEnum;

/**
 * 待办事项业务异常。
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    public BusinessException(TodoErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.errorCode = errorCodeEnum.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}