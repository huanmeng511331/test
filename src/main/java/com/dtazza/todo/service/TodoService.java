package com.dtazza.todo.service;

import com.dtazza.todo.api.request.CreateTodoRequest;
import com.dtazza.todo.model.vo.TodoVO;

/**
 * 待办事项业务服务。
 */
public interface TodoService {

    /**
     * 新增一条待办事项。
     *
     * @param request 新增请求
     * @return 创建后的待办事项
     */
    TodoVO create(CreateTodoRequest request);
}
