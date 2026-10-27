package com.dtk.todo.common.constant;

/**
 * 待办事项错误码枚举
 *
 * @author AiWork
 * @date 2026/10/27
 */
public enum TodoErrorCodeEnum {

    /** 参数不合法 */
    PARAM_INVALID("A0001", "请求参数不合法", "请检查输入内容"),

    /** 事项名称不能为空 */
    TITLE_REQUIRED("A0002", "事项名称不能为空", "请输入事项名称");

    /** 错误码 */
    private final String code;
    /** 错误信息 */
    private final String errorMessage;
    /** 用户提示 */
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
