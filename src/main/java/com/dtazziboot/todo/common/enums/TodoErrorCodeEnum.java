package com.dtazziboot.todo.common.enums;

/**
 * 待办事项错误码（来源：当前系统 B）。
 */
public enum TodoErrorCodeEnum {

    PARAM_INVALID("A0001", "请求参数不合法"),
    SYSTEM_ERROR("B0001", "系统执行出错");

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