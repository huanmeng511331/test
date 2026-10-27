package com.antdigital.todo.service;

import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.vo.TodoItemVO;

/**
 * 待办事项业务服务。
 */
public interface TodoService {

    /**
     * 新增待办事项。
     *
     * @param request 待办事项请求对象
     * @return 持久化后的待办事项展示对象
     */
    TodoItemVO createTodo(TodoCreateRequest request);
}