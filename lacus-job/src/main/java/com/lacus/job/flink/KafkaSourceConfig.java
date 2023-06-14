package com.lacus.job.flink;

import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.Properties;

public class KafkaSourceConfig {

    private String bootstrapServer;
    private List<String> topics;
    private String groupId;
    private KafkaDeserializationSchema<ConsumerRecord<String, String>> valueSerialize;
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


    public KafkaSourceConfig valueSerialize(KafkaDeserializationSchema<ConsumerRecord<String, String>> valueSerialize) {
        this.valueSerialize = (valueSerialize == null) ? new DataFormatDeSerializer() : valueSerialize;
        return this;
    }


    public KafkaSourceConfig offsetsInitializer(OffsetsInitializer offsetsInitializer) {
        this.offsetsInitializer = (offsetsInitializer == null) ? OffsetsInitializer.latest() : offsetsInitializer;
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
                .setDeserializer(KafkaRecordDeserializationSchema.of(valueSerialize))
                .setProperties(conf).build();

    }


}
