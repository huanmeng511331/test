package com.dtazziboot.todo.dao.entity;

import java.time.LocalDateTime;

/**
 * 待办事项数据对象
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
public class TodoDO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 待办事项名称
     */
    private String name;

    /**
     * 待办事项描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    private LocalDateTime gmtModified;

    /**
     * 获取主键ID
     *
     * @return 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取待办事项名称
     *
     * @return 待办事项名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置待办事项名称
     *
     * @param name 待办事项名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取待办事项描述
     *
     * @return 待办事项描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置待办事项描述
     *
     * @param description 待办事项描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置创建时间
     *
     * @param gmtCreate 创建时间
     */
    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置更新时间
     *
     * @param gmtModified 更新时间
     */
    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "TodoDO{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description
                + '\'' + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + '}';
    }
}