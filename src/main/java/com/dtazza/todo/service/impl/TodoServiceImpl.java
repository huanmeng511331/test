package com.dtazza.todo.service.impl;

import com.dtazza.todo.api.request.CreateTodoRequest;
import com.dtazza.todo.common.exception.BusinessException;
import com.dtazza.todo.dao.entity.TodoDO;
import com.dtazza.todo.dao.mapper.TodoMapper;
import com.dtazza.todo.model.vo.TodoVO;
import com.dtazza.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 待办事项业务服务实现。
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final TodoMapper todoMapper;

    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    public TodoVO create(CreateTodoRequest request) {
        String title = trimToNull(request.getTitle());
        if (title == null) {
            throw new BusinessException("A0001", "事项名称不能为空");
        }
        String description = request.getDescription() == null ? "" : request.getDescription().trim();

        TodoDO todo = new TodoDO();
        todo.setTitle(title);
        todo.setDescription(description);

        int affected = todoMapper.insert(todo);
        if (affected != 1) {
            throw new BusinessException("B0001", "新增待办事项失败");
        }
        logger.info("新增待办事项成功, id: {}", todo.getId());

        return toVo(todo);
    }

    private TodoVO toVo(TodoDO todo) {
        TodoVO vo = new TodoVO();
        vo.setId(todo.getId());
        vo.setTitle(todo.getTitle());
        vo.setDescription(todo.getDescription());
        return vo;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
