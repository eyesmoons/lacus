package com.lacus.core.processors;

import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaDbEntity;

import java.util.List;

@AutoService(AbsDatasourceProcessor.class)
public class OracleProcessor extends AbsJdbcProcessor {
    public OracleProcessor() {
        super("ORACLE");
    }

    public String validSqlConfig() {
        return "SELECT 1 FROM dual";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:oracle:thin:@%s:%s:%s";
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        return null;
    }
}
