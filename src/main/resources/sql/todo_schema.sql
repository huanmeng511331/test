-- 待办事项表
-- 遵循 MySQL 规约：表名/字段名小写下划线、整形单列自增主键、datetime 非 timestamp、必备 id/gmt_create/gmt_modified
CREATE TABLE IF NOT EXISTS `todo` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '系统自增主键',
    `title`         VARCHAR(100) NOT NULL                 COMMENT '事项名称',
    `description`   VARCHAR(1000) NOT NULL DEFAULT ''      COMMENT '事项描述，允许空串',
    `creator`       VARCHAR(64)  NOT NULL DEFAULT ''      COMMENT '创建人（登录用户标识）',
    `tenant_id`     VARCHAR(32)  NOT NULL DEFAULT ''      COMMENT '租户标识，默认单租户空值',
    `gmt_create`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                                COMMENT '创建时间',
    `gmt_modified`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP    COMMENT '修改时间',
    PRIMARY KEY (`id`),
    INDEX `idx_todo_tenant_creator` (`tenant_id`, `creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='待办事项表';
