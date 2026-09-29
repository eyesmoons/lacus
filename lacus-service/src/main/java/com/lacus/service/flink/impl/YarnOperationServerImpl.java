package com.lacus.service.flink.impl;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.flink.entity.FlinkJobEntity;
import com.lacus.dao.flink.entity.FlinkJobInstanceEntity;
import com.lacus.enums.FlinkStatusEnum;
import com.lacus.service.flink.IFlinkJobInstanceService;
import com.lacus.service.flink.IFlinkJobService;
import com.lacus.service.flink.IFlinkOperationService;
import com.lacus.service.flink.dto.YarnJobInfoDTO;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.utils.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.lacus.common.constant.Constants.FLINK_DEFAULT_SAVEPOINT_PATH;
import static com.lacus.common.constant.Constants.YARN_FLINK_OPERATION_SERVER;

/**
 * @author shengyu
 * @date 2024/10/26 20:45
 */
@Slf4j
@Service(YARN_FLINK_OPERATION_SERVER)
public class YarnOperationServerImpl extends FlinkJobBaseService implements IFlinkOperationService {

    private static final Integer RETRY_COUNT = 3;

    @Autowired
    private IFlinkJobService flinkJobService;

    @Autowired
    private IFlinkJobInstanceService flinkJobInstanceService;

    @Autowired
    private YarnRestService yarnRestService;

    @Override
    public void start(Long jobId, Boolean resume) {
        FlinkJobEntity flinkJobEntity = flinkJobService.getById(jobId);

        // 检查启动任务参数
        checkStartParams(flinkJobEntity);

        if (StringUtils.isNotEmpty(flinkJobEntity.getAppId())) {
            this.stop(flinkJobEntity);
        }

        // 保存任务实例
        FlinkJobInstanceEntity instance = new FlinkJobInstanceEntity();
        instance.setJobId(jobId);
        instance.setDeployMode(flinkJobEntity.getDeployMode());
        instance.setInstanceName(flinkJobEntity.getJobName() + "_" + System.currentTimeMillis());
        instance.setStatus(FlinkStatusEnum.RUNNING);
        instance.setSubmitTime(DateUtils.getNowDate());
        flinkJobInstanceService.save(instance);

        // 变更任务状态为：启动中
        flinkJobService.updateStatus(jobId, FlinkStatusEnum.RUNNING);

        String savepointPath = null;
        if (resume) {
            savepointPath = flinkJobEntity.getSavepoint();
        }

        // 异步提交任务
        submitJobAsync(generateSqlFile(flinkJobEntity), flinkJobEntity, instance, savepointPath);
    }

    @Override
    public void stop(Long jobId, Boolean isSavePoint) {
        log.info("开始停止任务[{}]", jobId);
        FlinkJobEntity flinkJobEntity = flinkJobService.getById(jobId);
        if (Objects.isNull(flinkJobEntity)) {
            throw new CustomException("任务不存在: " + jobId);
        }
        // 停止前保存savepoint
        try {
            this.savepoint(jobId);
        } catch (Exception e) {
            log.error("执行savePoint失败", e);
        }
        // 停止任务
        this.stop(flinkJobEntity);
        // 变更状态
        flinkJobService.updateStatus(jobId, FlinkStatusEnum.STOP);
        flinkJobInstanceService.updateStatusByJobId(jobId, FlinkStatusEnum.STOP, DateUtils.getNowDate());
    }

    public void savepoint(Long jobId) {
        FlinkJobEntity flinkJobEntity = flinkJobService.getById(jobId);
        checkSavepoint(flinkJobEntity);

        YarnJobInfoDTO jobInfo = yarnRestService.getJobInfoForPerYarnByAppId(flinkJobEntity.getAppId());
        if (Objects.isNull(jobInfo)) {
            throw new CustomException("yarn中未找到任务");
        }
        // 执行savepoint
        try {
            savepointForYarn(jobInfo.getId(), CommonPropertyUtils.getString(FLINK_DEFAULT_SAVEPOINT_PATH) + jobId, flinkJobEntity.getAppId());
        } catch (Exception e) {
            throw new CustomException("执行savePoint失败");
        }

        String savepointPath = yarnRestService.getSavepointPath(flinkJobEntity.getAppId(), jobInfo.getId());
        if (StringUtils.isEmpty(savepointPath)) {
            throw new CustomException("savepointPath路径不存在");
        }
        // 保存Savepoint
        flinkJobEntity.setSavepoint(savepointPath);
        flinkJobService.saveOrUpdate(flinkJobEntity);
    }

    private void stop(FlinkJobEntity flinkJobEntity) {
        int retryNum = 1;
        while (retryNum <= RETRY_COUNT) {
            YarnJobInfoDTO jobInfo = yarnRestService.getJobInfoForPerYarnByAppId(flinkJobEntity.getAppId());
            log.info("任务[{}]状态为：{}", flinkJobEntity.getJobId(), jobInfo);
            if (jobInfo != null && FlinkStatusEnum.RUNNING.name().equals(jobInfo.getStatus())) {
                log.info("执行停止操作，jobId: {}", flinkJobEntity.getJobId());
                yarnRestService.cancelJobForYarnByAppId(flinkJobEntity.getAppId(), jobInfo.getId());
            } else {
                log.info("任务已经停止, jobId: {}", flinkJobEntity.getJobId());
                break;
            }
            retryNum++;
        }
    }
}
