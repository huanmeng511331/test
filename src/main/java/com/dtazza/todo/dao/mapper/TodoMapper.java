package com.dtazza.todo.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dtazza.todo.dao.entity.TodoDO;

/**
 * 待办事项数据访问接口。
 */
@Mapper
public interface TodoMapper {

    /**
     * 插入一条待办事项记录。
     *
     * @param todo 待办事项数据对象
     * @return 受影响的行数
     */
    int insert(TodoDO todo);
}
