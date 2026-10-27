package com.antdigital.todo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增待办事项的请求对象。
 */
public class TodoCreateRequest {

    /** 事项名称 */
    @NotBlank(message = "事项名称不能为空")
    @Size(max = 200, message = "事项名称长度不能超过200个字符")
    private String itemName;

    /** 事项描述 */
    @Size(max = 1000, message = "事项描述长度不能超过1000个字符")
    private String description;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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
                + "itemName='" + itemName + '\''
                + ", description='" + description + '\''
                + '}';
    }
}