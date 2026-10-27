package com.dtazziboot.todo.controller;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.common.result.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，将异常转换为统一的 {@link ApiResult} 错误响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String ERR_BUSINESS = "B0001";

    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException ex) {
        LOGGER.warn("业务异常：{}", ex.getMessage());
        return ApiResult.error(ERR_BUSINESS, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("参数校验失败");
        LOGGER.warn("参数校验失败：{}", message);
        return ApiResult.error("A0001", message);
    }
}