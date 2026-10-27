package com.dtazziboot.todo.common.constant;

/**
 * 待办事项模块常量
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
public final class TodoConstants {

    /**
     * 待办事项名称最大长度
     */
    public static final int NAME_MAX_LENGTH = 100;

    /**
     * 待办事项描述最大长度
     */
    public static final int DESCRIPTION_MAX_LENGTH = 500;

    private TodoConstants() {
        throw new IllegalStateException("Utility class");
    }
}