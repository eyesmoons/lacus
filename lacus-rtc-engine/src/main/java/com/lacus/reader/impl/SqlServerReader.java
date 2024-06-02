package com.lacus.reader.impl;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import com.lacus.function.CustomerDeserializationSchemaMysql;
import com.lacus.model.JobConf;
import com.lacus.model.Source;
import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.sqlserver.source.SqlServerSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.SQLSERVER_READER;

/**
 * sqlserver采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseReader.class)
public class SqlServerReader extends BaseReader {

    public SqlServerReader(String name) {
        super(SQLSERVER_READER);
    }

    @Override
    public DataStreamSource<String> read(StreamExecutionEnvironment env, String jobName, String jobParams) {
        JobConf jobConf = JSON.parseObject(jobParams, JobConf.class);
        Source source = jobConf.getSource();
        List<String> databaseList = source.getDatabaseList();
        List<String> tableList = source.getTableList();
        StartupOptions startupOptions = getStartupOptions(source.getSyncType(), source.getTimeStamp());
        SqlServerSourceBuilder.SqlServerIncrementalSource<String> sqlserverSource = new SqlServerSourceBuilder<String>()
                .hostname(source.getHostname())
                .port(Integer.parseInt(source.getPort()))
                .username(source.getUsername())
                .password(source.getPassword())
                .databaseList(databaseList.toArray(new String[0]))
                .tableList(tableList.toArray(new String[0]))
                .startupOptions(startupOptions)
                .debeziumProperties(getDebeziumProperties())
                .deserializer(new CustomerDeserializationSchemaMysql())
                .build();
        return env.fromSource(sqlserverSource, WatermarkStrategy.noWatermarks(), SQLSERVER_READER + "_source");
    }
}