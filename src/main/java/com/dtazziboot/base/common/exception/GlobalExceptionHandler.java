package com.dtazziboot.base.common.exception;

import com.dtazziboot.base.common.api.ApiResult;
import com.dtazziboot.base.common.constant.CommonErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 底座统一异常处理:BizException 按业务码返回;其余兜底 SYS_999。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResult<Void> handleBizException(BizException e) {
        log.warn("biz exception, code: {}, msg: {}", e.getCode(), e.getMessage());
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public ApiResult<Void> handleParamException(Exception e) {
        log.warn("invalid request body, msg: {}", e.getMessage());
        return ApiResult.fail(CommonErrorCodeConstants.PARAM_ERROR, "请求参数错误");
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        log.error("unexpected exception, msg: {}", e.getMessage(), e);
        return ApiResult.fail(CommonErrorCodeConstants.SYS_ERROR, "系统繁忙,请稍后重试");
    }
}
