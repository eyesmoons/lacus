package com.lacus.datasource.plugins;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.auto.service.AutoService;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.base.AbstractDataSourcePlugin;
import com.lacus.datasource.context.VirtualSourceContext;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;
import com.lacus.enums.DatasourceTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@AutoService(DataSourcePlugin.class)
@Component
public class KafkaDataSourcePlugin extends AbstractDataSourcePlugin {

    private static final String KAFKA_DB = "kafka-db";

    @Override
    public String getName() {
        return "KAFKA";
    }

    @Override
    public Integer getType() {
        return DatasourceTypeEnum.MESSAGE_QUEUE.getValue();
    }

    @Override
    public String getRemark() {
        return "Kafka消息队列";
    }

    @Override
    public String getDriverName() {
        return "org.apache.kafka.clients.admin.AdminClient";
    }

    @Override
    public String getIcon() {
        return "Kafka.png";
    }

    @Override
    public Map<String, ParamDefinitionDTO> getConnectionParamDefinitions() {
        Map<String, ParamDefinitionDTO> definitions = new LinkedHashMap<>();
        definitions.put("bootstrapServers", ParamDefinitionDTO.builder()
                .required(true)
                .inputType("string")
                .displayName("Bootstrap地址")
                .description("逗号分隔的broker地址 host:port")
                .order(1)
                .build());
        definitions.put("securityProtocol", ParamDefinitionDTO.builder()
                .defaultValue("PLAINTEXT")
                .required(false)
                .inputType("string")
                .displayName("安全协议")
                .description("PLAINTEXT / SASL_PLAINTEXT / SASL_SSL")
                .order(2)
                .build());
        definitions.put("saslMechanism", ParamDefinitionDTO.builder()
                .defaultValue("PLAIN")
                .required(false)
                .inputType("string")
                .displayName("SASL机制")
                .description("PLAIN / SCRAM-SHA-256 / SCRAM-SHA-512")
                .order(3)
                .build());
        definitions.put("username", ParamDefinitionDTO.builder()
                .required(false)
                .inputType("string")
                .displayName("SASL用户名")
                .description("SASL认证用户名")
                .order(4)
                .build());
        definitions.put("password", ParamDefinitionDTO.builder()
                .required(false)
                .inputType("password")
                .displayName("SASL密码")
                .description("SASL认证密码")
                .order(5)
                .build());
        return definitions;
    }

    public Properties buildClientProperties(ConnectionParam param) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, param.getValue("bootstrapServers"));
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000");
        String protocol = StringUtils.defaultString(param.getValue("securityProtocol"), "PLAINTEXT");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
        if (!"PLAINTEXT".equalsIgnoreCase(protocol)) {
            String mechanism = StringUtils.defaultString(param.getValue("saslMechanism"), "PLAIN");
            String username = StringUtils.defaultString(param.getValue("username"), "");
            String password = StringUtils.defaultString(param.getValue("password"), "");
            props.put(SaslConfigs.SASL_MECHANISM, mechanism);
            props.put(SaslConfigs.SASL_JAAS_CONFIG, buildJaasConfig(mechanism, username, password));
        }
        return props;
    }

    private String buildJaasConfig(String mechanism, String username, String password) {
        String module;
        if (mechanism != null && mechanism.startsWith("SCRAM")) {
            module = "org.apache.kafka.common.security.scram.ScramLoginModule";
        } else {
            module = "org.apache.kafka.common.security.plain.PlainLoginModule";
        }
        return String.format("%s required username=\"%s\" password=\"%s\";", module, username, password);
    }

    @Override
    public String getJdbcUrl(ConnectionParam connectionParam) {
        throw new CustomException("该数据源类型不支持 JDBC 连接");
    }

    @Override
    protected String buildJdbcUrl(ConnectionParam connectionParam) {
        throw new CustomException("该数据源类型不支持 JDBC 连接");
    }

    @Override
    public DruidDataSource createDataSource(String connectionParams) {
        throw new CustomException("该数据源类型不支持 JDBC 连接");
    }

    @Override
    public Connection getConnection(String connectionParams) {
        throw new CustomException("该数据源类型不支持 JDBC 连接");
    }

    @Override
    public boolean testConnection(String connectionParams) {
        if (StringUtils.isEmpty(connectionParams)) {
            return false;
        }
        ConnectionParam param = parseConnectionParamsPreserveAll(connectionParams);
        return withAdminClient(param, adminClient -> {
            String clusterId = adminClient.describeCluster().clusterId().get(5, TimeUnit.SECONDS);
            return StringUtils.isNotEmpty(clusterId);
        });
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        SchemaDbEntity entity = new SchemaDbEntity();
        entity.setSchemaName(KAFKA_DB);
        entity.setDatasourceId(datasourceId);
        return Collections.singletonList(entity);
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        ConnectionParam param = currentParam();
        List<SchemaTableEntity> result = new ArrayList<>();
        if (param == null || !KAFKA_DB.equals(dbName)) {
            return result;
        }
        boolean ok = withAdminClient(param, adminClient -> {
            try {
                Set<String> topics = adminClient.listTopics().names().get(5, TimeUnit.SECONDS);
                for (String topic : topics) {
                    if (StringUtils.isNotEmpty(tableName) && !tableName.equals(topic)) {
                        continue;
                    }
                    SchemaTableEntity entity = new SchemaTableEntity();
                    entity.setTableSchema(KAFKA_DB);
                    entity.setTableName(topic);
                    entity.setTableType("VIRTUAL_KAFKA");
                    appendTopicMeta(adminClient, topic, entity);
                    result.add(entity);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        if (!ok) {
            return Collections.emptyList();
        }
        return result;
    }

    private void appendTopicMeta(AdminClient adminClient, String topic, SchemaTableEntity entity) throws Exception {
        KafkaFuture<TopicDescription> future = adminClient.describeTopics(Collections.singleton(topic)).values().get(topic);
        TopicDescription description = future.get(5, TimeUnit.SECONDS);
        int partitions = description.partitions().size();
        int replicas = description.partitions().isEmpty() ? 0 : description.partitions().get(0).replicas().size();
        entity.setTableComment(String.format("partitions=%d,replicas=%d", partitions, replicas));
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        return Collections.emptyList();
    }

    private ConnectionParam currentParam() {
        String params = VirtualSourceContext.get();
        if (StringUtils.isEmpty(params)) {
            return null;
        }
        return parseConnectionParamsPreserveAll(params);
    }

    private boolean withAdminClient(ConnectionParam param, AdminFunction action) {
        try (AdminClient adminClient = AdminClient.create(buildClientProperties(param))) {
            return action.apply(adminClient);
        } catch (Exception e) {
            return false;
        }
    }

    @FunctionalInterface
    private interface AdminFunction {
        boolean apply(AdminClient adminClient) throws Exception;
    }
}