package com.lacus.domain.dataquality.command;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * 数据质量调度新增/编辑命令
 */
@Data
public class DqScheduleCommand {

    /**
     * 任务ID（编辑用）
     */
    private Long jobId;

    @NotBlank(message = "调度名称不能为空")
    private String jobName;

    @NotNull(message = "绑定规则不能为空")
    private Long ruleId;

    @NotBlank(message = "cron表达式不能为空")
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行），默认放弃执行
     */
    private String misfirePolicy = "3";

    /**
     * 是否并发执行（0允许 1禁止），默认禁止并发
     */
    private String concurrent = "1";

    /**
     * 状态（NORMAL正常 PAUSE暂停），默认正常
     */
    private String status = "NORMAL";

    private String remark;
}
