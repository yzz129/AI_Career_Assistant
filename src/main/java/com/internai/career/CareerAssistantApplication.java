package com.internai.career;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.internai.career.mapper")
public class CareerAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(CareerAssistantApplication.class, args);
    }
}
