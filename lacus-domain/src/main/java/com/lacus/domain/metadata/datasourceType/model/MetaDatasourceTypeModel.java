package com.lacus.domain.metadata.datasourceType.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.domain.metadata.datasourceType.command.UpdateMetaDatasourceTypeCommand;
import com.lacus.service.metadata.IMetaDataSourceTypeService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MetaDatasourceTypeModel extends MetaDatasourceTypeEntity{

    public MetaDatasourceTypeModel(MetaDatasourceTypeEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity,this);
        }
    }

    public void checkDatasourceTypeNameUnique(IMetaDataSourceTypeService dataSourceTypeService) {
        if (dataSourceTypeService.isTypeNameDuplicated(getTypeId(), getTypeName())) {
            throw new ApiException(ErrorCode.Business.DATASOURCE_NAME_IS_NOT_UNIQUE, getTypeName());
        }
    }

    public void loadUpdateCommand(UpdateMetaDatasourceTypeCommand updateCommand) {
        MetaDatasourceTypeModelFactory.loadFromAddCommand(updateCommand, this);
    }
}
