package com.dtaz.todo.dao.mapper;

import com.dtaz.todo.model.entity.TodoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 待办事项数据访问接口。
 */
@Mapper
public interface TodoMapper {

    /**
     * 新增待办事项。
     *
     * @param todo 待办事项数据对象
     * @return 影响行数
     */
    int insert(@Param("todo") TodoDO todo);

    /**
     * 根据主键查询待办事项。
     *
     * @param id 主键ID
     * @return 待办事项，不存在时返回 null
     */
    TodoDO findById(@Param("id") Long id);
}