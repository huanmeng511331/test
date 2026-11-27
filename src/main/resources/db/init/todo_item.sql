-- 待办事项表(待办模块 todo)
-- 规范: 整型单列自增主键;datetime 时间字段;utf8mb4;无外键/enum/timestamp/float;索引前缀 pk_
CREATE TABLE IF NOT EXISTS `todo_item` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '租户ID,默认单租户',
    `name`        VARCHAR(64)  NOT NULL COMMENT '事项名称,trim 后 1~64 字符',
    `description` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '事项描述,选填,<=500 字符',
    `creator`     VARCHAR(64)  NOT NULL COMMENT '创建人工号/账号,服务端从登录态注入',
    `modifier`    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '最后修改人,创建时与 creator 相同',
    `is_deleted`  BIGINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除标记,0 未删除(预留,本次无删除语义)',
    `gmt_create`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间,应用层维护',
    `gmt_modified` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间,应用层维护',
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '待办事项表';
