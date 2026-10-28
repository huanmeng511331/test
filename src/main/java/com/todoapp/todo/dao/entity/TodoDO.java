package com.todoapp.todo.dao.entity;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象，映射 todo 表
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class TodoDO {

    /** 系统自增主键 */
    private Long id;

    /** 事项名称 */
    private String title;

    /** 事项描述，允许空串 */
    private String description;

    /** 创建人（登录用户标识） */
    private String creator;

    /** 租户标识，默认单租户空值 */
    private String tenantId;

    /** 创建时间 */
    private LocalDateTime gmtCreate;

    /** 修改时间 */
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
        return "TodoDO{id=" + id + ", title='" + title + "', description='" + description
                + "', creator='" + creator + "', tenantId='" + tenantId
                + "', gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + '}';
    }
}
