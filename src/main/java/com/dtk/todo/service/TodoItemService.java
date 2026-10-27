package com.dtk.todo.service;

import com.dtk.todo.model.dto.TodoItemCreateRequest;
import com.dtk.todo.model.dto.TodoItemVO;

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
     * @param request 新增请求对象
     * @return 新增后的待办事项视图对象
     */
    TodoItemVO createTodoItem(TodoItemCreateRequest request);
}
