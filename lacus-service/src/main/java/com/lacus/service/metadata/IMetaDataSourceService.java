package com.lacus.service.metadata;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;

import java.util.List;

public interface IMetaDataSourceService extends IService<MetaDatasourceEntity> {
    boolean isDatasourceNameDuplicated(Long datasourceId, String datasourceName);
    List<MetaDatasourceEntity> getDatasourceList(String datasourceName, String sourceType);
}
