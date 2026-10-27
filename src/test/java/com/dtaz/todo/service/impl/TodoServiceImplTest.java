package com.dtaz.todo.service.impl;

import com.dtaz.todo.common.exception.BusinessException;
import com.dtaz.todo.dao.mapper.TodoMapper;
import com.dtaz.todo.model.dto.TodoRequest;
import com.dtaz.todo.model.entity.TodoDO;
import com.dtaz.todo.model.vo.TodoVO;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    private static final String TEST_NAME = "写周报";
    private static final String TEST_DESCRIPTION = "周五前完成本周工作总结";

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(1L);
            return 1;
        });
    }

    // ==================== create 测试 ====================

    @Test
    void should_returnTodoVO_when_requestValid() {
        // Arrange
        TodoRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);

        // Act
        TodoVO result = todoService.create(request);

        // Assert
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        TodoDO captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo(TEST_NAME);
        assertThat(captured.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(captured.getGmtCreate()).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    void should_trimNameAndBlankDescription_when_requestHasWhitespace() {
        // Arrange
        TodoRequest request = buildRequest("  写周报  ", "   ");

        // Act
        TodoVO result = todoService.create(request);

        // Assert
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(TEST_NAME);
        assertThat(captor.getValue().getDescription()).isEmpty();
        assertThat(result.getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void should_throwException_when_requestIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> todoService.create(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("请求不能为空");
        verify(todoMapper, never()).insert(any(TodoDO.class));
    }

    @Test
    void should_throwException_when_nameIsBlank() {
        // Arrange
        TodoRequest request = buildRequest("   ", TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
        verify(todoMapper, never()).insert(any(TodoDO.class));
    }

    @Test
    void should_throwException_when_insertAffectedZeroRows() {
        // Arrange
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(0);
        TodoRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("新增待办事项失败");
    }

    private TodoRequest buildRequest(String name, String description) {
        TodoRequest request = new TodoRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}