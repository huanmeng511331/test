package com.antdigital.todo.dao.mapper;

import com.antdigital.todo.model.do.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问层。
 */
@Mapper
public interface TodoItemMapper {

    /**
     * 新增待办事项，回填主键到入参的 id 字段。
     *
     * @param todoItem 待办事项数据对象
     * @return 影响行数
     */
    int insert(TodoItemDO todoItem);
}