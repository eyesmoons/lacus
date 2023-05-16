package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncJobCatalogEntity;

import java.util.List;

public interface IDataSyncJobCatalogService extends IService<DataSyncJobCatalogEntity> {
    List<DataSyncJobCatalogEntity> listByName(String catalogName);
}
