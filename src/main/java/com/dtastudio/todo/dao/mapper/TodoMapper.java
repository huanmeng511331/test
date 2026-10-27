package com.dtastudio.todo.dao.mapper;

import com.dtastudio.todo.model.entity.TodoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 待办事项数据访问接口。
 *
 * @author AiWork
 * @date 2026/10/27
 */
@Mapper
public interface TodoMapper {

    /**
     * 插入待办事项。
     *
     * @param todo 待办事项数据对象
     * @return 受影响行数
     */
    int insert(TodoDO todo);

    /**
     * 根据主键查询待办事项。
     *
     * @param id 主键ID
     * @return 待办事项数据对象，不存在则返回 null
     */
    TodoDO selectById(@Param("id") Long id);
}