package com.lacus.utils;

import com.lacus.common.utils.PropertiesUtil;
import com.lacus.deserialization.ConsumerRecordDeserializationSchema;
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

import java.util.List;
import java.util.Properties;

/**
 * @author shengyu
 * @date 2024/4/30 14:46
 */
public class KafkaUtil {
    /**
     * kafka source
     *
     * @param bootStrapServers bootStrapServers
     * @param topics           topics
     * @param groupId          groupId
     */
    public static KafkaSource<ConsumerRecord<String, String>> getKafkaSource(String bootStrapServers, List<String> topics, String groupId) {
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
    public static KafkaSink<String> getKafkaSink(String bootStrapServers, List<String> topics) {
        return KafkaSink.<String>builder()
                .setBootstrapServers(bootStrapServers)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(convertTopics(bootStrapServers, topics))
                        .setValueSerializationSchema(new SimpleStringSchema()).build())
                .setKafkaProducerConfig(buildCommonKafkaProducerProps())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    public static String convertTopics(String bootStrapServers, List<String> topics) {
        if (ObjectUtils.isEmpty(topics)) {
            return null;
        }
        // 创建topic
        com.lacus.common.utils.KafkaUtil.createTopic(bootStrapServers, topics, 1, (short) 1);
        return String.join(",", topics);
    }

    /**
     * 构建kafka生产者参数
     */
    public static Properties buildKafkaProducerProps(String bootStrapServers) {
        Properties properties = buildCommonKafkaProducerProps();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
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

    /**
     * 构建kafka 消费者参数
     */
    public static Properties buildKafkaConsumerPros(String bootstrapServers, String groupId) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }
}