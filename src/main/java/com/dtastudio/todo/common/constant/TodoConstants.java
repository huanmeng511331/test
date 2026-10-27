package com.dtastudio.todo.common.constant;

/**
 * 待办事项常量定义。
 *
 * @author AiWork
 * @date 2026/10/27
 */
public final class TodoConstants {

    private TodoConstants() {
    }

    /** 事项名称最大长度 */
    public static final int TODO_NAME_MAX_LENGTH = 100;

    /** 事项描述最大长度 */
    public static final int TODO_DESCRIPTION_MAX_LENGTH = 500;

    /** 待办状态：未完成 */
    public static final int STATUS_UNDONE = 0;

    /** 待办状态：已完成 */
    public static final int STATUS_DONE = 1;
}