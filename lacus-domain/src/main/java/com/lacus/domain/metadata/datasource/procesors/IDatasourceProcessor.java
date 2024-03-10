package com.lacus.domain.metadata.datasource.procesors;


import com.lacus.common.utils.beans.MetaDatasource;

public interface IDatasourceProcessor {

    boolean testDatasourceConnection(MetaDatasource datasource);
}