package com.dtazziboot.todo.common.exception;

/**
 * 业务异常，用于表示带业务语义的可预期错误
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码，来源：A 用户 / B 系统 / C 第三方
     */
    private final String errorCode;

    /**
     * 构造业务异常
     *
     * @param errorCode 错误码
     * @param message   错误描述
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
}