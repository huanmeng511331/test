package com.todoapp.common.model;

import com.todoapp.common.constant.CommonConstants;

/**
 * 通用 API 响应包装结构
 *
 * <p>对应系分设计中的通用出参：
 * <ul>
 *   <li>result - 结果 code，OK 表示成功</li>
 *   <li>msg - 提示信息</li>
 *   <li>data - 业务数据</li>
 * </ul>
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class ApiResponse<T> {

    /** 结果 code，OK 表示成功 */
    private String result;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String result, String msg, T data) {
        this.result = result;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构建成功响应
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(CommonConstants.RESULT_OK, CommonConstants.MSG_SUCCESS, data);
    }

    /**
     * 构建错误响应
     *
     * @param errorCode 错误码
     * @param msg       提示信息
     * @param <T>       数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(String errorCode, String msg) {
        return new ApiResponse<>(errorCode, msg, null);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{result='" + result + "', msg='" + msg + "', data=" + data + '}';
    }
}
