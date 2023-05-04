package com.lacus.domain.metadata.db.model;

import com.lacus.dao.metadata.entity.MetaDbEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class MetaDbModel extends MetaDbEntity {
    private String label;
    private String uniqueFlag;
    private Boolean isLeaf = true;
    private Integer nodeLevel = 2;

    public MetaDbModel(MetaDbEntity entity) {
        BeanUtils.copyProperties(entity, this);
        label = entity.getDbName();
        uniqueFlag = entity.getDatasourceId() + "." + entity.getDbName();
    }
}
