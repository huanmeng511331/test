package com.example.todo.common.constant;

/**
 * 待办事项模块常量
 *
 * @author AiWork
 * @date 2026/10/27
 */
public final class TodoConstants {

    /**
     * 事项名称最大长度
     */
    public static final int NAME_MAX_LENGTH = 100;

    /**
     * 事项描述最大长度
     */
    public static final int DESCRIPTION_MAX_LENGTH = 500;

    private TodoConstants() {
        throw new IllegalStateException("Utility class");
    }
}