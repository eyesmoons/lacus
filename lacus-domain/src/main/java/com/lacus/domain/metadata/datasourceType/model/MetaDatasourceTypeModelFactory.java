package com.lacus.domain.metadata.datasourceType.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.domain.metadata.datasourceType.command.AddMetaDatasourceTypeCommand;
import com.lacus.service.metadata.IMetaDataSourceTypeService;

public class MetaDatasourceTypeModelFactory {

    public static MetaDatasourceTypeModel loadFromDb(Long typeId, IMetaDataSourceTypeService dataSourceTypeService) {
        MetaDatasourceTypeEntity byId = dataSourceTypeService.getById(typeId);
        if (byId == null) {
            throw new ApiException(ErrorCode.Business.OBJECT_NOT_FOUND, typeId, "数据源类型");
        }
        return new MetaDatasourceTypeModel(byId);
    }

    public static MetaDatasourceTypeModel loadFromAddCommand(AddMetaDatasourceTypeCommand addCommand, MetaDatasourceTypeModel model) {
        if (addCommand != null && model != null) {
            BeanUtil.copyProperties(addCommand, model);
        }
        return model;
    }
}
