package com.lacus.domain.metadata.datasource.procesors;

public class DorisProcessor extends AbsJdbcProcessor {
    public DorisProcessor() {
        super("DORIS");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:mysql://%s:%s/%s";
    }
}
