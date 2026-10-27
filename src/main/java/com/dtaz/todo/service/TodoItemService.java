package com.dtaz.todo.service;

import com.dtaz.todo.model.dto.TodoCreateRequest;
import com.dtaz.todo.model.vo.TodoItemVO;

/**
 * 待办事项业务服务
 *
 * @author AiWork
 * @date 2026/10/27
 */
public interface TodoItemService {

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 创建成功的待办事项视图对象
     */
    TodoItemVO createTodo(TodoCreateRequest request);
}