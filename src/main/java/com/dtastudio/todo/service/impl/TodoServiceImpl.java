package com.dtastudio.todo.service.impl;

import com.dtastudio.todo.common.constant.TodoConstants;
import com.dtastudio.todo.common.exception.BusinessException;
import com.dtastudio.todo.dao.mapper.TodoMapper;
import com.dtastudio.todo.model.dto.TodoCreateRequest;
import com.dtastudio.todo.model.dto.TodoVO;
import com.dtastudio.todo.model.entity.TodoDO;
import com.dtastudio.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 待办事项业务服务实现类。
 *
 * @author AiWork
 * @date 2026/10/27
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final TodoMapper todoMapper;

    /**
     * 构造注入待办事项数据访问接口。
     *
     * @param todoMapper 待办事项数据访问接口
     */
    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO createTodo(TodoCreateRequest request) {
        // 参数兜底校验（Service 层二次防御）
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessException("A0001", "事项名称不能为空");
        }

        TodoDO todo = new TodoDO();
        todo.setName(request.getName().trim());
        todo.setDescription(request.getDescription());
        todo.setStatus(TodoConstants.STATUS_UNDONE);
        LocalDateTime now = LocalDateTime.now();
        todo.setGmtCreate(now);
        todo.setGmtModified(now);

        int affectedRows = todoMapper.insert(todo);
        if (affectedRows != 1) {
            throw new BusinessException("B0002", "新增待办事项失败");
        }

        LOGGER.info("新增待办事项成功, id: {}, name: {}", todo.getId(), todo.getName());
        return convertToVO(todo);
    }

    /**
     * 将数据对象转换为展示对象。
     *
     * @param todo 待办事项数据对象
     * @return 展示对象
     */
    private TodoVO convertToVO(TodoDO todo) {
        TodoVO vo = new TodoVO();
        vo.setId(todo.getId());
        vo.setName(todo.getName());
        vo.setDescription(todo.getDescription());
        vo.setStatus(todo.getStatus());
        vo.setGmtCreate(todo.getGmtCreate());
        return vo;
    }
}