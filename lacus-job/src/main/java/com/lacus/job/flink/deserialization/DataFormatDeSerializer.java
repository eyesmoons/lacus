package com.lacus.job.flink.deserialization;

import com.lacus.job.utils.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;


public class DataFormatDeSerializer implements KafkaDeserializationSchema<ConsumerRecord<String, String>> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isEndOfStream(ConsumerRecord<String, String> record) {
        return false;
    }

    @Override
    public ConsumerRecord<String, String> deserialize(ConsumerRecord<byte[], byte[]> record) {
        if (StringUtils.checkValNull(record)) {
            return null;
        }
        String originValue = new String(record.value(), StandardCharsets.UTF_8);
        if (StringUtils.checkValNull(originValue)) {
            return null;
        }
        return new ConsumerRecord(
                record.topic(),
                record.partition(),
                record.offset(),
                record.timestamp(),
                record.timestampType(),
                record.serializedKeySize(),
                record.serializedValueSize(),
                record.key(),
                originValue,
                record.headers(),
                record.leaderEpoch());
    }

    @Override
    public TypeInformation<ConsumerRecord<String, String>> getProducedType() {
        return TypeInformation.of(new TypeHint<ConsumerRecord<String, String>>() {
        });
    }





}
