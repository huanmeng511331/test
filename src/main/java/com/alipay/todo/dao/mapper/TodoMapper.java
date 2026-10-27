package com.alipay.todo.dao.mapper;

import com.alipay.todo.dao.entity.TodoDO;

/**
 * 待办事项数据访问接口。
 */
public interface TodoMapper {

    /**
     * 插入待办事项，数据库生成主键后回填到 {@code todoDO.id}。
     *
     * @param todoDO 待保存的待办事项
     * @return 受影响行数
     */
    int insert(TodoDO todoDO);
}
