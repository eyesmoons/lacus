package com.lacus.service.flink.impl;

import com.lacus.common.exception.CustomException;
import com.lacus.dao.flink.entity.FlinkJobEntity;
import com.lacus.dao.flink.entity.FlinkJobInstanceEntity;
import com.lacus.enums.FlinkDeployModeEnum;
import com.lacus.enums.FlinkJobTypeEnum;
import com.lacus.enums.FlinkStatusEnum;
import com.lacus.service.flink.IFlinkJobInstanceService;
import com.lacus.service.flink.IFlinkJobService;
import com.lacus.service.flink.IFlinkOperationService;
import com.lacus.service.flink.dto.StandaloneFlinkJobInfo;
import com.lacus.utils.PropertyUtils;
import com.lacus.utils.time.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.lacus.common.constant.Constants.FLINK_DEFAULT_SAVEPOINT_PATH;
import static com.lacus.common.constant.Constants.STANDALONE_FLINK_OPERATION_SERVER;

/**
 * @author shengyu
 * @date 2024/10/26 20:44
 */
@Slf4j
@Service(STANDALONE_FLINK_OPERATION_SERVER)
public class StandaloneOperationServerImpl extends FlinkJobBaseService implements IFlinkOperationService {

    @Autowired
    private IFlinkJobService flinkJobService;

    @Autowired
    private StandaloneRestService flinkRpcService;

    @Autowired
    private IFlinkJobInstanceService flinkJobInstanceService;

    @Override
    public void start(Long jobId, Boolean resume) {
        FlinkJobEntity byId = flinkJobService.getById(jobId);
        if (ObjectUtils.isNotEmpty(byId.getAppId())) {
            if (!Objects.equals(byId.getJobType(), FlinkJobTypeEnum.BATCH_SQL)) {
                StandaloneFlinkJobInfo standaloneFlinkJobInfo = flinkRpcService.getJobInfoByAppId(byId.getAppId(), byId.getDeployMode());
                if (StringUtils.isNotBlank(standaloneFlinkJobInfo.getState()) && FlinkStatusEnum.RUNNING.name().equalsIgnoreCase(standaloneFlinkJobInfo.getState())) {
                    throw new CustomException("Flink任务[" + byId.getAppId() + "]处于[ " + standaloneFlinkJobInfo.getState() + "]状态，不能重复启动任务！");
                }
            }
        }

        // 检查启动任务参数
        checkStartParams(byId);

        // 保存任务实例
        FlinkJobInstanceEntity instance = new FlinkJobInstanceEntity();
        instance.setJobId(jobId);
        instance.setDeployMode(byId.getDeployMode());
        instance.setInstanceName(byId.getJobName() + "_" + System.currentTimeMillis());
        instance.setStatus(FlinkStatusEnum.RUNNING);
        instance.setSubmitTime(DateUtils.getNowDate());
        flinkJobInstanceService.save(instance);

        // 变更任务状态：启动中
        flinkJobService.updateStatus(jobId, FlinkStatusEnum.RUNNING);

        String savepointPath = null;
        if (resume) {
            savepointPath = byId.getSavepoint();
        }

        // 异步提交任务
        submitJobAsync(generateSqlFile(byId), byId, instance, savepointPath);
    }

    @Override
    public void stop(Long jobId, Boolean isSavePoint) {
        log.info("开始停止任务: {}", jobId);
        FlinkJobEntity flinkJobEntity = flinkJobService.getById(jobId);
        if (flinkJobEntity == null) {
            throw new CustomException("任务不存在");
        }
        StandaloneFlinkJobInfo jobStandaloneInfo = flinkRpcService.getJobInfoByAppId(flinkJobEntity.getAppId(), flinkJobEntity.getDeployMode());
        log.info("任务[{}]状态为：{}", jobId, jobStandaloneInfo);
        if (jobStandaloneInfo == null || StringUtils.isNotEmpty(jobStandaloneInfo.getErrors())) {
            log.warn("开始停止任务: {}，jobStandaloneInfo: {}", jobId, jobStandaloneInfo);
        } else {
            if (isSavePoint) {
                // 停止前先保存savepoint
                if (StringUtils.isNotBlank(flinkJobEntity.getSavepoint()) && flinkJobEntity.getJobType() != FlinkJobTypeEnum.BATCH_SQL && FlinkStatusEnum.RUNNING.name().equals(jobStandaloneInfo.getState())) {
                    log.info("开始保存savepoint: {}", jobId);
                    this.savepoint(jobId);
                }
            }
            // 停止任务
            if (FlinkStatusEnum.RUNNING.name().equals(jobStandaloneInfo.getState()) || FlinkStatusEnum.RESTARTING.name().equals(jobStandaloneInfo.getState())) {
                flinkRpcService.cancelJobByAppId(flinkJobEntity.getAppId(), flinkJobEntity.getDeployMode());
            }
        }
        // 变更任务状态
        flinkJobService.updateStatus(jobId, FlinkStatusEnum.STOP);
        flinkJobInstanceService.updateStatusByJobId(jobId, FlinkStatusEnum.STOP, DateUtils.getNowDate());
    }

    public void savepoint(Long jobId) {
        FlinkJobEntity flinkJobEntity = flinkJobService.getById(jobId);
        checkSavepoint(flinkJobEntity);

        StandaloneFlinkJobInfo jobStandaloneInfo = flinkRpcService.getJobInfoByAppId(flinkJobEntity.getAppId(), flinkJobEntity.getDeployMode());
        if (jobStandaloneInfo == null || StringUtils.isNotEmpty(jobStandaloneInfo.getErrors())
                || !FlinkStatusEnum.RUNNING.name().equals(jobStandaloneInfo.getState())) {
            throw new CustomException("未找到任务信息");
        }

        // 执行savepoint
        try {
            String targetDirectory = PropertyUtils.getString(FLINK_DEFAULT_SAVEPOINT_PATH) + jobId;
            if (FlinkDeployModeEnum.LOCAL.equals(flinkJobEntity.getDeployMode())) {
                targetDirectory = "savepoint/" + jobId;
            }
            savepointForStandalone(flinkJobEntity.getAppId(), targetDirectory);
        } catch (Exception e) {
            throw new CustomException("执行savePoint失败");
        }
        String savepointPath = flinkRpcService.getSavepointPath(flinkJobEntity.getAppId(), flinkJobEntity.getDeployMode());
        if (StringUtils.isEmpty(savepointPath)) {
            throw new CustomException("savepointPath目录不存在");
        }
        // 保存Savepoint
        flinkJobEntity.setSavepoint(savepointPath);
        flinkJobService.saveOrUpdate(flinkJobEntity);
    }
}
