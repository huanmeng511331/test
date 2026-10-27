package com.dtazziboot.todo.service.impl;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.dao.mapper.TodoItemMapper;
import com.dtazziboot.todo.model.dto.TodoItemCreateRequest;
import com.dtazziboot.todo.model.dto.TodoItemVO;
import com.dtazziboot.todo.model.entity.TodoItemDO;
import com.dtazziboot.todo.service.TodoItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现。
 */
@Service
public class TodoItemServiceImpl implements TodoItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoItemServiceImpl.class);

    private static final String ERR_NAME_REQUIRED = "事项名称不能为空";
    private static final String ERR_INSERT_FAILED = "新增待办事项失败";

    private final TodoItemMapper todoItemMapper;

    public TodoItemServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    @Override
    public TodoItemVO createTodoItem(TodoItemCreateRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException(ERR_NAME_REQUIRED);
        }

        LocalDateTime now = LocalDateTime.now();

        TodoItemDO todoItem = new TodoItemDO();
        todoItem.setName(request.getName().trim());
        todoItem.setDescription(request.getDescription());
        todoItem.setGmtCreate(now);
        todoItem.setGmtModified(now);

        int affectedRows = todoItemMapper.insert(todoItem);
        if (affectedRows != 1) {
            LOGGER.error("新增待办事项影响行数异常，affectedRows={}, todoItem={}", affectedRows, todoItem);
            throw new BusinessException(ERR_INSERT_FAILED);
        }

        return toVO(todoItem);
    }

    private TodoItemVO toVO(TodoItemDO todoItem) {
        TodoItemVO vo = new TodoItemVO();
        vo.setId(String.valueOf(todoItem.getId()));
        vo.setName(todoItem.getName());
        vo.setDescription(todoItem.getDescription());
        return vo;
    }
}