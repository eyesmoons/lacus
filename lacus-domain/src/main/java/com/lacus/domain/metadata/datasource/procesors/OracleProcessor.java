package com.lacus.domain.metadata.datasource.procesors;

import com.google.auto.service.AutoService;

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
}
