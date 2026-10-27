package com.dtk.todo.service.impl;

import com.dtk.todo.common.constant.TodoErrorCodeEnum;
import com.dtk.todo.common.exception.TodoException;
import com.dtk.todo.dao.mapper.TodoItemMapper;
import com.dtk.todo.model.dto.TodoItemCreateRequest;
import com.dtk.todo.model.dto.TodoItemVO;
import com.dtk.todo.model.entity.TodoItemDO;
import com.dtk.todo.service.TodoItemService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
     * 构造待办事项服务实现类
     *
     * @param todoItemMapper 待办事项数据访问接口
     */
    public TodoItemServiceImpl(TodoItemMapper todoItemMapper) {
        this.todoItemMapper = todoItemMapper;
    }

    @Override
    public TodoItemVO createTodoItem(TodoItemCreateRequest request) {
        String title = request.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new TodoException(TodoErrorCodeEnum.TITLE_REQUIRED.getCode(),
                    TodoErrorCodeEnum.TITLE_REQUIRED.getErrorMessage(),
                    TodoErrorCodeEnum.TITLE_REQUIRED.getUserTip());
        }

        String trimmedTitle = title.trim();
        String description = request.getDescription();
        if (description != null) {
            description = description.trim();
        }

        LocalDateTime now = LocalDateTime.now();
        TodoItemDO todoItemDO = new TodoItemDO();
        todoItemDO.setTitle(trimmedTitle);
        todoItemDO.setDescription(description);
        todoItemDO.setCreateTime(now);
        todoItemDO.setModifiedTime(now);

        todoItemMapper.insert(todoItemDO);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("新增待办事项成功, id: {}, title: {}", todoItemDO.getId(), trimmedTitle);
        }

        TodoItemVO todoItemVO = new TodoItemVO();
        todoItemVO.setId(todoItemDO.getId());
        todoItemVO.setTitle(todoItemDO.getTitle());
        todoItemVO.setDescription(todoItemDO.getDescription());
        todoItemVO.setCreateTime(todoItemDO.getCreateTime());
        return todoItemVO;
    }
}
