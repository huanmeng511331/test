package com.dtaz.todo.api.controller;

import com.dtaz.todo.common.model.ApiResponse;
import com.dtaz.todo.model.dto.TodoCreateRequest;
import com.dtaz.todo.model.vo.TodoItemVO;
import com.dtaz.todo.service.TodoItemService;
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
@RequestMapping("/todos")
public class TodoController {

    private final TodoItemService todoItemService;

    public TodoController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 创建成功的待办事项
     */
    @PostMapping
    public ApiResponse<TodoItemVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        TodoItemVO vo = todoItemService.createTodo(request);
        return ApiResponse.success(vo);
    }
}