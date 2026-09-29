package com.lacus.alert.plugin.spi;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotifyContext {

    private String recordNo;

    private String taskNo;

    private String title;

    private String content;

    private String alertLevel;

    private Map<String, Object> config;
}
