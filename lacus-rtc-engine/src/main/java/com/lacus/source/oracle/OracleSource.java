package com.lacus.source.oracle;

import com.google.auto.service.AutoService;
import com.lacus.model.JobConf;
import com.lacus.model.SourceConfig;
import com.lacus.source.BaseSource;
import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.oracle.source.OracleSourceBuilder;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.ORACLE_SOURCE;

/**
 * oracle采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseSource.class)
public class OracleSource extends BaseSource {
    private static final long serialVersionUID = 1L;
    public OracleSource() {
        super(ORACLE_SOURCE);
    }

    @Override
    public Source<String, ?, ?> getSource(StreamExecutionEnvironment env, String jobName, JobConf jobConf) {
        SourceConfig sourceConfig = jobConf.getSource();
        List<String> databaseList = sourceConfig.getDatabaseList();
        List<String> tableList = sourceConfig.getTableList();
        StartupOptions startupOptions = getStartupOptions(sourceConfig.getSyncType(), sourceConfig.getTimeStamp());
        return new OracleSourceBuilder<String>()
                .hostname(sourceConfig.getHostname())
                .port(Integer.parseInt(sourceConfig.getPort()))
                .databaseList(databaseList.toArray(new String[0]))
                .schemaList(databaseList.toArray(new String[0]))
                .tableList(tableList.toArray(new String[0]))
                .username(sourceConfig.getUsername())
                .password(sourceConfig.getPassword())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .includeSchemaChanges(true)
                .startupOptions(startupOptions)
                .debeziumProperties(getDebeziumProperties())
                .splitSize(2)
                .build();
    }

    @Override
    public String transform(String input) {
        // TODO: 实现具体的数据转换逻辑, 将数据转换为统一的数据格式，便于source和sink组件解耦
        return input;
    }
}
