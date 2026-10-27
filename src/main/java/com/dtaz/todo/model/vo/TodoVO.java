package com.dtaz.todo.model.vo;

import java.time.LocalDateTime;

/**
 * 待办事项视图对象。
 */
public class TodoVO {

    /** 主键ID */
    private Long id;

    /** 事项名称 */
    private String name;

    /** 事项描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}