package com.example.todo.service;

import com.example.todo.service.dto.TodoItemRequest;
import com.example.todo.service.dto.TodoItemVO;

/**
 * 待办事项服务接口
 *
 * @author AiWork
 * @date 2026/10/27
 */
public interface TodoItemService {

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 新增成功后的待办事项视图对象
     */
    TodoItemVO createTodoItem(TodoItemRequest request);
}