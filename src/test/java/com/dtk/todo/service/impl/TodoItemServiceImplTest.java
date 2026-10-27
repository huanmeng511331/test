package com.dtk.todo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dtk.todo.common.constant.TodoErrorCodeEnum;
import com.dtk.todo.common.exception.TodoException;
import com.dtk.todo.dao.mapper.TodoItemMapper;
import com.dtk.todo.model.dto.TodoItemCreateRequest;
import com.dtk.todo.model.dto.TodoItemVO;
import com.dtk.todo.model.entity.TodoItemDO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link TodoItemServiceImpl} 单元测试
 *
 * @author AiWork
 * @date 2026/10/27
 */
@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

    private static final Long TODO_ID = 1L;
    private static final String TITLE = "完成周报";
    private static final String DESCRIPTION = "整理本周工作内容并提交";

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    // ==================== createTodoItem 测试 ====================

    @Test
    void should_createTodoItem_when_requestIsValid() {
        // Arrange
        TodoItemCreateRequest request = buildRequest(TITLE, DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO todoItemDO = invocation.getArgument(0);
            todoItemDO.setId(TODO_ID);
            return 1;
        });

        // Act
        TodoItemVO todoItemVO = todoItemService.createTodoItem(request);

        // Assert
        assertThat(todoItemVO.getId()).isEqualTo(TODO_ID);
        assertThat(todoItemVO.getTitle()).isEqualTo(TITLE);
        assertThat(todoItemVO.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(todoItemVO.getCreateTime()).isNotNull();

        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper, times(1)).insert(captor.capture());
        TodoItemDO captured = captor.getValue();
        assertThat(captured.getTitle()).isEqualTo(TITLE);
        assertThat(captured.getDescription()).isEqualTo(DESCRIPTION);
        assertThat(captured.getCreateTime()).isNotNull();
        assertThat(captured.getModifiedTime()).isNotNull();
    }

    @Test
    void should_trimTitle_when_titleHasBlankSpace() {
        // Arrange
        TodoItemCreateRequest request = buildRequest("  " + TITLE + "  ", DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO todoItemDO = invocation.getArgument(0);
            todoItemDO.setId(TODO_ID);
            return 1;
        });

        // Act
        TodoItemVO todoItemVO = todoItemService.createTodoItem(request);

        // Assert
        assertThat(todoItemVO.getTitle()).isEqualTo(TITLE);
    }

    @Test
    void should_throwException_when_titleIsBlank() {
        // Arrange
        TodoItemCreateRequest request = buildRequest("   ", DESCRIPTION);

        // Act
        TodoException exception = catchThrowableOfType(
                () -> todoItemService.createTodoItem(request), TodoException.class);

        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getErrorCode()).isEqualTo(TodoErrorCodeEnum.TITLE_REQUIRED.getCode());
        assertThat(exception.getErrorMessage())
                .isEqualTo(TodoErrorCodeEnum.TITLE_REQUIRED.getErrorMessage());
        assertThat(exception.getUserTip()).isEqualTo(TodoErrorCodeEnum.TITLE_REQUIRED.getUserTip());
        verify(todoItemMapper, times(0)).insert(any(TodoItemDO.class));
    }

    /**
     * 构造新增请求对象
     *
     * @param title 事项名称
     * @param description 事项描述
     * @return 新增请求对象
     */
    private TodoItemCreateRequest buildRequest(String title, String description) {
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        return request;
    }
}