package com.example.todo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.todo.common.constant.ErrorCodeEnum;
import com.example.todo.common.exception.BusinessException;
import com.example.todo.dao.mapper.TodoItemMapper;
import com.example.todo.model.do.TodoItemDO;
import com.example.todo.service.dto.TodoItemRequest;
import com.example.todo.service.dto.TodoItemVO;

/**
 * 待办事项服务实现类单元测试
 *
 * @author AiWork
 * @date 2026/10/27
 */
@ExtendWith(MockitoExtension.class)
class TodoItemServiceImplTest {

    private static final String TEST_NAME = "购买牛奶";
    private static final String TEST_DESCRIPTION = "下班前去超市购买一箱牛奶";

    @Mock
    private TodoItemMapper todoItemMapper;

    @InjectMocks
    private TodoItemServiceImpl todoItemService;

    @BeforeEach
    void setUp() {
        // 通用初始化，Mockito 注解已由 MockitoExtension 处理
    }

    // ==================== createTodoItem 测试 ====================

    @Test
    @DisplayName("正常路径：新增待办事项成功并返回视图对象")
    void should_returnTodoItemVO_when_createSuccess() {
        // Arrange
        TodoItemRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO item = invocation.getArgument(0);
            item.setId(1L);
            return 1;
        });
        when(todoItemMapper.selectById(anyLong())).thenAnswer(invocation -> {
            TodoItemDO item = new TodoItemDO();
            item.setId(1L);
            item.setName(TEST_NAME);
            item.setDescription(TEST_DESCRIPTION);
            item.setGmtCreate(LocalDateTime.now());
            item.setGmtModified(LocalDateTime.now());
            return item;
        });

        // Act
        TodoItemVO vo = todoItemService.createTodoItem(request);

        // Assert
        assertThat(vo.getId()).isEqualTo("1");
        assertThat(vo.getName()).isEqualTo(TEST_NAME);
        assertThat(vo.getDescription()).isEqualTo(TEST_DESCRIPTION);
        verify(todoItemMapper).insert(any(TodoItemDO.class));
        verify(todoItemMapper).selectById(1L);
    }

    @Test
    @DisplayName("边界路径：描述为空仍正常创建")
    void should_createSuccess_when_descriptionIsNull() {
        // Arrange
        TodoItemRequest request = buildRequest(TEST_NAME, null);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenAnswer(invocation -> {
            TodoItemDO item = invocation.getArgument(0);
            item.setId(2L);
            return 1;
        });
        when(todoItemMapper.selectById(anyLong())).thenAnswer(invocation -> {
            TodoItemDO item = new TodoItemDO();
            item.setId(2L);
            item.setName(TEST_NAME);
            item.setDescription(null);
            item.setGmtCreate(LocalDateTime.now());
            item.setGmtModified(LocalDateTime.now());
            return item;
        });

        // Act
        TodoItemVO vo = todoItemService.createTodoItem(request);

        // Assert
        assertThat(vo.getId()).isEqualTo("2");
        assertThat(vo.getDescription()).isNull();
    }

    @Test
    @DisplayName("异常路径：插入失败抛出业务异常")
    void should_throwException_when_insertFails() {
        // Arrange
        TodoItemRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(0);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCodeEnum.SYSTEM_ERROR.getCode());
        verify(todoItemMapper, never()).selectById(anyLong());
    }

    @Test
    @DisplayName("异常路径：回查数据不存在抛出业务异常")
    void should_throwException_when_selectByIdReturnsNull() {
        // Arrange
        TodoItemRequest request = buildRequest(TEST_NAME, TEST_DESCRIPTION);
        when(todoItemMapper.insert(any(TodoItemDO.class))).thenReturn(1);
        when(todoItemMapper.selectById(anyLong())).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> todoItemService.createTodoItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCodeEnum.SYSTEM_ERROR.getCode());
        verify(todoItemMapper).selectById(anyLong());
    }

    /**
     * 构造新增待办事项请求
     *
     * @param name        事项名称
     * @param description 事项描述
     * @return 请求对象
     */
    private TodoItemRequest buildRequest(String name, String description) {
        TodoItemRequest request = new TodoItemRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}