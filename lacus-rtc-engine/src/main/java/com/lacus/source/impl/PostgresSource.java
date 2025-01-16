package com.lacus.source.impl;

import com.google.auto.service.AutoService;
import com.lacus.function.CustomerDeserializationSchemaMysql;
import com.lacus.model.JobConf;
import com.lacus.model.SourceConfig;
import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.postgres.source.PostgresSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.POSTGRES_READER;

/**
 * pg采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseSource.class)
public class PostgresSource extends BaseSource {

    public PostgresSource() {
        super(POSTGRES_READER);
    }

    @Override
    public Source<String, ?, ?> getSource(StreamExecutionEnvironment env, String jobName, JobConf jobConf) {
        SourceConfig sourceConfig = jobConf.getSource();
        List<String> databaseList = sourceConfig.getDatabaseList();
        List<String> tableList = sourceConfig.getTableList();
        StartupOptions startupOptions = getStartupOptions(sourceConfig.getSyncType(), sourceConfig.getTimeStamp());
        return PostgresSourceBuilder.PostgresIncrementalSource.<String>builder()
                .hostname(sourceConfig.getHostname())
                .port(Integer.parseInt(sourceConfig.getPort()))
                .username(sourceConfig.getUsername())
                .password(sourceConfig.getPassword())
                .database(databaseList.get(0))
                .schemaList(databaseList.toArray(new String[0]))
                .tableList(tableList.toArray(new String[0]))
                .slotName("flink")
                .decodingPluginName("decoderbufs") // use pgoutput for PostgreSQL 10+
                .deserializer(new CustomerDeserializationSchemaMysql())
                .includeSchemaChanges(true) // output the schema changes as well
                .splitSize(2) // the split size of each snapshot split
                .startupOptions(startupOptions)
                .build();
    }

    @Override
    public String transform(String input) {
        // TODO: 实现具体的数据转换逻辑, 将数据转换为统一的数据格式，便于source和sink组件解耦
        return input;
    }
}
