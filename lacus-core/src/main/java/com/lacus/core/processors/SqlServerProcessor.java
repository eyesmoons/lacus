package com.lacus.core.processors;

import cn.hutool.extra.spring.SpringUtil;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.SqlServerSchemaMapper;

import java.util.List;
import java.util.stream.Collectors;

@AutoService(AbsDatasourceProcessor.class)
public class SqlServerProcessor extends AbsJdbcProcessor {
    public SqlServerProcessor() {
        super("SQLSERVER");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:sqlserver://%s:%s;databaseName=%s";
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        SqlServerSchemaMapper mapper = SpringUtil.getBean(SqlServerSchemaMapper.class);
        List<SchemaDbEntity> schemaDbList = mapper.listAllSchemaDb();
        return schemaDbList.stream().peek(entity -> entity.setDatasourceId(datasourceId)).collect(Collectors.toList());
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        SqlServerSchemaMapper mapper = SpringUtil.getBean(SqlServerSchemaMapper.class);
        return mapper.listSchemaTable(dbName, tableName);
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        SqlServerSchemaMapper mapper = SpringUtil.getBean(SqlServerSchemaMapper.class);
        return mapper.listSchemaColumn(dbName, tableName);
    }
}
