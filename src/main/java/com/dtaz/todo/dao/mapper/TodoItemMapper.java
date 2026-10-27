package com.dtaz.todo.dao.mapper;

import com.dtaz.todo.model.do.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问层
 *
 * @author AiWork
 * @date 2026/10/27
 */
@Mapper
public interface TodoItemMapper {

    /**
     * 新增待办事项
     *
     * @param todoItemDO 待办事项数据对象
     * @return 受影响行数
     */
    int insert(TodoItemDO todoItemDO);
}