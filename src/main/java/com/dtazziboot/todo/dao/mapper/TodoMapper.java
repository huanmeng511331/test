package com.dtazziboot.todo.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.dtazziboot.todo.dao.entity.TodoDO;

/**
 * 待办事项数据访问接口
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
@Mapper
public interface TodoMapper {

    /**
     * 插入一条待办事项记录
     *
     * @param todoDO 待办事项数据对象，主键由数据库自增回填
     * @return 受影响行数
     */
    int insert(TodoDO todoDO);
}