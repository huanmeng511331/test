package com.dtazziboot.todo.service.impl;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.dao.mapper.TodoItemMapper;
import com.dtazziboot.todo.model.dto.TodoItemCreateRequest;
import com.dtazziboot.todo.model.dto.TodoItemVO;
import com.dtazziboot.todo.model.entity.TodoItemDO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoItemServiceImpl} 单元测试。
 *
 * <p>覆盖场景：正常路径、参数校验（null / 空白名称）、数据库写失败、名称去除首尾空白。
 */
@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

    private static final Long TEST_ID = 1001L;
    private static final String TEST_NAME = "写周报";
    private static final String TEST_DESCRIPTION = "整理本周工作进展";

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    // ==================== createTodoItem 测试 ====================

    @Test
    void should_returnTodoItemVO_when_requestIsValid() {
        // Arrange (Given)
        TodoItemCreateRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO entity = invocation.getArgument(0);
            entity.setId(TEST_ID);
            return 1;
        });

        // Act (When)
        TodoItemVO result = todoItemService.createTodoItem(request);

        // Assert (Then)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(String.valueOf(TEST_ID));
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getDescription()).isEqualTo(TEST_DESCRIPTION);
        verify(todoItemMapper, times(1)).insert(any(TodoItemDO.class));
    }

    @Test
    void should_trimName_when_nameHasLeadingAndTrailingSpaces() {
        // Arrange (Given)
        TodoItemCreateRequest request = buildRequest("  写周报  ", TEST_DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO entity = invocation.getArgument(0);
            entity.setId(TEST_ID);
            return 1;
        });

        // Act (When)
        TodoItemVO result = todoItemService.createTodoItem(request);

        // Assert (Then)
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper).insert(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void should_throwBusinessException_when_requestIsNull() {
        // Arrange (Given)

        // Act & Assert (Then)
        assertThatThrownBy(() -> todoItemService.createTodoItem(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
    }

    @Test
    void should_throwBusinessException_when_nameIsBlank() {
        // Arrange (Given)
        TodoItemCreateRequest request = buildRequest("   ", TEST_DESCRIPTION);

        // Act & Assert (Then)
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
    }

    @Test
    void should_throwBusinessException_when_insertAffectedZero() {
        // Arrange (Given)
        TodoItemCreateRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(0);

        // Act & Assert (Then)
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("新增待办事项失败");
    }

    private TodoItemCreateRequest buildRequest(String name, String description) {
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}