CREATE TABLE todo_item (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name         VARCHAR(100) NOT NULL COMMENT '事项名称',
    description  VARCHAR(500) DEFAULT NULL COMMENT '事项描述',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT pk_id PRIMARY KEY (id),
    KEY idx_gmt_create (gmt_create)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '待办事项表';