package com.dtaz.todo.model.entity;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象，对应表 todo。
 */
public class TodoDO {

    /** 主键ID */
    private Long id;

    /** 事项名称 */
    private String name;

    /** 事项描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

    /** 更新时间 */
    private LocalDateTime gmtModified;

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

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "TodoDO{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", gmtCreate=" + gmtCreate
                + '}';
    }
}