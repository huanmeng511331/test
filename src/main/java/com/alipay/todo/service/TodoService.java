package com.alipay.todo.service;

import com.alipay.todo.model.vo.TodoVO;

/**
 * 待办事项业务服务接口。
 */
public interface TodoService {

    /**
     * 新增待办事项。
     *
     * @param name        事项名称
     * @param description 事项描述
     * @return 新增后待办事项的视图对象
     */
    TodoVO createTodo(String name, String description);
}
