package com.lacus.utils;

import com.lacus.exception.CustomException;
import com.lacus.function.ConsumerRecordDeserializationSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class KafkaUtil {

    /**
     * 私有静态方法，创建Kafka生产者
     */
    private static KafkaProducer<String, byte[]> createProducerBytes(Properties properties) {
        if (ObjectUtils.isEmpty(properties)) {
            throw new CustomException("Kafka配置信息错误");
        }
        return new KafkaProducer<>((properties));
    }

    private static KafkaProducer<String, String> createProducer(Properties properties) {
        if (ObjectUtils.isEmpty(properties)) {
            throw new CustomException("Kafka配置信息错误");
        }
        return new KafkaProducer<>((properties));
    }

    /**
     * 同步发送消息到kafka
     */
    public static void sendMessageSync(String topicName, String jsonMessage, Properties properties) {
        try (KafkaProducer<String, String> producer = createProducer(properties)) {
            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topicName, jsonMessage));
            RecordMetadata recordMetadata = send.get();
            log.info("同步发送Kafka成功：topic：{}，分区：{}，偏移量：{}", topicName, recordMetadata.partition(), recordMetadata.offset());
        } catch (Exception ex) {
            log.error("同步发送Kafka失败：topic：{}，data：{}", topicName, jsonMessage, ex);
            throw new CustomException("发送Kafka失败");
        }
    }

    /**
     * 异步发送消息到kafka
     */
    public static void sendMessageAsync(String topicName, String jsonMessage, Properties properties) {
        try (KafkaProducer<String, byte[]> producer = createProducerBytes(properties)) {
            producer.send(new ProducerRecord<>(topicName, jsonMessage.getBytes(StandardCharsets.UTF_8)), (recordMetadata, e) -> {
                if (e != null) {
                    log.error("异步发送Kafka失败：topicName：{}，data：{}", topicName, jsonMessage, e);
                } else {
                    log.info("异步发送Kafka成功：topicName：{}，分区：{}，偏移量:{}", topicName, recordMetadata.partition(), recordMetadata.offset());
                }
            });
        } catch (Exception ex) {
            log.error("异步发送Kafka错误：topicName：{}，data：{}", topicName, jsonMessage, ex);
        }
    }

    /**
     * 创建topic
     */
    public static void createTopic(String bootstrapServers, List<String> topics, int numPartitions, short replicationFactor) {
        try (AdminClient adminClient = createAdminClient(bootstrapServers)) {
            List<NewTopic> newTopics = new ArrayList<>();
            for (String topic : topics) {
                newTopics.add(new NewTopic(topic, numPartitions, replicationFactor));
            }
            Set<String> topicList = listTopic(adminClient);
            if (Objects.nonNull(topicList) && topicList.containsAll(topics)) {
                log.warn("topic[{}]已存在", topics);
            } else {
                adminClient.createTopics(newTopics).all().get();
                log.info("topic[{}]创建成功", topics);
            }
        } catch (InterruptedException | ExecutionException e) {
            log.info("topic[{}]创建失败：{}", topics, e.getMessage());
        }
    }

    /**
     * topic列表
     */
    public static Set<String> listTopic(AdminClient adminClient) {
        ListTopicsResult topics = adminClient.listTopics();
        KafkaFuture<Set<String>> names = topics.names();
        Set<String> result = null;
        try {
            result = names.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.toString());
        }
        return result;
    }

    /**
     * 创建kafka客户端
     */
    public static AdminClient createAdminClient(String bootstrapServers) {
        // Configure admin client properties
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Create admin client
        return AdminClient.create(properties);
    }

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
        KafkaUtil.createTopic(bootStrapServers, topics, 1, (short) 1);
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
