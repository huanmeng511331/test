package com.dtazziboot.todo.model.dto;

/**
 * 待办事项展示对象
 *
 * @author dtazziboot
 * @date 2026/10/27
 */
public class TodoVO {

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
     * 创建时间（yyyy-MM-dd HH:mm:ss）
     */
    private String gmtCreate;

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
     * 获取创建时间字符串
     *
     * @return 创建时间字符串
     */
    public String getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置创建时间字符串
     *
     * @param gmtCreate 创建时间字符串
     */
    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "TodoVO{" + "id=" + id + ", name='" + name + '\'' + ", description='" + description
                + '\'' + ", gmtCreate='" + gmtCreate + '\'' + '}';
    }
}