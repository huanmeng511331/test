package com.dtazza.todo.api.controller;

import com.dtazza.todo.api.request.CreateTodoRequest;
import com.dtazza.todo.common.model.ApiResponse;
import com.dtazza.todo.model.vo.TodoVO;
import com.dtazza.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项开放接口。
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项。
     *
     * @param request 新增请求
     * @return 创建后的待办事项
     */
    @PostMapping
    public ApiResponse<TodoVO> create(@Valid @RequestBody CreateTodoRequest request) {
        return ApiResponse.success(todoService.create(request));
    }
}
