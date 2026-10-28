package com.todoapp.common.context;

import com.todoapp.common.exception.BusinessException;

/**
 * 登录上下文持有器（ThreadLocal）
 *
 * <p>基于 ThreadLocal 在请求线程内传递 {@link LoginContext}，
 * 拦截器在 afterCompletion 中负责清理，避免线程池复用导致的上下文泄漏。
 *
 * @author AiWork
 * @date 2026/10/28
 */
public final class LoginContextHolder {

    private static final ThreadLocal<LoginContext> CONTEXT_HOLDER = new ThreadLocal<>();

    /** 未登录错误码 */
    private static final String AUTH_NOT_LOGIN_CODE = "AUTH_001";

    /** 未登录提示信息 */
    private static final String AUTH_NOT_LOGIN_MSG = "未登录";

    private LoginContextHolder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 设置登录上下文
     *
     * @param context 登录上下文
     */
    public static void set(LoginContext context) {
        CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取登录上下文，未登录时抛出业务异常
     *
     * @return 登录上下文
     * @throws BusinessException 未登录时抛出
     */
    public static LoginContext get() {
        LoginContext context = CONTEXT_HOLDER.get();
        if (context == null) {
            throw new BusinessException(AUTH_NOT_LOGIN_CODE, AUTH_NOT_LOGIN_MSG);
        }
        return context;
    }

    /**
     * 清除登录上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
