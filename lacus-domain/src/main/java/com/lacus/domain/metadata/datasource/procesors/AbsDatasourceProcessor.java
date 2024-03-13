package com.lacus.domain.metadata.datasource.procesors;

import lombok.Getter;

@Getter
public abstract class AbsDatasourceProcessor implements IDatasourceProcessor {

    protected String datasourceName;

    public AbsDatasourceProcessor(String datasourceName) {
        this.datasourceName = datasourceName;
    }

}