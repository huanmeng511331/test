package com.todoapp.todo.dao.mapper;

import com.todoapp.todo.dao.entity.TodoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问接口
 *
 * @author AiWork
 * @date 2026/10/28
 */
@Mapper
public interface TodoMapper {

    /**
     * 插入待办记录
     *
     * @param todo 待办数据对象
     * @return 受影响行数
     */
    int insert(TodoDO todo);
}
