package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 实例状态分布（近 N 天 + 今日两份）
 */
@Data
public class StatusDistributionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 近 N 天五分类分布 */
    private List<CategoryCount> recent;

    /** 今日五分类分布 */
    private List<CategoryCount> today;

    @Data
    public static class CategoryCount implements Serializable {

        private static final long serialVersionUID = 1L;

        /** 统一分类：SUCCESS / FAILED / RUNNING / WAITING / STOPPED */
        private String category;

        /** 实例数 */
        private Long count;
    }
}
