-- 待办事项表
-- 说明：本脚本为建表参考 DDL，需在目标 MySQL 库中手工执行（或接入 Flyway/Liquibase 管理）。
CREATE TABLE todo (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name          VARCHAR(100) NOT NULL COMMENT '事项名称',
    description   VARCHAR(500) DEFAULT NULL COMMENT '事项描述',
    status        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0-未完成，1-已完成',
    gmt_create    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_status (status)
) COMMENT = '待办事项表';