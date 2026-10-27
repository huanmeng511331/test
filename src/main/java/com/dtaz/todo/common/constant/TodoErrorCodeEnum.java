package com.dtaz.todo.common.constant;

/**
 * 待办事项错误码枚举
 *
 * @author AiWork
 * @date 2026/10/27
 */
public enum TodoErrorCodeEnum {

    /** 参数错误 */
    INVALID_PARAMETER("A0001", "用户参数错误", "参数不合法，请检查后重试"),

    /** 系统执行出错 */
    SYSTEM_ERROR("B0001", "系统执行出错", "系统繁忙，请稍后重试");

    /** 错误码 */
    private final String code;

    /** 错误信息（面向排查） */
    private final String errorMessage;

    /** 用户提示信息 */
    private final String userTip;

    TodoErrorCodeEnum(String code, String errorMessage, String userTip) {
        this.code = code;
        this.errorMessage = errorMessage;
        this.userTip = userTip;
    }

    public String getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }
}