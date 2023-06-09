package com.lacus.domain.datasync.job.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DataSyncJobModel extends DataSyncJobEntity {
    private String catalogName;
    private String sourceDatasourceName;
    private String sinkDatasourceName;
    private String sourceStatus;
    private String sinkStatus;

    public DataSyncJobModel(DataSyncJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }
}
