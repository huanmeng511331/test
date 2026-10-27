package com.dtazza.todo.service.impl;

import com.dtazza.todo.api.request.CreateTodoRequest;
import com.dtazza.todo.common.exception.BusinessException;
import com.dtazza.todo.dao.entity.TodoDO;
import com.dtazza.todo.dao.mapper.TodoMapper;
import com.dtazza.todo.model.vo.TodoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private static final Long TEST_TODO_ID = 1L;
    private static final String TEST_TITLE = "整理周报";
    private static final String TEST_DESCRIPTION = "汇总本周工作进展";

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        // 无通用初始化需求
    }

    // ==================== create 测试 ====================

    @Test
    void should_createTodo_when_requestValid() {
        // Arrange
        CreateTodoRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(TEST_TODO_ID);
            return 1;
        });

        // Act
        TodoVO result = todoService.create(request);

        // Assert
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo(TEST_TITLE);
        assertThat(captor.getValue().getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(result.getId()).isEqualTo(TEST_TODO_ID);
        assertThat(result.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    void should_trimTitleAndDescription_when_hasSurroundingSpaces() {
        // Arrange
        CreateTodoRequest request = buildRequest("  " + TEST_TITLE + "  ", "   " + TEST_DESCRIPTION + "   ");
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(1);

        // Act
        TodoVO result = todoService.create(request);

        // Assert
        assertThat(result.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    void should_useEmptyDescription_when_descriptionNull() {
        // Arrange
        CreateTodoRequest request = buildRequest(TEST_TITLE, null);
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(1);

        // Act
        TodoVO result = todoService.create(request);

        // Assert
        assertThat(result.getDescription()).isEmpty();
    }

    @Test
    void should_throwException_when_titleBlank() {
        // Arrange
        CreateTodoRequest request = buildRequest("   ", TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
    }

    @Test
    void should_throwException_when_insertAffectsZeroRow() {
        // Arrange
        CreateTodoRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("新增待办事项失败");
    }

    private CreateTodoRequest buildRequest(String title, String description) {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle(title);
        request.setDescription(description);
        return request;
    }
}
