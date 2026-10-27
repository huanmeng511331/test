package com.dtazziboot.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.dtazziboot.todo.common.constant.TodoConstants;

/**
 * 新增待办事项请求对象。
 */
public class TodoCreateRequest {

    @NotBlank(message = "事项名称不能为空")
    @Size(max = TodoConstants.MAX_NAME_LENGTH, message = "事项名称长度不能超过100")
    private String name;

    @Size(max = TodoConstants.MAX_DESCRIPTION_LENGTH, message = "事项描述长度不能超过500")
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
        return "TodoCreateRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}