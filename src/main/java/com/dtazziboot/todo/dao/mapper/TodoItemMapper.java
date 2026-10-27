package com.dtazziboot.todo.dao.mapper;

import com.dtazziboot.todo.model.entity.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问层。
 */
@Mapper
public interface TodoItemMapper {

    /**
     * 新增待办事项。
     *
     * @param todoItem 待办事项数据对象，写入成功后回填主键 {@code id}
     * @return 受影响行数
     */
    int insert(TodoItemDO todoItem);
}