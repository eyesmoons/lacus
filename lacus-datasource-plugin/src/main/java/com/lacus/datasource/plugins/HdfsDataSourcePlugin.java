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
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AutoService(DataSourcePlugin.class)
@Component
public class HdfsDataSourcePlugin extends AbstractDataSourcePlugin {

    @Override
    public String getName() {
        return "HDFS";
    }

    @Override
    public Integer getType() {
        return DatasourceTypeEnum.DISTRIBUTED_FILE.getValue();
    }

    @Override
    public String getRemark() {
        return "HDFS分布式文件系统";
    }

    @Override
    public String getDriverName() {
        return "org.apache.hadoop.fs.FileSystem";
    }

    @Override
    public String getIcon() {
        return "HDFS.png";
    }

    @Override
    public Map<String, ParamDefinitionDTO> getConnectionParamDefinitions() {
        Map<String, ParamDefinitionDTO> definitions = new LinkedHashMap<>();
        definitions.put("defaultFS", ParamDefinitionDTO.builder()
                .defaultValue("hdfs://hadoop1:9000")
                .required(true)
                .inputType("string")
                .displayName("NameNode地址")
                .description("HDFS默认文件系统地址，如 hdfs://host:9000")
                .order(1)
                .build());
        definitions.put("user", ParamDefinitionDTO.builder()
                .defaultValue("hdfs")
                .required(true)
                .inputType("string")
                .displayName("HDFS用户名")
                .description("访问HDFS的用户名，默认hdfs")
                .order(2)
                .build());
        return definitions;
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
        try {
            ConnectionParam param = parseConnectionParamsPreserveAll(connectionParams);
            try (FileSystem fileSystem = buildFileSystem(param)) {
                return fileSystem.getFileStatus(new Path("/")) != null;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private FileSystem buildFileSystem(ConnectionParam param) throws Exception {
        String defaultFS = param.getValue("defaultFS");
        String user = StringUtils.defaultString(param.getValue("user"), "hdfs");
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", defaultFS);
        conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        conf.set("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem");
        return FileSystem.get(URI.create(defaultFS), conf, user);
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        ConnectionParam param = currentParam();
        if (param == null) {
            return Collections.emptyList();
        }
        List<SchemaDbEntity> result = new ArrayList<>();
        try (FileSystem fileSystem = buildFileSystem(param)) {
            FileStatus[] statuses = fileSystem.listStatus(new Path("/"));
            for (FileStatus status : statuses) {
                if (status.isDirectory()) {
                    SchemaDbEntity entity = new SchemaDbEntity();
                    entity.setSchemaName(status.getPath().getName());
                    entity.setDatasourceId(datasourceId);
                    result.add(entity);
                }
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        ConnectionParam param = currentParam();
        if (param == null) {
            return Collections.emptyList();
        }
        List<SchemaTableEntity> result = new ArrayList<>();
        try (FileSystem fileSystem = buildFileSystem(param)) {
            FileStatus[] statuses = fileSystem.listStatus(new Path("/" + dbName));
            for (FileStatus status : statuses) {
                if (!status.isDirectory()) {
                    continue;
                }
                String table = status.getPath().getName();
                if (StringUtils.isNotEmpty(tableName) && !tableName.equals(table)) {
                    continue;
                }
                SchemaTableEntity entity = new SchemaTableEntity();
                entity.setTableSchema(dbName);
                entity.setTableName(table);
                entity.setTableType("VIRTUAL_HDFS");
                result.add(entity);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        ConnectionParam param = currentParam();
        if (param == null) {
            return Collections.emptyList();
        }
        List<SchemaColumnEntity> result = new ArrayList<>();
        try (FileSystem fileSystem = buildFileSystem(param)) {
            FileStatus[] statuses = fileSystem.listStatus(new Path("/" + dbName + "/" + tableName));
            for (FileStatus status : statuses) {
                SchemaColumnEntity entity = new SchemaColumnEntity();
                entity.setTableSchema(dbName);
                entity.setTableName(tableName);
                entity.setColumnName(status.getPath().getName());
                entity.setDataType(status.isDirectory() ? "directory" : "file");
                entity.setColumnType(status.isDirectory() ? "directory" : String.valueOf(status.getLen()));
                entity.setColumnComment(status.getPath().toString());
                result.add(entity);
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return result;
    }

    private ConnectionParam currentParam() {
        String params = VirtualSourceContext.get();
        if (StringUtils.isEmpty(params)) {
            return null;
        }
        return parseConnectionParamsPreserveAll(params);
    }
}