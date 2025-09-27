package com.lacus.st.sink;

import com.google.auto.service.AutoService;
import com.lacus.st.abstracts.AbstractStSink;
import com.lacus.st.annotation.StComponent;
import com.lacus.st.annotation.StField;
import com.lacus.st.interfaces.StComponentInterface;
import com.lacus.st.annotation.StTag;
import com.lacus.st.annotation.StTag.TagDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Kafka Sink组件
 * Kafka消息队列输出组件
 * 
 * @author lacus
 */
@Slf4j
@StComponent(
        type = StComponent.ComponentType.SINK,
        name = "kafka_sink",
        displayName = "Kafka消息队列输出",
        description = "将Rows内容发送到Kafka topic，支持精确一次和CDC模式",
        version = "2.0.0",
        author = "lacus"
)
@StTag({
        @TagDefinition(name = "基础配置", displayName = "基础配置", order = 1, description = "Kafka基础配置"),
        @TagDefinition(name = "分区配置", displayName = "分区配置", order = 2, description = "分区相关配置"),
        @TagDefinition(name = "事务配置", displayName = "事务配置", order = 3, description = "事务处理相关配置"),
        @TagDefinition(name = "Protobuf配置", displayName = "Protobuf配置", order = 4, description = "Protobuf格式相关配置"),
        @TagDefinition(name = "Kafka配置", displayName = "Kafka配置", order = 5, description = "Kafka客户端相关配置")
})

@AutoService(StComponentInterface.class)
public class KafkaSink extends AbstractStSink {

    // 基础配置
    @StField(
            tag = "基础配置",
            order = 1,
            required = true,
            enName = "topic",
            cnName = "Topic名称",
            description = "要写入数据的Kafka topic名称，支持动态topic格式：${field_name}",
            placeHolder = "test_topic",
            formType = StField.FormType.TEXT
    )
    private String topic;

    @StField(
            tag = "基础配置",
            order = 2,
            required = true,
            enName = "bootstrap_servers",
            cnName = "Kafka服务器地址",
            description = "Kafka brokers地址，使用逗号分隔",
            placeHolder = "localhost:9092",
            formType = StField.FormType.TEXT
    )
    private String bootstrapServers;

