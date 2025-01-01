package com.lacus.domain.spark.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.domain.spark.job.command.AddSparkJarJobCommand;
import com.lacus.domain.spark.job.command.AddSparkSqlJobCommand;
import com.lacus.domain.spark.job.command.UpdateSparkJarJobCommand;
import com.lacus.domain.spark.job.command.UpdateSparkSqlJobCommand;
import com.lacus.domain.spark.job.model.SparkJobModel;
import com.lacus.domain.spark.job.model.SparkJobModelFactory;
import com.lacus.domain.spark.job.query.JobPageQuery;
import com.lacus.enums.SparkStatusEnum;
import com.lacus.service.spark.ISparkJobService;
import com.lacus.service.spark.ISparkOperationService;
import com.lacus.service.spark.ISparkSchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * @author shengyu
 * @date 2024/12/31 17:54
 */
@Slf4j
@Service
public class SparkJobBusiness {

    @Autowired
    private ISparkJobService sparkJobService;

    @Autowired
    private ISparkOperationService operationService;

    @Autowired
    private ISparkSchedulerService schedulerService;

    @SuppressWarnings({"unchecked"})
    public PageDTO pageList(@Valid JobPageQuery query) {
        Page<?> page = sparkJobService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public SparkJobModel addSparkSqlJob(AddSparkSqlJobCommand addCommand) {
        SparkJobModel model = SparkJobModelFactory.loadFromSqlAddCommand(addCommand, new SparkJobModel());
        model.checkJobNameUnique(sparkJobService);
        model.setJobStatus(SparkStatusEnum.CREATED);
        model.insert();
        return model;
    }

    public SparkJobModel addSparkJarJob(AddSparkJarJobCommand addCommand) {
        SparkJobModel model = SparkJobModelFactory.loadFromJarAddCommand(addCommand, new SparkJobModel());
        model.checkJobNameUnique(sparkJobService);
        model.setJobStatus(SparkStatusEnum.CREATED);
        model.insert();
        return model;
    }

    public void updateSparkSqlJob(@Valid UpdateSparkSqlJobCommand updateCommand) {
        SparkJobModel model = SparkJobModelFactory.loadFromSqlUpdateCommand(updateCommand, new SparkJobModel());
        model.checkJobNameUnique(sparkJobService);
        model.updateById();
    }

    public void updateSparkJarJob(@Valid UpdateSparkJarJobCommand updateCommand) {
        SparkJobModel model = SparkJobModelFactory.loadFromJarUpdateCommand(updateCommand, new SparkJobModel());
        model.checkJobNameUnique(sparkJobService);
        model.updateById();
    }

    public void deleteSparkJob(Long jobId) {
        sparkJobService.removeById(jobId);
    }

    public SparkJobEntity detail(Long jobId) {
        return sparkJobService.getById(jobId);
    }

    public void start(Long jobId) {
        operationService.start(jobId);
    }

    public void stop(Long jobId) {
        operationService.stop(jobId);
    }

    public void online(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }

        // 检查cron表达式
        if (StringUtils.isEmpty(sparkJobEntity.getCronExpression())) {
            throw new CustomException("定时任务的cron表达式不能为空");
        }

        // 检查任务状态
        if (sparkJobEntity.getScheduleStatus()) {
            throw new CustomException("任务已经是上线状态");
        }

        // 添加到定时调度系统
        schedulerService.addJob(jobId, sparkJobEntity.getCronExpression());

        // 更新调度状态为上线
        sparkJobEntity.setScheduleStatus(true);
        sparkJobService.updateById(sparkJobEntity);

        log.info("任务[{}]上线成功", jobId);
    }

    public void offline(Long jobId) {
        SparkJobEntity sparkJobEntity = sparkJobService.getById(jobId);
        if (sparkJobEntity == null) {
            throw new CustomException("任务不存在");
        }

        // 检查任务状态
        if (!sparkJobEntity.getScheduleStatus()) {
            throw new CustomException("任务已经是下线状态");
        }

        // 从定时调度系统中移除
        schedulerService.removeJob(jobId);

        // 更新调度状态为下线
        sparkJobEntity.setScheduleStatus(false);
        sparkJobService.updateById(sparkJobEntity);

        log.info("任务[{}]下线成功", jobId);
    }
}
