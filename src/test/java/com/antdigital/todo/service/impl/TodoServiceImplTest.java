package com.antdigital.todo.service.impl;

import com.antdigital.todo.common.enums.ErrorCodeEnum;
import com.antdigital.todo.common.exception.BusinessException;
import com.antdigital.todo.dao.mapper.TodoItemMapper;
import com.antdigital.todo.model.do.TodoItemDO;
import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.vo.TodoItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl 单元测试")
class TodoServiceImplTest {

    private static final long TEST_ID = 1L;

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO item = invocation.getArgument(0);
            item.setId(TEST_ID);
            return 1;
        });
    }

    // ==================== createTodo 测试 ====================

    @Nested
    @DisplayName("新增待办事项")
    class CreateTodoTest {

        @Test
        @DisplayName("正常路径：合法请求应返回持久化后的展示对象")
        void should_returnTodoItemVO_when_requestIsValid() {
            // Arrange
            TodoCreateRequest request = buildRequest("编写周报", "整理本周工作产出");

            // Act
            TodoItemVO vo = todoService.createTodo(request);

            // Assert
            assertThat(vo).isNotNull();
            assertThat(vo.getId()).isEqualTo(String.valueOf(TEST_ID));
            assertThat(vo.getItemName()).isEqualTo("编写周报");
            assertThat(vo.getDescription()).isEqualTo("整理本周工作产出");

            ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
            verify(todoItemMapper, times(1)).insert(captor.capture());
            TodoItemDO saved = captor.getValue();
            assertThat(saved.getItemName()).isEqualTo("编写周报");
            assertThat(saved.getDescription()).isEqualTo("整理本周工作产出");
        }

        @Test
        @DisplayName("边界路径：事项名称为 null 应抛出参数错误")
        void should_throwException_when_itemNameIsNull() {
            // Arrange
            TodoCreateRequest request = buildRequest(null, "描述");

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项名称不能为空")
                    .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                            .isEqualTo(ErrorCodeEnum.INVALID_PARAMETER.getCode()));
        }

        @Test
        @DisplayName("边界路径：事项名称为空白字符串应抛出参数错误")
        void should_throwException_when_itemNameIsBlank() {
            // Arrange
            TodoCreateRequest request = buildRequest("   ", "描述");

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项名称不能为空");
        }

        @Test
        @DisplayName("边界路径：事项名称超过 200 字符应抛出参数错误")
        void should_throwException_when_itemNameTooLong() {
            // Arrange
            TodoCreateRequest request = buildRequest("a".repeat(201), "描述");

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项名称长度不能超过200个字符");
        }

        @Test
        @DisplayName("边界路径：描述为 null 时按空串落库")
        void should_persistEmptyDescription_when_descriptionIsNull() {
            // Arrange
            TodoCreateRequest request = buildRequest("编写周报", null);

            // Act
            TodoItemVO vo = todoService.createTodo(request);

            // Assert
            assertThat(vo.getDescription()).isEqualTo("");

            ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
            verify(todoItemMapper, times(1)).insert(captor.capture());
            assertThat(captor.getValue().getDescription()).isEqualTo("");
        }

        @Test
        @DisplayName("边界路径：描述超过 1000 字符应抛出参数错误")
        void should_throwException_when_descriptionTooLong() {
            // Arrange
            TodoCreateRequest request = buildRequest("编写周报", "a".repeat(1001));

            // Act & Assert
            assertThatThrownBy(() -> todoService.createTodo(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("事项描述长度不能超过1000个字符");
        }

        @Test
        @DisplayName("正常路径：入参名称首尾空白应被 trim")
        void should_trimItemName_when_hasSurroundingWhitespace() {
            // Arrange
            TodoCreateRequest request = buildRequest("  编写周报  ", "整理本周工作产出");

            // Act
            TodoItemVO vo = todoService.createTodo(request);

            // Assert
            assertThat(vo.getItemName()).isEqualTo("编写周报");

            ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
            verify(todoItemMapper, times(1)).insert(captor.capture());
            assertThat(captor.getValue().getItemName()).isEqualTo("编写周报");
        }
    }

    private TodoCreateRequest buildRequest(String itemName, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setItemName(itemName);
        request.setDescription(description);
        return request;
    }
}