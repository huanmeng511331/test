package com.alipay.todo.api.controller;

import com.alipay.todo.api.request.TodoCreateRequest;
import com.alipay.todo.common.model.ApiResponse;
import com.alipay.todo.common.exception.TodoException;
import com.alipay.todo.model.vo.TodoVO;
import com.alipay.todo.service.TodoService;

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

    /**
     * 新增待办事项。
     */
    @PostMapping
    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        TodoVO vo = todoService.createTodo(request.getName(), request.getDescription());
        return ApiResponse.success(vo);
    }

    /**
     * 业务异常兜底处理示例：仅演示异常如何转换为统一的错误响应。
     * 实际全局异常处理器可在后续迭代接入。
     */
    public ApiResponse<Void> handleTodoException(TodoException ex) {
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage(), ex.getUserTip());
    }
}
