package com.todoapp.todo.constant;

import com.todoapp.common.exception.BusinessException;

/**
 * 待办事项模块错误码枚举
 *
 * <p>错误码格式：{MODULE}_{SEQ}，MODULE = TODO，SEQ 三位数字。
 *
 * @author AiWork
 * @date 2026/10/28
 */
public enum TodoErrorCode {

    /** 事项名称为空或空白（R01） */
    TITLE_EMPTY("TODO_001", "事项名称不能为空"),

    /** 事项名称长度超限（R02） */
    TITLE_LENGTH_EXCEEDED("TODO_002", "事项名称长度超限"),

    /** 事项描述长度超限（R03） */
    DESCRIPTION_LENGTH_EXCEEDED("TODO_002", "事项描述长度超限"),

    /** 待办创建失败，系统异常 */
    CREATE_FAILED("TODO_003", "待办创建失败"),

    /** 功能暂不可用（应急开关关闭） */
    FEATURE_DISABLED("TODO_004", "功能暂不可用");

    private final String code;
    private final String message;

    TodoErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 转换为业务异常
     *
     * @return 业务异常
     */
    public BusinessException toException() {
        return new BusinessException(code, message);
    }

    /**
     * 转换为业务异常（携带原因）
     *
     * @param cause 原始异常
     * @return 业务异常
     */
    public BusinessException toException(Throwable cause) {
        return new BusinessException(code, message, cause);
    }
}
