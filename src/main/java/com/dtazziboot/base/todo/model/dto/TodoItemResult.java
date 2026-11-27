package com.dtazziboot.base.todo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增待办事项结果(出参 data 节点)。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemResult {

    /** 系统生成的待办事项 ID */
    private Long id;

    /** 事项名称(服务端 trim 后回显) */
    private String name;

    /** 创建人工号 */
    private String creator;

    /** 创建时间,格式 yyyy-MM-dd HH:mm:ss */
    private String gmtCreate;
}
