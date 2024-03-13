package com.lacus.processors;

import com.alibaba.fastjson2.JSON;
import com.google.auto.service.AutoService;
import com.lacus.AbsFlinkProcessor;
import com.lacus.common.utils.PasswordUtil;
import com.lacus.deserialization.CustomerDeserializationSchemaMysql;
import com.lacus.function.BinlogFilterFunction;
import com.lacus.function.BinlogMapFunction;
import com.lacus.function.BinlogProcessWindowFunction;
import com.lacus.function.ETLFunction;
import com.lacus.handler.FailExecutionHandler;
import com.lacus.model.*;
import com.lacus.sink.DorisSink;
import com.lacus.sink.DorisStreamLoad;
import com.lacus.trigger.CountAndSizeTrigger;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.lacus.common.constant.ProcessorConstants.PROCESSOR_MYSQL;

/**
 * mysql采集处理器
 *
 * @created by shengyu on 2023/8/31 11:16
 */
@Slf4j
@AutoService(AbsFlinkProcessor.class)
public class MysqlCollectProcessor extends AbsFlinkProcessor {

    public MysqlCollectProcessor() {
        super(PROCESSOR_MYSQL);
    }

    private JobConf jobConf;
    private String bootStrapServers;
    private List<String> topics;

    @Override
    public DataStreamSource<String> reader(StreamExecutionEnvironment env, String jobName, String jobParams) {
        jobConf = JSON.parseObject(jobParams, JobConf.class);
        SourceV2 source = jobConf.getSource();
        List<String> databaseList = source.getDatabaseList();
        List<String> tableList = source.getTableList();
        topics = source.getTopics();
        bootStrapServers = source.getBootStrapServers();
        StartupOptions startupOptions = getStartupOptions(source.getSyncType(), source.getTimeStamp());
        MySqlSource<String> mysqlSource = MySqlSource.<String>builder()
                .hostname(source.getHostname())
                .port(Integer.parseInt(source.getPort()))
                .username(source.getUsername())
                .password(PasswordUtil.decryptPwd(source.getPassword()))
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
        return env.fromSource(mysqlSource, WatermarkStrategy.noWatermarks(), PROCESSOR_MYSQL + "_source");
    }

    @Override
    public void transform(DataStreamSource<String> reader) {
        reader.sinkTo(getKafkaSink(bootStrapServers, topics)).name("_kafka_channel");
    }

    @Override
    public void writer(StreamExecutionEnvironment env, String jobName, String jobParams) {
        JobInfo jobInfo = jobConf.getJobInfo();
        Sink sink = jobConf.getSink();
        FlinkConf flinkConf = jobConf.getFlinkConf();
        SourceV2 source = jobConf.getSource();
        List<String> topics = source.getTopics();
        String bootStrapServers = source.getBootStrapServers();
        Long jobId = jobInfo.getJobId();
        // read from kafka
        DataStreamSource<ConsumerRecord<String, String>> kafkaSourceDs = env.fromSource(getKafkaSource(bootStrapServers, topics, source.getGroupId()), WatermarkStrategy.noWatermarks(), "kafka_source");
        DynamicETL dynamicETL = sink.getDynamicETL();
        // kafka消息过滤
        SingleOutputStreamOperator<ConsumerRecord<String, String>> filterDs = kafkaSourceDs.filter(new BinlogFilterFunction()).name( "kafka_filter");
        // kafka消息增加删除标识和落库时间
        SingleOutputStreamOperator<Map<String, String>> mapDs = filterDs.map(new BinlogMapFunction()).name("kafka_map");
        // 构建错误消息组件
        FailExecutionHandler failExecutionHandler = buildFailExecutionHandler(jobId, source.getSourceName(), sink.getSinkDataSource().getDataSourceName(), jobInfo.getInstanceId(), jobName, bootStrapServers, convertTopics(bootStrapServers, topics));
        // 构建doris stream load配置
        Map<String, DorisStreamLoad> dorisStreamLoadMap = buildDorisStreamConfig(sink);
        // 应用动态清洗代码
        if (Objects.nonNull(dynamicETL)) {
            mapDs = mapDs.map(new ETLFunction(dynamicETL, failExecutionHandler, jobId, jobInfo.getInstanceId(), convertTopics(bootStrapServers, topics), sink.getSinkDataSource().getDataSourceName(), dorisStreamLoadMap)).name(PROCESSOR_MYSQL + "_kafka_etl");
        }
        // 应用开窗函数，主要起限流作用
        SingleOutputStreamOperator<Map<String, String>> triggerDs = mapDs.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(flinkConf.getMaxBatchInterval())))
                .trigger(new CountAndSizeTrigger<>(flinkConf.getMaxBatchRows(), flinkConf.getMaxBatchSize()))
                .apply(new BinlogProcessWindowFunction()).name("kafka_trigger");
        // 将kafka中的数据写入doris
        triggerDs.addSink(new DorisSink(dorisStreamLoadMap, failExecutionHandler)).name("doris_sink");
    }
}