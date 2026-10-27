package com.example.todo.common.constant;

/**
 * 错误码枚举
 *
 * <p>错误码为字符串类型，共5位：错误产生来源 + 四位数字编号。
 * <ul>
 *   <li>A：错误来源于用户</li>
 *   <li>B：错误来源于当前系统</li>
 *   <li>C：错误来源于第三方服务</li>
 * </ul>
 *
 * @author AiWork
 * @date 2026/10/27
 */
public enum ErrorCodeEnum {

    /** 成功 */
    SUCCESS("00000", "success"),

    /** 用户参数错误 */
    PARAM_ERROR("A0001", "参数错误"),

    /** 系统执行出错 */
    SYSTEM_ERROR("B0001", "系统异常");

    private final String code;

    private final String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取错误描述
     *
     * @return 错误描述
     */
    public String getMessage() {
        return message;
    }
}