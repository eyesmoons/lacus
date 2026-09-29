package com.lacus.dao.dataquality.entity;

import lombok.Data;

import java.util.List;

/**
 * 数据质量报告聚合视图对象（由 DqReportMapper 装配）
 */
@Data
public class DqReportVO {

    private OverviewVO overview;          // 概览
    private TrendVO trend;                // 趋势
    private DistributionVO distribution;  // 分布

    @Data
    public static class OverviewVO {
        private Integer total;        // 检测总量
        private Integer passed;       // 通过数
        private Integer failed;       // 失败数
        private Double passRate;      // 通过率，总量0时为null
        private Integer execSuccess;  // 执行成功数
        private Integer execFailed;   // 执行失败数
    }

    @Data
    public static class TrendVO {
        private String granularity; // "day" | "week"
        private List<TrendBucketVO> buckets;
    }

    @Data
    public static class TrendBucketVO {
        private String time;     // 时间桶
        private Integer count;   // 检测量
        private Double passRate; // 该桶通过率
    }

    @Data
    public static class DistributionVO {
        private List<RuleFailVO> byRule;       // 按规则 Top5
        private List<DimensionFailVO> byDimension; // 按维度
    }

    @Data
    public static class RuleFailVO {
        private String ruleName;
        private Integer fails;
    }

    @Data
    public static class DimensionFailVO {
        private String dimension;
        private Integer fails;
    }
}
