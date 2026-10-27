package com.dtk.todo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dtk.todo.common.constant.TodoErrorCodeEnum;
import com.dtk.todo.common.model.ApiResponse;
import com.dtk.todo.model.dto.TodoItemCreateRequest;
import com.dtk.todo.model.dto.TodoItemVO;
import com.dtk.todo.service.TodoItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * {@link TodoItemController} 切片测试
 *
 * @author AiWork
 * @date 2026/10/27
 */
@WebMvcTest(TodoItemController.class)
class TodoItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoItemService todoItemService;

    @Test
    void should_returnCreatedTodo_when_requestIsValid() throws Exception {
        // Arrange
        TodoItemCreateRequest request = buildRequest("完成周报", "整理本周工作内容");

        TodoItemVO vo = new TodoItemVO();
        vo.setId(1L);
        vo.setTitle("完成周报");
        vo.setDescription("整理本周工作内容");

        given(todoItemService.createTodoItem(any(TodoItemCreateRequest.class))).willReturn(vo);

        // Act & Assert
        mockMvc.perform(post("/todo_items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ApiResponse.SUCCESS_CODE))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("完成周报"))
                .andExpect(jsonPath("$.data.description").value("整理本周工作内容"));
    }

    @Test
    void should_returnParamError_when_titleIsBlank() throws Exception {
        // Arrange
        TodoItemCreateRequest request = buildRequest("   ", null);

        // Act & Assert
        mockMvc.perform(post("/todo_items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ApiResponse.ERROR_CODE))
                .andExpect(jsonPath("$.errorCode")
                        .value(TodoErrorCodeEnum.PARAM_INVALID.getCode()));
    }

    /**
     * 构造新增请求对象
     *
     * @param title 事项名称
     * @param description 事项描述
     * @return 新增请求对象
     */
    private TodoItemCreateRequest buildRequest(String title, String description) {
        TodoItemCreateRequest request = new TodoItemCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        return request;
    }
}