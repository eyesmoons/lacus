package com.lacus.job.flink;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.lacus.job.constants.Constant;
import com.lacus.job.enums.OperatorEnums;
import com.lacus.job.utils.DateUtils;
import com.lacus.job.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.util.CollectionUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class DataFormatDeSerializer implements KafkaDeserializationSchema<ConsumerRecord<String, String>> {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(DataFormatDeSerializer.class);


    @Override
    public boolean isEndOfStream(ConsumerRecord<String, String> record) {
        return false;
    }

    @Override
    public ConsumerRecord<String, String> deserialize(ConsumerRecord<byte[], byte[]> record) {
        if (StringUtils.checkValNull(record)) {
            return null;
        }
        String key = new String(record.key(), StandardCharsets.UTF_8);
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
                key,
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
