package com.lacus.service.dataCollect;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.dataCollect.entity.DataSyncJobCatalogEntity;

import java.util.List;

public interface IDataSyncJobCatalogService extends IService<DataSyncJobCatalogEntity> {
    List<DataSyncJobCatalogEntity> listByName(String catalogName);
}
