package com.lacus.domain.metadata.datasource.procesors;

public class OracleProcessor extends AbsJdbcProcessor {
    public OracleProcessor() {
        super("ORACLE");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:oracle:thin:@%s:%s:%s";
    }
}
