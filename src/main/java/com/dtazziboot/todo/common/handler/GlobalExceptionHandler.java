package com.dtazziboot.todo.common.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.common.model.ApiResponse;

/**
 * 全局异常处理器，将异常统一转换为前端可理解的响应
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验异常
     *
     * @param ex 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().isEmpty()
                ? "请求参数不合法"
                : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        logger.warn("请求参数校验失败: {}", message);
        return ApiResponse.error(400, message);
    }

    /**
     * 处理业务异常
     *
     * @param ex 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        logger.warn("业务异常, errorCode: {}, message: {}", ex.getErrorCode(), ex.getMessage());
        return ApiResponse.error(400, ex.getMessage());
    }

    /**
     * 处理未知异常
     *
     * @param ex 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        logger.error("系统异常, message: {}", ex.getMessage(), ex);
        return ApiResponse.error(500, "系统繁忙，请稍后重试");
    }
}