package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 近期实例动态条目（四引擎合并）
 */
@Data
public class InstanceActivityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 来源展示名：Flink / Spark / 采集 / 集成 */
    private String source;

    /** 实例 ID（用于前端跳转详情） */
    private Long instanceId;

    /** 展示名称 */
    private String name;

    /** 原始状态值（各引擎词表） */
    private String rawStatus;

    /** 统一状态分类：SUCCESS / FAILED / RUNNING / WAITING / STOPPED */
    private String statusGroup;

    /** 时间（submitTime，dig 为 startTime） */
    private Date time;

    /** 前端详情路由 */
    private String path;

    /** YARN 跟踪地址，非空时优先跳转 */
    private String trackingUrl;
}
