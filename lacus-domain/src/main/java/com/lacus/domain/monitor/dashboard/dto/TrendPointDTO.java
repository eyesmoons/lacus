package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 单日任务趋势点
 */
@Data
public class TrendPointDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计日期 yyyy-MM-dd */
    private String date;

    /** 当日提交实例总数（四引擎求和） */
    private Long submitted;

    /** 当日终态=成功的实例数 */
    private Long success;

    /** 当日终态=失败的实例数 */
    private Long failed;

    /** 其余状态实例数（等待中/停止等） */
    private Long others;

    /** 成功率 success/(success+failed)，无终态实例时为 null，保留 1 位小数 */
    private Double successRate;
}
