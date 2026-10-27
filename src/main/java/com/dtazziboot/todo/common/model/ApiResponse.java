package com.dtazziboot.todo.common.model;

/**
 * 统一API响应封装
 *
 * @param <T> 数据载荷类型
 * @author dtazziboot
 * @date 2026/10/27
 */
public class ApiResponse<T> {

    /**
     * 业务状态码，200 表示成功
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 数据载荷
     */
    private T data;

    /**
     * 私有构造，通过静态工厂创建
     */
    private ApiResponse() {
    }

    /**
     * 构建成功响应
     *
     * @param data 数据载荷
     * @param <T>  数据载荷类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    /**
     * 构建错误响应
     *
     * @param code    业务状态码
     * @param message 响应消息
     * @param <T>     数据载荷类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    /**
     * 获取业务状态码
     *
     * @return 业务状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置业务状态码
     *
     * @param code 业务状态码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     *
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取数据载荷
     *
     * @return 数据载荷
     */
    public T getData() {
        return data;
    }

    /**
     * 设置数据载荷
     *
     * @param data 数据载荷
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" + "code=" + code + ", message='" + message + '\'' + ", data=" + data + '}';
    }
}