package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;

import java.util.List;

public interface IDataSyncJobService extends IService<DataSyncJobEntity> {
    List<DataSyncJobEntity> listBySourceDatasourceId(Long datasourceId);

    List<DataSyncJobEntity> listSavedTableByJobId(String jobId);

    List<DataSyncJobEntity> listByCatalogId(String catalogId);

    List<DataSyncJobEntity> listByQuery(DataSyncJobEntity query);
}
