package com.todoapp.common.context;

/**
 * 登录上下文
 *
 * <p>持有当前请求的登录用户信息（userId）与租户标识（tenantId），
 * 由全局登录拦截器在请求前置阶段填充，业务层通过 {@link LoginContextHolder} 获取。
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class LoginContext {

    /** 登录用户标识 */
    private final String userId;

    /** 租户标识 */
    private final String tenantId;

    public LoginContext(String userId, String tenantId) {
        this.userId = userId;
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String toString() {
        return "LoginContext{userId='" + maskUserId(userId) + "', tenantId='" + tenantId + "'}";
    }

    /**
     * 对 userId 做脱敏处理（保留首尾各 1 字符，中间以 * 替代）
     *
     * @param userId 原始用户标识
     * @return 脱敏后的用户标识
     */
    public static String maskUserId(String userId) {
        if (userId == null || userId.length() <= 2) {
            return "***";
        }
        return userId.charAt(0) + "***" + userId.charAt(userId.length() - 1);
    }
}
