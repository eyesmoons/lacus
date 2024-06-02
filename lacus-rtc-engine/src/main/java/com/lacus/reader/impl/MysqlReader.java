package com.lacus.reader.impl;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import com.lacus.function.CustomerDeserializationSchemaMysql;
import com.lacus.model.JobConf;
import com.lacus.model.Source;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

import static com.lacus.constant.ConnectorContext.MYSQL_READER;

/**
 * mysql采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(BaseReader.class)
public class MysqlReader extends BaseReader {

    public MysqlReader() {
        super(MYSQL_READER);
    }

    @Override
    public DataStreamSource<String> read(StreamExecutionEnvironment env, String jobName, String jobParams) {
        JobConf jobConf = JSON.parseObject(jobParams, JobConf.class);
        Source source = jobConf.getSource();
        List<String> databaseList = source.getDatabaseList();
        List<String> tableList = source.getTableList();
        StartupOptions startupOptions = getMysqlStartupOptions(source.getSyncType(), source.getTimeStamp());
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
                .hostname(source.getHostname())
                .port(Integer.parseInt(source.getPort()))
                .username(source.getUsername())
                .password(source.getPassword())
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
        // read from mysql binlog
        return env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), MYSQL_READER + "_source");
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