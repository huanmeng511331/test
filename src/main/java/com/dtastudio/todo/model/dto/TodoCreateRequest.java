package com.dtastudio.todo.model.dto;

import com.dtastudio.todo.common.constant.TodoConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * 新增待办事项请求。
 *
 * @author AiWork
 * @date 2026/10/27
 */
public class TodoCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 事项名称 */
    @NotBlank(message = "不能为空")
    @Size(max = TodoConstants.TODO_NAME_MAX_LENGTH, message = "长度不能超过" + TodoConstants.TODO_NAME_MAX_LENGTH)
    private String name;

    /** 事项描述 */
    @Size(max = TodoConstants.TODO_DESCRIPTION_MAX_LENGTH, message = "长度不能超过" + TodoConstants.TODO_DESCRIPTION_MAX_LENGTH)
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
        return "TodoCreateRequest{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + '}';
    }
}