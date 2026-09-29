package com.lacus.domain.dataquality.vo;

import lombok.Data;

import java.util.Date;

/**
 * 数据质量调度视图对象
 */
@Data
public class DqScheduleVO {

    private Long jobId;

    private String jobName;

    private Long ruleId;

    private String ruleName;

    private String cronExpression;

    private String misfirePolicy;

    private String concurrent;

    private String status;

    private String remark;

    /**
     * 下次触发时间（由调用方根据 cron 计算或从调度器获取，此处预留）
     */
    private Date nextFireTime;

    private Date createTime;
}
