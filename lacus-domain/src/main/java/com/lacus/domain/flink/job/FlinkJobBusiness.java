package com.lacus.domain.flink.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.flink.entity.FlinkJobEntity;
import com.lacus.domain.flink.job.command.AddFlinkJarJobCommand;
import com.lacus.domain.flink.job.command.AddFlinkSqlJobCommand;
import com.lacus.domain.flink.job.command.UpdateFlinkJarJobCommand;
import com.lacus.domain.flink.job.command.UpdateFlinkSqlJobCommand;
import com.lacus.domain.flink.job.factory.FlinkOperationServerManager;
import com.lacus.domain.flink.job.model.FlinkJobModel;
import com.lacus.domain.flink.job.model.FlinkJobModelFactory;
import com.lacus.domain.flink.job.query.JobPageQuery;
import com.lacus.service.flink.IFlinkJobService;
import com.lacus.service.flink.IFlinkOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author shengyu
 * @date 2024/10/26 17:32
 */
@Slf4j
@Service
public class FlinkJobBusiness {

    @Autowired
    private IFlinkJobService flinkJobService;

    @SuppressWarnings({"unchecked"})
    public PageDTO pageList(@Valid JobPageQuery query) {
        Page<?> page = flinkJobService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public FlinkJobModel addFlinkSqlJob(AddFlinkSqlJobCommand addCommand) {
        FlinkJobModel model = FlinkJobModelFactory.loadFromSqlAddCommand(addCommand, new FlinkJobModel());
        model.checkJobNameUnique(flinkJobService);
        String flinkRunConfig = getFlinkRunConfig(addCommand.getJobName(), addCommand.getQueue(), addCommand.getSlot(), addCommand.getParallelism(), addCommand.getJobManager(), addCommand.getTaskManager());
        model.setFlinkRunConfig(flinkRunConfig);
        model.insert();
        return model;
    }

    public FlinkJobModel addFlinkJarJob(AddFlinkJarJobCommand addCommand) {
        FlinkJobModel model = FlinkJobModelFactory.loadFromJarAddCommand(addCommand, new FlinkJobModel());
        model.checkJobNameUnique(flinkJobService);
        String flinkRunConfig = getFlinkRunConfig(addCommand.getJobName(), addCommand.getQueue(), addCommand.getSlot(), addCommand.getParallelism(), addCommand.getJobManager(), addCommand.getTaskManager());
        model.setFlinkRunConfig(flinkRunConfig);
        model.insert();
        return model;
    }

    private String getFlinkRunConfig(String jobName, String queue, int slot, int parallelism, int jobManager, int taskManager) {
        return " -ys " + slot +
                " -yjm " + jobManager * 1024 +
                " -ytm " + taskManager * 1024 +
                " -yqu " + queue +
                " -ynm " + jobName +
                " -p " + parallelism;
    }

    public void updateFlinkSqlJob(@Valid UpdateFlinkSqlJobCommand updateCommand) {
        FlinkJobModel model = FlinkJobModelFactory.loadFromSqlUpdateCommand(updateCommand, new FlinkJobModel());
        model.checkJobNameUnique(flinkJobService);
        String flinkRunConfig = getFlinkRunConfig(updateCommand.getJobName(), updateCommand.getQueue(), updateCommand.getSlot(), updateCommand.getParallelism(), updateCommand.getJobManager(), updateCommand.getTaskManager());
        model.setFlinkRunConfig(flinkRunConfig);
        model.updateById();
    }

    public void updateFlinkJarJob(@Valid UpdateFlinkJarJobCommand updateCommand) {
        FlinkJobModel model = FlinkJobModelFactory.loadFromJarUpdateCommand(updateCommand, new FlinkJobModel());
        model.checkJobNameUnique(flinkJobService);
        String flinkRunConfig = getFlinkRunConfig(updateCommand.getJobName(), updateCommand.getQueue(), updateCommand.getSlot(), updateCommand.getParallelism(), updateCommand.getJobManager(), updateCommand.getTaskManager());
        model.setFlinkRunConfig(flinkRunConfig);
        model.updateById();
    }

    public void deleteFlinkJob(Long jobId) {
        flinkJobService.removeById(jobId);
    }

    public FlinkJobEntity detail(Long jobId) {
        return flinkJobService.getById(jobId);
    }

    public void start(Long jobId, Boolean resume) {
        try {
            IFlinkOperationService flinkOperationServer = getFlinkOperationServer(jobId);
            flinkOperationServer.start(jobId, resume);
        } catch (Exception e) {
            throw new CustomException(String.format("flink任务[%s]启动失败：%s", jobId, e.getMessage()));
        }
    }

    /**
     * 根据部署模式获取Flink操作类
     *
     * @param jobId 任务id
     */
    private IFlinkOperationService getFlinkOperationServer(Long jobId) {
        FlinkJobEntity byId = flinkJobService.getById(jobId);
        if (Objects.isNull(byId)) {
            throw new CustomException(String.format("任务[%s]不存在", jobId));
        }
        return FlinkOperationServerManager.getFlinkOperationServer(byId.getDeployMode());
    }

    public void stop(Long jobId, Boolean isSavePoint) {
        try {
            IFlinkOperationService flinkOperationServer = getFlinkOperationServer(jobId);
            flinkOperationServer.stop(jobId, isSavePoint);
        } catch (Exception e) {
            throw new CustomException(String.format("flink任务[%s]启动失败：%s", jobId, e.getMessage()));
        }
    }
}
