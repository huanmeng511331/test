package com.todoapp.todo.service.impl;

import com.todoapp.common.context.LoginContext;
import com.todoapp.todo.constant.TodoErrorCode;
import com.todoapp.todo.dao.entity.TodoDO;
import com.todoapp.todo.dao.mapper.TodoMapper;
import com.todoapp.todo.service.TodoService;
import com.todoapp.todo.service.dto.TodoCreateRequest;
import com.todoapp.todo.service.dto.TodoCreateResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 待办事项服务实现类
 *
 * <p>核心链路 {@link #createTodo(TodoCreateRequest)} 通过 Micrometer 埋点采集：
 * <ul>
 *   <li>{@code todo.create.total} — 调用总次数（Counter）</li>
 *   <li>{@code todo.create.success} — 成功次数（Counter）</li>
 *   <li>{@code todo.create.failure} — 失败次数（Counter）</li>
 *   <li>{@code todo.create.duration} — 处理耗时，含 P99 分位（Timer）</li>
 * </ul>
 *
 * @author AiWork
 * @date 2026/10/28
 */
@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    /** 事项名称最大长度 */
    private static final int TITLE_MAX_LENGTH = 100;

    /** 事项描述最大长度 */
    private static final int DESCRIPTION_MAX_LENGTH = 1000;

    private final TodoMapper todoMapper;
    private final MeterRegistry meterRegistry;
    private final Counter totalCounter;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer createTimer;

    public TodoServiceImpl(TodoMapper todoMapper, MeterRegistry meterRegistry) {
        this.todoMapper = todoMapper;
        this.meterRegistry = meterRegistry;
        this.totalCounter = meterRegistry.counter("todo.create.total");
        this.successCounter = meterRegistry.counter("todo.create.success");
        this.failureCounter = meterRegistry.counter("todo.create.failure");
        this.createTimer = Timer.builder("todo.create.duration")
                .publishPercentile(0.99)
                .register(meterRegistry);
    }

    @Override
    public TodoCreateResult createTodo(TodoCreateRequest request) {
        totalCounter.increment();
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            // R01: title 非空且非纯空白
            if (request.getTitle() == null || request.getTitle().isBlank()) {
                throw TodoErrorCode.TITLE_EMPTY.toException();
            }

            // R02: title 长度 <= 100
            if (request.getTitle().length() > TITLE_MAX_LENGTH) {
                throw TodoErrorCode.TITLE_LENGTH_EXCEEDED.toException();
            }

            // R03: description 长度 <= 1000，null 视为空串
            String description = request.getDescription() == null ? "" : request.getDescription();
            if (description.length() > DESCRIPTION_MAX_LENGTH) {
                throw TodoErrorCode.DESCRIPTION_LENGTH_EXCEEDED.toException();
            }

            // 构建数据对象，填充 creator / tenant_id（来源于登录上下文，禁止前端传入）
            TodoDO todo = new TodoDO();
            todo.setTitle(request.getTitle());
            todo.setDescription(description);
            todo.setCreator(request.getCreator());
            todo.setTenantId(request.getTenantId());

            try {
                todoMapper.insert(todo);
            } catch (Exception e) {
                logger.error("createTodo insert failed, title: {}, creator: {}",
                        request.getTitle(), LoginContext.maskUserId(request.getCreator()), e);
                throw TodoErrorCode.CREATE_FAILED.toException(e);
            }

            Long id = todo.getId();
            if (id == null) {
                logger.error("createTodo insert returned null id, title: {}", request.getTitle());
                throw TodoErrorCode.CREATE_FAILED.toException();
            }

            logger.info("createTodo success, id: {}, creator: {}",
                    id, LoginContext.maskUserId(request.getCreator()));
            successCounter.increment();
            return new TodoCreateResult(id);
        } catch (RuntimeException e) {
            failureCounter.increment();
            throw e;
        } finally {
            sample.stop(createTimer);
        }
    }
}
