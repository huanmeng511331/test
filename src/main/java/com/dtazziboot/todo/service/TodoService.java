package com.dtazziboot.todo.service;

import com.dtazziboot.todo.model.dto.TodoCreateRequest;
import com.dtazziboot.todo.model.dto.TodoVO;

/**
 * 待办事项服务接口。
 */
public interface TodoService {

    /**
     * 新增待办事项。
     *
     * @param request 新增待办事项请求
     * @return 创建后的待办事项视图对象
     */
    TodoVO createTodo(TodoCreateRequest request);
}