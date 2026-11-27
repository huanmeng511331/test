package com.dtazziboot.base.common.auth;

import com.dtazziboot.base.common.constant.CommonErrorCodeConstants;
import com.dtazziboot.base.common.exception.BizException;

/**
 * 登录态上下文:请求线程内保存当前登录用户(工号/账号)。
 *
 * <p>由 {@link LoginUserInterceptor} 在进入业务前写入、请求结束后清理;
 * 业务侧只读,确保创建人等身份信息不可被前端伪造。</p>
 */
public final class LoginUserContext {

    private static final ThreadLocal<String> CURRENT_USER = new ThreadLocal<>();

    private LoginUserContext() {
        throw new IllegalStateException("Utility class");
    }

    public static void setCurrentUser(String user) {
        CURRENT_USER.set(user);
    }

    public static void clear() {
        CURRENT_USER.remove();
    }

    /**
     * 获取当前登录用户,无有效登录态时抛出 AUTH_001。
     */
    public static String requireCurrentUser() {
        String user = CURRENT_USER.get();
        if (user == null || user.isBlank()) {
            throw new BizException(CommonErrorCodeConstants.AUTH_LOGIN_REQUIRED, "登录态失效,请重新登录");
        }
        return user;
    }
}
