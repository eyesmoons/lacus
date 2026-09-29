package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaDbTableEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;

import java.util.List;

public interface IMetaTableService extends IService<MetaTableEntity> {
    boolean isMetaTableExists(Long dbId, String tableName);
    MetaTableEntity getMetaTable(Long dbId, String tableName);
    List<MetaTableEntity> getMetaTables(List<Long> dbIds);
    List<MetaDbTableEntity> getMetaTables(Long datasourceId, String dbName);
    List<MetaDbTableEntity> listMetaTable(List<MetaDbTableEntity> params);

    /**
     * 按表名模糊查询全部表(跨库),用于血缘图全局表搜索
     */
    List<MetaTableEntity> searchTablesByKeyword(String tableName);
}
