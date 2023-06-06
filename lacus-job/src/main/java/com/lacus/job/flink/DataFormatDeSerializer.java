package com.lacus.job.flink;

import com.alibaba.fastjson2.JSONObject;

import com.lacus.job.enums.OperatorEnums;

import com.lacus.job.constants.Constant;
import com.lacus.job.utils.DateUtils;
import com.lacus.job.utils.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;


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
        JSONObject formatData = loadFormat(originValue);

        if (StringUtils.checkValNull(formatData)) {
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
                formatData,
                record.headers(),
                record.leaderEpoch());
    }

    @Override
    public TypeInformation<ConsumerRecord<String, String>> getProducedType() {
        return TypeInformation.of(new TypeHint<ConsumerRecord<String, String>>() {
        });
    }


    private JSONObject loadFormat(String originData) {
        if (StringUtils.checkValNull(originData)) {
            return null;
        }
        JSONObject jData = JSONObject.parseObject(originData);
        String op = jData.getString(Constant.OP);
        OperatorEnums opEnums = OperatorEnums.getOpEnums(op);
        JSONObject formatData = null;
        if (StringUtils.checkValNull(opEnums)) {
            return null;
        }
        switch (opEnums) {
            case INSERT_OP:
            case UPDATE_OP:
            case ROW_OP:
                formatData = jData.getJSONObject(Constant.AFTER);
                formatData.put(Constant.IS_DELETE_FILED, Constant.DELETE_FALSE);
                break;
            case DELETE_OP:
                formatData = jData.getJSONObject(Constant.BEFORE);
                formatData.put(Constant.IS_DELETE_FILED, Constant.DELETE_TRUE);
                break;
            default:
                break;
        }
        if (StringUtils.checkValNotNull(formatData)) {
            formatData.put(Constant.UPDATE_STAMP_FILED, DateUtils.getCurrentTime());
        }
        return formatData;
    }


}
