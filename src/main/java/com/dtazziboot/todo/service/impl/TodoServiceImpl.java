package com.dtazziboot.todo.service.impl;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.common.enums.TodoErrorCodeEnum;
import com.dtazziboot.todo.dao.mapper.TodoMapper;
import com.dtazziboot.todo.model.dto.TodoCreateRequest;
import com.dtazziboot.todo.model.dto.TodoVO;
import com.dtazziboot.todo.model.entity.TodoDO;
import com.dtazziboot.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 待办事项服务实现类。
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public TodoVO createTodo(TodoCreateRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw new BusinessException(TodoErrorCodeEnum.PARAM_INVALID);
        }

        TodoDO todoDO = new TodoDO();
        todoDO.setName(request.getName());
        todoDO.setDescription(request.getDescription());
        LocalDateTime now = LocalDateTime.now();
        todoDO.setGmtCreate(now);
        todoDO.setGmtModified(now);

        int rows = todoMapper.insert(todoDO);
        if (rows <= 0) {
            logger.error("新增待办事项失败, request: {}", request);
            throw new BusinessException(TodoErrorCodeEnum.SYSTEM_ERROR);
        }

        logger.info("新增待办事项成功, id: {}", todoDO.getId());
        return buildTodoVO(todoDO);
    }

    private TodoVO buildTodoVO(TodoDO todoDO) {
        TodoVO vo = new TodoVO();
        vo.setId(todoDO.getId());
        vo.setName(todoDO.getName());
        vo.setDescription(todoDO.getDescription());
        if (todoDO.getGmtCreate() != null) {
            vo.setCreateTime(todoDO.getGmtCreate().format(DATE_TIME_FORMATTER));
        }
        return vo;
    }
}