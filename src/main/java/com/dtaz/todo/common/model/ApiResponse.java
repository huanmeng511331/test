package com.dtaz.todo.common.model;

/**
 * 统一API响应结构
 *
 * @param <T> 响应数据类型
 * @author AiWork
 * @date 2026/10/27
 */
public class ApiResponse<T> {

    /** 是否成功 */
    private boolean success;

    /** 错误码，成功时为null */
    private String errorCode;

    /** 错误信息，成功时为null */
    private String errorMessage;

    /** 用户提示信息 */
    private String userTip;

    /** 响应数据 */
    private T data;

    private ApiResponse() {
    }

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    /**
     * 失败响应
     *
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     * @param userTip      用户提示
     * @param <T>          数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, String userTip) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.errorCode = errorCode;
        response.errorMessage = errorMessage;
        response.userTip = userTip;
        return response;
    }

    public boolean isSuccess() {
        return success;
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

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ApiResponse{"
                + "success=" + success
                + ", errorCode='" + errorCode + '\''
                + ", errorMessage='" + errorMessage + '\''
                + ", userTip='" + userTip + '\''
                + ", data=" + data
                + '}';
    }
}