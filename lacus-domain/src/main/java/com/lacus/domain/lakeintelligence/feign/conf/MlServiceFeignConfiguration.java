package com.lacus.domain.lakeintelligence.feign.conf;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ML 服务 Feign 配置
 *
 * <p>设置超时时间和请求头。训练和向量构建属于长耗时操作，超时时间单独配置。</p>
 */
@Configuration
public class MlServiceFeignConfiguration implements RequestInterceptor {

    /**
     * 连接超时（毫秒）— 用于短请求（探测、查询进度等）
     */
    private static final int CONNECT_TIMEOUT_MS = 5000;

    /**
     * 读取超时（毫秒）— 用于短请求
     */
    private static final int READ_TIMEOUT_MS = 10000;

    @Override
    public void apply(RequestTemplate template) {
        template.header("Content-Type", "application/json");
        template.header("Accept", "application/json");
    }

    @Bean
    public feign.Request.Options requestOptions() {
        return new feign.Request.Options(CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS);
    }

    @Bean
    public Retryer feignRetryer() {
        // 不重试，由上层业务决定
        return Retryer.NEVER_RETRY;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
