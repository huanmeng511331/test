package com.dtazziboot.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 底座应用启动入口:承载待办模块(todo)等业务能力。
 */
@SpringBootApplication
public class TodoBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoBaseApplication.class, args);
    }
}
