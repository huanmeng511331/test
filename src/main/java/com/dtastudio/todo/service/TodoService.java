package com.dtastudio.todo.service;

import com.dtastudio.todo.model.dto.TodoCreateRequest;
import com.dtastudio.todo.model.dto.TodoVO;

/**
 * 待办事项业务服务接口。
 *
 * @author AiWork
 * @date 2026/10/27
 */
public interface TodoService {

    /**
     * 新增待办事项。
     *
     * @param request 新增请求
     * @return 创建成功的待办事项展示对象
     */
    TodoVO createTodo(TodoCreateRequest request);
}