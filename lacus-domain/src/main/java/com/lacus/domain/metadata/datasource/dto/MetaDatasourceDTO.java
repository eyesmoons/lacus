package com.lacus.domain.metadata.datasource.dto;

import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.enums.DatasourceStatusEnum;
import com.lacus.dao.system.entity.SysUserEntity;
import com.lacus.dao.system.enums.interfaces.BasicEnumUtil;
import com.lacus.core.cache.CacheCenter;
import lombok.Data;

import java.util.Date;

@Data
public class MetaDatasourceDTO {
    private Long datasourceId;
    private String datasourceName;
    private String type;
    private String remark;
    private String ip;
    private Integer port;
    private String username;
    private String password;
    private String defaultDbName;
    private String connectionParams;
    private String status;
    private String statusName;
    private String creatorName;
    private Date createTime;

    public MetaDatasourceDTO(MetaDatasourceEntity entity) {
        this.datasourceId = entity.getDatasourceId();
        this.datasourceName = entity.getDatasourceName();
        this.type = entity.getType();
        this.remark = entity.getRemark();
        this.ip = entity.getIp();
        this.port = entity.getPort();
        this.username = entity.getUsername();
        this.defaultDbName = entity.getDefaultDbName();
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
