package com.dtazziboot.base.common.constant;

/**
 * 底座通用错误码常量。
 *
 * <p>格式: {MODULE}_{SEQ};错误码不能直接作为用户提示语输出,提示语见各抛出点。</p>
 */
public final class CommonErrorCodeConstants {

    /** 登录态失效/未登录 */
    public static final String AUTH_LOGIN_REQUIRED = "AUTH_001";

    /** 请求参数错误(请求体缺失/非 JSON 等) */
    public static final String PARAM_ERROR = "SYS_001";

    /** 系统内部异常(兜底) */
    public static final String SYS_ERROR = "SYS_999";

    private CommonErrorCodeConstants() {
        throw new IllegalStateException("Utility class");
    }
}
