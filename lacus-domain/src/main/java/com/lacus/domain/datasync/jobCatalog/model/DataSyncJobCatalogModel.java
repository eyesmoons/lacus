package com.lacus.domain.datasync.jobCatalog.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.datasync.entity.DataSyncJobCatalogEntity;
import com.lacus.domain.datasync.jobCatalog.command.UpdateJobCatalogCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataSyncJobCatalogModel extends DataSyncJobCatalogEntity {

    public DataSyncJobCatalogModel(DataSyncJobCatalogEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity,this);
        }
    }

    public void loadUpdateCommand(UpdateJobCatalogCommand updateCommand) {
        DataSyncJobCatalogModelFactory.loadFromAddCommand(updateCommand, this);
    }
}
