package com.lacus.domain.metadata.datasource.procesors;

public class MysqlProcessor extends AbsJdbcProcessor {
    public MysqlProcessor() {
        super("MYSQL");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:mysql://%s:%s/%s";
    }
}
