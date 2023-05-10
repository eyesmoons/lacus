package com.lacus.service.datasync;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;

import java.util.List;

public interface IDataSyncJobCatelogService extends IService<DataSyncJobCatelogEntity> {
    List<DataSyncJobCatelogEntity> listByName(String catelogName);
}
