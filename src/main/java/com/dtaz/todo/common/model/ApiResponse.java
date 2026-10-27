package com.dtaz.todo.common.model;

/**
 * 统一 API 响应结构。
 *
 * @param <T> 数据泛型
 */
public class ApiResponse<T> {

    /** 业务状态码，0 表示成功 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 错误码（成功时为 null） */
    private String errorCode;

    /** 用户提示语（成功时为 null） */
    private String userTip;

    /** 响应数据 */
    private T data;

    private ApiResponse(Integer code, String message, String errorCode, String userTip, T data) {
        this.code = code;
        this.message = message;
        this.errorCode = errorCode;
        this.userTip = userTip;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", null, null, data);
    }

    public static <T> ApiResponse<T> error(String errorCode, String message, String userTip) {
        return new ApiResponse<>(500, message, errorCode, userTip, null);
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserTip() {
        return userTip;
    }

    public T getData() {
        return data;
    }
}