package com.dtazziboot.base.common.auth;

import com.dtazziboot.base.common.api.ApiResult;
import com.dtazziboot.base.common.constant.CommonErrorCodeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态全局拦截器:/api/** 统一校验,未登录直接返回 AUTH_001,不进入业务逻辑。
 *
 * <p>底座登录态最小桩实现:由前置网关/SSO 完成认证后透传用户头
 * {@code X-User-Id}(工号/账号);后续接入真实 SSO 时仅替换本拦截器的身份解析逻辑。</p>
 */
@Component
@RequiredArgsConstructor
public class LoginUserInterceptor implements HandlerInterceptor {

    /** 网关认证透传的用户头 */
    public static final String USER_HEADER = "X-User-Id";

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String user = request.getHeader(USER_HEADER);
        if (user == null || user.isBlank()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResult.fail(CommonErrorCodeConstants.AUTH_LOGIN_REQUIRED, "登录态失效,请重新登录")));
            return false;
        }
        LoginUserContext.setCurrentUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        LoginUserContext.clear();
    }
}
