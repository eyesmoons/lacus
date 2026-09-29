package com.lacus.domain.monitor.alert;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lacus.alert")
public class AlertProperties {

    private Dispatch dispatch = new Dispatch();

    private Retry retry = new Retry();

    private Security security = new Security();

    @Data
    public static class Dispatch {
        private long fixedDelayMs = 3000L;
        private int batchSize = 100;
        private int threadPoolSize = 8;
    }

    @Data
    public static class Retry {
        private int maxTimes = 3;
        private int intervalSeconds = 60;
    }

    @Data
    public static class Security {
        private String encryptKey = "LacusAlertKey123";
    }
}
