package com.dtaz.todo.service.impl;

import com.dtaz.todo.common.constant.TodoErrorCodeEnum;
import com.dtaz.todo.common.exception.BusinessException;
import com.dtaz.todo.dao.mapper.TodoItemMapper;
import com.dtaz.todo.model.do.TodoItemDO;
import com.dtaz.todo.model.dto.TodoCreateRequest;
import com.dtaz.todo.model.vo.TodoItemVO;
import com.dtaz.todo.service.TodoItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现类
 *
 * @author AiWork
 * @date 2026/10/27
 */
@Service
public class TodoItemServiceImpl implements TodoItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoItemServiceImpl.class);

    private final TodoItemMapper todoItemMapper;

    public TodoItemServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 创建成功的待办事项视图对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoItemVO createTodo(TodoCreateRequest request) {
        if (request == null) {
            throw new BusinessException(TodoErrorCodeEnum.INVALID_PARAMETER, "请求参数不能为空");
        }

        TodoItemDO todoItemDO = new TodoItemDO();
        todoItemDO.setTitle(request.getTitle());
        todoItemDO.setDescription(request.getDescription());
        LocalDateTime now = LocalDateTime.now();
        todoItemDO.setGmtCreate(now);
        todoItemDO.setGmtModified(now);

        int rows = todoItemMapper.insert(todoItemDO);
        if (rows != 1) {
            LOGGER.error("新增待办事项失败, rows: {}, request: {}", rows, request);
            throw new BusinessException(TodoErrorCodeEnum.SYSTEM_ERROR);
        }

        TodoItemVO vo = new TodoItemVO();
        vo.setId(todoItemDO.getId());
        vo.setTitle(todoItemDO.getTitle());
        vo.setDescription(todoItemDO.getDescription());
        vo.setGmtCreate(todoItemDO.getGmtCreate());
        return vo;
    }
}