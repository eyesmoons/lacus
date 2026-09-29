package com.lacus.domain.scheduler.feign.conf;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerFeignConfiguration implements RequestInterceptor {

    @Value("${dolphinscheduler.token}")
    private String token;

    @Override
    public void apply(RequestTemplate template) {
        template.header("token", new String[]{this.token});
        template.header("Content-Type", "application/json");
    }

}
