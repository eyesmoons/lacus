package com.lacus.job.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.lacus.job.enums.SinkEnums;
import com.lacus.job.flink.BaseFlinkJob;
import com.lacus.job.flink.TaskTrigger;
import com.lacus.job.flink.deserialization.CustomerDeserializationSchemaMysql;
import com.lacus.job.flink.function.BinlogFilterFunction;
import com.lacus.job.flink.function.BinlogMapFunction;
import com.lacus.job.flink.function.BinlogProcessWindowFunction;
import com.lacus.job.flink.serialization.ConsumerRecordDeserializationSchema;
import com.lacus.job.flink.warehouse.DorisExecutorSink;
import com.lacus.job.model.*;
import com.lacus.job.utils.KafkaUtil;
import com.lacus.job.utils.PropertiesUtil;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.*;

/**
 * 数据同步任务
 */
@Slf4j
public class DataSyncJob extends BaseFlinkJob {
    private static final long serialVersionUID = -5029825537386780174L;

    public DataSyncJob(String[] args) {
        super(args);
    }

    @Override
    public void handle() throws Throwable {
        log.info("jobName：{}", jobName);
        List<DataSyncJobConf> dataSyncJobConfList = JSON.parseArray(jobConf, DataSyncJobConf.class);
        log.info("任务配置：{}", JSON.toJSONString(dataSyncJobConfList));
        for (DataSyncJobConf dataSyncJobConf : dataSyncJobConfList) {
            FlinkJobSource source = dataSyncJobConf.getSource();
            FlinkConf flinkConf = dataSyncJobConf.getFlinkConf();
            FlinkTaskSink sink = dataSyncJobConf.getSink();
            FlinkTaskEngine engine = sink.getEngine();
            String sinkType = sink.getSinkType();
            String subJobName = flinkConf.getJobName();
            String bootStrapServers = source.getBootStrapServers();
            String topic = source.getTopic();
            KafkaUtil.createTopic(bootStrapServers, Collections.singletonList(topic), 1, (short) 1);
            StartupOptions startupOptions = getStartupOptions(source.getSyncType(), source.getTimeStamp());
            MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                    .hostname(source.getHostname())
                    .port(source.getPort())
                    .username(source.getUsername())
                    .password(source.getPassword())
                    .databaseList(source.getDatabaseList().toArray(new String[0]))
                    // 可选配置项,如果不指定该参数,则会读取上一个配置下的所有表的数据，注意：指定的时候需要使用"db.table"的方式
                    .tableList(source.getTableList().toArray(new String[0]))
                    // 指定数据读取位置：initial，latest-offset，timestamp，specific-offset
                    .startupOptions(startupOptions)
                    // 设置时间格式
                    .debeziumProperties(getDebeziumProperties())
                    // 自定义序列化
                    .deserializer(new CustomerDeserializationSchemaMysql())
                    .build();
            SingleOutputStreamOperator<String> mysqlDS = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), subJobName);
            // 发往kafka
            KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                    .setBootstrapServers(bootStrapServers)
                    .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                            .setTopic(topic)
                            .setValueSerializationSchema(new SimpleStringSchema()).build())
                    .setKafkaProducerConfig(buildCommonKafkaProducerProps())
                    .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                    .build();
            mysqlDS.sinkTo(kafkaSink).name(jobName + "_kafka_sink");

            // kafka 到 doris
            KafkaSource<ConsumerRecord<String, String>> kafkaSource = KafkaSource.<ConsumerRecord<String, String>>builder()
                    // 设置bootstrapServers
                    .setBootstrapServers(bootStrapServers)
                    // 设置topics
                    .setTopics(Collections.singletonList(topic))
                    // 设置groupId
                    .setGroupId(source.getGroupId())
                    // 设置从最新消息消费
                    .setStartingOffsets(OffsetsInitializer.latest())
                    // 自定义反序列化
                    .setDeserializer(KafkaRecordDeserializationSchema.of(new ConsumerRecordDeserializationSchema()))
                    .build();
            DataStreamSource<ConsumerRecord<String, String>> kafkaSourceDs = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), subJobName + "_kafka_source");
            SingleOutputStreamOperator<ConsumerRecord<String, String>> filterDs = kafkaSourceDs.filter(new BinlogFilterFunction());
            SingleOutputStreamOperator<Map<String, String>> mapDs = filterDs.map(new BinlogMapFunction());
            SingleOutputStreamOperator<Map<String, String>> triggerDs = mapDs
                    .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(flinkConf.getMaxBatchInterval())))
                    .trigger(new TaskTrigger<>(flinkConf.getMaxBatchSize(), flinkConf.getMaxBatchRows()))
                    .apply(new BinlogProcessWindowFunction()).name(jobName + "_kafka_trigger");
            SinkEnums sinkEnum = SinkEnums.getSinkEnums(sinkType);
            if (sinkEnum == null) {
                log.debug("Flink sink executor type not found");
                return;
            }
            log.info("Initialize flink sink executor type : {} ", sinkEnum);
            switch (sinkEnum) {
                case DORIS:
                    triggerDs.addSink(new DorisExecutorSink(engine));
                    break;
                case PRESTO:
                case CLICKHOUSE:
                    break;
            }
        }
        env.execute(jobName);
    }

    /**
     * 设置启动方式
     * @param syncType 同步类型
     * @param timeStamp 时间戳
     */
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

    /**
     * 设置时间格式化
     */
    private static Properties getDebeziumProperties() {
        Properties properties = new Properties();
        properties.setProperty("converters", "dateConverters");
        //根据类在那个包下面修改
        properties.setProperty("dateConverters.type", "com.lacus.job.flink.function.MySqlDateTimeConverter");
        properties.setProperty("dateConverters.format.date", "yyyy-MM-dd");
        properties.setProperty("dateConverters.format.time", "HH:mm:ss");
        properties.setProperty("dateConverters.format.datetime", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("dateConverters.format.timestamp.zone", "UTC+8");
        // 全局读写锁，可能会影响在线业务，跳过锁设置
        properties.setProperty("debezium.snapshot.locking.mode", "none");
        properties.setProperty("include.schema.changes", "true");
        properties.setProperty("bigint.unsigned.handling.mode", "long");
        properties.setProperty("decimal.handling.mode", "double");
        return properties;
    }

    /**
     * 构建kafka公共生产者参数
     */
    public static Properties buildCommonKafkaProducerProps() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        properties.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG));
        properties.put(ProducerConfig.RETRIES_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.RETRIES_CONFIG));
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.MAX_REQUEST_SIZE_CONFIG));
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.BUFFER_MEMORY_CONFIG));
        return properties;
    }

    public static void main(String[] args) {
        new DataSyncJob(args).run();
    }
}
