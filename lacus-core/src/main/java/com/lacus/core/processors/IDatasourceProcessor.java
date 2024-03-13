package com.lacus.core.processors;


import com.lacus.common.utils.beans.MetaDatasource;
import com.lacus.dao.metadata.entity.SchemaDbEntity;

import java.util.List;

public interface IDatasourceProcessor {

    boolean testDatasourceConnection(MetaDatasource datasource);

    List<SchemaDbEntity> listAllSchemaDb(Long datasourceId);
}