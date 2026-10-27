package com.dtastudio.todo.controller;

import com.dtastudio.todo.common.model.ApiResponse;
import com.dtastudio.todo.model.dto.TodoCreateRequest;
import com.dtastudio.todo.model.dto.TodoVO;
import com.dtastudio.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器。
 *
 * @author AiWork
 * @date 2026/10/27
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    /**
     * 构造注入待办事项业务服务。
     *
     * @param todoService 待办事项业务服务
     */
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项。
     *
     * @param request 新增请求
     * @return 创建成功的待办事项
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        return ApiResponse.success(todoService.createTodo(request));
    }
}