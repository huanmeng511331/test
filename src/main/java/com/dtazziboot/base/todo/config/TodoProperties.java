package com.dtazziboot.base.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 待办模块配置项(对应 todo.create.enabled 应急开关)。
 */
@Data
@Component
@ConfigurationProperties(prefix = "todo")
public class TodoProperties {

    private final Create create = new Create();

    @Data
    public static class Create {

        /** 新增待办功能开关,默认开启 */
        private boolean enabled = true;
    }
}
