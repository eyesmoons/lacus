package com.lacus.domain.metadata.datasource.dto;

import com.lacus.core.cache.CacheCenter;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.system.entity.SysUserEntity;
import com.lacus.enums.DatasourceStatusEnum;
import com.lacus.enums.interfaces.BasicEnumUtil;
import lombok.Data;

import java.util.Date;

@Data
public class MetaDatasourceDTO {
    private Long datasourceId;
    private String datasourceName;
    private String type;
    private Integer sourceType;
    private String remark;
    private String connectionParams;
    private String status;
    private String statusName;
    private String creatorName;
    private Date createTime;

    public MetaDatasourceDTO(MetaDatasourceEntity entity) {
        this.datasourceId = entity.getDatasourceId();
        this.datasourceName = entity.getDatasourceName();
        this.type = entity.getType();
        this.sourceType = entity.getSourceType();
        this.remark = entity.getRemark();
        this.connectionParams = entity.getConnectionParams();
        this.status = String.valueOf(entity.getStatus());
        this.statusName = BasicEnumUtil.getDescriptionByValue(DatasourceStatusEnum.class, entity.getStatus());
        this.createTime = entity.getCreateTime();
        SysUserEntity creator = CacheCenter.userCache.getObjectById(entity.getCreatorId());
        if (creator != null) {
            this.creatorName = creator.getUsername();
        }
    }
}
