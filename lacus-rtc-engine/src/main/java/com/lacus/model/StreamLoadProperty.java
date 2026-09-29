package com.lacus.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @created by shengyu on 2023/9/6 10:05
 */
@Data
public class StreamLoadProperty implements Serializable {
    private static final long serialVersionUID = -1309741971661872010L;
    private String sinkTable;
    private String format = "json";
    private String maxFilterRatio = "1.0";
    private String stripOuterArray = "true";
    private String columns;
    private String jsonpaths;
}