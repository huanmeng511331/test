package com.dtk.todo.controller;

import com.dtk.todo.common.model.ApiResponse;
import com.dtk.todo.model.dto.TodoItemCreateRequest;
import com.dtk.todo.model.dto.TodoItemVO;
import com.dtk.todo.service.TodoItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器
 *
 * @author AiWork
 * @date 2026/10/27
 */
@RestController
@RequestMapping("/todo_items")
public class TodoItemController {

    private final TodoItemService todoItemService;

    /**
     * 构造待办事项控制器
     *
     * @param todoItemService 待办事项服务接口
     */
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增请求对象
     * @return 新增后的待办事项
     */
    @PostMapping
    public ApiResponse<TodoItemVO> create(@Valid @RequestBody TodoItemCreateRequest request) {
        TodoItemVO todoItemVO = todoItemService.createTodoItem(request);
        return ApiResponse.success(todoItemVO);
    }
}
