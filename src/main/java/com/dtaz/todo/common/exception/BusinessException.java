package com.dtaz.todo.common.exception;

import com.dtaz.todo.common.constant.TodoErrorCodeEnum;

/**
 * 待办事项业务异常
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final String errorCode;

    /** 用户提示信息 */
    private final String userTip;

    /**
     * 使用错误码枚举构造业务异常
     *
     * @param errorCodeEnum 错误码枚举
     */
    public BusinessException(TodoErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getErrorMessage());
        this.errorCode = errorCodeEnum.getCode();
        this.userTip = errorCodeEnum.getUserTip();
    }

    /**
     * 使用自定义参数错误信息构造业务异常
     *
     * @param errorCodeEnum 错误码枚举
     * @param errorMessage  自定义错误信息
     */
    public BusinessException(TodoErrorCodeEnum errorCodeEnum, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCodeEnum.getCode();
        this.userTip = errorCodeEnum.getUserTip();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserTip() {
        return userTip;
    }
}