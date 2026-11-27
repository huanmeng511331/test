package com.dtazziboot.base.todo.constant;

/**
 * 待办模块错误码常量。
 *
 * <p>TODO_004(功能维护中)为可应急功能开关关闭时的返回码,
 * 是系分错误码表 TODO_001~TODO_003 的应急扩展项。</p>
 */
public final class TodoErrorCodeConstants {

    /** 事项名称不能为空 */
    public static final String TODO_NAME_REQUIRED = "TODO_001";

    /** 事项名称超过 64 字符 */
    public static final String TODO_NAME_TOO_LONG = "TODO_002";

    /** 事项描述超过 500 字符 */
    public static final String TODO_DESCRIPTION_TOO_LONG = "TODO_003";

    /** 功能维护中(创建开关关闭) */
    public static final String TODO_MAINTENANCE = "TODO_004";

    private TodoErrorCodeConstants() {
        throw new IllegalStateException("Utility class");
    }
}
