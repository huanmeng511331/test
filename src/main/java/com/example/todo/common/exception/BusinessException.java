package com.example.todo.common.exception;

import com.example.todo.common.constant.ErrorCodeEnum;

/**
 * 业务异常
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final String errorCode;

    /**
     * 用户提示信息
     */
    private final String userTip;

    /**
     * 使用错误码枚举构造业务异常
     *
     * @param errorCodeEnum 错误码枚举
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum, errorCodeEnum.getMessage());
    }

    /**
     * 使用错误码枚举和用户提示信息构造业务异常
     *
     * @param errorCodeEnum 错误码枚举
     * @param userTip       用户提示信息
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum, String userTip) {
        super(errorCodeEnum.getCode() + ": " + errorCodeEnum.getMessage());
        this.errorCode = errorCodeEnum.getCode();
        this.userTip = userTip;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取用户提示信息
     *
     * @return 用户提示信息
     */
    public String getUserTip() {
        return userTip;
    }
}