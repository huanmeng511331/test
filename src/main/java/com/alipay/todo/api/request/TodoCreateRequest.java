package com.alipay.todo.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建待办事项请求。
 */
public class TodoCreateRequest {

    @NotBlank(message = "事项名称不能为空")
    @Size(max = 100, message = "事项名称长度不能超过100")
    private String name;

    @Size(max = 500, message = "事项描述长度不能超过500")
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
        return "TodoCreateRequest{name='" + name + "', description='" + description + "'}";
    }
}
