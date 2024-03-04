package com.lacus.function;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.model.MySqlBinLog;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.lacus.common.constant.Constants.*;

/**
 * @created by shengyu on 2023/9/7 16:24
 */
public class BinlogMapFunction implements MapFunction<ConsumerRecord<String, String>, Map<String, String>> {

    private static final long serialVersionUID = -1452603066461684174L;

    public BinlogMapFunction() {
    }

    @Override
    public Map<String, String> map(ConsumerRecord<String, String> record) throws Exception {
        String value = record.value();
        MySqlBinLog mySqlBinLog = JSON.parseObject(value, MySqlBinLog.class);
        String op = mySqlBinLog.getOp();
        String db = mySqlBinLog.getDb();
        String tableName = mySqlBinLog.getTableName();
        String dbTable = db + "." + tableName;
        JSONObject after = mySqlBinLog.getAfter();
        JSONObject before = mySqlBinLog.getBefore();
        JSONObject data = Objects.equals(OPERATION_DELETE, op) ? before : after;
        if (!data.containsKey(DELETE_KEY)) {
            // 设置删除标识
            if (OPERATION_DELETE.equals(op)) {
                data.put(DELETE_KEY, DELETE_VALUE_DELETED);
            } else {
                data.put(DELETE_KEY, DELETE_VALUE_NORMAL);
            }
        }

        // 设置落库时间
        if (!data.containsKey(UPDATE_STAMP_KEY)) {
            data.put(UPDATE_STAMP_KEY, new Date());
        }
        Map<String, String> dataMap = new HashMap<>();
        JSONArray dataArr = new JSONArray();
        dataArr.add(data);
        dataMap.put(dbTable, JSON.toJSONString(dataArr));
        return dataMap;
    }
}