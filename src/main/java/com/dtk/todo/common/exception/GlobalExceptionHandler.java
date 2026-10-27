package com.dtk.todo.common.exception;

import com.dtk.todo.common.constant.TodoErrorCodeEnum;
import com.dtk.todo.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
     * 处理参数校验异常
     *
     * @param exception 参数校验异常
     * @return 参数错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("参数校验失败: {}", exception.getMessage());
        }
        ApiResponse<Void> response = ApiResponse.error(TodoErrorCodeEnum.PARAM_INVALID.getCode(),
                TodoErrorCodeEnum.PARAM_INVALID.getErrorMessage(),
                TodoErrorCodeEnum.PARAM_INVALID.getUserTip());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 处理业务异常
     *
     * @param exception 业务异常
     * @return 业务错误响应
     */
    @ExceptionHandler(TodoException.class)
    public ResponseEntity<ApiResponse<Void>> handleTodoException(TodoException exception) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("业务异常, errorCode: {}, message: {}",
                    exception.getErrorCode(), exception.getErrorMessage());
        }
        ApiResponse<Void> response = ApiResponse.error(exception.getErrorCode(),
                exception.getErrorMessage(), exception.getUserTip());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 处理未知系统异常
     *
     * @param exception 未知异常
     * @return 系统错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        LOGGER.error("系统异常: {}", exception.getMessage(), exception);
        ApiResponse<Void> response = ApiResponse.error("B0001", "系统异常", "系统繁忙，请稍后重试");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
