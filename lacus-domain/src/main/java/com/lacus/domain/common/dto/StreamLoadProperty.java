package com.lacus.domain.common.dto;

import lombok.Data;

/**
 * @created by shengyu on 2023/9/6 10:05
 */
@Data
public class StreamLoadProperty {
    private String sinkTable;
    private String format = "json";
    private String maxFilterRatio = "1.0";
    private String stripOuterArray = "true";
    private String columns;
    private String jsonpaths;
}