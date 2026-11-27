package com.dtazziboot.base.common.exception;

import lombok.Getter;

/**
 * 业务异常:携带 {MODULE}_{SEQ} 格式错误码,由全局异常处理器转为统一响应。
 */
@Getter
public class BizException extends RuntimeException {

    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }
}
