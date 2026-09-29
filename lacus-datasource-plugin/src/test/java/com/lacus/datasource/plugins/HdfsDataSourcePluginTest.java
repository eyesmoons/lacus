package com.lacus.datasource.plugins;

import com.lacus.common.exception.CustomException;
import com.lacus.datasource.context.VirtualSourceContext;
import com.lacus.datasource.model.ParamDefinitionDTO;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HdfsDataSourcePluginTest {

    private final HdfsDataSourcePlugin plugin = new HdfsDataSourcePlugin();

    @Test
    public void shouldExposeContractFields() {
        assertEquals("HDFS", plugin.getName());
        assertEquals(Integer.valueOf(4), plugin.getType());
        assertEquals("org.apache.hadoop.fs.FileSystem", plugin.getDriverName());
        assertEquals("HDFS.png", plugin.getIcon());
    }

    @Test
    public void shouldDefineExpectedParams() {
        Map<String, ParamDefinitionDTO> defs = plugin.getConnectionParamDefinitions();
        assertTrue(defs.containsKey("defaultFS"));
        assertTrue(defs.containsKey("user"));
    }

    @Test(expected = CustomException.class)
    public void shouldRejectJdbcMethods() {
        plugin.getJdbcUrl(null);
    }

    @Test(expected = CustomException.class)
    public void shouldRejectCreateDataSource() {
        plugin.createDataSource("{\"defaultFS\":\"hdfs://x\",\"user\":\"hdfs\"}");
    }

    @Test
    public void shouldConnectToLocalFileSystem() {
        String params = "{\"defaultFS\":\"file:///\",\"user\":\"casey\"}";
        assertTrue(plugin.testConnection(params));
    }

    @Test
    public void shouldRejectUnreachableNameNode() {
        String params = "{\"defaultFS\":\"hdfs://127.0.0.1:1\",\"user\":\"hdfs\"}";
        assertFalse(plugin.testConnection(params));
    }

    @Test
    public void shouldReturnEmptyWhenContextAbsent() {
        VirtualSourceContext.clear();
        assertTrue(plugin.listAllSchemaDb(1L).isEmpty());
        assertTrue(plugin.listSchemaTable("db", null).isEmpty());
        assertTrue(plugin.listSchemaColumn("db", "t").isEmpty());
    }
}