package com.alipay.todo.service.impl;

import com.alipay.todo.common.constant.TodoConstants;
import com.alipay.todo.common.exception.TodoErrorCodeEnum;
import com.alipay.todo.common.exception.TodoException;
import com.alipay.todo.dao.entity.TodoDO;
import com.alipay.todo.dao.mapper.TodoMapper;
import com.alipay.todo.model.vo.TodoVO;
import com.alipay.todo.service.TodoService;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 待办事项业务服务实现。
 */
@Service
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO createTodo(String name, String description) {
        validate(name, description);

        LocalDateTime now = LocalDateTime.now();
        TodoDO todoDO = new TodoDO();
        todoDO.setName(name);
        todoDO.setDescription(description);
        todoDO.setGmtCreate(now);
        todoDO.setGmtModified(now);

        todoMapper.insert(todoDO);

        return toVO(todoDO);
    }

    private void validate(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new TodoException(TodoErrorCodeEnum.TODO_NAME_BLANK);
        }
        if (name.length() > TodoConstants.NAME_MAX_LENGTH) {
            throw new TodoException(TodoErrorCodeEnum.TODO_NAME_TOO_LONG);
        }
        if (description != null && description.length() > TodoConstants.DESCRIPTION_MAX_LENGTH) {
            throw new TodoException(TodoErrorCodeEnum.TODO_DESCRIPTION_TOO_LONG);
        }
    }

    private TodoVO toVO(TodoDO todoDO) {
        TodoVO vo = new TodoVO();
        vo.setId(todoDO.getId());
        vo.setName(todoDO.getName());
        vo.setDescription(todoDO.getDescription());
        vo.setGmtCreate(todoDO.getGmtCreate());
        vo.setGmtModified(todoDO.getGmtModified());
        return vo;
    }
}
