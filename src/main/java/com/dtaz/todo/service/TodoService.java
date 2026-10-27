package com.dtaz.todo.service;

import com.dtaz.todo.model.dto.TodoRequest;
import com.dtaz.todo.model.vo.TodoVO;

/**
 * 待办事项业务服务。
 */
public interface TodoService {

    /**
     * 新增待办事项。
     *
     * @param request 新增请求
     * @return 创建后的待办事项视图对象
     */
    TodoVO create(TodoRequest request);
}