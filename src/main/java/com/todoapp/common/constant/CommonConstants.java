package com.todoapp.common.constant;

/**
 * 系统公共常量
 *
 * @author AiWork
 * @date 2026/10/28
 */
public final class CommonConstants {

    private CommonConstants() {
        throw new IllegalStateException("Utility class");
    }

    /** 响应结果码 - 成功 */
    public static final String RESULT_OK = "OK";

    /** 响应提示信息 - 成功 */
    public static final String MSG_SUCCESS = "SUCCESS";

    /** 默认租户标识（单租户内部部署，预留空值） */
    public static final String DEFAULT_TENANT_ID = "";
}
