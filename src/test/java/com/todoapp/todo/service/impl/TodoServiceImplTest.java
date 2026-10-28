package com.todoapp.todo.service.impl;

import com.todoapp.common.exception.BusinessException;
import com.todoapp.todo.constant.TodoErrorCode;
import com.todoapp.todo.dao.entity.TodoDO;
import com.todoapp.todo.dao.mapper.TodoMapper;
import com.todoapp.todo.service.dto.TodoCreateRequest;
import com.todoapp.todo.service.dto.TodoCreateResult;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link TodoServiceImpl} 单元测试
 *
 * <p>被测类: TodoServiceImpl
 * <p>Mock 依赖: TodoMapper
 * <p>真实依赖: SimpleMeterRegistry（内存指标注册表，验证埋点正确性）
 * <p>覆盖场景: 正常路径、参数校验（R01~R03）、边界值、异常路径（DB 异常/ID 为空）、指标埋点
 *
 * @author AiWork
 * @date 2026/10/28
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl 单元测试")
class TodoServiceImplTest {

    private static final String TEST_TITLE = "整理周报";
    private static final String TEST_DESCRIPTION = "汇总本周交付与下周计划";
    private static final String TEST_CREATOR = "user001";
    private static final String TEST_TENANT_ID = "";

    private static final int TITLE_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MAX_LENGTH = 1000;

    @Mock
    private TodoMapper todoMapper;

    private MeterRegistry meterRegistry;

    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        todoService = new TodoServiceImpl(todoMapper, meterRegistry);
    }

    // ==================== createTodo 正常路径 ====================

    @Test
    @DisplayName("正常路径：合法 title 和 description，返回新建 ID")
    void should_returnId_when_validRequest() {
        // Arrange
        TodoCreateRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(1001L);
            return 1;
        });

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1001L);
    }

    @Test
    @DisplayName("边界值：description 为 null 时视为空串，正常创建")
    void should_useEmptyString_when_descriptionIsNull() {
        // Arrange
        TodoCreateRequest request = buildRequest(TEST_TITLE, null);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(1002L);
            return 1;
        });

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getId()).isEqualTo(1002L);
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        assertThat(captor.getValue().getDescription()).isEqualTo("");
    }

    @Test
    @DisplayName("边界值：title 恰好 100 字符、description 恰好 1000 字符，正常创建")
    void should_createSuccessfully_when_titleAndDescriptionAtMaxLength() {
        // Arrange
        String maxTitle = "a".repeat(TITLE_MAX_LENGTH);
        String maxDescription = "b".repeat(DESCRIPTION_MAX_LENGTH);
        TodoCreateRequest request = buildRequest(maxTitle, maxDescription);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(1003L);
            return 1;
        });

        // Act
        TodoCreateResult result = todoService.createTodo(request);

        // Assert
        assertThat(result.getId()).isEqualTo(1003L);
    }

    @Test
    @DisplayName("数据验证：mapper 接收的 TodoDO 字段与请求一致")
    void should_passCorrectTodoToMapper_when_validRequest() {
        // Arrange
        TodoCreateRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(1004L);
            return 1;
        });

        // Act
        todoService.createTodo(request);

        // Assert
        ArgumentCaptor<TodoDO> captor = ArgumentCaptor.forClass(TodoDO.class);
        verify(todoMapper).insert(captor.capture());
        TodoDO saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo(TEST_TITLE);
        assertThat(saved.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(saved.getCreator()).isEqualTo(TEST_CREATOR);
        assertThat(saved.getTenantId()).isEqualTo(TEST_TENANT_ID);
    }

    // ==================== createTodo 参数校验（R01~R03） ====================

    @Test
    @DisplayName("R01：title 为 null 时抛出 TODO_001")
    void should_throwException_when_titleIsNull() {
        // Arrange
        TodoCreateRequest request = buildRequest(null, TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(TodoErrorCode.TITLE_EMPTY.getCode());
                    assertThat(be.getErrorMessage()).isEqualTo(TodoErrorCode.TITLE_EMPTY.getMessage());
                });
    }

    @Test
    @DisplayName("R01：title 为纯空白时抛出 TODO_001")
    void should_throwException_when_titleIsBlank() {
        // Arrange
        TodoCreateRequest request = buildRequest("   ", TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(TodoErrorCode.TITLE_EMPTY.getCode());
                });
    }

    @Test
    @DisplayName("R02：title 超过 100 字符时抛出 TODO_002")
    void should_throwException_when_titleExceedsMaxLength() {
        // Arrange
        String longTitle = "x".repeat(TITLE_MAX_LENGTH + 1);
        TodoCreateRequest request = buildRequest(longTitle, TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(TodoErrorCode.TITLE_LENGTH_EXCEEDED.getCode());
                    assertThat(be.getErrorMessage()).isEqualTo(TodoErrorCode.TITLE_LENGTH_EXCEEDED.getMessage());
                });
    }

    @Test
    @DisplayName("R03：description 超过 1000 字符时抛出 TODO_002")
    void should_throwException_when_descriptionExceedsMaxLength() {
        // Arrange
        String longDescription = "y".repeat(DESCRIPTION_MAX_LENGTH + 1);
        TodoCreateRequest request = buildRequest(TEST_TITLE, longDescription);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(TodoErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getCode());
                    assertThat(be.getErrorMessage()).isEqualTo(TodoErrorCode.DESCRIPTION_LENGTH_EXCEEDED.getMessage());
                });
    }

    // ==================== createTodo 异常路径 ====================

    @Test
    @DisplayName("异常路径：mapper.insert 抛出异常时返回 TODO_003")
    void should_throwException_when_mapperInsertThrows() {
        // Arrange
        TodoCreateRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class)))
                .thenThrow(new RuntimeException("DB connection failed"));

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(TodoErrorCode.CREATE_FAILED.getCode());
                });
    }

    @Test
    @DisplayName("异常路径：insert 后 id 为 null 时返回 TODO_003")
    void should_throwException_when_insertReturnsNullId() {
        // Arrange
        TodoCreateRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(TodoErrorCode.CREATE_FAILED.getCode());
                });
    }

    // ==================== 指标埋点验证 ====================

    @Test
    @DisplayName("指标埋点：成功路径后 total/success 计数 +1，failure 为 0")
    void should_recordSuccessMetrics_when_createSucceeds() {
        // Arrange
        TodoCreateRequest request = buildRequest(TEST_TITLE, TEST_DESCRIPTION);
        when(todoMapper.insert(any(TodoDO.class))).thenAnswer(invocation -> {
            TodoDO todo = invocation.getArgument(0);
            todo.setId(2001L);
            return 1;
        });

        // Act
        todoService.createTodo(request);

        // Assert
        assertThat(meterRegistry.counter("todo.create.total").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("todo.create.success").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("todo.create.failure").count()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("指标埋点：失败路径后 total/failure 计数 +1，success 为 0")
    void should_recordFailureMetrics_when_createFails() {
        // Arrange
        TodoCreateRequest request = buildRequest(null, TEST_DESCRIPTION);

        // Act & Assert
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class);

        assertThat(meterRegistry.counter("todo.create.total").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("todo.create.failure").count()).isEqualTo(1.0);
        assertThat(meterRegistry.counter("todo.create.success").count()).isEqualTo(0.0);
    }

    // ==================== 测试数据构造 ====================

    /**
     * 构建创建待办请求
     *
     * @param title       事项名称
     * @param description 事项描述
     * @return 创建待办请求
     */
    private static TodoCreateRequest buildRequest(String title, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCreator(TEST_CREATOR);
        request.setTenantId(TEST_TENANT_ID);
        return request;
    }
}
