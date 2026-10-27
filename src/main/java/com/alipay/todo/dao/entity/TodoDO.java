package com.alipay.todo.dao.entity;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象，对应表 todo_item。
 */
public class TodoDO {

    private Long id;

    private String name;

    private String description;

    private LocalDateTime gmtCreate;

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
        return "TodoDO{id=" + id + ", name='" + name + "', description='" + description
                + "', gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + "}";
    }
}