    @StField(
            tag = "基础配置",
            order = 3,
            required = false,
            enName = "format",
            cnName = "数据格式",
            defaultValue = "json",
            description = "数据格式，支持json、text、canal-json、debezium-json、avro、protobuf、native",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"json", "text", "canal-json", "debezium-json", "avro", "protobuf", "native"}
    )
    private String format;

    @StField(
            tag = "基础配置",
            order = 4,
            required = false,
            enName = "field_delimiter",
            cnName = "字段分隔符",
            defaultValue = ",",
            description = "自定义数据格式的字段分隔符",
            placeHolder = ",",
            formType = StField.FormType.TEXT
    )
    private String fieldDelimiter;

    // 分区配置
    @StField(
            tag = "分区配置",
            order = 5,
            required = false,
            enName = "partition_key_fields",
            cnName = "分区键字段",
            description = "配置字段用作kafka消息的key，用逗号分隔",
            placeHolder = "id,name",
            formType = StField.FormType.TEXT
    )
    private String partitionKeyFields;

    @StField(
            tag = "分区配置",
            order = 6,
            required = false,
            enName = "partition",
            cnName = "指定分区",
            description = "可以指定分区号，所有消息都会发送到此分区",
            placeHolder = "0",
            formType = StField.FormType.NUMBER
    )
    private Integer partition;

    @StField(
            tag = "分区配置",
            order = 7,
            required = false,
            enName = "assign_partitions",
            cnName = "分区分配规则",
            description = "根据消息内容决定发送到哪个分区，用逗号分隔",
            placeHolder = "shoe,clothing",
            formType = StField.FormType.TEXT
    )
    private String assignPartitions;

    // 事务配置
    @StField(
            tag = "事务配置",
            order = 8,
            required = false,
            enName = "semantics",
            cnName = "语义保证",
            defaultValue = "NON",
            description = "语义保证级别：EXACTLY_ONCE、AT_LEAST_ONCE、NON",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"EXACTLY_ONCE", "AT_LEAST_ONCE", "NON"}
    )
    private String semantics;

    @StField(
            tag = "事务配置",
            order = 9,
            required = false,
            enName = "transaction_prefix",
            cnName = "事务前缀",
            description = "当semantics为EXACTLY_ONCE时，kafka事务ID的前缀",
            placeHolder = "seatunnel-sink-",
            formType = StField.FormType.TEXT
    )
    private String transactionPrefix;

    // Protobuf配置
    @StField(
            tag = "Protobuf配置",
            order = 10,
            required = false,
            enName = "protobuf_message_name",
            cnName = "Protobuf消息名称",
            description = "当format为protobuf时生效，指定Message名称",
            placeHolder = "Person",
            formType = StField.FormType.TEXT
    )
    private String protobufMessageName;

    @StField(
            tag = "Protobuf配置",
            order = 11,
            required = false,
            enName = "protobuf_schema",
            cnName = "Protobuf Schema",
            description = "当format为protobuf时生效，Protobuf schema定义",
            placeHolder = "syntax = \"proto3\"; message Person { ... }",
            formType = StField.FormType.TEXT_AREA
    )
    private String protobufSchema;

    // Kafka客户端配置
    @StField(
            tag = "Kafka配置",
            order = 12,
            required = false,
            enName = "kafka_request_timeout_ms",
            cnName = "请求超时时间(毫秒)",
            defaultValue = "60000",
            description = "Kafka客户端请求超时时间",
            placeHolder = "60000",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Integer kafkaRequestTimeoutMs;

    @StField(
            tag = "Kafka配置",
            order = 13,
            required = false,
            enName = "kafka_acks",
            cnName = "确认模式",
            defaultValue = "all",
            description = "Producer确认模式：all、1、0",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"all", "1", "0"}
    )
    private String kafkaAcks;

    @StField(
            tag = "Kafka配置",
            order = 14,
            required = false,
            enName = "kafka_buffer_memory",
            cnName = "缓冲区内存大小",
            defaultValue = "33554432",
            description = "Producer缓冲区内存大小（字节）",
            placeHolder = "33554432",
            formType = StField.FormType.POSITIVE_NUMBER
    )
    private Long kafkaBufferMemory;

    // 安全配置
    @StField(
            tag = "安全配置",
            order = 15,
            required = false,
            enName = "security_protocol",
            cnName = "安全协议",
            defaultValue = "PLAINTEXT",
            description = "Kafka安全协议",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"PLAINTEXT", "SSL", "SASL_PLAINTEXT", "SASL_SSL"}
    )
    private String securityProtocol;

    @StField(
            tag = "安全配置",
            order = 16,
            required = false,
            enName = "sasl_mechanism",
            cnName = "SASL机制",
            description = "SASL认证机制",
            formType = StField.FormType.SINGLE_SELECT,
            dictType = StField.DictType.ENUM,
            dictEnum = {"GSSAPI", "PLAIN", "SCRAM-SHA-256", "SCRAM-SHA-512", "AWS_MSK_IAM"}
    )
    private String saslMechanism;

    @StField(
            tag = "安全配置",
            order = 17,
            required = false,
            enName = "sasl_jaas_config",
            cnName = "SASL JAAS配置",
            description = "SASL JAAS配置字符串",
            placeHolder = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";",
            formType = StField.FormType.TEXT_AREA
    )
    private String saslJaasConfig;

    @Override
    protected boolean doCheckConnection() {
        try {
            // 验证必填字段
            if (topic == null || topic.trim().isEmpty()) {
                log.error("Kafka Sink配置错误：topic不能为空");
                return false;
            }

            if (bootstrapServers == null || bootstrapServers.trim().isEmpty()) {
                log.error("Kafka Sink配置错误：bootstrap_servers不能为空");
                return false;
            }

            // 验证bootstrap_servers格式
            String[] servers = bootstrapServers.split(",");
            for (String server : servers) {
                String trimmedServer = server.trim();
                if (!trimmedServer.contains(":")) {
                    log.error("Kafka Sink配置错误：bootstrap_servers格式不正确，应为host:port格式：{}", trimmedServer);
                    return false;
                }
            }

            // 验证格式
            if (format != null && !format.trim().isEmpty()) {
                String[] supportedFormats = {"json", "text", "canal-json", "debezium-json", "avro", "protobuf", "native"};
                boolean formatSupported = false;
                for (String supportedFormat : supportedFormats) {
                    if (supportedFormat.equals(format.trim())) {
                        formatSupported = true;
                        break;
                    }
                }
                if (!formatSupported) {
                    log.error("Kafka Sink配置错误：不支持的数据格式 {}，支持的格式：{}", format, String.join(", ", supportedFormats));
                    return false;
                }
            }

            // 验证语义保证
            if (semantics != null && !semantics.trim().isEmpty()) {
                String sem = semantics.trim();
                if (!"EXACTLY_ONCE".equals(sem) && !"AT_LEAST_ONCE".equals(sem) && !"NON".equals(sem)) {
                    log.error("Kafka Sink配置错误：不支持的语义保证 {}，支持的语义：EXACTLY_ONCE, AT_LEAST_ONCE, NON", semantics);
                    return false;
                }
                
                // 当使用EXACTLY_ONCE时，建议配置transaction_prefix
                if ("EXACTLY_ONCE".equals(sem) && (transactionPrefix == null || transactionPrefix.trim().isEmpty())) {
                    log.warn("Kafka Sink警告：使用EXACTLY_ONCE语义时建议配置transaction_prefix");
                }
            }

            // 验证Protobuf配置
            if ("protobuf".equals(format)) {
                if (protobufMessageName == null || protobufMessageName.trim().isEmpty()) {
                    log.error("Kafka Sink配置错误：使用protobuf格式时，protobuf_message_name不能为空");
                    return false;
                }
                if (protobufSchema == null || protobufSchema.trim().isEmpty()) {
                    log.error("Kafka Sink配置错误：使用protobuf格式时，protobuf_schema不能为空");
                    return false;
                }
            }

            log.info("Kafka Sink连接检查成功，topic: {}, servers: {}", topic, bootstrapServers);
            return true;
        } catch (Exception e) {
            log.error("Kafka Sink连接检查失败", e);
            return false;
        }
    }

    /**
     * 获取分区键字段列表
     */
    public List<String> getPartitionKeyFieldsList() {
        if (partitionKeyFields == null || partitionKeyFields.trim().isEmpty()) {
            return null;
        }
        String[] fields = partitionKeyFields.split(",");
        return java.util.Arrays.stream(fields)
                .map(String::trim)
                .filter(field -> !field.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取分区分配规则列表
     */
    public List<String> getAssignPartitionsList() {
        if (assignPartitions == null || assignPartitions.trim().isEmpty()) {
            return null;
        }
        String[] partitions = assignPartitions.split(",");
        return java.util.Arrays.stream(partitions)
                .map(String::trim)
                .filter(partition -> !partition.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    // Getter and Setter methods
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getFormat() {
        return format != null ? format : "json";
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter != null ? fieldDelimiter : ",";
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getPartitionKeyFields() {
        return partitionKeyFields;
    }

    public void setPartitionKeyFields(String partitionKeyFields) {
        this.partitionKeyFields = partitionKeyFields;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public String getAssignPartitions() {
        return assignPartitions;
    }

    public void setAssignPartitions(String assignPartitions) {
        this.assignPartitions = assignPartitions;
    }

    public String getSemantics() {
        return semantics != null ? semantics : "NON";
    }

    public void setSemantics(String semantics) {
        this.semantics = semantics;
    }

    public String getTransactionPrefix() {
        return transactionPrefix;
    }

    public void setTransactionPrefix(String transactionPrefix) {
        this.transactionPrefix = transactionPrefix;
    }

    public String getProtobufMessageName() {
        return protobufMessageName;
    }

    public void setProtobufMessageName(String protobufMessageName) {
        this.protobufMessageName = protobufMessageName;
    }

    public String getProtobufSchema() {
        return protobufSchema;
    }

    public void setProtobufSchema(String protobufSchema) {
        this.protobufSchema = protobufSchema;
    }

    public Integer getKafkaRequestTimeoutMs() {
        return kafkaRequestTimeoutMs != null ? kafkaRequestTimeoutMs : 60000;
    }

    public void setKafkaRequestTimeoutMs(Integer kafkaRequestTimeoutMs) {
        this.kafkaRequestTimeoutMs = kafkaRequestTimeoutMs;
    }

    public String getKafkaAcks() {
        return kafkaAcks != null ? kafkaAcks : "all";
    }

    public void setKafkaAcks(String kafkaAcks) {
        this.kafkaAcks = kafkaAcks;
    }

    public Long getKafkaBufferMemory() {
        return kafkaBufferMemory != null ? kafkaBufferMemory : 33554432L;
    }

    public void setKafkaBufferMemory(Long kafkaBufferMemory) {
        this.kafkaBufferMemory = kafkaBufferMemory;
    }

    public String getSecurityProtocol() {
        return securityProtocol != null ? securityProtocol : "PLAINTEXT";
    }

    public void setSecurityProtocol(String securityProtocol) {
        this.securityProtocol = securityProtocol;
    }

    public String getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(String saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    public String getSaslJaasConfig() {
        return saslJaasConfig;
    }

    public void setSaslJaasConfig(String saslJaasConfig) {
        this.saslJaasConfig = saslJaasConfig;
    }
}