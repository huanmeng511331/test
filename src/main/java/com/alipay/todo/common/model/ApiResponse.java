package com.alipay.todo.common.model;

/**
 * 统一 API 响应体。
 *
 * @param <T> 业务数据类型
 */
public class ApiResponse<T> {

    private Integer code;

    private String message;

    private String errorCode;

    private String userTip;

    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, String userTip) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setMessage(errorMessage);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{code=" + code + ", message='" + message + "', errorCode='"
                + errorCode + "', userTip='" + userTip + "', data=" + data + "}";
    }
}
