package com.lacus.core.processors;

import com.lacus.utils.beans.MetaDatasource;
import com.lacus.utils.sql.JdbcUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbsJdbcProcessor extends AbsDatasourceProcessor{
    public AbsJdbcProcessor(String datasourceName) {
        super(datasourceName);
    }

    @Override
    public boolean testDatasourceConnection(MetaDatasource datasource) {
        try {
            datasource.setJdbcUrl(jdbcUrlConfig());
            JdbcUtil.executeQuery(datasource, validSqlConfig());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    protected abstract String validSqlConfig();
    protected abstract String jdbcUrlConfig();
}
