package com.dtazziboot.base.todo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dtazziboot.base.common.auth.LoginUserContext;
import com.dtazziboot.base.common.exception.BizException;
import com.dtazziboot.base.todo.mapper.TodoItemMapper;
import com.dtazziboot.base.todo.model.dto.CreateTodoItemRequest;
import com.dtazziboot.base.todo.model.dto.TodoItemResult;
import com.dtazziboot.base.todo.model.entity.TodoItemDO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

/**
 * TodoItemServiceImpl 单元测试:纯 Java 单测,不启动 Spring 容器。
 */
@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

    private static final String CREATOR = "zhangsan";

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    @BeforeEach
    void setUp() {
        LoginUserContext.setCurrentUser(CREATOR);
    }

    @AfterEach
    void tearDown() {
        LoginUserContext.clear();
    }

    // ==================== createTodoItem 测试 ====================

    @Test
    @DisplayName("合法请求:落库并返回含自增ID与创建时间的结果")
    void should_insertAndReturnResult_when_validRequest() {
        // Arrange:模拟 DB 自增主键回填与 gmt 字段自动填充
        CreateTodoItemRequest request = buildRequest("周三前完成评审材料", "补充性能测试数据后提交");
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO entity = invocation.getArgument(0);
            entity.setId(10001L);
            entity.setGmtCreate(java.time.LocalDateTime.of(2026, 11, 27, 10, 30, 0));
            return 1;
        });

        // Act
        TodoItemResult result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result.getId()).isEqualTo(10001L);
        assertThat(result.getName()).isEqualTo("周三前完成评审材料");
        assertThat(result.getCreator()).isEqualTo(CREATOR);
        assertThat(result.getGmtCreate()).isEqualTo("2026-11-27 10:30:00");

        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper).insert(captor.capture());
        TodoItemDO inserted = captor.getValue();
        assertThat(inserted.getTenantId()).isEqualTo(0L);
        assertThat(inserted.getDeleted()).isEqualTo(0L);
        assertThat(inserted.getCreator()).isEqualTo(CREATOR);
        assertThat(inserted.getModifier()).isEqualTo(CREATOR);
        assertThat(inserted.getDescription()).isEqualTo("补充性能测试数据后提交");
    }

    @Test
    @DisplayName("名称含首尾空格:trim 后落库并回显")
    void should_trimName_when_nameHasSurroundingSpaces() {
        // Arrange
        CreateTodoItemRequest request = buildRequest("  完成周报  ", null);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(1);

        // Act
        TodoItemResult result = todoItemService.createTodoItem(request);

        // Assert
        assertThat(result.getName()).isEqualTo("完成周报");
        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper).insert(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("完成周报");
    }

    @Test
    @DisplayName("描述缺省:按空串落库")
    void should_defaultEmptyDescription_when_descriptionIsNull() {
        // Arrange
        CreateTodoItemRequest request = buildRequest("完成周报", null);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(1);

        // Act
        todoItemService.createTodoItem(request);

        // Assert
        ArgumentCaptor<TodoItemDO> captor = ArgumentCaptor.forClass(TodoItemDO.class);
        verify(todoItemMapper).insert(captor.capture());
        assertThat(captor.getValue().getDescription()).isEmpty();
    }

    @Test
    @DisplayName("无有效登录态:拒绝并返回 AUTH_001,不落库")
    void should_throwAuthException_when_noLoginUser() {
        // Arrange:清除登录态
        LoginUserContext.clear();

        // Act
        BizException ex = catchThrowableOfType(
                () -> todoItemService.createTodoItem(buildRequest("完成周报", null)),
                BizException.class);

        // Assert
        assertThat(ex.getCode()).isEqualTo("AUTH_001");
        verify(todoItemMapper, never()).insert(any(TodoItemDO.class));
    }

    @Test
    @DisplayName("DB 写入失败:异常向上抛出,由全局异常处理转 SYS_999")
    void should_propagateException_when_dbInsertFails() {
        // Arrange
        when(todoItemMapper.insert(any(TodoItemDO.class)))
                .thenThrow(new DataAccessResourceFailureException("connection refused"));

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(buildRequest("完成周报", null)))
                .isInstanceOf(DataAccessResourceFailureException.class)
                .hasMessageContaining("connection refused");
    }

    private CreateTodoItemRequest buildRequest(String name, String description) {
        CreateTodoItemRequest request = new CreateTodoItemRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}
