package com.dtazziboot.todo.service;

import com.dtazziboot.todo.model.dto.TodoRequest;
import com.dtazziboot.todo.model.dto.TodoVO;

/**
 * 待办事项业务服务
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
public interface TodoService {

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 创建成功的待办事项
     */
    TodoVO createTodo(TodoRequest request);
}