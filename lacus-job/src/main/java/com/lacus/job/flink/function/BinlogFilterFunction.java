package com.lacus.job.flink.function;

import com.alibaba.fastjson2.JSON;
import com.lacus.job.model.MySqlBinLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import static com.lacus.job.constants.Constant.OPERATION_TYPES;

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
            log.error("过滤掉不合法的数据：{}", e.getMessage());
            return false;
        }
        return false;
    }
}