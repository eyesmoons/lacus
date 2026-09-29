package com.lacus.service.rtc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.rtc.entity.DataSyncJobCatalogEntity;

import java.util.List;

public interface IDataSyncJobCatalogService extends IService<DataSyncJobCatalogEntity> {
    List<DataSyncJobCatalogEntity> listByName(String catalogName);
}
