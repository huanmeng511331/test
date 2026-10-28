package com.todoapp.todo.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增待办事项 API 请求
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class TodoCreateApiRequest {

    /** 事项名称，长度 1~100 */
    @NotBlank(message = "事项名称不能为空")
    @Size(max = 100, message = "事项名称长度不能超过100")
    private String title;

    /** 事项描述，长度 0~1000，选填 */
    @Size(max = 1000, message = "事项描述长度不能超过1000")
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
        return "TodoCreateApiRequest{title='" + title + "', description='" + description + "'}";
    }
}
