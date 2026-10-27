package com.dtk.todo.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 新增待办事项请求对象
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoItemCreateRequest {

    /** 事项名称 */
    @NotBlank(message = "事项名称不能为空")
    private String title;

    /** 事项描述 */
    private String description;

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

    @Override
    public String toString() {
        return "TodoItemCreateRequest{"
                + "title='" + title + '\''
                + ", description='" + description + '\''
                + '}';
    }
}
