package com.lacus.domain.metadata.datasource.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.metadata.datasource.command.AddMetaDatasourceCommand;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.service.metadata.IMetaDataSourceService;

public class MetaDatasourceModelFactory {

    public static MetaDatasourceModel loadFromDb(Long datasourceId, IMetaDataSourceService metadataSourceService) {
        MetaDatasourceEntity byId = metadataSourceService.getById(datasourceId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, datasourceId, "数据源");
        }
        return new MetaDatasourceModel(byId);
    }

    public static MetaDatasourceModel loadFromAddCommand(AddMetaDatasourceCommand addCommand, MetaDatasourceModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }
}
