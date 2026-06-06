package com.sqx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 登录服务端启动类
 */
@SpringBootApplication(scanBasePackages = "com.sqx")
public class SqxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqxApplication.class, args);
    }
}
