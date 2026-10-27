package com.dtk.todo.model.dto;

import java.time.LocalDateTime;

/**
 * 待办事项视图对象
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoItemVO {

    /** 主键ID */
    private Long id;
    /** 事项名称 */
    private String title;
    /** 事项描述 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createTime;

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

    @Override
    public String toString() {
        return "TodoItemVO{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", createTime=" + createTime
                + '}';
    }
}
