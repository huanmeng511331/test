package com.dtastudio.todo.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待办事项展示对象。
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 事项名称 */
    private String name;

    /** 事项描述 */
    private String description;

    /** 状态：0-未完成，1-已完成 */
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TodoVO{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", status=" + status
                + ", gmtCreate=" + gmtCreate
                + '}';
    }
}