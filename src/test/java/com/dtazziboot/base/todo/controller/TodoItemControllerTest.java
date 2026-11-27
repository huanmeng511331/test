package com.dtazziboot.base.todo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dtazziboot.base.common.exception.GlobalExceptionHandler;
import com.dtazziboot.base.todo.config.TodoProperties;
import com.dtazziboot.base.todo.metrics.TodoItemCreateMetrics;
import com.dtazziboot.base.todo.model.dto.CreateTodoItemRequest;
import com.dtazziboot.base.todo.model.dto.TodoItemResult;
import com.dtazziboot.base.todo.service.TodoItemService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * TodoItemController 切片测试:MockMvc standalone,Mock Service 层。
 */
class TodoItemControllerTest {

    private static final String CREATE_URI = "/api/todo/items";

    private TodoItemService todoItemService;

    private TodoProperties todoProperties;

    private SimpleMeterRegistry meterRegistry;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        todoItemService = mock(TodoItemService.class);
        todoProperties = new TodoProperties();
        // CR P0-1 修复:standalone 模式不自动装配 @RestControllerAdvice,必须显式注册全局异常处理器,
        // 否则 BizException 未转统一错误体,负向用例断言 $.code 前即抛 NestedServletException
        meterRegistry = new SimpleMeterRegistry();
        mockMvc = MockMvcBuilders.standaloneSetup(new TodoItemController(todoItemService, todoProperties,
                        new TodoItemCreateMetrics(meterRegistry)))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("合法请求:返回 code=OK 与创建结果")
    void should_returnOk_when_createSuccess() throws Exception {
        // Arrange
        when(todoItemService.createTodoItem(any(CreateTodoItemRequest.class)))
                .thenReturn(new TodoItemResult(10001L, "周三前完成评审材料", "zhangsan", "2026-11-27 10:30:00"));

        // Act & Assert
        mockMvc.perform(post(CREATE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"周三前完成评审材料\",\"description\":\"补充性能测试数据后提交\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.msg").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(10001))
                .andExpect(jsonPath("$.data.creator").value("zhangsan"))
                .andExpect(jsonPath("$.data.gmtCreate").value("2026-11-27 10:30:00"));
    }

    @Test
    @DisplayName("DB 写入失败:埋点单独计数,全局异常处理兜底返回 SYS_999")
    void should_returnSys999_when_dbWriteFails() throws Exception {
        // Arrange
        when(todoItemService.createTodoItem(any(CreateTodoItemRequest.class)))
                .thenThrow(new DataAccessResourceFailureException("connection refused"));

        // Act & Assert:控制器捕获计数后原样上抛,由 GlobalExceptionHandler 转 SYS_999
        mockMvc.perform(post(CREATE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"完成周报\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SYS_999"))
                .andExpect(jsonPath("$.msg").value("系统繁忙,请稍后重试"));
        verify(todoItemService).createTodoItem(any());
        assertThat(meterRegistry.counter("todo_item_create_total",
                "source", "oneapi", "result", "db_fail", "code", "SYS_999").count()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("名称为空/纯空格:返回 TODO_001,不进入业务")
    void should_returnTodo001_when_nameBlank() throws Exception {
        // Act & Assert
        mockMvc.perform(post(CREATE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TODO_001"))
                .andExpect(jsonPath("$.msg").value("事项名称不能为空"));
        verify(todoItemService, never()).createTodoItem(any());
    }

    @Test
    @DisplayName("名称超长(65 字符):返回 TODO_002")
    void should_returnTodo002_when_nameTooLong() throws Exception {
        // Arrange:65 个汉字
        String longName = "名".repeat(65);

        // Act & Assert
        mockMvc.perform(post(CREATE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + longName + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TODO_002"))
                .andExpect(jsonPath("$.msg").value("事项名称超过 64 字符"));
        verify(todoItemService, never()).createTodoItem(any());
    }

    @Test
    @DisplayName("描述超长(501 字符):返回 TODO_003")
    void should_returnTodo003_when_descriptionTooLong() throws Exception {
        // Arrange:501 个汉字
        String longDescription = "描".repeat(501);

        // Act & Assert
        mockMvc.perform(post(CREATE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"完成周报\",\"description\":\"" + longDescription + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TODO_003"))
                .andExpect(jsonPath("$.msg").value("事项描述超过 500 字符"));
        verify(todoItemService, never()).createTodoItem(any());
    }

    @Test
    @DisplayName("功能开关关闭:返回功能维护中,不进入业务")
    void should_returnMaintenance_when_switchDisabled() throws Exception {
        // Arrange
        todoProperties.getCreate().setEnabled(false);

        // Act & Assert
        mockMvc.perform(post(CREATE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"完成周报\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TODO_004"))
                .andExpect(jsonPath("$.msg").value("功能维护中,请稍后重试"));
        verify(todoItemService, never()).createTodoItem(any());
    }
}
