package com.lacus.dao.metadata.entity;

import lombok.Data;

@Data
public class MetaDbTableEntity extends MetaTableEntity{
    private Long datasourceId;
    private String datasourceName;
    private String datasourceType;
    private Long dbId;
    private String dbName;
}
