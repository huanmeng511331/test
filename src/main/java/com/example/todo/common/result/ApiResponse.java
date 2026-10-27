package com.example.todo.common.result;

import com.example.todo.common.constant.ErrorCodeEnum;

/**
 * 统一 API 响应体
 *
 * <p>服务端返回信息包含：errorCode（错误排查）、errorMessage（前端开发）、
 * userTip（用户）、data（业务数据）四个部分。
 *
 * @param <T> 业务数据类型
 * @author AiWork
 * @date 2026/10/27
 */
public class ApiResponse<T> {

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 用户提示信息
     */
    private String userTip;

    /**
     * 业务数据
     */
    private T data;

    /**
     * 构造成功响应
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setErrorCode(ErrorCodeEnum.SUCCESS.getCode());
        response.setErrorMessage(ErrorCodeEnum.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    /**
     * 构造错误响应
     *
     * @param errorCodeEnum 错误码枚举
     * @param userTip       用户提示信息
     * @param <T>           业务数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(ErrorCodeEnum errorCodeEnum, String userTip) {
        return error(errorCodeEnum.getCode(), errorCodeEnum.getMessage(), userTip);
    }

    /**
     * 构造错误响应
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     * @param userTip      用户提示信息
     * @param <T>          业务数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, String userTip) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        response.setUserTip(userTip);
        return response;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }

    public void setUserTip(String userTip) {
        this.userTip = userTip;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}