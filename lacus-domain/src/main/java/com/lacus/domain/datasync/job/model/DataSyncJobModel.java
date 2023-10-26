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
    private static final long serialVersionUID = -9052533747911630039L;
    private String catalogName;
    private String sourceDatasourceName;
    private String sinkDatasourceName;
    private String status;

    public DataSyncJobModel(DataSyncJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }
}
