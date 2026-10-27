package com.antdigital.todo.service.impl;

import com.antdigital.todo.common.enums.ErrorCodeEnum;
import com.antdigital.todo.common.exception.BusinessException;
import com.antdigital.todo.dao.mapper.TodoItemMapper;
import com.antdigital.todo.model.do.TodoItemDO;
import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.vo.TodoItemVO;
import com.antdigital.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 待办事项业务服务实现。
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    private static final int MAX_NAME_LENGTH = 200;

    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    private final TodoItemMapper todoItemMapper;

    public TodoServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoItemVO createTodo(TodoCreateRequest request) {
        String itemName = request.getItemName();
        String description = request.getDescription();

        if (itemName == null || itemName.trim().isEmpty()) {
            logger.warn("新增待办失败，事项名称为空");
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "事项名称不能为空");
        }
        if (itemName.length() > MAX_NAME_LENGTH) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER,
                    "事项名称长度不能超过" + MAX_NAME_LENGTH + "个字符");
        }
        if (description == null) {
            description = "";
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER,
                    "事项描述长度不能超过" + MAX_DESCRIPTION_LENGTH + "个字符");
        }

        TodoItemDO todoItem = new TodoItemDO();
        todoItem.setItemName(itemName.trim());
        todoItem.setDescription(description);

        todoItemMapper.insert(todoItem);
        logger.info("新增待办事项成功, id: {}, itemName: {}", todoItem.getId(), todoItem.getItemName());

        return toVO(todoItem);
    }

    private TodoItemVO toVO(TodoItemDO todoItem) {
        TodoItemVO vo = new TodoItemVO();
        vo.setId(todoItem.getId() == null ? null : String.valueOf(todoItem.getId()));
        vo.setItemName(todoItem.getItemName());
        vo.setDescription(todoItem.getDescription());
        vo.setGmtCreate(todoItem.getGmtCreate());
        vo.setGmtModified(todoItem.getGmtModified());
        return vo;
    }
}