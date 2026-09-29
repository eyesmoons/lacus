package com.lacus.datasource.plugins;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.base.AbstractDataSourcePlugin;
import com.lacus.datasource.context.VirtualSourceContext;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;
import com.lacus.datasource.model.ParamValidation;
import com.lacus.enums.DatasourceTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AutoService(DataSourcePlugin.class)
@Component
public class HiveDataSourcePlugin extends AbstractDataSourcePlugin {

    private static final String HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    @Override
    public String getName() {
        return "HIVE";
    }

    @Override
    public Integer getType() {
        return DatasourceTypeEnum.DATA_WAREHOUSE.getValue();
    }

    @Override
    public String getRemark() {
        return "Hive数据仓库（HiveServer2）";
    }

    @Override
    public String getDriverName() {
        return HIVE_DRIVER;
    }

    @Override
    public String getIcon() {
        return "Hive.png";
    }

    @Override
    public Map<String, ParamDefinitionDTO> getConnectionParamDefinitions() {
        Map<String, ParamDefinitionDTO> definitions = new LinkedHashMap<>();

        definitions.put("host", ParamDefinitionDTO.builder()
                .required(true)
                .inputType("string")
                .displayName("主机地址")
                .description("HiveServer2主机地址")
                .order(1)
                .build());

        definitions.put("port", ParamDefinitionDTO.builder()
                .defaultValue(10000)
                .required(true)
                .inputType("number")
                .displayName("端口")
                .description("HiveServer2端口")
                .order(2)
                .validation(ParamValidation.builder()
                        .minValue(1)
                        .maxValue(65535)
                        .build())
                .build());

        definitions.put("database", ParamDefinitionDTO.builder()
                .defaultValue("default")
                .required(true)
                .inputType("string")
                .displayName("数据库")
                .description("要连接或浏览的默认数据库")
                .order(3)
                .build());

        definitions.put("username", ParamDefinitionDTO.builder()
                .defaultValue("hive")
                .required(true)
                .inputType("string")
                .displayName("用户名")
                .description("HiveServer2用户名")
                .order(4)
                .build());

        definitions.put("password", ParamDefinitionDTO.builder()
                .required(false)
                .inputType("password")
                .displayName("密码")
                .description("HiveServer2密码")
                .order(5)
                .build());

        definitions.put("authType", ParamDefinitionDTO.builder()
                .defaultValue("NOSASL")
                .required(false)
                .inputType("string")
                .displayName("认证方式")
                .description("NOSASL / KERBEROS / LDAP，默认NOSASL")
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
        String database = StringUtils.defaultString(connectionParam.getValue("database"), "default");
        String authType = StringUtils.defaultString(connectionParam.getValue("authType"), "NOSASL");
        StringBuilder url = new StringBuilder(String.format("jdbc:hive2://%s:%d/%s", host, port, database));
        if ("NOSASL".equalsIgnoreCase(authType)) {
            url.append(";auth=noSasl");
        } else if ("KERBEROS".equalsIgnoreCase(authType)) {
            url.append(";auth=kerberos");
        } else if ("LDAP".equalsIgnoreCase(authType)) {
            url.append(";auth=ldap");
        }
        return url.toString();
    }

    @Override
    public DruidDataSource createDataSource(String connectionParams) {
        ConnectionParam connectionParam = parseConnectionParamsPreserveAll(connectionParams);
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(HIVE_DRIVER);
        druidDataSource.setUrl(getJdbcUrl(connectionParam));
        druidDataSource.setUsername(connectionParam.getValue("username"));
        if (StringUtils.isNotEmpty(connectionParam.getValue("password"))) {
            druidDataSource.setPassword(connectionParam.getValue("password"));
        }
        druidDataSource.setBreakAfterAcquireFailure(true);
        druidDataSource.setConnectionErrorRetryAttempts(1);
        druidDataSource.setMaxWait(5000);
        druidDataSource.setFailFast(true);
        return druidDataSource;
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        String params = VirtualSourceContext.get();
        if (StringUtils.isEmpty(params)) {
            return Collections.emptyList();
        }
        List<SchemaDbEntity> result = new ArrayList<>();
        try (Connection connection = createDataSource(params).getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW DATABASES")) {
            while (resultSet.next()) {
                SchemaDbEntity entity = new SchemaDbEntity();
                entity.setSchemaName(resultSet.getString(1));
                entity.setDatasourceId(datasourceId);
                result.add(entity);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        String params = VirtualSourceContext.get();
        if (StringUtils.isEmpty(params)) {
            return Collections.emptyList();
        }
        List<SchemaTableEntity> result = new ArrayList<>();
        try (Connection connection = createDataSource(params).getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES IN `" + dbName + "`")) {
            while (resultSet.next()) {
                String table = resultSet.getString(1);
                if (StringUtils.isNotEmpty(tableName) && !tableName.equals(table)) {
                    continue;
                }
                SchemaTableEntity entity = new SchemaTableEntity();
                entity.setTableSchema(dbName);
                entity.setTableName(table);
                entity.setTableType("TABLE");
                result.add(entity);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        String params = VirtualSourceContext.get();
        if (StringUtils.isEmpty(params)) {
            return Collections.emptyList();
        }
        List<SchemaColumnEntity> result = new ArrayList<>();
        try (Connection connection = createDataSource(params).getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("DESC `" + dbName + "`.`" + tableName + "`")) {
            while (resultSet.next()) {
                SchemaColumnEntity entity = new SchemaColumnEntity();
                entity.setTableSchema(dbName);
                entity.setTableName(tableName);
                entity.setColumnName(resultSet.getString(1));
                entity.setColumnType(resultSet.getString(2));
                entity.setDataType(resultSet.getString(2));
                entity.setColumnComment(resultSet.getString(3));
                result.add(entity);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return result;
    }
}