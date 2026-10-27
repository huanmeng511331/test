package com.dtaz.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增待办事项请求对象
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoCreateRequest {

    /** 事项名称 */
    @NotBlank(message = "事项名称不能为空")
    @Size(max = 100, message = "事项名称长度不能超过100")
    private String title;

    /** 事项描述 */
    @Size(max = 500, message = "事项描述长度不能超过500")
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
        return "TodoCreateRequest{"
                + "title='" + title + '\''
                + ", description='" + description + '\''
                + '}';
    }
}