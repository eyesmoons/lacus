package com.lacus.source.impl;

import com.google.auto.service.AutoService;
import com.lacus.function.CustomerDeserializationSchemaMysql;
import com.lacus.model.JobConf;
import com.lacus.model.SourceConfig;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.connector.source.Source;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.MYSQL_SOURCE;

/**
 * mysql采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseSource.class)
public class MysqlSource extends BaseSource {
    private static final long serialVersionUID = 1L;
    public MysqlSource() {
        super(MYSQL_SOURCE);
    }

    @Override
    public Source<String, ?, ?> getSource(StreamExecutionEnvironment env, String jobName, JobConf jobConf) {
        SourceConfig sourceConfig = jobConf.getSource();
        List<String> databaseList = sourceConfig.getDatabaseList();
        List<String> tableList = sourceConfig.getTableList();
        StartupOptions startupOptions = getMysqlStartupOptions(sourceConfig.getSyncType(), sourceConfig.getTimeStamp());
        // read from mysql binlog
        return MySqlSource.<String>builder()
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
                .deserializer(new CustomerDeserializationSchemaMysql())
                .build();
    }

    @Override
    public String transform(String input) {
        // TODO: 实现具体的数据转换逻辑, 将数据转换为统一的数据格式，便于source和sink组件解耦
        return input;
    }

    protected StartupOptions getMysqlStartupOptions(String syncType, Long timeStamp) {
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
}
