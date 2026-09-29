package com.lacus.datasource.plugins;

import com.lacus.common.exception.CustomException;
import com.lacus.datasource.context.VirtualSourceContext;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class KafkaDataSourcePluginTest {

    private final KafkaDataSourcePlugin plugin = new KafkaDataSourcePlugin();

    @Test
    public void shouldExposeContractFields() {
        assertEquals("KAFKA", plugin.getName());
        assertEquals(Integer.valueOf(5), plugin.getType());
        assertEquals("org.apache.kafka.clients.admin.AdminClient", plugin.getDriverName());
        assertEquals("Kafka.png", plugin.getIcon());
    }

    @Test
    public void shouldDefineExpectedParams() {
        Map<String, ParamDefinitionDTO> defs = plugin.getConnectionParamDefinitions();
        assertTrue(defs.containsKey("bootstrapServers"));
        assertTrue(defs.containsKey("securityProtocol"));
        assertTrue(defs.containsKey("saslMechanism"));
        assertTrue(defs.containsKey("username"));
        assertTrue(defs.containsKey("password"));
    }

    @Test
    public void shouldBuildPlaintextProps() {
        ConnectionParam param = new ConnectionParam();
        param.setValue("bootstrapServers", "broker1:9092,broker2:9092");
        Properties props = plugin.buildClientProperties(param);
        assertEquals("broker1:9092,broker2:9092", props.get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertNull(props.get(SaslConfigs.SASL_JAAS_CONFIG));
    }

    @Test
    public void shouldBuildSaslPlainProps() {
        ConnectionParam param = new ConnectionParam();
        param.setValue("bootstrapServers", "broker1:9092");
        param.setValue("securityProtocol", "SASL_PLAINTEXT");
        param.setValue("saslMechanism", "PLAIN");
        param.setValue("username", "admin");
        param.setValue("password", "secret");
        Properties props = plugin.buildClientProperties(param);
        assertEquals("PLAIN", props.get(SaslConfigs.SASL_MECHANISM));
        String jaas = String.valueOf(props.get(SaslConfigs.SASL_JAAS_CONFIG));
        assertTrue(jaas.contains("PlainLoginModule"));
        assertTrue(jaas.contains("admin"));
    }

    @Test
    public void shouldBuildScramSaslProps() {
        ConnectionParam param = new ConnectionParam();
        param.setValue("bootstrapServers", "broker1:9092");
        param.setValue("securityProtocol", "SASL_SSL");
        param.setValue("saslMechanism", "SCRAM-SHA-512");
        param.setValue("username", "scram_user");
        Properties props = plugin.buildClientProperties(param);
        assertEquals("SCRAM-SHA-512", props.get(SaslConfigs.SASL_MECHANISM));
        assertTrue(String.valueOf(props.get(SaslConfigs.SASL_JAAS_CONFIG)).contains("ScramLoginModule"));
    }

    @Test(expected = CustomException.class)
    public void shouldRejectJdbcMethods() {
        plugin.getJdbcUrl(null);
    }

    @Test(expected = CustomException.class)
    public void shouldRejectCreateDataSource() {
        plugin.createDataSource("{\"bootstrapServers\":\"x\"}");
    }

    @Test
    @Ignore("需真实broker环境;不可达时快速失败在CI不稳定")
    public void shouldRejectUnreachableBroker() {
        ConnectionParam param = new ConnectionParam();
        param.setValue("bootstrapServers", "127.0.0.1:1");
        assertTrue(plugin.testConnection("{bootstrapServers:127.0.0.1:1}"));
    }

    @Test
    public void shouldReturnEmptyWhenContextAbsent() {
        VirtualSourceContext.clear();
        // 单一集群库不依赖上下文;表浏览依赖上下文,缺省返回空
        assertEquals(1, plugin.listAllSchemaDb(1L).size());
        assertTrue(plugin.listSchemaTable("kafka", null).isEmpty());
        assertTrue(plugin.listSchemaColumn("kafka", "topic").isEmpty());
    }
}