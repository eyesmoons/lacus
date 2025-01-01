package com.lacus.function;

import com.alibaba.fastjson2.JSON;
import com.lacus.model.MySqlBinLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static com.lacus.constant.CommonContext.OPERATION_TYPES;

/**
 * @created by shengyu on 2023/9/7 16:33
 */
@Slf4j
public class BinlogFilterFunction implements FilterFunction<ConsumerRecord<String, String>> {

    private static final long serialVersionUID = 6617278623863706553L;

    @Override
    public boolean filter(ConsumerRecord<String, String> record) {
        try {
            String value = record.value();
            MySqlBinLog mySqlBinLog = JSON.parseObject(value, MySqlBinLog.class);
            if (OPERATION_TYPES.contains(mySqlBinLog.getOp())) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}