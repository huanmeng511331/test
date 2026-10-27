package com.dtazziboot.todo.common.result;

import java.io.Serializable;

/**
 * 统一 API 响应结构。
 */
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;

    /** 业务状态码 */
    private int code;

    /** 错误码（错误场景） */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    /** 响应数据 */
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(SUCCESS_CODE);
        result.setData(data);
        return result;
    }

    public static <T> ApiResult<T> error(String errorCode, String errorMessage) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(ERROR_CODE);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}