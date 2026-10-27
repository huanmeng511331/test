package com.antdigital.todo.common.enums;

/**
 * 业务错误码枚举，遵循「来源 + 四位数字」的 5 位错误码规范。
 * A：来源于用户；B：来源于当前系统。
 */
public enum ErrorCodeEnum {

    /** 用户端参数错误 */
    INVALID_PARAMETER("A0001", "请求参数错误"),

    /** 系统执行异常 */
    SYSTEM_ERROR("B0001", "系统执行出错");

    private final String code;

    private final String message;

    ErrorCodeEnum(String code, String message) {
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