package com.todoapp.common.exception;

/**
 * 业务异常
 *
 * <p>携带错误码（errorCode）与用户提示信息（errorMessage），
 * 由全局异常处理器统一捕获并转换为 {@link com.todoapp.common.model.ApiResponse}。
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final String errorCode;

    /** 用户提示信息 */
    private final String errorMessage;

    /**
     * 构造业务异常
     *
     * @param errorCode    错误码
     * @param errorMessage 用户提示信息
     */
    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 构造业务异常（携带原因）
     *
     * @param errorCode    错误码
     * @param errorMessage 用户提示信息
     * @param cause        原始异常
     */
    public BusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "BusinessException{errorCode='" + errorCode + "', errorMessage='" + errorMessage + "'}";
    }
}
