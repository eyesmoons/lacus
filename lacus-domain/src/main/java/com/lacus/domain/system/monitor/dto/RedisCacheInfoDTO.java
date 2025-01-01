package com.lacus.domain.system.monitor.dto;

import java.util.List;
import java.util.Properties;
import lombok.Data;

@Data
public class RedisCacheInfoDTO {

    private Properties info;
    private Object dbSize;
    private List<CommonStatusDTO> commandStats;

    @Data
    public static class CommonStatusDTO {
        private String name;
        private String value;
    }

}
