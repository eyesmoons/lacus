package com.lacus.job.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class KafkaUtil {

    /**
     * 私有静态方法，创建Kafka生产者
     */
    private static KafkaProducer<String, String> createProducer(Properties properties) {
        if (ObjectUtils.isEmpty(properties)) {
            throw new RuntimeException("Kafka配置信息错误");
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
            throw new RuntimeException("发送Kafka失败");
        }
    }

    /**
     * 异步发送消息到kafka
     */
    public static void sendMessageAsync(String topicName, String jsonMessage, Properties properties) {
        try (KafkaProducer<String, String> producer = createProducer(properties)) {
            producer.send(new ProducerRecord<>(topicName, jsonMessage), (recordMetadata, e) -> {
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
            if (topicList.containsAll(topics)) {
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
}
