package com.lacus.domain.metadata.datasource.procesors;

import com.google.auto.service.AutoService;

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
}
