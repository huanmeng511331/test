package com.example.todo.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.todo.model.do.TodoItemDO;

/**
 * 待办事项数据访问接口
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

    /**
     * 根据ID查询待办事项
     *
     * @param id 主键ID
     * @return 待办事项数据对象，不存在返回null
     */
    TodoItemDO selectById(@Param("id") Long id);
}