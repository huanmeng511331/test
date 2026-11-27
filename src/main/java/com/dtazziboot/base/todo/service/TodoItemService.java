package com.dtazziboot.base.todo.service;

import com.dtazziboot.base.todo.model.dto.CreateTodoItemRequest;
import com.dtazziboot.base.todo.model.dto.TodoItemResult;

/**
 * 待办事项服务。
 */
public interface TodoItemService {

    /**
     * 新增待办事项:组装创建人/租户/时间并落库,返回创建结果。
     *
     * @param request 新增待办请求(名称必填,描述选填)
     * @return 创建结果(含系统生成的待办 ID 与创建时间)
     */
    TodoItemResult createTodoItem(CreateTodoItemRequest request);
}
