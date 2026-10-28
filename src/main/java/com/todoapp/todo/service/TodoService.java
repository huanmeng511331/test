package com.todoapp.todo.service;

import com.todoapp.todo.service.dto.TodoCreateRequest;
import com.todoapp.todo.service.dto.TodoCreateResult;

/**
 * 待办事项服务接口
 *
 * @author AiWork
 * @date 2026/10/28
 */
public interface TodoService {

    /**
     * 创建待办事项
     *
     * <p>校验事项名称非空且长度合规、描述长度合规；
     * 填充 creator/tenant_id 后持久化待办记录。
     *
     * @param request 创建待办请求
     * @return 创建结果，包含新建待办 ID
     */
    TodoCreateResult createTodo(TodoCreateRequest request);
}
