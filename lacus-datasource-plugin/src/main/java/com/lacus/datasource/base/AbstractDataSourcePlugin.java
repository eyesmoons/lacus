package com.lacus.datasource.base;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson2.JSON;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.model.ConnectionParam;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源插件抽象基类
 */
public abstract class AbstractDataSourcePlugin implements DataSourcePlugin {

    @Override
    public boolean testConnection(String connectionParams) throws SQLException {
        try (Connection connection = getConnection(connectionParams)) {
            return connection != null && !connection.isClosed();
        }
    }

    @Override
    public Connection getConnection(String connectionParams) throws SQLException {
        DruidDataSource dataSource = createDataSource(connectionParams);
        return dataSource.getConnection();
    }

    /**
     * 解析连接参数
     */
    public ConnectionParam parseConnectionParams(String connectionParams) {
        return JSON.parseObject(connectionParams, ConnectionParam.class);
    }

    /**
     * 构建JDBC URL
     */
    protected abstract String buildJdbcUrl(ConnectionParam param);

}
