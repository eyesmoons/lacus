package com.lacus.domain.metadata.datasource.procesors;

public class SqlServerProcessor extends AbsJdbcProcessor {
    public SqlServerProcessor() {
            super("SQLSERVER");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:microsoft:sqlserver://%s:%s;DatabaseName=%s";
    }
}
