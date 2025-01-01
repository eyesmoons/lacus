package com.lacus.service.spark;

public interface ISparkSchedulerService {

    /**
     * 添加定时任务
     */
    void addJob(Long jobId, String cronExpression);

    /**
     * 移除定时任务
     */
    void removeJob(Long jobId);

    /**
     * 更新定时任务
     */
    void updateJob(Long jobId, String cronExpression);
} 