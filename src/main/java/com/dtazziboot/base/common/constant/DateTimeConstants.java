package com.dtazziboot.base.common.constant;

import java.time.ZoneId;

/**
 * 日期时间统一常量。
 *
 * <p>应用统一时区与 JDBC 连接串 {@code serverTimezone=Asia/Shanghai} 对齐:
 * 获取当前时间必须显式传入该时区,禁止依赖 JVM 默认时区
 * (宿主机时区可能为 UTC+0,会造成落库墙钟 8 小时偏差,见 CR P1-1/M016)。</p>
 */
public final class DateTimeConstants {

    /** 应用统一时区(与 JDBC serverTimezone 一致) */
    public static final ZoneId SYSTEM_ZONE = ZoneId.of("Asia/Shanghai");

    private DateTimeConstants() {
        throw new IllegalStateException("Utility class");
    }
}
