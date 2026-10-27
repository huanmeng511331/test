package com.dtazziboot.todo.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dtazziboot.todo.common.constant.TodoConstants;
import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.dao.entity.TodoDO;
import com.dtazziboot.todo.dao.mapper.TodoMapper;
import com.dtazziboot.todo.model.dto.TodoRequest;
import com.dtazziboot.todo.model.dto.TodoVO;
import com.dtazziboot.todo.service.TodoService;

/**
 * 待办事项业务服务实现类
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
@Service
public class TodoServiceImpl implements TodoService {

    /**
     * 标准时间格式
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    /**
     * 待办事项数据访问接口
     */
    private final TodoMapper todoMapper;

    /**
     * 构造注入依赖
     *
     * @param todoMapper 待办事项数据访问接口
     */
    public TodoServiceImpl(TodoMapper todoMapper) {
        this.todoMapper = todoMapper;
    }

    /**
     * 新增待办事项
     *
     * @param request 新增待办事项请求
     * @return 创建成功的待办事项
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TodoVO createTodo(TodoRequest request) {
        // 名称必填校验（后端兜底，前端与注解校验之外的双重保障）
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("A0001", "待办事项名称不能为空");
        }
        if (name.length() > TodoConstants.NAME_MAX_LENGTH) {
            throw new BusinessException("A0002", "待办事项名称长度不能超过100");
        }

        String description = request.getDescription();
        if (description != null && description.length() > TodoConstants.DESCRIPTION_MAX_LENGTH) {
            throw new BusinessException("A0003", "待办事项描述长度不能超过500");
        }

        LocalDateTime now = LocalDateTime.now();

        TodoDO todoDO = new TodoDO();
        todoDO.setName(name.trim());
        todoDO.setDescription(description);
        todoDO.setGmtCreate(now);
        todoDO.setGmtModified(now);

        int affectedRows = todoMapper.insert(todoDO);
        if (affectedRows != 1) {
            throw new BusinessException("B0001", "待办事项创建失败");
        }

        if (logger.isInfoEnabled()) {
            logger.info("待办事项创建成功, id: {}", todoDO.getId());
        }
        return convertToVO(todoDO);
    }

    /**
     * 数据对象转展示对象
     *
     * @param todoDO 待办事项数据对象
     * @return 待办事项展示对象
     */
    private TodoVO convertToVO(TodoDO todoDO) {
        TodoVO vo = new TodoVO();
        vo.setId(todoDO.getId());
        vo.setName(todoDO.getName());
        vo.setDescription(todoDO.getDescription());
        if (todoDO.getGmtCreate() != null) {
            vo.setGmtCreate(todoDO.getGmtCreate().format(DATE_TIME_FORMATTER));
        }
        return vo;
    }
}