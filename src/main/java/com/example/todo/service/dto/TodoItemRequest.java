package com.example.todo.service.dto;

import com.example.todo.common.constant.TodoConstants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增待办事项请求对象
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoItemRequest {

    /**
     * 事项名称
     */
    @NotBlank(message = "事项名称不能为空")
    @Size(max = TodoConstants.NAME_MAX_LENGTH, message = "事项名称长度不能超过" + TodoConstants.NAME_MAX_LENGTH)
    private String name;

    /**
     * 事项描述
     */
    @Size(max = TodoConstants.DESCRIPTION_MAX_LENGTH, message = "事项描述长度不能超过" + TodoConstants.DESCRIPTION_MAX_LENGTH)
    private String description;

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

    @Override
    public String toString() {
        return "TodoItemRequest{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + '}';
    }
}