package com.lacus.domain.dataCollect.instance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.dataCollect.entity.DataSyncJobEntity;
import com.lacus.dao.dataCollect.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.dataCollect.enums.FlinkStatusEnum;
import com.lacus.domain.common.utils.DataCollectorJobUtil;
import com.lacus.domain.dataCollect.instance.model.DataSyncJobInstanceModel;
import com.lacus.domain.dataCollect.instance.query.JobInstancePageQuery;
import com.lacus.domain.dataCollect.job.JobMonitorBusiness;
import com.lacus.service.dataCollect.IDataSyncJobInstanceService;
import com.lacus.service.dataCollect.IDataSyncJobService;
import com.lacus.utils.CommonPropertyUtils;
import com.lacus.utils.time.DateUtils;
import com.lacus.utils.yarn.FlinkJobDetail;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lacus.common.constant.Constants.YARN_RESTAPI_ADDRESS;

/**
 * @created by shengyu on 2024/2/26 21:32
 */

@Service
public class JobInstanceBusiness {

    @Autowired
    private IDataSyncJobInstanceService dataSyncJobInstanceService;

    @Autowired
    private IDataSyncJobService dataSyncJobService;

    @Autowired
    private JobMonitorBusiness monitorService;

    @Autowired
    private DataCollectorJobUtil dataCollectorJobUtil;

    public void updateInstance(DataSyncJobInstanceEntity instance, String applicationId) {
        try {
            if (Objects.isNull(applicationId)) {
                instance.setFinishedTime(new Date());
                instance.setStatus(FlinkStatusEnum.STOP.getStatus());
                dataSyncJobInstanceService.saveOrUpdate(instance);
            }
            instance.setApplicationId(applicationId);
            FlinkJobDetail jobDetail = monitorService.flinkJobDetail(applicationId);
            instance.setSubmitTime(DateUtils.getDate(jobDetail.getStartTime()));
            instance.setStatus(dataCollectorJobUtil.convertTaskStatus(jobDetail.getState()));
            if (jobDetail.getEndTime() > 0) {
                instance.setFinishedTime(DateUtils.getDate(jobDetail.getEndTime()));
            }
            instance.setFlinkJobId(jobDetail.getJid());
            dataSyncJobInstanceService.saveOrUpdate(instance);
        } catch (Exception e) {
            dataCollectorJobUtil.updateStopStatusForInstance(instance);
        }
    }

    public DataSyncJobInstanceEntity saveInstance(DataSyncJobEntity job, String syncType, String timeStamp, String jobScript) {
        DataSyncJobInstanceEntity entity = new DataSyncJobInstanceEntity();
        entity.setJobId(job.getJobId());
        entity.setInstanceName(dataCollectorJobUtil.genInstanceNam(job.getJobName()));
        entity.setSyncType(syncType);
        entity.setTimeStamp(timeStamp);
        entity.setJobScript(jobScript);
        entity.setSubmitTime(new Date());
        entity.insert();
        return entity;
    }

    public PageDTO pageList(JobInstancePageQuery query) {
        Page page = dataSyncJobInstanceService.page(query.toPage(), query.toQueryWrapper());
        List<DataSyncJobInstanceEntity> records = page.getRecords();
        List<Long> jobIds = records.stream().map(DataSyncJobInstanceEntity::getJobId).collect(Collectors.toList());
        Map<Long, String> jobMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(jobIds)) {
            List<DataSyncJobEntity> dataSyncJobEntities = dataSyncJobService.listByIds(jobIds);
            if (ObjectUtils.isNotEmpty(dataSyncJobEntities)) {
                jobMap = dataSyncJobEntities.stream().collect(Collectors.toMap(DataSyncJobEntity::getJobId, DataSyncJobEntity::getJobName, (k1, k2) -> k2));
            }
        }
        Map<Long, String> finalJobMap = jobMap;
        List<DataSyncJobInstanceModel> resultRecord = records.stream().map(entity -> {
            DataSyncJobInstanceModel model = new DataSyncJobInstanceModel(entity);
            model.setJobName(finalJobMap.get(entity.getJobId()));
            if (Objects.equals(FlinkStatusEnum.RUNNING.getStatus(), entity.getStatus())) {
                model.setTrackingUrl(CommonPropertyUtils.getString(YARN_RESTAPI_ADDRESS) + entity.getApplicationId() + "/#/overview");
            }
            return model;
        }).collect(Collectors.toList());
        return new PageDTO(resultRecord, page.getTotal());
    }
}
