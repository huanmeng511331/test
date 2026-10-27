package com.dtaz.todo.common.exception;

import com.dtaz.todo.common.constant.ErrorCodeEnum;
import com.dtaz.todo.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一转换为 API 响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常处理。
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        LOGGER.warn("业务异常: code={}, message={}", e.getErrorCode(), e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage(), e.getMessage());
    }

    /**
     * 参数校验异常处理。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getDefaultMessage())
                .orElse(ErrorCodeEnum.INVALID_PARAMETER.getMessage());
        LOGGER.warn("参数校验失败: {}", message);
        return ApiResponse.error(ErrorCodeEnum.INVALID_PARAMETER.getCode(), message, message);
    }

    /**
     * 兜底异常处理。
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        LOGGER.error("系统异常", e);
        return ApiResponse.error(ErrorCodeEnum.SYSTEM_ERROR.getCode(),
                ErrorCodeEnum.SYSTEM_ERROR.getMessage(), "系统繁忙，请稍后重试");
    }
}