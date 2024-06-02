package com.lacus.reader.impl;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import com.lacus.model.JobConf;
import com.lacus.model.Source;
import com.ververica.cdc.connectors.base.options.StartupOptions;
import com.ververica.cdc.connectors.mongodb.source.MongoDBSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.MONGODB_READER;

/**
 * mongodb采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseReader.class)
public class MongoDbReader extends BaseReader {

    public MongoDbReader() {
        super(MONGODB_READER);
    }

    @Override
    public DataStreamSource<String> read(StreamExecutionEnvironment env, String jobName, String jobParams) {
        JobConf jobConf = JSON.parseObject(jobParams, JobConf.class);
        Source source = jobConf.getSource();
        List<String> databaseList = source.getDatabaseList();
        List<String> tableList = source.getTableList();
        StartupOptions startupOptions = getStartupOptions(source.getSyncType(), source.getTimeStamp());
        MongoDBSource<String> mongoSource =
                MongoDBSource.<String>builder()
                        .hosts(source.getHostname() + ":" + source.getPort())
                        .databaseList(databaseList.toArray(new String[0])) // set captured database, support regex
                        .collectionList(tableList.toArray(new String[0])) //set captured collections, support regex
                        .username(source.getUsername())
                        .password(source.getPassword())
                        .deserializer(new JsonDebeziumDeserializationSchema())
                        .startupOptions(startupOptions)
                        .build();
        return env.fromSource(mongoSource, WatermarkStrategy.noWatermarks(), MONGODB_READER + "_source");
    }
}