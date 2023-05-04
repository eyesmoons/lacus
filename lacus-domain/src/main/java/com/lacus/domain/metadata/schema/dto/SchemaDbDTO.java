package com.lacus.domain.metadata.schema.dto;

import com.lacus.dao.metadata.entity.SchemaDbEntity;
import lombok.Data;

@Data
public class SchemaDbDTO {
    private Long datasourceId;
    private String dbName;
    private String label;
    private String uniqueFlag;

    public SchemaDbDTO(SchemaDbEntity entity) {
        dbName = entity.getSchemaName();
        label = entity.getSchemaName();
        uniqueFlag = entity.getSchemaName();
    }
}
