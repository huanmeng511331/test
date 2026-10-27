package com.dtazziboot.todo.model.dto;

import java.io.Serializable;

/**
 * 待办事项展示对象。
 */
public class TodoItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID，使用字符串避免前端 Long 精度丢失 */
    private String id;

    /** 事项名称 */
    private String name;

    /** 事项描述 */
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
        return "TodoItemVO{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + '}';
    }
}