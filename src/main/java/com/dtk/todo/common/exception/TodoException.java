package com.dtk.todo.common.exception;

/**
 * 待办事项业务异常
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoException extends RuntimeException {

    /** 错误码 */
    private final String errorCode;
    /** 错误信息 */
    private final String errorMessage;
    /** 用户提示 */
    private final String userTip;

    /**
     * 构造业务异常
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param userTip 用户提示
     */
    public TodoException(String errorCode, String errorMessage, String userTip) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userTip = userTip;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }
}
