package com.lacus.core.processors;

import cn.hutool.extra.spring.SpringUtil;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.MysqlSchemaMapper;
import com.lacus.dao.metadata.mapper.OracleSchemaMapper;

import java.util.List;
import java.util.stream.Collectors;

@AutoService(AbsDatasourceProcessor.class)
public class OracleProcessor extends AbsJdbcProcessor {
    public OracleProcessor() {
        super("ORACLE");
    }

    public String validSqlConfig() {
        return "SELECT 1 FROM dual";
    }

    @Override
    protected String jdbcUrlConfig() {
        return "jdbc:oracle:thin:@%s:%s:%s";
    }

    @Override
    public List<SchemaDbEntity> listAllSchemaDb(Long datasourceId) {
        OracleSchemaMapper mapper = SpringUtil.getBean(OracleSchemaMapper.class);
        List<SchemaDbEntity> schemaDbList = mapper.listAllSchemaDb();
        return schemaDbList.stream().peek(entity -> entity.setDatasourceId(datasourceId)).collect(Collectors.toList());
    }

    @Override
    public List<SchemaTableEntity> listSchemaTable(String dbName, String tableName) {
        OracleSchemaMapper mapper = SpringUtil.getBean(OracleSchemaMapper.class);
        return mapper.listSchemaTable(dbName, tableName);
    }

    @Override
    public List<SchemaColumnEntity> listSchemaColumn(String dbName, String tableName) {
        OracleSchemaMapper mapper = SpringUtil.getBean(OracleSchemaMapper.class);
        return mapper.listSchemaColumn(dbName, tableName);
    }
}
