package com.lacus.service.quartz.utils;

import com.alibaba.fastjson2.JSON;
import com.lacus.dao.quartz.entity.SysJob;
import org.quartz.*;

/**
 * 定时任务工具类
 *
 * @author lacus
 */
public class ScheduleUtils {

    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob) {
        boolean isConcurrent = "0".equals(sysJob.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey("TASK_" + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey("TASK_" + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException {
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        // 将 SysJob 对象序列化为 JSON 字符串，以满足 useProperties=true 的要求
        jobDetail.getJobDataMap().put("TASK_PROPERTIES", JSON.toJSONString(job));

        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停任务
        if ("1".equals(job.getStatus())) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb)
            throws SchedulerException {
        switch (job.getMisfirePolicy()) {
            case "1":
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case "2":
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
        }
    }
}
