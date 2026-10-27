package com.dtaz.todo.common.constant;

/**
 * 错误码枚举，遵循「来源 + 四位数字」命名规则。
 */
public enum ErrorCodeEnum {

    /** 参数错误 */
    INVALID_PARAMETER("A0001", "参数错误"),

    /** 业务处理出错 */
    BUSINESS_ERROR("B0001", "业务处理出错"),

    /** 系统内部错误 */
    SYSTEM_ERROR("B0002", "系统内部错误");

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