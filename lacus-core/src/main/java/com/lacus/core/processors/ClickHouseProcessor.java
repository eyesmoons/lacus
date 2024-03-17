package com.lacus.core.processors;

import cn.hutool.extra.spring.SpringUtil;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.ClickHouseSchemaMapper;
import com.lacus.dao.metadata.mapper.MysqlSchemaMapper;

import java.util.List;
import java.util.stream.Collectors;

@AutoService(AbsDatasourceProcessor.class)
public class ClickHouseProcessor extends AbsJdbcProcessor {

    public ClickHouseProcessor() {
        super("CLICKHOUSE");
    }

    public String validSqlConfig() {
        return "select 1";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:clickhouse://%s:%s/%s";
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        ClickHouseSchemaMapper mapper = SpringUtil.getBean(ClickHouseSchemaMapper.class);
        List<SchemaDbEntity> schemaDbList = mapper.listAllSchemaDb();
        return schemaDbList.stream().peek(entity -> entity.setDatasourceId(datasourceId)).collect(Collectors.toList());
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        ClickHouseSchemaMapper mapper = SpringUtil.getBean(ClickHouseSchemaMapper.class);
        return mapper.listSchemaTable(dbName, tableName);
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        ClickHouseSchemaMapper mapper = SpringUtil.getBean(ClickHouseSchemaMapper.class);
        return mapper.listSchemaColumn(dbName, tableName);
    }
}
