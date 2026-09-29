package com.lacus.datasource.api;

import com.alibaba.druid.pool.DruidDataSource;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据源插件接口
 */
public interface DataSourcePlugin {

    /**
     * 获取数据源名称
     */
    String getName();

    /**
     * 获取数据源类型
     */
    Integer getType();

    /**
     * 获取数据源描述
     */
    String getRemark();

    /**
     * 获取驱动类名
     */
    String getDriverName();

    /**
     * 获取图标URL
     */
    String getIcon();

    /**
     * 获取连接参数定义
     *
     * @return 参数定义Map，key为参数名，value为参数定义
     */
    Map<String, ParamDefinitionDTO> getConnectionParamDefinitions();

    /**
     * 获取JDBC URL
     *
     * @param connectionParam 连接参数
     * @return JDBC URL
     */
    String getJdbcUrl(ConnectionParam connectionParam);

    /**
     * 测试数据源连接
     */
    boolean testConnection(String connectionParams) throws SQLException;

    /**
     * 获取数据库连接
     */
    Connection getConnection(String connectionParams) throws SQLException;

    List<SchemaDbEntity> listAllSchemaDb(Long datasourceId);

    List<SchemaTableEntity> listSchemaTable(String dbName, String tableName);

    List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName);

    DruidDataSource createDataSource(String connectionParams);
}
