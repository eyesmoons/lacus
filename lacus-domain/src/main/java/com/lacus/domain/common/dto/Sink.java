package com.lacus.domain.common.dto;

import lombok.Data;

import java.util.Map;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class Sink {
    private SinkDataSource sinkDataSource;
    private Map<String, StreamLoadProperty> streamLoadPropertyMap;
    private DynamicETL dynamicETL;
}
