package com.lacus.datasource.plugins;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.SqlServerSchemaMapper;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.base.AbstractDataSourcePlugin;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;
import com.lacus.datasource.model.ParamValidation;
import com.lacus.enums.DatasourceTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQLServer数据源插件
 */
@AutoService(DataSourcePlugin.class)
public class SQLServerDataSourcePlugin extends AbstractDataSourcePlugin {

    @Override
    public String getName() {
        return "SQLSERVER";
    }

    @Override
    public Integer getType() {
        return DatasourceTypeEnum.RELATIONAL.getValue();
    }

    @Override
    public String getRemark() {
        return "SQLServer关系型数据库";
    }

    @Override
    public String getDriverName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getIcon() {
        return "SQLServer.png";
    }

    @Override
    public DruidDataSource createDataSource(String connectionParams) {
        ConnectionParam connectionParam = parseConnectionParams(connectionParams);
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        druidDataSource.setUrl(getJdbcUrl(connectionParam));
        druidDataSource.setUsername(connectionParam.getUsername());
        druidDataSource.setBreakAfterAcquireFailure(true);
        druidDataSource.setConnectionErrorRetryAttempts(1);
        druidDataSource.setMaxWait(2000);
        druidDataSource.setFailFast(true);
        if (StringUtils.isNotEmpty(connectionParam.getPassword())) {
            druidDataSource.setPassword(connectionParam.getPassword());
        }
        return druidDataSource;
    }

    @Override
    public Map<String, ParamDefinitionDTO> getConnectionParamDefinitions() {
        Map<String, ParamDefinitionDTO> definitions = new LinkedHashMap<>();

        // 主机
        definitions.put("host", ParamDefinitionDTO.builder()
                .defaultValue("localhost")
                .required(true)
                .inputType("string")
                .displayName("主机地址")
                .description("SQL Server数据库服务器地址")
                .order(1)
                .build());

        // 端口
        definitions.put("port", ParamDefinitionDTO.builder()
                .defaultValue(1433)
                .required(true)
                .inputType("number")
                .displayName("端口")
                .description("SQL Server数据库服务器端口")
                .order(2)
                .validation(ParamValidation.builder()
                        .minValue(1)
                        .maxValue(65535)
                        .build())
                .build());

        // 数据库
        definitions.put("database", ParamDefinitionDTO.builder()
                .defaultValue("master")
                .required(true)
                .inputType("string")
                .displayName("数据库名")
                .description("要连接的数据库名称")
                .order(3)
                .build());

        // 用户名
        definitions.put("username", ParamDefinitionDTO.builder()
                .defaultValue("sa")
                .required(true)
                .inputType("string")
                .displayName("用户名")
                .description("数据库用户名")
                .order(4)
                .build());

        // 密码
        definitions.put("password", ParamDefinitionDTO.builder()
                .required(true)
                .inputType("password")
                .displayName("密码")
                .description("数据库密码")
                .order(5)
                .build());

        // 其他参数
        definitions.put("params", ParamDefinitionDTO.builder()
                .defaultValue("encrypt=true&trustServerCertificate=true")
                .required(false)
                .inputType("string")
                .displayName("连接参数")
                .description("其他连接参数")
                .order(6)
                .build());

        return definitions;
    }

    @Override
    public String getJdbcUrl(ConnectionParam connectionParam) {
        return buildJdbcUrl(connectionParam);
    }

    @Override
    protected String buildJdbcUrl(ConnectionParam connectionParam) {
        String host = connectionParam.getValue("host");
        Integer port = connectionParam.getValue("port");
        String database = connectionParam.getValue("database");
        String params = connectionParam.getValue("params");

        return String.format("jdbc:sqlserver://%s:%d;databaseName=%s;%s", host, port, database, params);
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        SqlServerSchemaMapper mapper = SpringUtil.getBean(SqlServerSchemaMapper.class);
        List<SchemaDbEntity> schemaDbList = mapper.listAllSchemaDb();
        return schemaDbList.stream().peek(entity -> entity.setDatasourceId(datasourceId)).collect(Collectors.toList());
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        SqlServerSchemaMapper mapper = SpringUtil.getBean(SqlServerSchemaMapper.class);
        return mapper.listSchemaTable(dbName, tableName);
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        SqlServerSchemaMapper mapper = SpringUtil.getBean(SqlServerSchemaMapper.class);
        return mapper.listSchemaColumn(dbName, tableName);
    }
}
