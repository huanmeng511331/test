package com.dtastudio.todo.common.model;

import java.io.Serializable;

/**
 * 统一 API 响应结构。
 *
 * @param <T> 业务数据类型
 * @author AiWork
 * @date 2026/10/27
 */
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    private static final String CODE_SUCCESS = "0";

    /** 业务状态码："0" 表示成功；失败时为 5 位错误码（A/B/C + 数字） */
    private String code;

    /** 提示信息 */
    private String message;

    /** 业务数据 */
    private T data;

    private ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应。
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return API 响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(CODE_SUCCESS, "success", data);
    }

    /**
     * 失败响应。
     *
     * @param code    错误码（5 位字符串，如 A/B/C + 数字）
     * @param message 提示信息
     * @param <T>     业务数据类型
     * @return API 响应
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
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