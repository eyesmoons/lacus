package com.lacus.reader.impl;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import com.lacus.model.JobConf;
import com.lacus.model.Source;
import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.base.source.jdbc.JdbcIncrementalSource;
import com.ververica.cdc.connectors.oracle.source.OracleSourceBuilder;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.ORACLE_READER;

/**
 * oracle采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseReader.class)
public class OracleReader extends BaseReader {

    public OracleReader() {
        super(ORACLE_READER);
    }

    @Override
    public DataStreamSource<String> read(StreamExecutionEnvironment env, String jobName, String jobParams) {
        JobConf jobConf = JSON.parseObject(jobParams, JobConf.class);
        Source source = jobConf.getSource();
        List<String> databaseList = source.getDatabaseList();
        List<String> tableList = source.getTableList();
        StartupOptions startupOptions = getStartupOptions(source.getSyncType(), source.getTimeStamp());
        JdbcIncrementalSource<String> oracleSource =
                new OracleSourceBuilder<String>()
                        .hostname(source.getHostname())
                        .port(Integer.parseInt(source.getPort()))
                        .databaseList(databaseList.toArray(new String[0]))
                        .schemaList(databaseList.toArray(new String[0]))
                        .tableList(tableList.toArray(new String[0]))
                        .username(source.getUsername())
                        .password(source.getPassword())
                        .deserializer(new JsonDebeziumDeserializationSchema())
                        .includeSchemaChanges(true)
                        .startupOptions(startupOptions)
                        .debeziumProperties(getDebeziumProperties())
                        .splitSize(2)
                        .build();
        return env.fromSource(oracleSource, WatermarkStrategy.noWatermarks(), ORACLE_READER + "_source");
    }
}