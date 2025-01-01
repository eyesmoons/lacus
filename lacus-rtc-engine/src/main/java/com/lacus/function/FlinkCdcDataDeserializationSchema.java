package com.lacus.function;

import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

public class FlinkCdcDataDeserializationSchema implements DebeziumDeserializationSchema<String> {

    private static final long serialVersionUID = 1228633688846411139L;

    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {

        Struct valueStruct = (Struct) sourceRecord.value();
        Struct sourceStruct = valueStruct.getStruct("source");

        //获取数据库名称,表名,操作类型
        String database = sourceStruct.getString("db");
        String table = sourceStruct.getString("table");
        String type = Envelope.operationFor(sourceRecord).toString().toLowerCase();

        if (type.equals("create")) type = "insert";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("database", database);
        jsonObject.put("table", table);
        jsonObject.put("type", type);

        //格式转换
        Struct beforeStruct = valueStruct.getStruct("before");
        JSONObject beforeDataJson = new JSONObject();
        if (beforeStruct != null) {
            for (Field field : beforeStruct.schema().fields()) {
                beforeDataJson.put(field.name(), beforeStruct.get(field));
            }
        }

        Struct afterStruct = valueStruct.getStruct("after");
        JSONObject afterDataJson = new JSONObject();
        if (afterStruct != null) {
            for (Field field : afterStruct.schema().fields()) {
                afterDataJson.put(field.name(), afterStruct.get(field));
            }
        }

        jsonObject.put("beforeData", beforeDataJson);
        jsonObject.put("afterData", afterDataJson);

        //向下游传递数据
        collector.collect(jsonObject.toJSONString());

    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }
}
