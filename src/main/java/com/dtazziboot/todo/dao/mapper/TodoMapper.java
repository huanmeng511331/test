package com.dtazziboot.todo.dao.mapper;

import com.dtazziboot.todo.model.entity.TodoDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问层接口。
 */
@Mapper
public interface TodoMapper {

    /**
     * 新增待办事项。
     *
     * @param todoDO 待办事项数据对象
     * @return 受影响行数
     */
    int insert(TodoDO todoDO);
}