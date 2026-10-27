package com.dtk.todo.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一API响应结构
 *
 * @author AiWork
 * @date 2026/10/27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** 成功状态码 */
    public static final int SUCCESS_CODE = 200;
    /** 错误状态码 */
    public static final int ERROR_CODE = 500;

    /** 状态码 */
    private Integer code;
    /** 提示信息 */
    private String message;
    /** 业务数据 */
    private T data;
    /** 错误码，仅错误响应返回 */
    private String errorCode;
    /** 用户提示，仅错误响应返回 */
    private String userTip;

    /**
     * 构造成功响应
     *
     * @param data 业务数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 构造错误响应
     *
     * @param errorCode 错误码
     * @param message 错误信息
     * @param userTip 用户提示
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(String errorCode, String message, String userTip) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ERROR_CODE);
        response.setMessage(message);
        response.setErrorCode(errorCode);
        response.setUserTip(userTip);
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getUserTip() {
        return userTip;
    }

    public void setUserTip(String userTip) {
        this.userTip = userTip;
    }
}
