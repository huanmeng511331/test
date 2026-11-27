package com.dtazziboot.base.todo.controller;

import com.dtazziboot.base.common.api.ApiResult;
import com.dtazziboot.base.common.exception.BizException;
import com.dtazziboot.base.todo.config.TodoProperties;
import com.dtazziboot.base.todo.constant.TodoErrorCodeConstants;
import com.dtazziboot.base.todo.metrics.TodoItemCreateMetrics;
import com.dtazziboot.base.todo.model.dto.CreateTodoItemRequest;
import com.dtazziboot.base.todo.model.dto.TodoItemResult;
import com.dtazziboot.base.todo.service.TodoItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 待办事项 Web 控制台接口(oneapi)。
 */
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoItemController {

    /** 事项名称 trim 后最大长度 */
    private static final int NAME_MAX_LENGTH = 64;

    /** 事项描述最大长度 */
    private static final int DESCRIPTION_MAX_LENGTH = 500;

    private final TodoItemService todoItemService;

    private final TodoProperties todoProperties;

    private final TodoItemCreateMetrics todoItemCreateMetrics;

    /**
     * W01 新增待办事项:POST /api/todo/items。
     *
     * <p>埋点(系分 §7.1,CR P0-3):成功/业务失败(按错误码)/DB 写入失败分流计数,并记录处理耗时。</p>
     */
    @PostMapping("/items")
    public ApiResult<TodoItemResult> createTodoItem(@RequestBody CreateTodoItemRequest request) {
        long startMillis = System.currentTimeMillis();
        try {
            ApiResult<TodoItemResult> result = doCreateTodoItem(request);
            todoItemCreateMetrics.recordSuccess(elapsedMillis(startMillis));
            return result;
        } catch (BizException e) { // 已埋点后原样 throw e,由 GlobalExceptionHandler 统一记录日志(含 traceId)
            todoItemCreateMetrics.recordBizFailure(e.getCode(), elapsedMillis(startMillis));
            throw e;
        } catch (RuntimeException e) { // 已单独计数后原样 throw e,由 GlobalExceptionHandler 记录 ERROR+traceId
            // 非业务异常的运行时失败当前唯一来源为 DB 写入,按系分 §7.1 单独计数后原样上抛
            todoItemCreateMetrics.recordDbFailure(elapsedMillis(startMillis));
            throw e;
        }
    }

    /**
     * 业务编排:开关检查 → 参数校验(R01~R03)→ 服务调用。
     */
    private ApiResult<TodoItemResult> doCreateTodoItem(CreateTodoItemRequest request) {
        if (!todoProperties.getCreate().isEnabled()) {
            throw new BizException(TodoErrorCodeConstants.TODO_MAINTENANCE, "功能维护中,请稍后重试");
        }
        validateCreateRequest(request);
        return ApiResult.ok(todoItemService.createTodoItem(request));
    }

    private static long elapsedMillis(long startMillis) {
        return System.currentTimeMillis() - startMillis;
    }

    /**
     * 服务端参数校验(R01~R03),拒绝而非截断。
     */
    private void validateCreateRequest(CreateTodoItemRequest request) {
        String name = request.getName() == null ? "" : request.getName().trim();
        if (name.isEmpty()) {
            throw new BizException(TodoErrorCodeConstants.TODO_NAME_REQUIRED, "事项名称不能为空");
        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new BizException(TodoErrorCodeConstants.TODO_NAME_TOO_LONG, "事项名称超过 64 字符");
        }
        String description = request.getDescription() == null ? "" : request.getDescription();
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            throw new BizException(TodoErrorCodeConstants.TODO_DESCRIPTION_TOO_LONG, "事项描述超过 500 字符");
        }
    }
}
