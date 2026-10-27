package com.dtaz.todo.controller;

import com.dtaz.todo.common.model.ApiResponse;
import com.dtaz.todo.model.dto.TodoRequest;
import com.dtaz.todo.model.vo.TodoVO;
import com.dtaz.todo.service.TodoService;
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
     *
     * @param request 新增请求
     * @return 创建后的待办事项
     */
    @PostMapping
    public ApiResponse<TodoVO> create(@Valid @RequestBody TodoRequest request) {
        TodoVO vo = todoService.create(request);
        return ApiResponse.success(vo);
    }
}