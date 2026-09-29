package com.lacus.domain.monitor.alert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lacus.alert.plugin.manager.AlertNotifierManager;
import com.lacus.alert.plugin.spi.AlertNotifier;
import com.lacus.alert.plugin.spi.NotifyContext;
import com.lacus.alert.plugin.spi.NotifyResult;
import com.lacus.dao.alert.entity.AlertChannelInstanceEntity;
import com.lacus.dao.alert.entity.AlertRecordEntity;
import com.lacus.dao.alert.entity.AlertSendLogEntity;
import com.lacus.dao.alert.entity.AlertSendTaskEntity;
import com.lacus.dao.alert.mapper.AlertChannelInstanceMapper;
import com.lacus.dao.alert.mapper.AlertRecordMapper;
import com.lacus.dao.alert.mapper.AlertSendLogMapper;
import com.lacus.dao.alert.mapper.AlertSendTaskMapper;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AlertDispatchBusiness {

    @Autowired
    private AlertSendTaskMapper alertSendTaskMapper;
    @Autowired
    private AlertSendLogMapper alertSendLogMapper;
    @Autowired
    private AlertRecordMapper alertRecordMapper;
    @Autowired
    private AlertChannelInstanceMapper alertChannelInstanceMapper;
    @Autowired
    private AlertNotifierManager alertNotifierManager;
    @Autowired
    private AlertConfigCipher alertConfigCipher;
    @Autowired
    private AlertProperties alertProperties;

    @Transactional(rollbackFor = Exception.class)
    public List<Long> claimReadyTaskIds(int limit) {
        List<AlertSendTaskEntity> tasks = alertSendTaskMapper.selectReadyTasksForLock(limit);
        if (CollUtil.isEmpty(tasks)) {
            return Collections.emptyList();
        }
        Date now = new Date();
        List<Long> ids = tasks.stream().map(AlertSendTaskEntity::getId).collect(Collectors.toList());
        LambdaUpdateWrapper<AlertSendTaskEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(AlertSendTaskEntity::getId, ids)
            .set(AlertSendTaskEntity::getStatus, AlertCenterConstants.TaskStatus.SENDING)
            .set(AlertSendTaskEntity::getStartedTime, now);
        alertSendTaskMapper.update(null, updateWrapper);

        List<Long> recordIds = tasks.stream().map(AlertSendTaskEntity::getRecordId).distinct().collect(Collectors.toList());
        if (CollUtil.isNotEmpty(recordIds)) {
            LambdaUpdateWrapper<AlertRecordEntity> recordUpdate = new LambdaUpdateWrapper<>();
            recordUpdate.in(AlertRecordEntity::getId, recordIds)
                .set(AlertRecordEntity::getStatus, AlertCenterConstants.RecordStatus.SENDING);
            alertRecordMapper.update(null, recordUpdate);
        }
        return ids;
    }

    @Transactional(rollbackFor = Exception.class)
    public void dispatchTask(Long taskId) {
        AlertSendTaskEntity task = alertSendTaskMapper.selectById(taskId);
        if (task == null || !AlertCenterConstants.TaskStatus.SENDING.equals(task.getStatus())) {
            return;
        }
        AlertRecordEntity record = alertRecordMapper.selectById(task.getRecordId());
        AlertChannelInstanceEntity instance = alertChannelInstanceMapper.selectById(task.getChannelInstanceId());
        int attemptNo = task.getRetryCount() == null ? 1 : task.getRetryCount() + 1;

        NotifyResult result;
        if (record == null) {
            result = failed("告警记录不存在");
        } else if (instance == null || !Boolean.TRUE.equals(instance.getEnabled())) {
            result = failed("告警实例不存在或已停用");
        } else {
            AlertNotifier notifier = alertNotifierManager.getNotifier(task.getChannelTypeCode());
            if (notifier == null) {
                result = failed("未找到渠道插件: " + task.getChannelTypeCode());
            } else {
                try {
                    result = notifier.send(NotifyContext.builder()
                        .recordNo(record.getRecordNo())
                        .taskNo(task.getTaskNo())
                        .title(record.getTitle())
                        .content(record.getContent())
                        .alertLevel(record.getAlertLevel())
                        .config(alertConfigCipher.decryptConfig(task.getChannelTypeCode(), instance.getConfigJson()))
                        .build());
                } catch (Exception ex) {
                    log.error("发送告警失败, taskId={}", taskId, ex);
                    result = failed(ex.getMessage());
                }
            }
        }
        persistDispatchResult(task, result, attemptNo);
        aggregateRecord(task.getRecordId());
    }

    private NotifyResult failed(String message) {
        return NotifyResult.builder().success(false).errorMessage(message).responseSummary(message).costMs(0L).build();
    }

    private void persistDispatchResult(AlertSendTaskEntity task, NotifyResult result, int attemptNo) {
        Date now = new Date();
        AlertSendLogEntity logEntity = new AlertSendLogEntity();
        logEntity.setTaskId(task.getId());
        logEntity.setAttemptNo(attemptNo);
        logEntity.setRequestPayload(result.getRequestPayload());
        logEntity.setResponsePayload(result.getResponsePayload());
        logEntity.setSuccess(result.isSuccess());
        logEntity.setCostMs((int) result.getCostMs());
        logEntity.setErrorMessage(truncate(result.getErrorMessage(), 1000));
        alertSendLogMapper.insert(logEntity);

        task.setResponseSummary(truncate(StrUtil.blankToDefault(result.getResponseSummary(), result.getResponsePayload()), 1000));
        task.setLastError(truncate(result.getErrorMessage(), 1000));
        task.setRetryCount(attemptNo);
        task.setFinishedTime(result.isSuccess() ? now : null);
        task.setNextRetryTime(null);
        if (result.isSuccess()) {
            task.setStatus(AlertCenterConstants.TaskStatus.SUCCESS);
        } else if (attemptNo < resolveMaxRetryCount(task)) {
            task.setStatus(AlertCenterConstants.TaskStatus.RETRYING);
            task.setNextRetryTime(new Date(now.getTime() + alertProperties.getRetry().getIntervalSeconds() * 1000L));
        } else {
            task.setStatus(AlertCenterConstants.TaskStatus.FAILED);
            task.setFinishedTime(now);
        }
        alertSendTaskMapper.updateById(task);
    }

    private int resolveMaxRetryCount(AlertSendTaskEntity task) {
        if (task.getMaxRetryCount() == null || task.getMaxRetryCount() <= 0) {
            return Math.max(alertProperties.getRetry().getMaxTimes(), 1);
        }
        return task.getMaxRetryCount();
    }

    @Transactional(rollbackFor = Exception.class)
    public void aggregateRecord(Long recordId) {
        AlertRecordEntity record = alertRecordMapper.selectById(recordId);
        if (record == null) {
            return;
        }
        LambdaQueryWrapper<AlertSendTaskEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertSendTaskEntity::getRecordId, recordId).orderByAsc(AlertSendTaskEntity::getId);
        List<AlertSendTaskEntity> tasks = alertSendTaskMapper.selectList(wrapper);
        if (CollUtil.isEmpty(tasks)) {
            record.setStatus(AlertCenterConstants.RecordStatus.FAILED);
            record.setErrorMessage("告警任务不存在");
            record.setFinishedTime(new Date());
            alertRecordMapper.updateById(record);
            return;
        }
        int successCount = 0;
        int failedCount = 0;
        boolean hasSending = false;
        boolean hasWaiting = false;
        StringBuilder errorBuilder = new StringBuilder();
        for (AlertSendTaskEntity task : tasks) {
            if (AlertCenterConstants.TaskStatus.SUCCESS.equals(task.getStatus())) {
                successCount++;
            } else if (AlertCenterConstants.TaskStatus.FAILED.equals(task.getStatus())) {
                failedCount++;
                if (StrUtil.isNotBlank(task.getLastError())) {
                    if (errorBuilder.length() > 0) {
                        errorBuilder.append("; ");
                    }
                    errorBuilder.append(task.getInstanceName()).append(": ").append(task.getLastError());
                }
            } else if (AlertCenterConstants.TaskStatus.SENDING.equals(task.getStatus())) {
                hasSending = true;
            } else if (AlertCenterConstants.TaskStatus.WAITING.equals(task.getStatus())
                || AlertCenterConstants.TaskStatus.RETRYING.equals(task.getStatus())) {
                hasWaiting = true;
            }
        }
        record.setChannelCount(tasks.size());
        record.setSuccessCount(successCount);
        record.setFailedCount(failedCount);
        record.setErrorMessage(truncate(errorBuilder.toString(), 1000));
        if (hasSending) {
            record.setStatus(AlertCenterConstants.RecordStatus.SENDING);
            record.setFinishedTime(null);
        } else if (hasWaiting) {
            record.setStatus(AlertCenterConstants.RecordStatus.PENDING);
            record.setFinishedTime(null);
        } else if (successCount == tasks.size()) {
            record.setStatus(AlertCenterConstants.RecordStatus.SUCCESS);
            record.setFinishedTime(new Date());
        } else if (failedCount == tasks.size()) {
            record.setStatus(AlertCenterConstants.RecordStatus.FAILED);
            record.setFinishedTime(new Date());
        } else {
            record.setStatus(AlertCenterConstants.RecordStatus.PARTIAL_SUCCESS);
            record.setFinishedTime(new Date());
        }
        alertRecordMapper.updateById(record);
    }

    private String truncate(String value, int max) {
        if (StrUtil.isBlank(value) || value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }
}
