package com.dtazziboot.todo.controller;

import com.dtazziboot.todo.common.result.ApiResult;
import com.dtazziboot.todo.model.dto.TodoItemCreateRequest;
import com.dtazziboot.todo.model.dto.TodoItemVO;
import com.dtazziboot.todo.service.TodoItemService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器。
 */
@Validated
@RestController
@RequestMapping("/todo_items")
public class TodoItemController {

    private final TodoItemService todoItemService;

    public TodoItemController(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    /**
     * 新增待办事项。
     *
     * @param request 新增请求
     * @return 新建成功的待办事项
     */
    @PostMapping
    public ApiResult<TodoItemVO> create(@Valid @RequestBody TodoItemCreateRequest request) {
        TodoItemVO vo = todoItemService.createTodoItem(request);
        return ApiResult.success(vo);
    }
}