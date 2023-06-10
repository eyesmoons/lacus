package com.lacus.domain.datasync.job.dto;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobDTO extends DataSyncJobEntity {
    private String sourceDbName;
    private String sinkDbName;
    private MappedTableDTO mappedTable;

    public JobDTO(DataSyncJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }
}
