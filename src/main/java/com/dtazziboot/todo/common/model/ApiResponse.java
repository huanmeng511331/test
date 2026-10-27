package com.dtazziboot.todo.common.model;

/**
 * 统一 API 响应结构。
 *
 * @param <T> 响应数据类型
 */
public class ApiResponse<T> {

    private Integer code;

    /** 错误码，成功时为空 */
    private String errorCode;

    private String message;

    private T data;

    public ApiResponse() {
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
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

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}