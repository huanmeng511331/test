package com.todoapp.todo.api.controller;

import com.todoapp.common.context.LoginContext;
import com.todoapp.common.context.LoginContextHolder;
import com.todoapp.common.model.ApiResponse;
import com.todoapp.todo.api.request.TodoCreateApiRequest;
import com.todoapp.todo.api.response.TodoCreateData;
import com.todoapp.todo.constant.TodoErrorCode;
import com.todoapp.todo.service.TodoService;
import com.todoapp.todo.service.dto.TodoCreateRequest;
import com.todoapp.todo.service.dto.TodoCreateResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项控制器
 *
 * @author AiWork
 * @date 2026/10/28
 */
@RestController
@RequestMapping("/api/todo")
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    @Value("${feature.todo.create.enabled:true}")
    private boolean todoCreateEnabled;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 新增待办事项
     *
     * <p>POST /api/todo/create
     *
     * @param apiRequest API 请求（title, description）
     * @return 通用响应，data 包含新建待办 ID
     */
    @PostMapping("/create")
    public ApiResponse<TodoCreateData> create(@Valid @RequestBody TodoCreateApiRequest apiRequest) {
        // 功能开关：可灰度、可应急
        if (!todoCreateEnabled) {
            logger.warn("todo create feature disabled");
            return ApiResponse.error(TodoErrorCode.FEATURE_DISABLED.getCode(),
                    TodoErrorCode.FEATURE_DISABLED.getMessage());
        }

        // 从登录上下文获取 creator / tenantId，禁止前端传入
        LoginContext loginContext = LoginContextHolder.get();

        // 构建 Service 层请求
        TodoCreateRequest serviceRequest = new TodoCreateRequest();
        serviceRequest.setTitle(apiRequest.getTitle());
        serviceRequest.setDescription(apiRequest.getDescription());
        serviceRequest.setCreator(loginContext.getUserId());
        serviceRequest.setTenantId(loginContext.getTenantId());

        // 调用 Service
        TodoCreateResult result = todoService.createTodo(serviceRequest);

        return ApiResponse.success(new TodoCreateData(result.getId()));
    }
}
