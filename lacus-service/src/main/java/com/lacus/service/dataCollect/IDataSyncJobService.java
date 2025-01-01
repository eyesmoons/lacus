package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncJobEntity;

import java.util.List;

public interface IDataSyncJobService extends IService<DataSyncJobEntity> {
    List<DataSyncJobEntity> listBySourceDatasourceId(Long datasourceId);

    List<DataSyncJobEntity> listSavedTableByJobId(String jobId);

    List<DataSyncJobEntity> listByCatalogId(String catalogId);

    List<DataSyncJobEntity> listByQuery(DataSyncJobEntity query);

    boolean isJobNameDuplicated(Long jobId, String jobName);
}
