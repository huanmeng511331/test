package com.dtazziboot.todo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.dao.entity.TodoDO;
import com.dtazziboot.todo.dao.mapper.TodoMapper;
import com.dtazziboot.todo.model.dto.TodoRequest;
import com.dtazziboot.todo.model.dto.TodoVO;

/**
 * 待办事项业务服务实现类单元测试
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        // 模拟数据库自增主键回填
        doAnswer(invocation -> {
            TodoDO todoDO = invocation.getArgument(0);
            todoDO.setId(10001L);
            return 1;
        }).when(todoMapper).insert(any(TodoDO.class));
    }

    // ==================== createTodo 测试 ====================

    @Test
    void should_createTodoAndReturnVO_when_requestValid() {
        // Arrange
        TodoRequest request = buildRequest("写周报", "整理本周工作进展");

        // Act
        TodoVO result = todoService.createTodo(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10001L);
        assertThat(result.getName()).isEqualTo("写周报");
        assertThat(result.getDescription()).isEqualTo("整理本周工作进展");
        assertThat(result.getGmtCreate()).isNotBlank();
        verify(todoMapper).insert(any(TodoDO.class));
    }

    @Test
    void should_createTodo_when_descriptionIsNull() {
        // Arrange
        TodoRequest request = buildRequest("写周报", null);

        // Act
        TodoVO result = todoService.createTodo(request);

        // Assert
        assertThat(result.getDescription()).isNull();
        assertThat(result.getName()).isEqualTo("写周报");
    }

    @Test
    void should_trimName_when_nameHasLeadingTrailingBlank() {
        // Arrange
        TodoRequest request = buildRequest("  写周报  ", "描述");

        // Act
        TodoVO result = todoService.createTodo(request);

        // Assert
        assertThat(result.getName()).isEqualTo("写周报");
    }

    @Test
    void should_throwException_when_nameIsNull() {
        // Arrange
        TodoRequest request = buildRequest(null, "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待办事项名称不能为空");
    }

    @Test
    void should_throwException_when_nameIsBlank() {
        // Arrange
        TodoRequest request = buildRequest("   ", "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待办事项名称不能为空");
    }

    @Test
    void should_throwException_when_nameExceedsMaxLength() {
        // Arrange
        String overLengthName = "a".repeat(101);
        TodoRequest request = buildRequest(overLengthName, "描述");

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待办事项名称长度不能超过100");
    }

    @Test
    void should_throwException_when_descriptionExceedsMaxLength() {
        // Arrange
        String overLengthDescription = "a".repeat(501);
        TodoRequest request = buildRequest("写周报", overLengthDescription);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待办事项描述长度不能超过500");
    }

    @Test
    void should_throwException_when_insertAffectedRowsNotOne() {
        // Arrange
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(buildRequest("写周报", "描述")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待办事项创建失败");
    }

    /**
     * 构建测试请求对象
     *
     * @param name        待办事项名称
     * @param description 待办事项描述
     * @return 测试请求对象
     */
    private TodoRequest buildRequest(String name, String description) {
        TodoRequest request = new TodoRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}