package com.todoapp.todo.service.dto;

/**
 * 创建待办结果（Service 层 DTO）
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class TodoCreateResult {

    /** 新建待办 ID */
    private final Long id;

    public TodoCreateResult(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TodoCreateResult{id=" + id + '}';
    }
}
