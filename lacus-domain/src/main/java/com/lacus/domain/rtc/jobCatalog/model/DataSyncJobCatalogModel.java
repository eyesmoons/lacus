package com.lacus.domain.rtc.jobCatalog.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.rtc.entity.DataSyncJobCatalogEntity;
import com.lacus.domain.rtc.jobCatalog.command.UpdateJobCatalogCommand;
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
