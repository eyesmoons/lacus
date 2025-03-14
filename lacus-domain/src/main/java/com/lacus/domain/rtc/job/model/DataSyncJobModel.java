package com.lacus.domain.rtc.job.model;

import cn.hutool.core.bean.BeanUtil;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.rtc.entity.DataSyncJobEntity;
import com.lacus.service.rtc.IDataSyncJobService;
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
    private String sourceDbName;
    private String sinkDbName;
    private String status;

    public DataSyncJobModel(DataSyncJobEntity entity) {
        BeanUtil.copyProperties(entity, this);
    }

    public void checkJobNameUnique(IDataSyncJobService jobService) {
        if (jobService.isJobNameDuplicated(getJobId(), getJobName())) {
            throw new ApiException(ErrorCode.Business.JOB_NAME_IS_NOT_UNIQUE, getJobName());
        }
    }
}
