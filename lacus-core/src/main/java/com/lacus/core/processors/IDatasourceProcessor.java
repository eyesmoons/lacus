package com.lacus.core.processors;


import com.lacus.common.utils.beans.MetaDatasource;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;

import java.util.List;

public interface IDatasourceProcessor {

    boolean testDatasourceConnection(MetaDatasource datasource);

    List<SchemaDbEntity> listAllSchemaDb(Long datasourceId);

    List<SchemaTableEntity> listSchemaTable(String dbName, String tableName);

    List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName);
}