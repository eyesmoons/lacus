package com.lacus.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @created by shengyu on 2023/9/6 10:01
 */
@Data
public class Sink implements Serializable {
    private static final long serialVersionUID = 6624054873451223148L;
    private SinkDataSource sinkDataSource;
    private Map<String, StreamLoadProperty> streamLoadPropertyMap;
    private DynamicETL dynamicETL;
}
