package com.alipay.todo.service.impl;

import com.alipay.todo.common.exception.TodoErrorCodeEnum;
import com.alipay.todo.common.exception.TodoException;
import com.alipay.todo.dao.entity.TodoDO;
import com.alipay.todo.dao.mapper.TodoMapper;
import com.alipay.todo.model.vo.TodoVO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * {@link TodoServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private static final String VALID_NAME = "买牛奶";
    private static final String VALID_DESCRIPTION = "下班后顺路购买";

    @Mock
    private TodoMapper todoMapper;

    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoServiceImpl(todoMapper);
    }

    // ==================== createTodo 测试 ====================

    @Test
    void should_createTodo_when_validRequest() {
        // Arrange (Given)
        doAnswer(invocation -> {
            TodoDO todoDO = invocation.getArgument(0);
            todoDO.setId(1L);
            return 1;
        }).when(todoMapper).insert(any(TodoDO.class));

        // Act (When)
        TodoVO vo = todoService.createTodo(VALID_NAME, VALID_DESCRIPTION);

        // Assert (Then)
        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getName()).isEqualTo(VALID_NAME);
        assertThat(vo.getDescription()).isEqualTo(VALID_DESCRIPTION);
        assertThat(vo.getGmtCreate()).isNotNull();
        assertThat(vo.getGmtModified()).isNotNull();

        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(VALID_NAME);
        assertThat(captor.getValue().getDescription()).isEqualTo(VALID_DESCRIPTION);
    }

    @Test
    void should_createTodo_when_descriptionIsNull() {
        // Arrange (Given)
        doAnswer(invocation -> {
            TodoDO todoDO = invocation.getArgument(0);
            todoDO.setId(2L);
            return 1;
        }).when(todoMapper).insert(any(TodoDO.class));

        // Act (When)
        TodoVO vo = todoService.createTodo(VALID_NAME, null);

        // Assert (Then)
        assertThat(vo.getDescription()).isNull();
        assertThat(vo.getId()).isEqualTo(2L);
    }

    @Test
    void should_throwException_when_nameIsBlank() {
        assertThatThrownBy(() -> todoService.createTodo("   ", VALID_DESCRIPTION))
                .isInstanceOf(TodoException.class)
                .extracting(TodoException::getErrorCode)
                .isEqualTo(TodoErrorCodeEnum.TODO_NAME_BLANK.getCode());
    }

    @Test
    void should_throwException_when_nameIsNull() {
        assertThatThrownBy(() -> todoService.createTodo(null, VALID_DESCRIPTION))
                .isInstanceOf(TodoException.class)
                .extracting(TodoException::getErrorCode)
                .isEqualTo(TodoErrorCodeEnum.TODO_NAME_BLANK.getCode());
    }

    @Test
    void should_throwException_when_nameIsTooLong() {
        String longName = "a".repeat(101);

        assertThatThrownBy(() -> todoService.createTodo(longName, VALID_DESCRIPTION))
                .isInstanceOf(TodoException.class)
                .extracting(TodoException::getErrorCode)
                .isEqualTo(TodoErrorCodeEnum.TODO_NAME_TOO_LONG.getCode());
    }

    @Test
    void should_throwException_when_descriptionIsTooLong() {
        String longDescription = "b".repeat(501);

        assertThatThrownBy(() -> todoService.createTodo(VALID_NAME, longDescription))
                .isInstanceOf(TodoException.class)
                .extracting(TodoException::getErrorCode)
                .isEqualTo(TodoErrorCodeEnum.TODO_DESCRIPTION_TOO_LONG.getCode());
    }

    @Test
    void should_acceptBoundary_when_nameAndDescriptionAtMaxLength() {
        // Arrange (Given)
        String nameAtMax = "c".repeat(100);
        String descriptionAtMax = "d".repeat(500);
        doAnswer(invocation -> {
            TodoDO todoDO = invocation.getArgument(0);
            todoDO.setId(3L);
            return 1;
        }).when(todoMapper).insert(any(TodoDO.class));

        // Act (When)
        TodoVO vo = todoService.createTodo(nameAtMax, descriptionAtMax);

        // Assert (Then)
        assertThat(vo.getName()).hasSize(100);
        assertThat(vo.getDescription()).hasSize(500);
    }
}
