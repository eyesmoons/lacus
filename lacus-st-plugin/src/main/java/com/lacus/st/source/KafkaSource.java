package com.lacus.st.source;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSource;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import com.lacus.st.interfaces.StComponentInterface;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kafka数据源组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SOURCE,
        name = "kafka_source",
        displayName = "Kafka数据源",
        description = "从Apache Kafka消息队列读取数据，支持多种数据格式和消费模式",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "基本配置", displayName = "基本配置", order = 1, description = "Kafka连接和主题配置"),
        @TagDefinition(name = "消费者配置", displayName = "消费者配置", order = 2, description = "消费者相关配置"),
        @TagDefinition(name = "启动模式配置", displayName = "启动模式配置", order = 3, description = "消费起始位置配置"),
        @TagDefinition(name = "数据格式配置", displayName = "数据格式配置", order = 4, description = "数据序列化格式配置"),
        @TagDefinition(name = "Schema配置", displayName = "Schema配置", order = 5, description = "数据结构定义配置")
})
@AutoService(StComponentInterface.class)
public class KafkaSource extends AbstractStSource {

    // 基本配置
    @StField(
            tag = "基本配置",
            order = 1,
            required = true,
            enName = "topic",
            cnName = "主题名称",
            description = "要消费的Kafka主题，支持多个主题用逗号分隔",
            placeHolder = "topic1,topic2,topic3",
            formType = StField.FormType.TEXT
    )
    private String topic;

    @StField(
            tag = "基本配置",
            order = 2,
            required = true,
            enName = "bootstrap_servers",
            cnName = "Kafka集群地址",
            description = "Kafka broker地址列表，用逗号分隔",
            placeHolder = "localhost:9092,localhost:9093",
            formType = StField.FormType.TEXT
    )
    private String bootstrapServers;

