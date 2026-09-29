package com.lacus.dao.dashboard.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 趋势聚合查询行：某引擎某天某原始状态的实例数
 */
@Data
public class InstanceTrendRow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计日期 yyyy-MM-dd（DATE(时间列)） */
    private String statDate;

    /** 引擎标识：FLINK / SPARK / DATASYNC / DIG */
    private String engine;

    /** 原始状态值（各引擎词表不同，由 Java 侧归一化） */
    private String rawStatus;

    /** 实例数 */
    private Long cnt;
}
