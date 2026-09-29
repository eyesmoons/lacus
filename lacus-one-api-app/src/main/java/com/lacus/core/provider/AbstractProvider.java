package com.lacus.core.provider;

import com.lacus.dao.entity.DataSourceEntity;
import com.lacus.service.dto.ApiConfigDTO;

import java.util.List;
import java.util.Map;

public abstract class AbstractProvider {

    protected DataSourceEntity datasourceInfo;

    public abstract Long processTotal(ApiConfigDTO config, Map<String, Object> params);

    public abstract List<Map<String, Object>> process(ApiConfigDTO config, Map<String, Object> params);

    public void initDatasource(DataSourceEntity datasourceInfo) {
        this.datasourceInfo = datasourceInfo;
    }

}
