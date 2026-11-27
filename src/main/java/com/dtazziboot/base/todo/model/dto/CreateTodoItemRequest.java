package com.dtazziboot.base.todo.model.dto;

import lombok.Data;

/**
 * 新增待办事项请求。
 *
 * <p>创建人/租户/创建时间由服务端注入,接口不接收,防止越权伪造。</p>
 */
@Data
public class CreateTodoItemRequest {

    /** 事项名称,trim 后长度 1~64 字符,必填 */
    private String name;

    /** 事项描述,长度 <=500 字符,选填,缺省按空串处理 */
    private String description;
}
