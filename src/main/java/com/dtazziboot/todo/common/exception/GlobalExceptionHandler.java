package com.dtazziboot.todo.common.exception;

import com.dtazziboot.todo.common.enums.TodoErrorCodeEnum;
import com.dtazziboot.todo.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.warn("请求参数校验失败: {}", e.getMessage());
        return ApiResponse.error(TodoErrorCodeEnum.PARAM_INVALID.getCode(),
                TodoErrorCodeEnum.PARAM_INVALID.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        logger.warn("业务异常, errorCode: {}, message: {}", e.getErrorCode(), e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ApiResponse.error(TodoErrorCodeEnum.SYSTEM_ERROR.getCode(),
                TodoErrorCodeEnum.SYSTEM_ERROR.getMessage());
    }
}