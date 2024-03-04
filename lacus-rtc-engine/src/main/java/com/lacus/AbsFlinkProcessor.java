package com.lacus;

import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Maps;
import com.lacus.common.utils.DorisUtil;
import com.lacus.common.utils.KafkaUtil;
import com.lacus.common.utils.PropertiesUtil;
import com.lacus.deserialization.ConsumerRecordDeserializationSchema;
import com.lacus.handler.FailExecutionHandler;
import com.lacus.handler.KafkaFailExecutionHandler;
import com.lacus.model.Sink;
import com.lacus.model.SinkDataSource;
import com.lacus.model.StreamLoadProperty;
import com.lacus.sink.DorisStreamLoad;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.*;
import java.util.stream.Collectors;

import static com.lacus.common.constant.Constants.*;

/**
 * flink任务抽象处理器，定义flink任务公共操作，所有自定义的采集组件必须继承此类
 *
 * @created by shengyu on 2023/8/31 10:55
 */
public abstract class AbsFlinkProcessor implements IFlinkProcessor {

    // 采集组件名称，比如：collect-mysql
    protected String name;

    public AbsFlinkProcessor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * kafka source
     *
     * @param bootStrapServers bootStrapServers
     * @param topics           topics
     * @param groupId          groupId
     */
    protected KafkaSource<ConsumerRecord<String, String>> getKafkaSource(String bootStrapServers, List<String> topics, String groupId) {
        return KafkaSource.<ConsumerRecord<String, String>>builder()
                // 设置bootstrapServers
                .setBootstrapServers(bootStrapServers)
                // 设置topics
                .setTopics(topics)
                // 设置groupId
                .setGroupId(groupId)
                // 设置从最新消息消费
                .setStartingOffsets(OffsetsInitializer.latest())
                // 自定义反序列化
                .setDeserializer(KafkaRecordDeserializationSchema.of(new ConsumerRecordDeserializationSchema()))
                .build();
    }

    /**
     * kafka sink
     *
     * @param bootStrapServers bootStrapServers
     * @param topics           topics
     */
    protected KafkaSink<String> getKafkaSink(String bootStrapServers, List<String> topics) {
        return KafkaSink.<String>builder()
                .setBootstrapServers(bootStrapServers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(convertTopics(bootStrapServers, topics))
                        .setValueSerializationSchema(new SimpleStringSchema()).build())
                .setKafkaProducerConfig(buildCommonKafkaProducerProps())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    protected String convertTopics(String bootStrapServers, List<String> topics) {
        if (ObjectUtils.isEmpty(topics)) {
            return null;
        }
        // 创建topic
        KafkaUtil.createTopic(bootStrapServers, topics, 1, (short) 1);
        return String.join(",", topics);
    }

    /**
     * 构建kafka生产者参数
     */
    protected Properties buildKafkaProducerProps(String bootStrapServers) {
        Properties properties = buildCommonKafkaProducerProps();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        return properties;
    }

    /**
     * 构建kafka公共生产者参数
     */
    protected Properties buildCommonKafkaProducerProps() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        properties.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG));
        properties.put(ProducerConfig.RETRIES_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.RETRIES_CONFIG));
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.MAX_REQUEST_SIZE_CONFIG));
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, PropertiesUtil.getPropValue(ProducerConfig.BUFFER_MEMORY_CONFIG));
        return properties;
    }

    /**
     * 构建kafka 消费者参数
     */
    protected Properties buildKafkaConsumerPros(String bootstrapServers, String groupId) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }

    /**
     * 构建错误消息实例
     *
     * @param jobId            任务ID
     * @param jobName          任务名称
     * @param bootStrapServers bootStrapServers
     * @param topics           topic
     */
    protected FailExecutionHandler buildFailExecutionHandler(Long jobId, String sourceName, String sinkName, Long instanceId, String jobName, String bootStrapServers, String topics) {
        return new KafkaFailExecutionHandler(jobId, sourceName, sinkName, instanceId, topics, jobName, ERROR_TOPIC, buildKafkaProducerProps(bootStrapServers));
    }

    /**
     * 构建doris stream load参数
     *
     * @param sink sink
     */
    protected Map<String, DorisStreamLoad> buildDorisStreamConfig(Sink sink) {
        Map<String, DorisStreamLoad> dorisStreamLoadMap = Maps.newHashMap();
        SinkDataSource sinkDataSource = sink.getSinkDataSource();
        String hostPort = sinkDataSource.getIp() + ":" + sinkDataSource.getPort();
        sinkDataSource.setHostPort(hostPort);
        Map<String, Integer> beConfig = DorisUtil.getBeConfig(sinkDataSource);

        Map<String, StreamLoadProperty> streamLoadPropertyMap = sink.getStreamLoadPropertyMap();
        for (Map.Entry<String, StreamLoadProperty> entry : streamLoadPropertyMap.entrySet()) {
            StreamLoadProperty streamLoadProperty = entry.getValue();
            Map<String, String> conf = new HashMap<>();

            // 填充 columns
            conf.put(STREAM_LOAD_COLUMNS, convertColumns(streamLoadProperty.getColumns()));

            // 填充json Path
            conf.put(STREAM_LOAD_JSONPATH, convertJsonPaths(streamLoadProperty.getJsonpaths()));
            conf.put(STREAM_LOAD_FORMAT, streamLoadProperty.getFormat());
            conf.put(STREAM_LOAD_MAX_FILTER_RATIO, streamLoadProperty.getMaxFilterRatio());
            conf.put(STREAM_LOAD_STRIP_OUTER_ARRAY, streamLoadProperty.getStripOuterArray());

            DorisStreamLoad dorisStreamLoad = new DorisStreamLoad(
                    hostPort,
                    sinkDataSource.getDbName(),
                    streamLoadProperty.getSinkTable(),
                    sinkDataSource.getUserName(),
                    sinkDataSource.getPassword(),
                    conf,
                    beConfig);
            dorisStreamLoadMap.put(entry.getKey(), dorisStreamLoad);
        }
        return dorisStreamLoadMap;
    }

    /**
     * 转换json path，添加 删除标识 和 落库时间
     */
    protected String convertJsonPaths(String jsonpaths) {
        JSONArray jsonArr = JSONArray.parseArray(jsonpaths);
        if (!jsonArr.contains("$." + DELETE_KEY)) {
            jsonArr.add("$." + DELETE_KEY);
        }
        if (!jsonArr.contains("$." + UPDATE_STAMP_KEY)) {
            jsonArr.add("$." + UPDATE_STAMP_KEY);
        }
        return jsonArr.toJSONString();
    }

    protected String convertColumns(String columns) {
        List<String> columnList = Arrays.asList(columns.split(","));
        columnList = columnList.stream().map(column -> {
            String replaceColumn = column.trim().replace("`", "");
            return "`" + replaceColumn + "`";
        }).collect(Collectors.toList());
        if (!columnList.contains("`" + DELETE_KEY + "`")) {
            columns = columns + "," + "`" + DELETE_KEY + "`";
        }
        if (!columnList.contains("`" + UPDATE_STAMP_KEY + "`")) {
            columns = columns + "," + "`" + UPDATE_STAMP_KEY + "`";
        }
        return columns;
    }

    protected StartupOptions getStartupOptions(String syncType, Long timeStamp) {
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

    protected Properties getDebeziumProperties() {
        Properties properties = new Properties();
        properties.setProperty("converters", "dateConverters");
        //根据类在那个包下面修改
        properties.setProperty("dateConverters.type", "com.lacus.function.MySqlDateTimeConverter");
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
}