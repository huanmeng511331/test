package com.dtazziboot.base.todo.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 待办事项数据对象,对应表 todo_item。
 */
@Data
@TableName("todo_item")
public class TodoItemDO {

    /** 系统自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 租户ID,默认单租户 */
    private Long tenantId;

    /** 事项名称,trim 后 1~64 字符 */
    private String name;

    /** 事项描述,选填,<=500 字符 */
    private String description;

    /** 创建人工号/账号,服务端从登录态注入 */
    private String creator;

    /** 最后修改人,创建时与 creator 相同 */
    private String modifier;

    /** 逻辑删除标记,0 未删除(预留字段,布尔属性不加 is 前缀) */
    @TableField("is_deleted")
    private Long deleted;

    /** 创建时间,应用层维护 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    /** 修改时间,应用层维护 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtModified;
}
