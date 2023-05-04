package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaTableEntity;

public interface IMetaTableService extends IService<MetaTableEntity> {
    boolean isMetaTableExists(Long dbId, String tableName);
    MetaTableEntity getMetaTable(Long dbId, String tableName);
}
