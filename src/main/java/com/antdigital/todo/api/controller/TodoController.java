package com.antdigital.todo.api.controller;

import com.antdigital.todo.common.response.ApiResponse;
import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.vo.TodoItemVO;
import com.antdigital.todo.service.TodoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项开放接口层。
 */
@RestController
@RequestMapping("/todos")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项。
     *
     * @param request 待办事项请求对象
     * @return 统一响应结构，data 为新增的待办事项展示对象
     */
    @PostMapping
    public ApiResponse<TodoItemVO> createTodo(
            @Valid @RequestBody TodoCreateRequest request) {
        logger.info("收到新增待办事项请求, itemName: {}", request.getItemName());
        TodoItemVO vo = todoService.createTodo(request);
        return ApiResponse.success(vo);
    }
}