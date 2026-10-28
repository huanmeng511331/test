package com.todoapp.common.handler;

import com.todoapp.common.exception.BusinessException;
import com.todoapp.common.model.ApiResponse;
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
 * <p>统一捕获业务异常、参数校验异常与未知系统异常，
 * 转换为标准 {@link ApiResponse} 响应。
 *
 * @author AiWork
 * @date 2026/10/28
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 参数校验失败错误码 */
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";

    /** 系统异常错误码 */
    private static final String SYSTEM_ERROR_CODE = "SYSTEM_ERROR";

    /** 系统异常提示信息 */
    private static final String SYSTEM_ERROR_MSG = "系统异常";

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        logger.warn("business exception: code={}, msg={}", e.getErrorCode(), e.getErrorMessage());
        return ApiResponse.error(e.getErrorCode(), e.getErrorMessage());
    }

    /**
     * 处理参数校验异常
     *
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        logger.warn("validation exception: field={}, msg={}",
                fieldError != null ? fieldError.getField() : "unknown", msg);
        return ApiResponse.error(VALIDATION_ERROR_CODE, msg);
    }

    /**
     * 处理未知系统异常
     *
     * @param e 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        logger.error("unexpected exception", e);
        return ApiResponse.error(SYSTEM_ERROR_CODE, SYSTEM_ERROR_MSG);
    }
}
