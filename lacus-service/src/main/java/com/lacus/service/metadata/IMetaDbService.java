package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaDbEntity;

import java.util.List;

public interface IMetaDbService extends IService<MetaDbEntity> {
    boolean isMetaDbExists(Long datasourceId, String dbName);
    MetaDbEntity getMetaDb(Long datasourceId, String dbName);
    List<MetaDbEntity> listByDatasourceId(Long datasourceId);
}
