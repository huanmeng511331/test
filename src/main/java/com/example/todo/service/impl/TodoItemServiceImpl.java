package com.example.todo.service.impl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.todo.common.constant.ErrorCodeEnum;
import com.example.todo.common.exception.BusinessException;
import com.example.todo.dao.mapper.TodoItemMapper;
import com.example.todo.model.do.TodoItemDO;
import com.example.todo.service.TodoItemService;
import com.example.todo.service.dto.TodoItemRequest;
import com.example.todo.service.dto.TodoItemVO;

/**
 * 待办事项服务实现类
 *
 * @author AiWork
 * @date 2026/10/27
 */
@Service
public class TodoItemServiceImpl implements TodoItemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoItemServiceImpl.class);

    private final TodoItemMapper todoItemMapper;

    /**
     * 构造注入待办事项数据访问接口
     *
     * @param todoItemMapper 待办事项数据访问接口
     */
    public TodoItemServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 新增成功后的待办事项视图对象
     */
    @Override
    public TodoItemVO createTodoItem(TodoItemRequest request) {
        TodoItemDO todoItemDO = new TodoItemDO();
        todoItemDO.setName(request.getName());
        todoItemDO.setDescription(request.getDescription());
        todoItemDO.setGmtCreate(LocalDateTime.now());
        todoItemDO.setGmtModified(LocalDateTime.now());

        int affectedRows = todoItemMapper.insert(todoItemDO);
        if (affectedRows <= 0) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "新增待办事项失败");
        }
        LOGGER.info("创建待办事项成功, id: {}", todoItemDO.getId());

        TodoItemDO saved = todoItemMapper.selectById(todoItemDO.getId());
        if (saved == null) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "新增待办事项失败");
        }
        return toVO(saved);
    }

    /**
     * 数据对象转视图对象
     *
     * @param todoItemDO 待办事项数据对象
     * @return 待办事项视图对象
     */
    private TodoItemVO toVO(TodoItemDO todoItemDO) {
        TodoItemVO vo = new TodoItemVO();
        vo.setId(String.valueOf(todoItemDO.getId()));
        vo.setName(todoItemDO.getName());
        vo.setDescription(todoItemDO.getDescription());
        vo.setGmtCreate(todoItemDO.getGmtCreate());
        vo.setGmtModified(todoItemDO.getGmtModified());
        return vo;
    }
}