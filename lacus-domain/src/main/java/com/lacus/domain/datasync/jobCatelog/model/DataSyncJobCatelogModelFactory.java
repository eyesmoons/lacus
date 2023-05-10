package com.lacus.domain.datasync.jobCatelog.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;
import com.lacus.domain.datasync.jobCatelog.command.AddJobCatelogCommand;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.domain.metadata.datasource.model.MetaDatasourceModel;
import com.lacus.service.datasync.IDataSyncJobCatelogService;

public class DataSyncJobCatelogModelFactory {

    public static DataSyncJobCatelogModel loadFromDb(Long catelogId, IDataSyncJobCatelogService dataSyncJobCatelogService) {
        DataSyncJobCatelogEntity byId = dataSyncJobCatelogService.getById(catelogId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, catelogId, "任务分组");
        }
        return new DataSyncJobCatelogModel(byId);
    }

    public static DataSyncJobCatelogModel loadFromAddCommand(AddJobCatelogCommand addCommand, DataSyncJobCatelogModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }
}
