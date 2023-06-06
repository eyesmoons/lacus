package com.lacus.job.flink;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Properties;

public class KafkaSourceConfig {

    private String bootstrapServer;
    private List<String> topics;
    private String groupId;
    private DeserializationSchema<ConsumerRecord<String, String>> valueSerialize;
    private OffsetsInitializer offsetsInitializer;
    private Properties conf;


    public static KafkaSourceConfig builder() {
        return new KafkaSourceConfig();
    }


    public KafkaSourceConfig bootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
        return this;
    }

    public KafkaSourceConfig topics(List<String> topics) {
        this.topics = topics;
        return this;
    }

    public KafkaSourceConfig groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }


    public KafkaSourceConfig valueSerialize(DeserializationSchema<ConsumerRecord<String, String>> valueSerialize) {
        this.valueSerialize = (valueSerialize == null) ? (DeserializationSchema<ConsumerRecord<String, String>>) new DataFormatDeSerializer() : valueSerialize;
        return this;
    }


    public KafkaSourceConfig offsetsInitializer(OffsetsInitializer offsetsInitializer) {
        this.offsetsInitializer = (offsetsInitializer == null) ? OffsetsInitializer.earliest() : offsetsInitializer;
        return this;
    }

    public KafkaSourceConfig conf(Properties conf) {
        this.conf = conf;
        return this;
    }

    public KafkaSource<ConsumerRecord<String, String>> build() {
        return KafkaSource.<ConsumerRecord<String, String>>builder()
                .setTopics(topics)
                .setStartingOffsets(offsetsInitializer)
                .setGroupId(groupId)
                .setBootstrapServers(bootstrapServer)
                .setValueOnlyDeserializer(valueSerialize)
                .setProperties(conf).build();

    }


}
