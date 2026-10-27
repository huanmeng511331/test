package com.alipay.todo.common.exception;

/**
 * 待办事项错误码枚举。
 *
 * <p>错误码格式遵循“错误来源 + 四位数字”：A 表示来源于用户端错误。</p>
 */
public enum TodoErrorCodeEnum {

    /** 事项名称不能为空 */
    TODO_NAME_BLANK("A0001", "事项名称不能为空"),

    /** 事项名称长度超过限制 */
    TODO_NAME_TOO_LONG("A0002", "事项名称长度超过限制"),

    /** 事项描述长度超过限制 */
    TODO_DESCRIPTION_TOO_LONG("A0003", "事项描述长度超过限制");

    private final String code;

    private final String message;

    TodoErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
