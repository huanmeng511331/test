package com.todoapp.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.common.constant.CommonConstants;
import com.todoapp.common.context.LoginContext;
import com.todoapp.common.context.LoginContextHolder;
import com.todoapp.common.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局登录态拦截器
 *
 * <p>校验请求头中的登录用户标识（X-User-Id），通过后构建 {@link LoginContext}
 * 存入 {@link LoginContextHolder}。未登录则返回 401 + 错误响应。
 *
 * <p>本期不自实现登录注册流程，通过请求头模拟 SSO/IAM 下发的登录态，
 * 后续接入真实 SSO 时替换请求头来源即可。
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    /** 登录用户标识请求头 */
    private static final String HEADER_USER_ID = "X-User-Id";

    /** 租户标识请求头 */
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader(HEADER_USER_ID);
        if (userId == null || userId.isBlank()) {
            logger.warn("login check failed, uri: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ApiResponse<Void> errorResponse = ApiResponse.error("AUTH_001", "未登录");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        String tenantId = request.getHeader(HEADER_TENANT_ID);
        if (tenantId == null) {
            tenantId = CommonConstants.DEFAULT_TENANT_ID;
        }

        LoginContextHolder.set(new LoginContext(userId, tenantId));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LoginContextHolder.clear();
    }
}
