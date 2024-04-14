package com.lacus.core.processors;

import cn.hutool.extra.spring.SpringUtil;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.MysqlSchemaMapper;

import java.util.List;
import java.util.stream.Collectors;

@AutoService(AbsDatasourceProcessor.class)
public class DorisProcessor extends AbsJdbcProcessor {
    public DorisProcessor() {
        super("DORIS");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:mysql://%s:%s/%s";
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        MysqlSchemaMapper mysqlSchemaMapper = SpringUtil.getBean(MysqlSchemaMapper.class);
        List<SchemaDbEntity> schemaDbList = mysqlSchemaMapper.listAllSchemaDb();
        return schemaDbList.stream().peek(entity -> entity.setDatasourceId(datasourceId)).collect(Collectors.toList());
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        MysqlSchemaMapper mysqlSchemaMapper = SpringUtil.getBean(MysqlSchemaMapper.class);
        return mysqlSchemaMapper.listSchemaTable(dbName, tableName);
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        MysqlSchemaMapper mysqlSchemaMapper = SpringUtil.getBean(MysqlSchemaMapper.class);
        return mysqlSchemaMapper.listSchemaColumn(dbName, tableName);
    }
}
