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

import java.util.Map;

/**
 * StarRocks数据源组件
 *
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "starrocks_source",
        displayName = "StarRocks数据源",
        description = "从StarRocks数据库读取数据，支持高性能实时分析",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "StarRocks"
)
@StTag({
        @TagDefinition(name = "数据源配置", displayName = "数据源配置", order = 1, description = "数据源选择和连接配置"),
        @TagDefinition(name = "查询配置", displayName = "查询配置", order = 2, description = "查询配置"),
        @TagDefinition(name = "性能配置", displayName = "性能配置", order = 3, description = "性能优化相关配置"),
})
@AutoService(StComponentInterface.class)
public class StarRocksSource extends AbstractStSource {

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
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.URL,
            dictUrl = "/metadata/table/listTable"
    )
    private String table;

    @StField(
            tag = "查询配置",
            order = 1,
            required = false,
            enName = "nodeUrls",
            cnName = "FE 地址",
            description = "FE 地址",
            placeHolder = "请输入FE 地址：fe_host:fe_http_port",
            formType = StField.FormType.TEXT
    )
    private String nodeUrls;

    @StField(
            tag = "查询配置",
            order = 2,
            required = false,
            enName = "scan_filter",
            cnName = "查询条件",
            description = "scan_filter",
            placeHolder = "请输入scan_filter",
            formType = StField.FormType.TEXT
    )
    private String scan_filter;

    @StField(
            tag = "性能配置",
            order = 1,
            required = false,
            enName = "request_tablet_size",
            cnName = "请求tablet大小",
            defaultValue = "65535",
            description = "请求tablet大小",
            placeHolder = "请求tablet大小",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer request_tablet_size;

    @StField(
            tag = "性能配置",
            order = 2,
            required = false,
            enName = "scan_connect_timeout_ms",
            cnName = "连接超时时间（毫秒）",
            defaultValue = "30000",
            description = "连接超时时间（毫秒）",
            placeHolder = "连接超时时间（毫秒）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scan_connect_timeout_ms;

    @StField(
            tag = "性能配置",
            order = 3,
            required = false,
            enName = "scan_query_timeout_sec",
            cnName = "查询超时时间（秒）",
            defaultValue = "3600",
            description = "查询超时时间（秒）",
            placeHolder = "查询超时时间（秒）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scan_query_timeout_sec;

    @StField(
            tag = "性能配置",
            order = 4,
            required = false,
            enName = "scan_keep_alive_min",
            cnName = "最小保持连接时间（分钟）",
            defaultValue = "10",
            description = "最小保持连接时间（分钟）",
            placeHolder = "最小保持连接时间（分钟）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scan_keep_alive_min;

    @StField(
            tag = "性能配置",
            order = 5,
            required = false,
            enName = "scan_batch_rows",
            cnName = "批处理大小",
            defaultValue = "1024",
            description = "批处理大小",
            placeHolder = "批处理大小",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scan_batch_rows;

    @StField(
            tag = "性能配置",
            order = 6,
            required = false,
            enName = "scan_mem_limit",
            cnName = "内存大小（GB）",
            defaultValue = "2",
            description = "内存大小（GB）",
            placeHolder = "内存大小（GB）",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer scan_mem_limit;

    @StField(
            tag = "性能配置",
            order = 7,
            required = false,
            enName = "max_retries",
            cnName = "最大重试次数",
            defaultValue = "3",
            description = "最大重试次数",
            placeHolder = "最大重试次数",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer max_retries;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        if (datasourceId != null) {
            putIfNotEmpty(config, "fenodes", connectionConfig.getString("fenodes"));
            putIfNotEmpty(config, "query-port", connectionConfig.getString("port"));
            putIfNotEmpty(config, "database", connectionConfig.getString("database"));
            putIfNotEmpty(config, "user", connectionConfig.getString("username"));
            putIfNotEmpty(config, "password", connectionConfig.getString("password"));
        }
        putIfNotEmpty(config, "nodeUrls", connectionConfig.getString("nodeUrls"));
        putIfNotEmpty(config, "scan_filter", connectionConfig.getString("scan_filter"));
        putIfNotEmpty(config, "request_tablet_size", connectionConfig.getLong("request_tablet_size"));
        putIfNotEmpty(config, "scan_connect_timeout_ms", connectionConfig.getLong("scan_connect_timeout_ms"));
        putIfNotEmpty(config, "scan_query_timeout_sec", connectionConfig.getLong("scan_query_timeout_sec"));
        putIfNotEmpty(config, "scan_keep_alive_min", connectionConfig.getLong("scan_keep_alive_min"));
        putIfNotEmpty(config, "scan_batch_rows", connectionConfig.getLong("scan_batch_rows"));
        putIfNotEmpty(config, "scan_mem_limit", connectionConfig.getLong("scan_mem_limit") * 1024 * 1024 * 1024);
        putIfNotEmpty(config, "max_retries", config.getLong("max_retries"));

        JSONArray table_list = new JSONArray();
        JSONObject outputModel = connectionConfig.getJSONObject("outputModel");
        JSONObject tableFields = outputModel.getJSONObject("tableFields");
        for (Map.Entry<String, Object> entry : tableFields.entrySet()) {
            String tableName = entry.getKey();
            JSONArray columns = (JSONArray) entry.getValue();
            JSONObject schema = new JSONObject();
            JSONObject fields = new JSONObject();
            for (Object column : columns) {
                fields.put(column.toString(), "STRING");
            }
            schema.put("fields", fields);
            JSONObject table = new JSONObject();
            table.put("table", tableName);
            table.put("schema", schema);
            table_list.add(table);
        }
        config.put("table_list", table_list);
        return config;
    }
}
