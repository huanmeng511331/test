package com.dtazziboot.todo.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 新增待办事项请求。
 */
public class TodoItemCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 事项名称，必填 */
    @NotBlank(message = "事项名称不能为空")
    private String name;

    /** 事项描述 */
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
        return "TodoItemCreateRequest{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + '}';
    }
}