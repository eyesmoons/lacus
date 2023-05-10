package com.lacus.domain.datasync.jobCatelog.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;
import com.lacus.domain.datasync.jobCatelog.command.UpdateJobCatelogCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataSyncJobCatelogModel extends DataSyncJobCatelogEntity {

    public DataSyncJobCatelogModel(DataSyncJobCatelogEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity,this);
        }
    }

    public void loadUpdateCommand(UpdateJobCatelogCommand updateCommand) {
        DataSyncJobCatelogModelFactory.loadFromAddCommand(updateCommand, this);
    }
}
