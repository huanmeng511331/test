package com.dtazziboot.todo.service.impl;

import com.dtazziboot.todo.common.exception.BusinessException;
import com.dtazziboot.todo.common.enums.TodoErrorCodeEnum;
import com.dtazziboot.todo.dao.mapper.TodoMapper;
import com.dtazziboot.todo.model.dto.TodoCreateRequest;
import com.dtazziboot.todo.model.dto.TodoVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 待办事项服务实现类单元测试。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class TodoServiceImplTest {

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    @BeforeEach
    void setUp() {
        // 通用初始化，无场景化数据准备
    }

    // ==================== createTodo 测试 ====================

    @Test
    void should_createTodo_when_requestIsValid() {
        // Arrange (Given)
        TodoCreateRequest request = buildRequest("买牛奶", "下班后去超市购买");
        when(todoMapper.insert(any())).thenAnswer(invocation -> {
            com.dtazziboot.todo.model.entity.TodoDO arg = invocation.getArgument(0);
            arg.setId(1L);
            return 1;
        });

        // Act (When)
        TodoVO result = todoService.createTodo(request);

        // Assert (Then)
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("买牛奶");
        assertThat(result.getDescription()).isEqualTo("下班后去超市购买");
        assertThat(result.getCreateTime()).isNotBlank();
        verify(todoMapper, times(1)).insert(any());
    }

    @Test
    void should_createTodo_when_descriptionIsNull() {
        // Arrange (Given)
        TodoCreateRequest request = buildRequest("仅有名称", null);
        when(todoMapper.insert(any())).thenAnswer(invocation -> {
            com.dtazziboot.todo.model.entity.TodoDO arg = invocation.getArgument(0);
            arg.setId(2L);
            return 1;
        });

        // Act (When)
        TodoVO result = todoService.createTodo(request);

        // Assert (Then)
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("仅有名称");
        assertThat(result.getDescription()).isNull();
    }

    @Test
    void should_throwBusinessException_when_requestIsNull() {
        // Act & Assert (When & Then)
        assertThatThrownBy(() -> todoService.createTodo(null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.PARAM_INVALID.getMessage());

        // verify 未写库
        verify(todoMapper, never()).insert(any());
    }

    @Test
    void should_throwBusinessException_when_nameIsBlank() {
        // Arrange (Given)
        TodoCreateRequest request = buildRequest("   ", "描述");

        // Act & Assert (When & Then)
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.PARAM_INVALID.getMessage());

        verify(todoMapper, never()).insert(any());
    }

    @Test
    void should_throwBusinessException_when_insertAffectedZeroRow() {
        // Arrange (Given)
        TodoCreateRequest request = buildRequest("名称", "描述");
        ArgumentCaptor<com.dtazziboot.todo.model.entity.TodoDO> captor =
                ArgumentCaptor.forClass(com.dtazziboot.todo.model.entity.TodoDO.class);
        when(todoMapper.insert(any())).thenReturn(0);

        // Act & Assert (When & Then)
        assertThatThrownBy(() -> todoService.createTodo(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(TodoErrorCodeEnum.SYSTEM_ERROR.getMessage());

        verify(todoMapper, times(1)).insert(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("名称");
        assertThat(captor.getValue().getGmtCreate()).isNotNull();
        assertThat(captor.getValue().getGmtModified()).isNotNull();
    }

    private TodoCreateRequest buildRequest(String name, String description) {
        TodoCreateRequest request = new TodoCreateRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }
}