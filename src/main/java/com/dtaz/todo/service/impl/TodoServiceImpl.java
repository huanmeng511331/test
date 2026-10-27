package com.dtaz.todo.service.impl;

import com.dtaz.todo.common.constant.ErrorCodeEnum;
import com.dtaz.todo.common.exception.BusinessException;
import com.dtaz.todo.dao.mapper.TodoMapper;
import com.dtaz.todo.model.dto.TodoRequest;
import com.dtaz.todo.model.entity.TodoDO;
import com.dtaz.todo.model.vo.TodoVO;
import com.dtaz.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现。
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoServiceImpl.class);

    private static final int INSERT_SUCCESS_ROWS = 1;

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public TodoVO create(TodoRequest request) {
        validateRequest(request);

        TodoDO todo = new TodoDO();
        todo.setName(request.getName().trim());
        todo.setDescription(trimToEmpty(request.getDescription()));
        todo.setGmtCreate(LocalDateTime.now());
        todo.setGmtModified(todo.getGmtCreate());

        int rows = todoMapper.insert(todo);
        if (rows != INSERT_SUCCESS_ROWS) {
            LOGGER.error("新增待办事项失败, name: {}, affectedRows: {}", todo.getName(), rows);
            throw new BusinessException(ErrorCodeEnum.BUSINESS_ERROR.getCode(), "新增待办事项失败");
        }

        LOGGER.info("新增待办事项成功, id: {}, name: {}", todo.getId(), todo.getName());
        return toVO(todo);
    }

    private void validateRequest(TodoRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER.getCode(), "请求不能为空");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER.getCode(), "事项名称不能为空");
        }
    }

    private String trimToEmpty(String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private TodoVO toVO(TodoDO todo) {
        TodoVO vo = new TodoVO();
        vo.setId(todo.getId());
        vo.setName(todo.getName());
        vo.setDescription(todo.getDescription());
        vo.setGmtCreate(todo.getGmtCreate());
        return vo;
    }
}