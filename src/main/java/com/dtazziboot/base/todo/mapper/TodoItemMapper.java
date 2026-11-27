package com.dtazziboot.base.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtazziboot.base.todo.model.entity.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问:单表插入,走 MyBatis-Plus 通用能力,参数化预编译。
 */
@Mapper
public interface TodoItemMapper extends BaseMapper<TodoItemDO> {
}
