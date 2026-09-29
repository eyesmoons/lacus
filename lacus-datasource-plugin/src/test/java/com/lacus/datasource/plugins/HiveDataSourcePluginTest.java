package com.lacus.datasource.plugins;

import com.lacus.datasource.context.VirtualSourceContext;
import com.lacus.datasource.model.ConnectionParam;
import com.lacus.datasource.model.ParamDefinitionDTO;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HiveDataSourcePluginTest {

    private final HiveDataSourcePlugin plugin = new HiveDataSourcePlugin();

    @Test
    public void shouldExposeContractFields() {
        assertEquals("HIVE", plugin.getName());
        assertEquals(Integer.valueOf(6), plugin.getType());
        assertEquals("org.apache.hive.jdbc.HiveDriver", plugin.getDriverName());
        assertEquals("Hive.png", plugin.getIcon());
    }

    @Test
    public void shouldDefineExpectedParams() {
        Map<String, ParamDefinitionDTO> defs = plugin.getConnectionParamDefinitions();
        assertTrue(defs.containsKey("host"));
        assertTrue(defs.containsKey("port"));
        assertTrue(defs.containsKey("database"));
        assertTrue(defs.containsKey("username"));
        assertTrue(defs.containsKey("password"));
        assertTrue(defs.containsKey("authType"));
        // port 默认 10000 且限制 1-65535
        assertEquals(10000, ((Number) defs.get("port").getDefaultValue()).intValue());
    }

    @Test
    public void shouldBuildJdbcUrlWithNoSasl() {
        ConnectionParam param = new ConnectionParam();
        param.setHost("hs2.example");
        param.setPort(10000);
        param.setDatabase("warehouse");
        param.setValue("authType", "NOSASL");
        assertEquals("jdbc:hive2://hs2.example:10000/warehouse;auth=noSasl", plugin.getJdbcUrl(param));
    }

    @Test
    public void shouldBuildJdbcUrlForLdap() {
        ConnectionParam param = new ConnectionParam();
        param.setHost("hs2.example");
        param.setPort(10000);
        param.setDatabase("warehouse");
        param.setValue("authType", "LDAP");
        assertEquals("jdbc:hive2://hs2.example:10000/warehouse;auth=ldap", plugin.getJdbcUrl(param));
    }

    @Test
    public void shouldReturnEmptyWhenContextAbsent() {
        VirtualSourceContext.clear();
        assertTrue(plugin.listSchemaTable("db", null).isEmpty());
        assertTrue(plugin.listSchemaColumn("db", "t").isEmpty());
        assertTrue(plugin.listAllSchemaDb(1L).isEmpty());
    }
}