package com.lacus.service.spark.impl;

import com.lacus.common.exception.CustomException;
import com.lacus.service.spark.ISparkSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SparkSchedulerServiceImpl implements ISparkSchedulerService {

    private static final String JOB_GROUP = "SPARK_JOB_GROUP";
    private static final String TRIGGER_GROUP = "SPARK_TRIGGER_GROUP";

    @Autowired
    private Scheduler scheduler;

    @Override
    public void addJob(Long jobId, String cronExpression) {
        try {
            // 创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(SparkQuartzJobService.class)
                .withIdentity(getJobKey(jobId))
                .usingJobData("jobId", jobId)
                .build();

            // 创建Trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(jobId))
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

            // 调度任务
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("添加Spark定时任务成功, jobId: {}, cron: {}", jobId, cronExpression);
        } catch (SchedulerException e) {
            log.error("添加Spark定时任务失败", e);
            throw new CustomException("添加定时任务失败: " + e.getMessage());
        }
    }

    @Override
    public void removeJob(Long jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
            log.info("移除Spark定时任务成功, jobId: {}", jobId);
        } catch (SchedulerException e) {
            log.error("移除Spark定时任务失败", e);
            throw new CustomException("移除定时任务失败: " + e.getMessage());
        }
    }

    @Override
    public void updateJob(Long jobId, String cronExpression) {
        try {
            TriggerKey triggerKey = getTriggerKey(jobId);

            // 创建新的Trigger
            CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

            // 更新触发器
            scheduler.rescheduleJob(triggerKey, trigger);

            log.info("更新Spark定时任务成功, jobId: {}, cron: {}", jobId, cronExpression);
        } catch (SchedulerException e) {
            log.error("更新Spark定时任务失败", e);
            throw new CustomException("更新定时任务失败: " + e.getMessage());
        }
    }

    private JobKey getJobKey(Long jobId) {
        return JobKey.jobKey("spark_job_" + jobId, JOB_GROUP);
    }

    private TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey("spark_trigger_" + jobId, TRIGGER_GROUP);
    }
}
