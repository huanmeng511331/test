package com.dtastudio.todo.common.exception;

/**
 * 业务异常，携带 5 位错误码（错误来源 + 四位数字编号）。
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 5 位错误码：A-用户端 / B-系统端 / C-第三方 */
    private final String errorCode;

    /**
     * 构造业务异常。
     *
     * @param errorCode 5 位错误码
     * @param message   错误信息
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}