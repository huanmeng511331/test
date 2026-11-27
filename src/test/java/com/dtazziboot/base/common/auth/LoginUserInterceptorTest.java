package com.dtazziboot.base.common.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dtazziboot.base.common.api.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LoginUserInterceptor 拦截测试:/api/** 登录态出口(AUTH_001)与 traceId MDC 注入/清理(CR P2-7/P1-2)。
 */
class LoginUserInterceptorTest {

    private static final String CREATE_URI = "/api/todo/items";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StubController.capturedTraceId = null;
        mockMvc = MockMvcBuilders.standaloneSetup(new StubController())
                .addInterceptors(new LoginUserInterceptor(new ObjectMapper()))
                .build();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        LoginUserContext.clear();
    }

    @Test
    @DisplayName("未登录(缺 X-User-Id):返回 AUTH_001 不进入业务,拒绝分支就地清理 MDC")
    void should_returnAuth001_when_userHeaderMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("AUTH_001"))
                .andExpect(jsonPath("$.msg").value("登录态失效,请重新登录"));

        // Assert:preHandle=false 时 afterCompletion 不会回调,须由拒绝分支就地清理
        assertThat(MDC.get("traceId")).isNull();
    }

    @Test
    @DisplayName("已登录(带 X-User-Id):放行业务,网关 traceId 透传注入并在完成后清理")
    void should_passThrough_when_userHeaderPresent() throws Exception {
        // Act & Assert
        mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON)
                        .header(LoginUserInterceptor.USER_HEADER, "zhangsan")
                        .header(LoginUserInterceptor.TRACE_HEADER, "trace-123")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        // Assert:业务处理期间 traceId 已注入(透传优先),完成后 MDC 已清理
        assertThat(StubController.capturedTraceId).isEqualTo("trace-123");
        assertThat(MDC.get("traceId")).isNull();
    }

    @Test
    @DisplayName("已登录且无网关 traceId:自动生成 32 位 traceId")
    void should_generateTraceId_when_traceHeaderAbsent() throws Exception {
        // Act & Assert
        mockMvc.perform(post(CREATE_URI).contentType(MediaType.APPLICATION_JSON)
                        .header(LoginUserInterceptor.USER_HEADER, "zhangsan")
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"));

        // Assert
        assertThat(StubController.capturedTraceId).isNotBlank().hasSize(32);
    }

    /**
     * 桩控制器:仅用于驱动拦截器链路,捕获业务处理期间的 MDC traceId。
     */
    @RestController
    static class StubController {

        static String capturedTraceId;

        @PostMapping(CREATE_URI)
        public ApiResult<String> create() {
            capturedTraceId = MDC.get("traceId");
            return ApiResult.ok("done");
        }
    }
}
