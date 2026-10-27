package com.dtazziboot.todo.controller;

import com.dtazziboot.todo.common.model.ApiResponse;
import com.dtazziboot.todo.model.dto.TodoCreateRequest;
import com.dtazziboot.todo.model.dto.TodoVO;
import com.dtazziboot.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器。
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        TodoVO todoVO = todoService.createTodo(request);
        return ApiResponse.success(todoVO);
    }
}