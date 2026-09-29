package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 资源总览计数（KPI 卡片主指标 + 副指标）
 */
@Data
public class ResourceOverviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 数据源总数 */
    private Long datasourceTotal;

    /** 启用中的数据源数（副指标） */
    private Long datasourceEnabled;

    /** 实时采集任务总数 */
    private Long datasyncJobTotal;

    /** Flink 任务总数 */
    private Long flinkJobTotal;

    /** Spark 任务总数 */
    private Long sparkJobTotal;

    /** 数据集成任务总数 */
    private Long digJobTotal;

    /** 统一 API 总数 */
    private Long apiTotal;

    /** 已发布的 API 数（副指标） */
    private Long apiPublished;

    /** 质量规则总数 */
    private Long qualityRuleTotal;

    /** 当前运行中的 Flink 实例数（副指标） */
    private Long flinkRunning;

    /** 当前运行中的 Spark 实例数（副指标） */
    private Long sparkRunning;

    /** 当前运行中的采集实例数（副指标） */
    private Long datasyncRunning;

    /** 当前运行中的集成实例数（副指标） */
    private Long digRunning;
}
