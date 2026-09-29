package com.lacus.utils.kafka;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DeleteConsumerGroupsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class KafkaUtil {

    public static Map<String,Long> kafkaMessageLag(Map<String,Object> consumeProps,List<String> topics){
        Map<String,Long> topicLag = new HashMap<>();
        for(String topic : topics){
            Map<String, Long> stringLongMap = kafkaMessageLag(consumeProps, topic);
            topicLag.putAll(stringLongMap);
        }
       return topicLag;
    }

    public static Map<String,Long> kafkaMessageLag(Map<String,Object> consumeProps,String topic){
        Map<String,Long> topicLag = new HashMap<>();
        try{
            Map<Integer, Long> endOffsetMap = new HashMap<Integer, Long>();
            Map<Integer, Long> commitOffsetMap = new HashMap<Integer, Long>();

            System.out.println("consumer properties:" + consumeProps);
            //查询topic partitions
            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumeProps);
            List<TopicPartition> topicPartitions = new ArrayList<TopicPartition>();
            List<PartitionInfo> partitionsFor = consumer.partitionsFor(topic, Duration.ofSeconds(5L));
            for (PartitionInfo partitionInfo : partitionsFor) {
                TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                topicPartitions.add(topicPartition);
            }

            //查询log size
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
            for (TopicPartition partitionInfo : endOffsets.keySet()) {
                endOffsetMap.put(partitionInfo.partition(), endOffsets.get(partitionInfo));
            }
            for (Integer partitionId : endOffsetMap.keySet()) {
                log.info(String.format("at %s, topic:%s, partition:%s, logSize:%s", System.currentTimeMillis(), topic, partitionId, endOffsetMap.get(partitionId)));
            }

            //查询消费offset
            for (TopicPartition topicAndPartition : topicPartitions) {
                OffsetAndMetadata committed = consumer.committed(topicAndPartition);
                if(committed != null){
                    commitOffsetMap.put(topicAndPartition.partition(), committed.offset());
                }
            }

            //累加lag
            long lagSum = 0L;
            if (endOffsetMap.size() == commitOffsetMap.size()) {
                for (Integer partition : endOffsetMap.keySet()) {
                    long endOffSet = endOffsetMap.get(partition);
                    long commitOffSet = commitOffsetMap.get(partition);
                    long diffOffset = endOffSet - commitOffSet;
                    lagSum += diffOffset;
                    log.info("Topic:" + topic + ", groupID:" + consumeProps.get("group.id") + ", partition:" + partition + ", endOffset:" + endOffSet + ", commitOffset:" + commitOffSet + ", diffOffset:" + diffOffset);
                }
                log.info("Topic:" + topic + ", groupID:" + consumeProps.get("group.id") + ", LAG:" + lagSum);
                topicLag.put(topic,lagSum);
            } else {
                log.info("this topic partitions lost");
            }

            consumer.close();
            return topicLag;
        }catch (Exception ex){
            log.error("kafka连接失败:",ex);
            throw new KafkaException("Kafka连接失败");
        }

    }


    public static void deleteConsumeGroup(String bootstrapServers,String groupId){
        Properties props =  new Properties();
        props.put("bootstrap.servers",bootstrapServers);
        KafkaAdminClient kafkaAdminClient = (KafkaAdminClient) AdminClient.create(props);
       kafkaAdminClient.deleteConsumerGroups(Lists.newArrayList(groupId));
    }

    public static void main(String[] args) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("bootstrap.servers","hadoop1:9092,hadoop2:9092,hadoop3:9092");
        map.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        map.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        map.put("group.id","group_1");
        kafkaMessageLag(map,"test1233");
    }
}
