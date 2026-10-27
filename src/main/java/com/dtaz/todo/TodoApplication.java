package com.dtaz.todo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 待办事项服务启动类
 *
 * @author AiWork
 * @date 2026/10/27
 */
@SpringBootApplication
@MapperScan("com.dtaz.todo.dao.mapper")
public class TodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}