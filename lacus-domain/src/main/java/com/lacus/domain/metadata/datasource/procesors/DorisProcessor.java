package com.lacus.domain.metadata.datasource.procesors;

import com.google.auto.service.AutoService;

@AutoService(AbsDatasourceProcessor.class)
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
