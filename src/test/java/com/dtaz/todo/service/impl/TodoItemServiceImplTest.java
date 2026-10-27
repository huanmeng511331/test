package com.dtaz.todo.service.impl;

import com.dtaz.todo.common.constant.TodoErrorCodeEnum;
import com.dtaz.todo.common.exception.BusinessException;
import com.dtaz.todo.dao.mapper.TodoItemMapper;
import com.dtaz.todo.model.do.TodoItemDO;
import com.dtaz.todo.model.dto.TodoCreateRequest;
import com.dtaz.todo.model.vo.TodoItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 待办事项业务服务实现类单元测试
 *
 * @author AiWork
 * @date 2026/10/27
 */
@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

    private static final Long TEST_TODO_ID = 10001L;

    @Mock
    private TodoItemMapper todoItemMapper;

    private TodoItemServiceImpl todoItemService;

    @BeforeEach
    void setUp() {
        todoItemService = new TodoItemServiceImpl(todoItemMapper);
    }

    // ==================== createTodo 测试 ====================

    @Test
    void should_returnTodoVO_when_createRequestIsValid() {
        // Arrange
        TodoCreateRequest request = buildCreateRequest("买菜", "晚上去超市买蔬菜");
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO argument = invocation.getArgument(0);
            argument.setId(TEST_TODO_ID);
            return 1;
        });

        // Act
        TodoItemVO result = todoItemService.createTodo(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_TODO_ID);
        assertThat(result.getTitle()).isEqualTo("买菜");
        assertThat(result.getDescription()).isEqualTo("晚上去超市买蔬菜");
        assertThat(result.getGmtCreate()).isNotNull();

        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper, times(1)).insert(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("买菜");
        assertThat(captor.getValue().getDescription()).isEqualTo("晚上去超市买蔬菜");
    }

    @Test
    void should_setCurrentTime_when_createTodo() {
        // Arrange
        TodoCreateRequest request = buildCreateRequest("开会", null);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(1);

        // Act
        TodoItemVO result = todoItemService.createTodo(request);

        // Assert
        assertThat(result.getGmtCreate()).isNotNull();
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void should_throwInvalidParameterException_when_requestIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodo(null))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(TodoErrorCodeEnum.INVALID_PARAMETER.getCode());

        verify(todoItemMapper, never()).insert(any(TodoItemDO.class));
    }

    @Test
    void should_throwSystemException_when_insertReturnsZero() {
        // Arrange
        TodoCreateRequest request = buildCreateRequest("写周报", null);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(TodoErrorCodeEnum.SYSTEM_ERROR.getCode());
    }

    private TodoCreateRequest buildCreateRequest(String title, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        return request;
    }
}