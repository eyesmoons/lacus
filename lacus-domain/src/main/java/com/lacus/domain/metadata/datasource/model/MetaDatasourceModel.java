package com.lacus.domain.metadata.datasource.model;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.domain.metadata.datasource.command.UpdateMetaDatasourceCommand;
import com.lacus.domain.metadata.db.model.MetaDbModel;
import com.lacus.service.metadata.IMetaDataSourceService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetaDatasourceModel extends MetaDatasourceEntity {
    private String label;
    private String uniqueFlag;
    private Integer nodeLevel = 1;

    public MetaDatasourceModel(MetaDatasourceEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity,this);
            label = entity.getDatasourceName();
            uniqueFlag = entity.getDatasourceId().toString();
        }
    }

    public void checkDatasourceNameUnique(IMetaDataSourceService metadataSourceService) {
        if (metadataSourceService.isDatasourceNameDuplicated(getDatasourceId(), getDatasourceName())) {
            throw new ApiException(ErrorCode.Business.DATASOURCE_NAME_IS_NOT_UNIQUE, getDatasourceName());
        }
    }

    public void loadUpdateCommand(UpdateMetaDatasourceCommand updateCommand) {
        MetaDatasourceModelFactory.loadFromAddCommand(updateCommand, this);
        setStatus(Convert.toInt(updateCommand.getStatus(), 1));
    }
}
