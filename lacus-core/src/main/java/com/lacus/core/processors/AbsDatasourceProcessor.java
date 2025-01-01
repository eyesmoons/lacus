package com.lacus.core.processors;

import lombok.Getter;

@Getter
public abstract class AbsDatasourceProcessor implements IDatasourceProcessor {

    protected String datasourceName;

    public AbsDatasourceProcessor(String datasourceName) {
        this.datasourceName = datasourceName;
    }

}