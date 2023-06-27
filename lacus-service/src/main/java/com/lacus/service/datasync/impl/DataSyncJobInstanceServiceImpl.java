package com.lacus.service.datasync.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.common.utils.time.DateUtils;
import com.lacus.common.utils.yarn.FlinkJobDetail;
import com.lacus.dao.datasync.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.datasync.enums.FlinkStatusEnum;
import com.lacus.dao.datasync.mapper.DataSyncJobInstanceMapper;
import com.lacus.service.datasync.IDataSyncJobInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DataSyncJobInstanceServiceImpl extends ServiceImpl<DataSyncJobInstanceMapper, DataSyncJobInstanceEntity> implements IDataSyncJobInstanceService{

    @Autowired
    private DataSyncJobInstanceMapper mapper;

    @Override
    public DataSyncJobInstanceEntity getLastInstanceByJobId(String catalogId, Integer type) {
        return mapper.getLastInstanceByJobId(catalogId, type);
    }

    @Override
    public void saveInstance(String catalogId, Integer type, String syncType, String applicationId, FlinkJobDetail jobDetail) {
        DataSyncJobInstanceEntity entity = new DataSyncJobInstanceEntity();
        entity.setCatalogId(catalogId);
        entity.setType(type);
        entity.setSyncType(syncType);
        entity.setSubmitTime(DateUtils.getDate(jobDetail.getStartTime()));
        entity.setStatus(jobDetail.getState());
        if (jobDetail.getEndTime() > 0) {
            entity.setFinishedTime(DateUtils.getDate(jobDetail.getEndTime()));
        }
        entity.setApplicationId(applicationId);
        entity.setFlinkJobId(jobDetail.getJid());
        this.save(entity);
    }

    @Override
    public void failInstance(String catalogId, Integer type, String syncType, String applicationId) {
        DataSyncJobInstanceEntity entity = new DataSyncJobInstanceEntity();
        entity.setCatalogId(catalogId);
        entity.setType(type);
        entity.setSyncType(syncType);
        entity.setApplicationId(applicationId);
        entity.setSubmitTime(new Date());
        entity.setFinishedTime(new Date());
        entity.setStatus(FlinkStatusEnum.STOP.getStatus());
    }
}
