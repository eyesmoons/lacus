package com.lacus.job.flink.deserialization;

import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

public class CustomerDeserializationSchemaMysql implements DebeziumDeserializationSchema<String> {
    private static final long serialVersionUID = 829949485716463956L;

    /**
     *{
     * "db":""
     * "tablename":"",
     *  befor:json
     *  after:json
     *  op
     *  }
     */
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
        JSONObject result = new JSONObject();
        Struct value = (Struct) sourceRecord.value();
        Struct sourceStruct = value.getStruct("source");
        String database = sourceStruct.getString("db");
        String table = sourceStruct.getString("table");
        result.put("db", database);
        result.putIfAbsent("tableName", table);

        Struct before = value.getStruct("before");
        JSONObject beforeJson = new JSONObject();

        if (before != null) {
            Schema schema = before.schema();
            List<Field> fieldsList = schema.fields();

            for (Field field : fieldsList) {
                beforeJson.put(field.name(), before.get(field));
            }
        }
        result.put("before", beforeJson);

        Struct after = value.getStruct("after");
        JSONObject afterJson = new JSONObject();

        if (after != null) {
            Schema schema = after.schema();
            List<Field> fieldsList = schema.fields();

            for (Field field : fieldsList) {
                afterJson.put(field.name(), after.get(field));
            }
        }
        result.put("after", afterJson);

        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        result.put("op", operation.toString());

        collector.collect(result.toString());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }
}
