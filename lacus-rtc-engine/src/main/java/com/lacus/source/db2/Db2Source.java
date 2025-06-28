package com.lacus.source.db2;

import com.google.auto.service.AutoService;
import com.lacus.model.JobConf;
import com.lacus.model.SourceConfig;
import com.lacus.source.BaseSource;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.cdc.connectors.base.options.StartupOptions;
import org.apache.flink.cdc.connectors.db2.source.Db2SourceBuilder;
import org.apache.flink.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.DB2_SOURCE;

/**
 * @author shengyu
 * @date 2025/6/27 16:23
 */
@AutoService(BaseSource.class)
public class Db2Source extends BaseSource {
    private static final long serialVersionUID = 1L;

    public Db2Source() {
        super(DB2_SOURCE);
    }

    @Override
    public Source<String, ?, ?> getSource(StreamExecutionEnvironment env, String jobName, JobConf jobConf) {
        SourceConfig sourceConfig = jobConf.getSource();
        List<String> databaseList = sourceConfig.getDatabaseList();
        List<String> tableList = sourceConfig.getTableList();
        StartupOptions startupOptions = getDB2StartupOptions(sourceConfig.getSyncType(), sourceConfig.getTimeStamp());
        return new Db2SourceBuilder<String>()
                .hostname(sourceConfig.getHostname())
                .port(Integer.parseInt(sourceConfig.getPort()))
                .username(sourceConfig.getUsername())
                .password(sourceConfig.getPassword())
                // 设置捕获的数据库
                .databaseList(databaseList.toArray(new String[0]))
                // 设置捕获的表 [db.table]
                .tableList(tableList.toArray(new String[0]))
                // 启动模式
                .startupOptions(startupOptions)
                // 设置时间格式
                .debeziumProperties(getDebeziumProperties())
                // 自定义反序列化
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();
    }

    private StartupOptions getDB2StartupOptions(String syncType, Long timeStamp) {
        StartupOptions startupOptions = null;
        switch (syncType) {
            case "initial":
                startupOptions = StartupOptions.initial();
                break;
            case "earliest":
                startupOptions = StartupOptions.earliest();
                break;
            case "latest":
                startupOptions = StartupOptions.latest();
                break;
            case "timestamp":
                startupOptions = StartupOptions.timestamp(timeStamp);
                break;
        }
        return startupOptions;
    }

    @Override
    public String transform(String input) {
        return input;
    }
}