    @StField(
            tag = "基本配置",
            order = 3,
            required = false,
            enName = "pattern",
            cnName = "主题模式匹配",
            defaultValue = "false",
            description = "是否使用正则表达式匹配主题名称",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean pattern;

    // 消费者配置
    @StField(
            tag = "消费者配置",
            order = 4,
            required = false,
            enName = "consumer_group",
            cnName = "消费者组ID",
            defaultValue = "SeaTunnel-Consumer-Group",
            description = "Kafka消费者组标识",
            placeHolder = "my-consumer-group",
            formType = StField.FormType.TEXT
    )
    private String consumerGroup;

    @StField(
            tag = "消费者配置",
            order = 5,
            required = false,
            enName = "commit_on_checkpoint",
            cnName = "检查点提交",
            defaultValue = "true",
            description = "是否在检查点时提交偏移量",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean commitOnCheckpoint;

    @StField(
            tag = "消费者配置",
            order = 6,
            required = false,
            enName = "poll_timeout",
            cnName = "轮询超时时间",
            defaultValue = "10000",
            description = "Kafka轮询的超时时间(毫秒)",
            placeHolder = "10000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Long pollTimeout;

    // 启动模式配置
    @StField(
            tag = "启动模式配置",
            order = 7,
            required = false,
            enName = "start_mode",
            cnName = "消费起始模式",
            defaultValue = "group_offsets",
            description = "消费者的初始消费模式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"earliest", "latest", "group_offsets", "specific_offsets", "timestamp"}
    )
    private String startMode;

    @StField(
            tag = "启动模式配置",
            order = 8,
            required = false,
            enName = "start_mode_offsets",
            cnName = "指定偏移量",
            description = "当start_mode为specific_offsets时的偏移量配置，格式：partition:offset",
            placeHolder = "0:100,1:200,2:300",
            formType = StField.FormType.TEXT_AREA
    )
    private String startModeOffsets;

    @StField(
            tag = "启动模式配置",
            order = 9,
            required = false,
            enName = "start_mode_timestamp",
            cnName = "起始时间戳",
            description = "当start_mode为timestamp时的时间戳(毫秒)",
            placeHolder = "1640995200000",
            formType = StField.FormType.NUMBER
    )
    private Long startModeTimestamp;

    @StField(
            tag = "启动模式配置",
            order = 10,
            required = false,
            enName = "end_mode_timestamp",
            cnName = "结束时间戳",
            description = "批处理模式下的结束时间戳(毫秒)",
            placeHolder = "1640995200000",
            formType = StField.FormType.NUMBER
    )
    private Long endModeTimestamp;

    // 数据格式配置
    @StField(
            tag = "数据格式配置",
            order = 11,
            required = false,
            enName = "format",
            cnName = "数据格式",
            defaultValue = "json",
            description = "数据序列化格式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"json", "text", "avro", "protobuf", "canal_json", "debezium_json", "ogg_json", "maxwell_json"}
    )
    private String format;

    @StField(
            tag = "数据格式配置",
            order = 12,
            required = false,
            enName = "field_delimiter",
            cnName = "字段分隔符",
            defaultValue = ",",
            description = "当格式为text时的字段分隔符",
            placeHolder = ",",
            formType = StField.FormType.TEXT
    )
    private String fieldDelimiter;

    @StField(
            tag = "数据格式配置",
            order = 13,
            required = false,
            enName = "format_error_handle_way",
            cnName = "格式错误处理方式",
            defaultValue = "fail",
            description = "数据格式错误时的处理方式",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"fail", "skip"}
    )
    private String formatErrorHandleWay;

    // Schema配置
    @StField(
            tag = "Schema配置",
            order = 14,
            required = false,
            enName = "schema",
            cnName = "数据结构定义",
            description = "数据结构定义，JSON格式",
            placeHolder = "{\"fields\":{\"id\":\"int\",\"name\":\"string\"}}",
            formType = StField.FormType.TEXT_AREA
    )
    private String schema;

    // Protobuf配置
    @StField(
            tag = "Protobuf配置",
            order = 15,
            required = false,
            enName = "protobuf_message_name",
            cnName = "Protobuf消息名",
            description = "当格式为protobuf时的消息类型名称",
            placeHolder = "Person",
            formType = StField.FormType.TEXT
    )
    private String protobufMessageName;

    @StField(
            tag = "Protobuf配置",
            order = 16,
            required = false,
            enName = "protobuf_schema",
            cnName = "Protobuf Schema",
            description = "Protobuf的schema定义",
            placeHolder = "syntax = \"proto3\"; message Person { string name = 1; }",
            formType = StField.FormType.TEXT_AREA
    )
    private String protobufSchema;

    // 性能配置
    @StField(
            tag = "性能配置",
            order = 17,
            required = false,
            enName = "partition_discovery_interval_millis",
            cnName = "分区发现间隔",
            defaultValue = "-1",
            description = "动态发现主题和分区的间隔时间(毫秒)，-1表示禁用",
            placeHolder = "30000",
            formType = StField.FormType.NUMBER
    )
    private Long partitionDiscoveryIntervalMillis;

    @StField(
            tag = "性能配置",
            order = 18,
            required = false,
            enName = "reader_cache_queue_size",
            cnName = "读取缓存队列大小",
            defaultValue = "1024",
            description = "Reader分片缓存队列大小",
            placeHolder = "1024",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer readerCacheQueueSize;

    // 安全配置
    @StField(
            tag = "安全配置",
            order = 19,
            required = false,
            enName = "kafka_config",
            cnName = "Kafka客户端配置",
            description = "额外的Kafka客户端配置，格式：key1=value1;key2=value2",
            placeHolder = "security.protocol=SASL_SSL;sasl.mechanism=SCRAM-SHA-256",
            formType = StField.FormType.TEXT_AREA
    )
    private String kafkaConfig;

    // 多表配置
    @StField(
            tag = "多表配置",
            order = 20,
            required = false,
            enName = "table_list",
            cnName = "表配置列表",
            description = "多表配置，JSON格式的表配置数组",
            placeHolder = "[{\"topic\":\"topic1\",\"format\":\"json\"},{\"topic\":\"topic2\",\"format\":\"avro\"}]",
            formType = StField.FormType.TEXT_AREA
    )
    private String tableList;

    // CDC配置
    @StField(
            tag = "CDC配置",
            required = false,
            enName = "debezium_record_table_filter",
            cnName = "Debezium表过滤",
            description = "Debezium格式数据的表过滤配置",
            placeHolder = "{\"database_name\":\"test\",\"table_name\":\"orders\"}",
            formType = StField.FormType.TEXT_AREA
    )
    private String debeziumRecordTableFilter;

    // 原生格式配置
    @StField(
            tag = "原生格式配置",
            order = 21,
            required = false,
            enName = "is_native",
            cnName = "保留原生信息",
            defaultValue = "false",
            description = "是否保留Kafka记录的原生信息（如headers、key、partition等）",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"true", "false"}
    )
    private Boolean isNative;

    @Override
    protected boolean doCheckConnection() {
        try {
            // 对于Kafka，我们可以创建一个消费者来测试连接
            java.util.Properties props = new java.util.Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("group.id", "test-connection-group");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("auto.offset.reset", "earliest");
            
            // 解析额外的Kafka配置
            if (kafkaConfig != null && !kafkaConfig.trim().isEmpty()) {
                String[] configs = kafkaConfig.split(";");
                for (String config : configs) {
                    String[] keyValue = config.split("=", 2);
                    if (keyValue.length == 2) {
                        props.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
            
            // 创建消费者测试连接
            try (org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = 
                 new org.apache.kafka.clients.consumer.KafkaConsumer<>(props)) {
                
                // 获取元数据来测试连接
                consumer.listTopics(java.time.Duration.ofSeconds(10));
                return true;
            }
            
        } catch (Exception e) {
            log.error("Kafka连接检查失败", e);
            return false;
        }
    }
}