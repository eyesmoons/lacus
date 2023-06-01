package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaColumnEntity;

import java.util.List;

public interface IMetaColumnService extends IService<MetaColumnEntity> {
    List<MetaColumnEntity> getColumnsByTableId(Long tableId);
    void removeColumnsByTableIds(List<Long> tableIds);
    boolean removeColumnsByTableId(Long tableId);
    List<MetaColumnEntity> getColumnsByTableName(Long datasourceId, String dbName, String tableName);
}
