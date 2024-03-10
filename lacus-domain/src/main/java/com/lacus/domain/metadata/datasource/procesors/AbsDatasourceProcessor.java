package com.lacus.domain.metadata.datasource.procesors;

public abstract class AbsDatasourceProcessor implements IDatasourceProcessor {

    protected String datasourceName;

    public AbsDatasourceProcessor(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDatasourceName() {
        return datasourceName;
    }
}