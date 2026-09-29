package com.lacus.st.sink;

import com.alibaba.fastjson2.JSONObject;
import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Sink 组件
 * 属性对齐 SeaTunnel 2.3.12 Kafka Sink Options。
 * 文档：https://seatunnel.apache.org/zh-CN/docs/2.3.12/connector-v2/sink/Kafka
 *
 * @author lacus
 */
@Data
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "kafka_sink",
        displayName = "Kafka消息队列输出",
        description = "将 Rows 内容发送到 Kafka topic，支持精确一次(2pc)、CDC",
        version = "2.0.0",
        author = "lacus",
        connectorKey = "Kafka"
)
@StTag({
        @TagDefinition(name = "基础配置", displayName = "基础配置", order = 1, description = "Topic 与 Broker"),
        @TagDefinition(name = "格式配置", displayName = "格式配置", order = 2, description = "数据格式与分隔符"),
        @TagDefinition(name = "分区配置", displayName = "分区配置", order = 3, description = "分区与 key"),
        @TagDefinition(name = "语义与事务", displayName = "语义与事务", order = 4, description = "semantics、transaction_prefix"),
        @TagDefinition(name = "Protobuf", displayName = "Protobuf", order = 5, description = "format=protobuf 时生效"),
        @TagDefinition(name = "Kafka配置", displayName = "Kafka配置", order = 6, description = "kafka.config 扩展参数")
})
@AutoService(StComponentInterface.class)
public class KafkaSink extends AbstractStSink {

    @StField(tag = "基础配置", order = 1, required = true, enName = "topic", cnName = "Topic",
            description = "写入数据的 topic，支持 ${field_name} 动态 topic",
            placeHolder = "test_topic", formType = StField.FormType.TEXT)
    private String topic;

    @StField(tag = "基础配置", order = 2, required = true, enName = "bootstrap_servers", cnName = "Brokers",
            description = "Kafka brokers，逗号分隔",
            placeHolder = "localhost:9092", formType = StField.FormType.TEXT)
    private String bootstrapServers;

    @StField(tag = "格式配置", order = 1, required = false, enName = "format", cnName = "数据格式",
            defaultValue = "json",
            description = "json、text、canal-json、debezium-json、avro、protobuf、native",
            formType = StField.FormType.SINGLE_SELECT, dictType = StField.DictType.ENUM,
            dictEnum = {"json", "text", "canal-json", "debezium-json", "avro", "protobuf", "native"})
    private String format;

    @StField(tag = "格式配置", order = 2, required = false, enName = "field_delimiter", cnName = "字段分隔符",
            defaultValue = ",",
            description = "自定义格式时的字段分隔符",
            placeHolder = ",", formType = StField.FormType.TEXT)
    private String fieldDelimiter;

    @StField(tag = "分区配置", order = 1, required = false, enName = "partition_key_fields", cnName = "分区键字段",
            description = "用作消息 key 的字段，逗号分隔",
            placeHolder = "id,name", formType = StField.FormType.TEXT)
    private String partitionKeyFields;

    @StField(tag = "分区配置", order = 2, required = false, enName = "partition", cnName = "指定分区",
            description = "所有消息发送到该分区",
            placeHolder = "0", formType = StField.FormType.NUMBER)
    private Integer partition;

    @StField(tag = "分区配置", order = 3, required = false, enName = "assign_partitions", cnName = "分区分配",
            description = "按消息内容分配分区，逗号分隔，如 shoe,clothing",
            placeHolder = "shoe,clothing", formType = StField.FormType.TEXT)
    private String assignPartitions;

    @StField(tag = "语义与事务", order = 1, required = false, enName = "semantics", cnName = "语义",
            defaultValue = "NON",
            description = "EXACTLY_ONCE/AT_LEAST_ONCE/NON",
            formType = StField.FormType.SINGLE_SELECT, dictType = StField.DictType.ENUM,
            dictEnum = {"EXACTLY_ONCE", "AT_LEAST_ONCE", "NON"})
    private String semantics;

    @StField(tag = "语义与事务", order = 2, required = false, enName = "transaction_prefix", cnName = "事务ID前缀",
            description = "EXACTLY_ONCE 时 Kafka 事务 ID 前缀",
            placeHolder = "seatunnel-sink-", formType = StField.FormType.TEXT)
    private String transactionPrefix;

    @StField(tag = "Protobuf", order = 1, required = false, enName = "protobuf_message_name", cnName = "Message 名称",
            description = "format=protobuf 时生效",
            placeHolder = "Person", formType = StField.FormType.TEXT)
    private String protobufMessageName;

    @StField(tag = "Protobuf", order = 2, required = false, enName = "protobuf_schema", cnName = "Protobuf Schema",
            description = "format=protobuf 时生效",
            placeHolder = "syntax = \"proto3\"; message Person { ... }", formType = StField.FormType.TEXT_AREA)
    private String protobufSchema;

    @StField(tag = "Kafka配置", order = 1, required = false, enName = "kafka_config", cnName = "kafka.config",
            description = "Producer 扩展参数，JSON 或 key=value 多行，参考 Kafka 官方 Producer 配置",
            placeHolder = "acks=all\nrequest.timeout.ms=60000", formType = StField.FormType.TEXT_AREA)
    private String kafkaConfig;

    @Override
    protected boolean doCheckConnection() {
        return true;
    }

    @Override
    public JSONObject buildTaskConfig(JSONObject connectionConfig, Long datasourceId) {
        JSONObject config = new JSONObject();
        putIfNotEmpty(config, "topic", connectionConfig.getString("topic"));
        putIfNotEmpty(config, "bootstrap.servers", connectionConfig.getString("bootstrap_servers"));
        putIfNotEmpty(config, "format", connectionConfig.getString("format"));
        putIfNotEmpty(config, "field_delimiter", connectionConfig.getString("field_delimiter"));
        putIfNotEmpty(config, "partition_key_fields", connectionConfig.getString("partition_key_fields"));
        putIfNotEmpty(config, "partition", connectionConfig.getInteger("partition"));
        putIfNotEmpty(config, "assign_partitions", connectionConfig.getString("assign_partitions"));
        putIfNotEmpty(config, "semantics", connectionConfig.getString("semantics"));
        putIfNotEmpty(config, "transaction_prefix", connectionConfig.getString("transaction_prefix"));
        putIfNotEmpty(config, "protobuf_message_name", connectionConfig.getString("protobuf_message_name"));
        putIfNotEmpty(config, "protobuf_schema", connectionConfig.getString("protobuf_schema"));
        putIfNotEmpty(config, "kafka.config", connectionConfig.getString("kafka_config"));
        return config;
    }
}
