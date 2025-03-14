package com.lacus.domain.rtc.jobCatalog.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.rtc.entity.DataSyncJobCatalogEntity;
import com.lacus.domain.rtc.jobCatalog.command.AddJobCatalogCommand;
import com.lacus.service.rtc.IDataSyncJobCatalogService;

public class DataSyncJobCatalogModelFactory {

    public static DataSyncJobCatalogModel loadFromDb(String catalogId, IDataSyncJobCatalogService dataSyncJobCatalogService) {
        DataSyncJobCatalogEntity byId = dataSyncJobCatalogService.getById(catalogId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, catalogId, "任务分组");
        }
        return new DataSyncJobCatalogModel(byId);
    }

    public static DataSyncJobCatalogModel loadFromAddCommand(AddJobCatalogCommand addCommand, DataSyncJobCatalogModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }
}
