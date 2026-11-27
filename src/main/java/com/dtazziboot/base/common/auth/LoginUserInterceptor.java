package com.dtazziboot.base.common.auth;

import com.dtazziboot.base.common.api.ApiResult;
import com.dtazziboot.base.common.constant.CommonErrorCodeConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录态全局拦截器:/api/** 统一校验,未登录直接返回 AUTH_001,不进入业务逻辑。
 *
 * <p>底座登录态最小桩实现:由前置网关/SSO 完成认证后透传用户头
 * {@code X-User-Id}(工号/账号);后续接入真实 SSO 时仅替换本拦截器的身份解析逻辑。</p>
 *
 * <p>同时注入链路标识:优先透传网关头 {@code X-Trace-Id},缺省自动生成;
 * 写入 MDC 供日志 pattern 的 %X{traceId} 输出,请求结束(含未登录拒绝分支)统一清理。</p>
 */
@Component
@RequiredArgsConstructor
public class LoginUserInterceptor implements HandlerInterceptor {

    /** 网关认证透传的用户头 */
    public static final String USER_HEADER = "X-User-Id";

    /** 网关透传的链路标识头 */
    public static final String TRACE_HEADER = "X-Trace-Id";

    /** MDC 中的链路标识 key,与 logging.pattern.console 的 %X{traceId} 对应 */
    private static final String MDC_TRACE_ID_KEY = "traceId";

    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String traceId = request.getHeader(TRACE_HEADER);
        MDC.put(MDC_TRACE_ID_KEY, traceId == null || traceId.isBlank()
                ? UUID.randomUUID().toString().replace("-", "") : traceId);
        String user = request.getHeader(USER_HEADER);
        if (user == null || user.isBlank()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(
                    ApiResult.fail(CommonErrorCodeConstants.AUTH_LOGIN_REQUIRED, "登录态失效,请重新登录")));
            // preHandle 返回 false 时本拦截器的 afterCompletion 不会被回调,须就地清理,避免 MDC 跨请求泄漏
            MDC.remove(MDC_TRACE_ID_KEY);
            return false;
        }
        LoginUserContext.setCurrentUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        MDC.remove(MDC_TRACE_ID_KEY);
        LoginUserContext.clear();
    }
}
