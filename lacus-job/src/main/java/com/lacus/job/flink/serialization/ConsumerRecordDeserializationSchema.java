package com.lacus.job.flink.serialization;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.UnsupportedEncodingException;

public class ConsumerRecordDeserializationSchema implements KafkaDeserializationSchema<ConsumerRecord<String, String>> {
    private static final long serialVersionUID = 1644620987261400895L;

    private static final String encoding = "UTF8";

    @Override
    public boolean isEndOfStream(ConsumerRecord<String, String> stringStringConsumerRecord) {
        return false;
    }

    @Override
    public ConsumerRecord<String, String> deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
        return new ConsumerRecord<>(
                record.topic(),
                record.partition(),
                record.offset(),
                record.timestamp(),
                record.timestampType(),
                record.serializedKeySize(),
                record.serializedValueSize(),
                getByteString(record.key()),
                getByteString(record.value()),
                record.headers(),
                record.leaderEpoch());
    }

    @Override
    public TypeInformation<ConsumerRecord<String, String>> getProducedType() {
        return TypeInformation.of(new TypeHint<ConsumerRecord<String, String>>(){});
    }

    private String getByteString(byte[] byteArray) throws UnsupportedEncodingException {
        if (byteArray == null) {
            return null;
        }
        return new String(byteArray,encoding);
    }
}
