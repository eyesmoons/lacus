package com.lacus.domain.datasync.job.dto;

import com.lacus.dao.datasync.entity.DataSyncJobEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class JobTreeDTO {

    public JobTreeDTO(DataSyncJobEntity entity) {
        if (entity != null) {
            this.jobId = entity.getJobId();
            this.catalogId = entity.getCatalogId();
            this.jobName = entity.getJobName();
            this.syncType = entity.getSyncType();
            this.appContainer = entity.getAppContainer();
            this.remark = entity.getRemark();
            this.sourceDatasourceName = entity.getSourceDatasourceName();
            this.sinkDatasourceName = entity.getSinkDatasourceName();
            this.sourceStatus = entity.getSourceStatus();
            this.sinkStatus = entity.getSinkStatus();
            this.createTime = entity.getCreateTime();
        }
    }

    private String jobId;
    private String catalogId;
    private String jobName;
    private Integer syncType;
    private String appContainer;
    private String remark;
    private String sourceDatasourceName;
    private String sinkDatasourceName;
    private String sourceStatus;
    private String sinkStatus;
    private Date createTime;
}
