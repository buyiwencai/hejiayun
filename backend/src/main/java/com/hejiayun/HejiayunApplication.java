package com.hejiayun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hejiayun.mapper")
public class HejiayunApplication {
    public static void main(String[] args) {
        SpringApplication.run(HejiayunApplication.class, args);
    }
}
