package com.example.todo.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.common.result.ApiResponse;
import com.example.todo.service.TodoItemService;
import com.example.todo.service.dto.TodoItemRequest;
import com.example.todo.service.dto.TodoItemVO;

/**
 * 待办事项控制器
 *
 * @author AiWork
 * @date 2026/10/27
 */
@RestController
@RequestMapping("/todos")
public class TodoItemController {

    private final TodoItemService todoItemService;

    /**
     * 构造注入待办事项服务
     *
     * @param todoItemService 待办事项服务
     */
    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 新增成功后的待办事项视图对象
     */
    @PostMapping
    public ApiResponse<TodoItemVO> createTodoItem(@Valid @RequestBody TodoItemRequest request) {
        TodoItemVO vo = todoItemService.createTodoItem(request);
        return ApiResponse.success(vo);
    }
}