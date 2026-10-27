package com.dtazziboot.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.dtazziboot.todo.common.constant.TodoConstants;

/**
 * 新增待办事项请求对象
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
public class TodoRequest {

    /**
     * 待办事项名称
     */
    @NotBlank(message = "待办事项名称不能为空")
    @Size(max = TodoConstants.NAME_MAX_LENGTH, message = "待办事项名称长度不能超过100")
    private String name;

    /**
     * 待办事项描述
     */
    @Size(max = TodoConstants.DESCRIPTION_MAX_LENGTH, message = "待办事项描述长度不能超过500")
    private String description;

    /**
     * 获取待办事项名称
     *
     * @return 待办事项名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置待办事项名称
     *
     * @param name 待办事项名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取待办事项描述
     *
     * @return 待办事项描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置待办事项描述
     *
     * @param description 待办事项描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TodoRequest{" + "name='" + name + '\'' + ", description='" + description + '\'' + '}';
    }
}