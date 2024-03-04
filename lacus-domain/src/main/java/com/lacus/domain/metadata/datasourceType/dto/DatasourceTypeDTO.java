package com.lacus.domain.metadata.datasourceType.dto;

import com.lacus.core.cache.CacheCenter;
import com.lacus.dao.metadata.entity.MetaDatasourceTypeEntity;
import com.lacus.dao.system.entity.SysUserEntity;
import lombok.Data;

import java.util.Date;

@Data
public class DatasourceTypeDTO {
    public DatasourceTypeDTO(MetaDatasourceTypeEntity entity) {
        if (entity != null) {
            this.typeId = entity.getTypeId();
            this.typeName = entity.getTypeName();
            this.createTime = entity.getCreateTime();
            this.typeCode = entity.getTypeCode();
            this.typeCatalog = entity.getTypeCatalog();
            this.driverName = entity.getDriverName();
            this.icon = entity.getIcon();
            this.jdbcUrl = entity.getJdbcUrl();
            this.remark = entity.getRemark();
            SysUserEntity cacheUser = CacheCenter.userCache.getObjectById(entity.getCreatorId());
            if (cacheUser != null) {
                this.creatorName = cacheUser.getUsername();
            }
        }
    }

    private Long typeId;
    private String typeName;
    private String typeCode;
    private String typeCatalog;
    private String driverName;
    private String icon;
    private String jdbcUrl;
    private String remark;
    private Date createTime;
    private String creatorName;
}
