package com.todoapp.todo.service.impl;

import com.todoapp.todo.constant.TodoErrorCode;
import com.todoapp.todo.dao.entity.TodoDO;
import com.todoapp.todo.dao.mapper.TodoMapper;
import com.todoapp.todo.service.TodoService;
import com.todoapp.todo.service.dto.TodoCreateRequest;
import com.todoapp.todo.service.dto.TodoCreateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 待办事项服务实现类
 *
 * @author AiWork
 * @date 2026/10/28
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    /** 事项名称最大长度 */
    private static final int TITLE_MAX_LENGTH = 100;

    /** 事项描述最大长度 */
    private static final int DESCRIPTION_MAX_LENGTH = 1000;

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public TodoCreateResult createTodo(TodoCreateRequest request) {
        // R01: title 非空且非纯空白
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw TodoErrorCode.TITLE_EMPTY.toException();
        }

        // R02: title 长度 <= 100
        if (request.getTitle().length() > TITLE_MAX_LENGTH) {
            throw TodoErrorCode.TITLE_LENGTH_EXCEEDED.toException();
        }

        // R03: description 长度 <= 1000，null 视为空串
        String description = request.getDescription() == null ? "" : request.getDescription();
        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            throw TodoErrorCode.DESCRIPTION_LENGTH_EXCEEDED.toException();
        }

        // 构建数据对象，填充 creator / tenant_id（来源于登录上下文，禁止前端传入）
        TodoDO todo = new TodoDO();
        todo.setTitle(request.getTitle());
        todo.setDescription(description);
        todo.setCreator(request.getCreator());
        todo.setTenantId(request.getTenantId());

        try {
            todoMapper.insert(todo);
        } catch (Exception e) {
            logger.error("createTodo insert failed, title: {}, creator: {}",
                    request.getTitle(), request.getCreator(), e);
            throw TodoErrorCode.CREATE_FAILED.toException(e);
        }

        Long id = todo.getId();
        if (id == null) {
            logger.error("createTodo insert returned null id, title: {}", request.getTitle());
            throw TodoErrorCode.CREATE_FAILED.toException();
        }

        logger.info("createTodo success, id: {}, creator: {}", id, request.getCreator());
        return new TodoCreateResult(id);
    }
}
