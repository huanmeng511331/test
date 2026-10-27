package com.dtk.todo.model.entity;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoItemDO {

    /** 主键ID */
    private Long id;
    /** 事项名称 */
    private String title;
    /** 事项描述 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 更新时间 */
    private LocalDateTime modifiedTime;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return "TodoItemDO{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", createTime=" + createTime
                + ", modifiedTime=" + modifiedTime
                + '}';
    }
}
