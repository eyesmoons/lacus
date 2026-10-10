package com.lacus.domain.scheduler.feign.conf;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * DolphinScheduler Feign 配置。
 *
 * <p>刻意不加 @Configuration：该类通过 SchedulerFeign 的 configuration 属性按客户端注册。
 * 若标注 @Configuration，会被组件扫描成全局 RequestInterceptor，泄漏到其它 Feign 客户端
 * （曾导致 ML 客户端的 multipart 请求被强制改成 application/json）。</p>
 */
public class SchedulerFeignConfiguration implements RequestInterceptor {

    @Value("${dolphinscheduler.token}")
    private String token;

    @Override
    public void apply(RequestTemplate template) {
        template.header("token", new String[]{this.token});
        template.header("Content-Type", "application/json");
    }

}
