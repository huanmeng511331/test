package com.dtazza.todo.api.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增待办事项请求。
 */
public class CreateTodoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "事项名称不能为空")
    @Size(max = 200, message = "事项名称长度不能超过200")
    private String title;

    @Size(max = 2000, message = "事项描述长度不能超过2000")
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
        return "CreateTodoRequest{"
                + "title='" + title + '\''
                + ", description='" + description + '\''
                + '}';
    }
}
