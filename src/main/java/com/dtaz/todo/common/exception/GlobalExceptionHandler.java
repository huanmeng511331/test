package com.dtaz.todo.common.exception;

import com.dtaz.todo.common.constant.TodoErrorCodeEnum;
import com.dtaz.todo.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author AiWork
 * @date 2026/10/27
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param ex 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("业务异常, errorCode: {}, errorMessage: {}", ex.getErrorCode(), ex.getMessage());
        }
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage(), ex.getUserTip());
    }

    /**
     * 处理参数校验异常
     *
     * @param ex 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        TodoErrorCodeEnum codeEnum = TodoErrorCodeEnum.INVALID_PARAMETER;
        return ApiResponse.error(codeEnum.getCode(), errorMessage, codeEnum.getUserTip());
    }

    /**
     * 处理未知异常
     *
     * @param ex 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleException(Exception ex) {
        LOGGER.error("系统异常: {}", ex.getMessage(), ex);
        TodoErrorCodeEnum codeEnum = TodoErrorCodeEnum.SYSTEM_ERROR;
        return ApiResponse.error(codeEnum.getCode(), codeEnum.getErrorMessage(), codeEnum.getUserTip());
    }
}