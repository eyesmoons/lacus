package com.lacus.dao.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lacus.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务调度日志表 sys_job_log
 *
 * @author lacus
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_job_log")
public class SysJobLog extends BaseEntity<SysJobLog> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务日志ID
     */
    @TableId(value = "job_log_id", type = IdType.AUTO)
    private Long jobLogId;

    /**
     * 任务名称
     */
    @TableField("job_name")
    private String jobName;

    /**
     * 任务组名
     */
    @TableField("job_group")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @TableField("invoke_target")
    private String invokeTarget;

    /**
     * 日志信息
     */
    @TableField("job_message")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @TableField("status")
    private String status;

    /**
     * 异常信息
     */
    @TableField("exception_info")
    private String exceptionInfo;

    /**
     * 开始时间
     */
    @TableField(exist = false)
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(exist = false)
    private Date endTime;
}
