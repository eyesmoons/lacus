package com.lacus.job.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.lacus.job.flink.BaseFlinkJob;
import com.lacus.job.flink.deserialization.CustomerDeserializationSchemaMysql;
import com.lacus.job.model.SourceConf;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;

import java.util.List;

/**
 * flink source端任务
 */
@Slf4j
public class SourceFlinkJob extends BaseFlinkJob {

    public SourceFlinkJob(String[] args) {super(args);}

    @Override
    public void handle() throws Throwable {
        log.info("jobName：{}", jobName);
        List<SourceConf> sourceConfList = JSON.parseArray(jobConf, SourceConf.class);
        log.info("sourceConfList：{}", JSON.toJSONString(sourceConfList));
        for (SourceConf sourceConf : sourceConfList) {
            StartupOptions syncType = StartupOptions.initial();
            syncType = getStartupOptions(sourceConf, syncType);
            DebeziumSourceFunction<String> mysqlSource = MySQLSource.<String>builder()
                    .hostname(sourceConf.getHostname())
                    .port(sourceConf.getPort())
                    .username(sourceConf.getUsername())
                    .password(sourceConf.getPassword())
                    .databaseList(sourceConf.getDatabaseList().toArray(new String[0]))
                    // 可选配置项,如果不指定该参数,则会读取上一个配置下的所有表的数据，注意：指定的时候需要使用"db.table"的方式
                    .tableList(sourceConf.getTableList().toArray(new String[0]))
                    // 指定数据读取位置：initial，latest-offset，timestamp，specific-offset
                    .startupOptions(syncType)
                    .deserializer(new CustomerDeserializationSchemaMysql())
                    .build();
            DataStreamSource<String> mysqlDS = env.addSource(mysqlSource);
            mysqlDS.addSink(kafkaSink(sourceConf.getBootStrapServer(),sourceConf.getTopic()));
        }
        env.execute(jobName);
    }

    private static StartupOptions getStartupOptions(SourceConf sourceConf, StartupOptions syncType) {
        switch (sourceConf.getSyncType()) {
            case "initial":
                syncType = StartupOptions.initial();
                break;
            case "earliest":
                syncType = StartupOptions.earliest();
                break;
            case "latest":
                syncType = StartupOptions.latest();
                break;
            case "timestamp":
                syncType = StartupOptions.timestamp(sourceConf.getTimeStamp());
                break;
        }
        return syncType;
    }

    public static void main(String[] args) {
        new SourceFlinkJob(args).run();
    }
}
