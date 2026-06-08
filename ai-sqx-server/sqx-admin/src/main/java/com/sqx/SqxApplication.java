package com.sqx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.sqx")
public class SqxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqxApplication.class, args);
    }
}
