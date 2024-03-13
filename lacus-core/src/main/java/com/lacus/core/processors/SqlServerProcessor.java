package com.lacus.core.processors;

import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaDbEntity;

import java.util.List;

@AutoService(AbsDatasourceProcessor.class)
public class SqlServerProcessor extends AbsJdbcProcessor {
    public SqlServerProcessor() {
            super("SQLSERVER");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:sqlserver://%s:%s;databaseName=%s";
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        return null;
    }
}
