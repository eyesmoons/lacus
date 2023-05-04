package com.lacus.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.lacus.*")
public class LacusApplication {

    public static void main(String[] args) {
        SpringApplication.run(LacusApplication.class, args);
        System.out.println("启动成功");
    }
}
