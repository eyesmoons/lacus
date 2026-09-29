package com.lacus.domain.metadata.datasource.command;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 创建数据源定时同步任务命令
 */
@Data
public class CreateSyncJobCommand {

    /**
     * cron执行表达式
     */
    @NotBlank(message = "cron表达式不能为空")
    private String cronExpression;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    private String concurrent = "1";

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    private String misfirePolicy = "2";

    /**
     * 状态（0正常 1暂停）
     */
    private String status = "0";

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
}
