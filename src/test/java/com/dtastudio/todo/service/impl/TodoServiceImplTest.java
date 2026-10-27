package com.dtastudio.todo.service.impl;

import com.dtastudio.todo.common.constant.TodoConstants;
import com.dtastudio.todo.common.exception.BusinessException;
import com.dtastudio.todo.dao.mapper.TodoMapper;
import com.dtastudio.todo.model.dto.TodoCreateRequest;
import com.dtastudio.todo.model.dto.TodoVO;
import com.dtastudio.todo.model.entity.TodoDO;
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
 * {@link TodoServiceImpl} 单元测试。
 *
 * @author AiWork
 * @date 2026/10/27
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    /** 测试用待办事项 ID */
    private static final long TEST_TODO_ID = 1001L;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ==================== createTodo 测试 ====================

    @Test
    void should_createTodo_when_validRequest() {
        // Arrange
        stubInsertWithIdBackfill();
        TodoCreateRequest request = buildRequest(" 采购开发机 ", " 双十一前完成采购 ");

        // Act
        TodoVO vo = todoService.createTodo(request);

        // Assert
        assertThat(vo).isNotNull();
        assertThat(vo.getId()).isEqualTo(TEST_TODO_ID);
        assertThat(vo.getName()).isEqualTo("采购开发机");
        assertThat(vo.getDescription()).isEqualTo(" 双十一前完成采购 ");
        assertThat(vo.getStatus()).isEqualTo(TodoConstants.STATUS_UNDONE);
        assertThat(vo.getGmtCreate()).isNotNull();

        verify(todoMapper, times(1)).insert(any(TodoDO.class));
    }

    @Test
    void should_persistStatusUndoneAndTimestamps_when_createTodo() {
        // Arrange
        stubInsertWithIdBackfill();
        TodoCreateRequest request = buildRequest(" 交周报 ", null);

        // Act
        todoService.createTodo(request);

        // Assert —— 验证落库对象字段正确
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        TodoDO saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("交周报");
        assertThat(saved.getStatus()).isEqualTo(TodoConstants.STATUS_UNDONE);
        assertThat(saved.getGmtCreate()).isNotNull();
        assertThat(saved.getGmtModified()).isNotNull();
    }

    @Test
    void should_throwException_when_nameIsBlank() {
        // Arrange
        TodoCreateRequest blankName = buildRequest("   ", null);
        TodoCreateRequest nullName = buildRequest(null, null);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(blankName))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
        assertThatThrownBy(() -> todoService.createTodo(nullName))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
    }

    @Test
    void should_throwException_when_requestIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("事项名称不能为空");
    }

    @Test
    void should_throwException_when_insertAffectsZeroRows() {
        // Arrange
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(buildRequest(" 待办 ", null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("新增待办事项失败");
    }

    /**
     * 模拟数据库自增主键回填的插入行为。
     */
    private void stubInsertWithIdBackfill() {
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(TEST_TODO_ID);
            return 1;
        });
    }

    /**
     * 构造测试请求。
     *
     * @param name        事项名称
     * @param description 事项描述
     * @return 新增请求
     */
    private TodoCreateRequest buildRequest(String name, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}