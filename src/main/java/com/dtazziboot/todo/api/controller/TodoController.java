package com.dtazziboot.todo.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dtazziboot.todo.common.model.ApiResponse;
import com.dtazziboot.todo.model.dto.TodoRequest;
import com.dtazziboot.todo.model.dto.TodoVO;
import com.dtazziboot.todo.service.TodoService;

import jakarta.validation.Valid;

/**
 * 待办事项控制器
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    /**
     * 待办事项业务服务
     */
    private final TodoService todoService;

    /**
     * 构造注入依赖
     *
     * @param todoService 待办事项业务服务
     */
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 创建成功的待办事项
     */
    @PostMapping
    public ApiResponse<TodoVO> createTodo(@Valid @RequestBody TodoRequest request) {
        TodoVO todoVO = todoService.createTodo(request);
        return ApiResponse.success(todoVO);
    }
}