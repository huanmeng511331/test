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

    /**
     * 根据主键查询待办事项，不存在时返回 null。
     *
     * @param id 主键ID
     * @return 待办事项数据对象，可能为 null
     */
    TodoItemDO selectById(Long id);
}