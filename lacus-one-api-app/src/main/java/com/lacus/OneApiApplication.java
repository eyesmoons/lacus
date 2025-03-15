package com.lacus;

import cn.hutool.core.date.DateUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OneApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneApiApplication.class, args);
        System.out.println("Lacus统一API服务启动成功：" + DateUtil.now());
    }

}
