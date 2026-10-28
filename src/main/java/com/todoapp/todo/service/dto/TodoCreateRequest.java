package com.todoapp.todo.service.dto;

/**
 * 创建待办请求（Service 层 DTO）
 *
 * <p>由 Controller 从 API 请求与登录上下文构建，传入 Service 层。
 * creator 与 tenantId 来源于登录上下文，禁止前端直接传入。
 *
 * @author AiWork
 * @date 2026/10/28
 */
public class TodoCreateRequest {

    /** 事项名称 */
    private String title;

    /** 事项描述，允许为空 */
    private String description;

    /** 创建人（登录用户标识） */
    private String creator;

    /** 租户标识 */
    private String tenantId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "TodoCreateRequest{title='" + title + "', description='" + description
                + "', creator='" + creator + "', tenantId='" + tenantId + "'}";
    }
}
