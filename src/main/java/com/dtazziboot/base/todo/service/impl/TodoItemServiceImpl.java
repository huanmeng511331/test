package com.dtazziboot.base.todo.service.impl;

import com.dtazziboot.base.common.auth.LoginUserContext;
import com.dtazziboot.base.common.constant.DateTimeConstants;
import com.dtazziboot.base.common.exception.BizException;
import com.dtazziboot.base.todo.constant.TodoErrorCodeConstants;
import com.dtazziboot.base.todo.mapper.TodoItemMapper;
import com.dtazziboot.base.todo.model.dto.CreateTodoItemRequest;
import com.dtazziboot.base.todo.model.dto.TodoItemResult;
import com.dtazziboot.base.todo.model.entity.TodoItemDO;
import com.dtazziboot.base.todo.service.TodoItemService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 待办事项服务实现:单表单条 INSERT,语句级原子性,无显式事务编排。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TodoItemServiceImpl implements TodoItemService {

    /**
     * 出参创建时间格式(值来源为 DB 会话时区 Asia/Shanghai 墙钟或显式 SYSTEM_ZONE 当前时间;
     * 命名不含时区语义,时区统一见 {@link DateTimeConstants#SYSTEM_ZONE},CR P1-1/P2-3)。
     */
    private static final DateTimeFormatter CREATED_AT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 默认单租户 */
    private static final long DEFAULT_TENANT_ID = 0L;

    /** 逻辑删除标记:未删除 */
    private static final long NOT_DELETED = 0L;

    private final TodoItemMapper todoItemMapper;

    @Override
    public TodoItemResult createTodoItem(CreateTodoItemRequest request) {
        String name = requireValidName(request);
        String description = request.getDescription() == null ? "" : request.getDescription();
        String creator = LoginUserContext.requireCurrentUser();

        TodoItemDO todoItem = new TodoItemDO();
        todoItem.setTenantId(DEFAULT_TENANT_ID);
        todoItem.setName(name);
        todoItem.setDescription(description);
        todoItem.setCreator(creator);
        todoItem.setModifier(creator);
        todoItem.setDeleted(NOT_DELETED);
        todoItemMapper.insert(todoItem);

        LocalDateTime gmtCreate = todoItem.getGmtCreate() == null
                ? LocalDateTime.now(DateTimeConstants.SYSTEM_ZONE)
                : todoItem.getGmtCreate();
        log.info("todo item created, id: {}, creator: {}, nameLength: {}",
                todoItem.getId(), creator, name.length());
        return new TodoItemResult(todoItem.getId(), name, creator, CREATED_AT_FORMATTER.format(gmtCreate));
    }

    /**
     * 防御性兜底:Controller 已前置 R01 校验,此处拦截内部调用方绕过 Web 层时的 null 入参,避免 NPE(CR P2-2/G11.3)。
     */
    private static String requireValidName(CreateTodoItemRequest request) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BizException(TodoErrorCodeConstants.TODO_NAME_REQUIRED, "事项名称不能为空");
        }
        return request.getName().trim();
    }
}
