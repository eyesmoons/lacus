package com.lacus.datasource.plugins;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.ClickHouseSchemaMapper;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.base.AbstractDataSourcePlugin;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;
import com.lacus.datasource.model.ParamValidation;
import com.lacus.enums.DatasourceTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClickHouse数据源插件
 */
@AutoService(DataSourcePlugin.class)
public class ClickHouseDataSourcePlugin extends AbstractDataSourcePlugin {

    @Override
    public String getName() {
        return "CLICKHOUSE";
    }

    @Override
    public Integer getType() {
        return DatasourceTypeEnum.OLAP.getValue();
    }

    @Override
    public String getRemark() {
        return "ClickHouse列式存储数据库";
    }

    @Override
    public String getDriverName() {
        return "ru.yandex.clickhouse.ClickHouseDriver";
    }

    @Override
    public String getIcon() {
        return "ClickHouse.png";
    }

    @Override
    public DruidDataSource createDataSource(String connectionParams) {
        ConnectionParam connectionParam = parseConnectionParams(connectionParams);
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("ru.yandex.clickhouse.ClickHouseDriver");
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
                .description("ClickHouse服务器地址")
                .order(1)
                .build());

        // 端口
        definitions.put("port", ParamDefinitionDTO.builder()
                .defaultValue(8123)
                .required(true)
                .inputType("number")
                .displayName("端口")
                .description("ClickHouse服务器端口")
                .order(2)
                .validation(ParamValidation.builder()
                        .minValue(1)
                        .maxValue(65535)
                        .build())
                .build());

        // 数据库
        definitions.put("database", ParamDefinitionDTO.builder()
                .defaultValue("default")
                .required(true)
                .inputType("string")
                .displayName("数据库名")
                .description("要连接的数据库名称")
                .order(3)
                .build());

        // 用户名
        definitions.put("username", ParamDefinitionDTO.builder()
                .defaultValue("default")
                .required(true)
                .inputType("string")
                .displayName("用户名")
                .description("数据库用户名")
                .order(4)
                .build());

        // 密码
        definitions.put("password", ParamDefinitionDTO.builder()
                .required(false)
                .inputType("password")
                .displayName("密码")
                .description("数据库密码")
                .order(5)
                .build());

        // 其他参数
        definitions.put("params", ParamDefinitionDTO.builder()
                .defaultValue("socket_timeout=300000&max_rows_to_group_by=5000000")
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

        return String.format("jdbc:clickhouse://%s:%d/%s?%s", host, port, database, params);
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        ClickHouseSchemaMapper mapper = SpringUtil.getBean(ClickHouseSchemaMapper.class);
        List<SchemaDbEntity> schemaDbList = mapper.listAllSchemaDb();
        return schemaDbList.stream().peek(entity -> entity.setDatasourceId(datasourceId)).collect(Collectors.toList());
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        ClickHouseSchemaMapper mapper = SpringUtil.getBean(ClickHouseSchemaMapper.class);
        return mapper.listSchemaTable(dbName, tableName);
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        ClickHouseSchemaMapper mapper = SpringUtil.getBean(ClickHouseSchemaMapper.class);
        return mapper.listSchemaColumn(dbName, tableName);
    }
}
