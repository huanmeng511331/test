# 待办事项应用架构文档

> 文档版本: v1.0 | 创建日期: 2026-10-28 | 作者: AiWork

## 1. 架构概述

待办事项应用是一个单体 Java 后端服务，帮助内部用户记录日常待办事项。采用 MVC 分层架构，技术栈为 JDK 21 + Spring Boot 3.2 + MyBatis + MySQL。

### 技术栈

| 组件 | 选型 | 版本 |
|------|------|------|
| JDK | OpenJDK | 21 (LTS) |
| 框架 | Spring Boot | 3.2.5 |
| ORM | MyBatis Spring Boot Starter | 3.0.3 |
| 数据库 | MySQL | 8.x |
| 构建 | Maven | 3.9.x |

## 2. 分层架构

```
com.todoapp
├── TodoApplication              # 应用启动类
├── common                       # 公共层
│   ├── config                   # 配置（WebMvcConfig）
│   ├── constant                 # 常量（CommonConstants）
│   ├── context                  # 登录上下文（LoginContext / LoginContextHolder）
│   ├── exception                # 异常（BusinessException）
│   ├── handler                  # 全局异常处理器（GlobalExceptionHandler）
│   ├── interceptor              # 拦截器（LoginInterceptor）
│   └── model                    # 通用模型（ApiResponse）
└── todo                         # 待办事项模块
    ├── api
    │   ├── controller           # TodoController
    │   ├── request              # TodoCreateApiRequest
    │   └── response             # TodoCreateData
    ├── constant                 # TodoErrorCode
    ├── dao
    │   ├── entity               # TodoDO
    │   └── mapper               # TodoMapper
    └── service
        ├── dto                  # TodoCreateRequest / TodoCreateResult
        ├── impl                 # TodoServiceImpl
        └── TodoService          # 接口
```

### 分层调用关系

```
Web层（Controller） → Service层 → DAO层（Mapper） → MySQL
```

## 3. 模块清单

| 模块 | 职责 | 依赖 |
|------|------|------|
| 待办事项模块（todo） | 接收待办创建请求，参数校验，持久化待办记录 | MySQL |

## 4. 约束与规范

- **包结构**：按功能模块划分，接口与实现分离（impl 包）
- **命名**：大驼峰类名、小驼峰方法名、常量全大写下划线分隔
- **异常**：BusinessException 统一携带错误码，GlobalExceptionHandler 统一捕获
- **日志**：SLF4J + 占位符，creator 字段日志脱敏
- **安全**：全局登录拦截器校验登录态，SQL 参数化 #{}，输入 @Valid 校验
- **数据库**：表名小写下划线、必备 id/gmt_create/gmt_modified、datetime 类型、resultMap 映射

## 5. 变更三板斧

- **可监控**：createTodo 接口级日志（INFO 成功 / ERROR 失败），DB 写入失败记录日志
- **可灰度**：功能开关 `feature.todo.create.enabled`
- **可应急**：关闭功能开关快速止血，纯新增表无回滚依赖
