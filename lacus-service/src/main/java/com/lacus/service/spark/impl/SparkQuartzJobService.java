package com.lacus.service.spark.impl;

import com.lacus.service.spark.ISparkOperationService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SparkQuartzJobService extends QuartzJobBean {

    @Autowired
    private ISparkOperationService sparkOperationService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        Long jobId = jobDataMap.getLong("jobId");

        try {
            log.info("开始执行定时Spark任务, jobId: {}", jobId);
            sparkOperationService.start(jobId);
        } catch (Exception e) {
            log.error("执行定时Spark任务失败, jobId: {}, error: {}", jobId, e.getMessage());
            throw new JobExecutionException(e);
        }
    }
}
