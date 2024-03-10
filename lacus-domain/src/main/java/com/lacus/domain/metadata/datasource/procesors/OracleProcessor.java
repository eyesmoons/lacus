package com.lacus.domain.metadata.datasource.procesors;

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
