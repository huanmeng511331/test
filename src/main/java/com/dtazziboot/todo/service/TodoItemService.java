package com.dtazziboot.todo.service;

import com.dtazziboot.todo.model.dto.TodoItemCreateRequest;
import com.dtazziboot.todo.model.dto.TodoItemVO;

/**
 * 待办事项业务服务。
 */
public interface TodoItemService {

    /**
     * 新增待办事项。
     *
     * @param request 新增请求，不能为 null
     * @return 新建成功后的待办事项
     */
    TodoItemVO createTodoItem(TodoItemCreateRequest request);
}