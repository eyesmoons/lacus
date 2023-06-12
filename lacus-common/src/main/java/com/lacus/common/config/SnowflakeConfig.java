package com.lacus.common.config;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeConfig {

    @Bean
    SnowflakeGenerator snowflakeGenerator() {
        return new SnowflakeGenerator(1, 0);
    }
}
