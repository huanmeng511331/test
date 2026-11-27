package com.dtazziboot.base.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结构 {code, msg, data}。
 *
 * @param <T> 业务数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    /** 成功 code */
    public static final String CODE_OK = "OK";

    /** 成功 msg */
    public static final String MSG_SUCCESS = "SUCCESS";

    private String code;

    private String msg;

    private T data;

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(CODE_OK, MSG_SUCCESS, data);
    }

    public static ApiResult<Void> fail(String code, String msg) {
        return new ApiResult<>(code, msg, null);
    }
}
