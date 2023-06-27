package com.lacus.job.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.lacus.job.flink.BaseFlinkJob;
import com.lacus.job.flink.deserialization.CustomerDeserializationSchemaMysql;
import com.lacus.job.model.SourceConf;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

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
            StartupOptions startupOptions = getStartupOptions(sourceConf.getSyncType(), sourceConf.getTimeStamp());
            DebeziumSourceFunction<String> mysqlSource = MySQLSource.<String>builder()
                    .hostname(sourceConf.getHostname())
                    .port(sourceConf.getPort())
                    .username(sourceConf.getUsername())
                    .password(sourceConf.getPassword())
                    .databaseList(sourceConf.getDatabaseList().toArray(new String[0]))
                    // 可选配置项,如果不指定该参数,则会读取上一个配置下的所有表的数据，注意：指定的时候需要使用"db.table"的方式
                    .tableList(sourceConf.getTableList().toArray(new String[0]))
                    // 指定数据读取位置：initial，latest-offset，timestamp，specific-offset
                    .startupOptions(startupOptions)
                    .deserializer(new CustomerDeserializationSchemaMysql())
                    .build();
            SingleOutputStreamOperator<String> mysqlDS = env.addSource(mysqlSource).name("source_" + sourceConf.getJobName());
            mysqlDS.print("data");
            mysqlDS.addSink(kafkaSink(sourceConf.getBootStrapServer(),sourceConf.getTopic())).name("sink_" + sourceConf.getJobName());
        }
        env.execute(jobName);
    }

    private static StartupOptions getStartupOptions(String syncType, Long timeStamp) {
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

    public static void main(String[] args) {
        new SourceFlinkJob(args).run();
    }
}
