package com.todoapp.todo.api.response;

/**
 * 新增待办事项 API 响应数据
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class TodoCreateData {

    /** 新建待办 ID */
    private final Long id;

    public TodoCreateData(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TodoCreateData{id=" + id + '}';
    }
}
