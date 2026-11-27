package com.dtazziboot.base.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.dtazziboot.base.common.constant.DateTimeConstants;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 自动填充:插入时维护 gmt_create / gmt_modified(应用层维护时间,显式统一时区)。
 */
@Component
public class GmtFieldsMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now(DateTimeConstants.SYSTEM_ZONE);
        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime.class,
                LocalDateTime.now(DateTimeConstants.SYSTEM_ZONE));
    }
}
