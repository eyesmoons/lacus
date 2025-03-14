package com.lacus.domain.rtc.job.dto;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.dao.rtc.entity.DataSyncJobEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
