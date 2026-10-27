package com.antdigital.todo.common.response;

/**
 * 统一 API 响应结构。
 *
 * @param <T> 数据主体类型
 */
public class ApiResponse<T> {

    /** 业务状态码，200 表示成功 */
    private Integer code;

    /** 提示信息 */
    private String message;

    /** 数据主体 */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
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

    @Override
    public String toString() {
        return "ApiResponse{"
                + "code=" + code
                + ", message='" + message + '\''
                + ", data=" + data
                + '}';
    }
}