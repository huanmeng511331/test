package com.alipay.todo.common.constant;

/**
 * 待办事项模块常量。
 *
 * <p>统一维护待办事项相关的长度边界等常量，避免魔法值散落到业务代码中。</p>
 */
public final class TodoConstants {

    private TodoConstants() {
        throw new IllegalStateException("Utility class");
    }

    /** 事项名称最大长度 */
    public static final int NAME_MAX_LENGTH = 100;

    /** 事项描述最大长度 */
    public static final int DESCRIPTION_MAX_LENGTH = 500;
}
