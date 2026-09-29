package com.lacus.st.source;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSource;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

/**
 * ClickHouse数据源组件
 *
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "clickhouse_source",
        displayName = "ClickHouse数据源",
        description = "从ClickHouse数据库读取数据，支持高性能OLAP查询",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "ClickHouse"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "ClickHouse配置", displayName = "ClickHouse配置", order = 2, description = "ClickHouse特有配置")
})

@AutoService(StComponentInterface.class)
public class ClickHouseSource extends AbstractStSource {

    // 数据源配置
    @StField(
            tag = "数据源配置",
            order = 1,
            required = true,
            enName = "datasourceId",
            cnName = "数据源",
            placeHolder = "请选择数据源",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/datasource/list"
    )
    private String datasourceId;

    @StField(
            tag = "数据源配置",
            order = 2,
            required = true,
            enName = "database",
            cnName = "数据库",
            placeHolder = "请选择数据库",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/db/list/{datasourceId}"
    )
    private String database;

    @StField(
            tag = "数据源配置",
            order = 3,
            required = true,
            enName = "table",
            cnName = "数据表",
            placeHolder = "请选择数据表",
            formType = StField.FormType.MULTI_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String table;

    // ClickHouse特殊配置
    @StField(
            tag = "ClickHouse配置",
            order = 1,
            required = false,
            enName = "clickhouse_config",
            cnName = "ClickHouse配置",
            description = "额外的ClickHouse配置参数，格式：key1=value1;key2=value2",
            placeHolder = "max_execution_time=3600;max_memory_usage=10000000000",
            formType = StField.FormType.TEXT_AREA
    )
    private String clickhouseConfig;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        if (datasourceId != null) {
            String host = connectionConfig.getString("host");
            Integer port = connectionConfig.getInteger("port");
            if (ObjectUtils.isNotEmpty(host) && ObjectUtils.isNotEmpty(port)) {
                config.put("host", host + ":" + port);
            }
            putIfNotEmpty(config, "user", connectionConfig.getString("username"));
            putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        }
        putIfNotEmpty(config, "clickhouse.config", connectionConfig.getString("clickhouse_config"));
        putIfNotEmpty(config, "server_time_zone", connectionConfig.getString("server_time_zone"));

        addTableList(config, connectionConfig, null);
        return config;
    }
}
