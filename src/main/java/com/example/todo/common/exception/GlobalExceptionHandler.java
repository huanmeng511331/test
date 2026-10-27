package com.example.todo.common.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.todo.common.constant.ErrorCodeEnum;
import com.example.todo.common.result.ApiResponse;

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
     * @param e 参数校验异常
     * @return 400 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Void> body = ApiResponse.error(ErrorCodeEnum.PARAM_ERROR, message);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 按错误码来源判定状态码的响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ApiResponse<Void> body = ApiResponse.error(e.getErrorCode(), e.getMessage(), e.getUserTip());
        return ResponseEntity.status(resolveStatus(e.getErrorCode())).body(body);
    }

    /**
     * 处理未预期异常
     *
     * @param e 未预期异常
     * @return 500 响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        LOGGER.error("system error occurred", e);
        ApiResponse<Void> body = ApiResponse.error(ErrorCodeEnum.SYSTEM_ERROR, "系统繁忙，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * 按错误码来源解析 HTTP 状态码：A 开头（用户侧）为 400，其余为 500
     *
     * @param errorCode 错误码
     * @return HTTP 状态码
     */
    private HttpStatus resolveStatus(String errorCode) {
        if (errorCode != null && errorCode.startsWith("A")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}