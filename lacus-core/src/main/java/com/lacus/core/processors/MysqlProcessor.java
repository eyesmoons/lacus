package com.lacus.core.processors;

import cn.hutool.extra.spring.SpringUtil;
import com.google.auto.service.AutoService;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.mapper.MysqlSchemaMapper;

import java.util.List;
import java.util.stream.Collectors;

@AutoService(AbsDatasourceProcessor.class)
public class MysqlProcessor extends AbsJdbcProcessor {

    public MysqlProcessor() {
        super("MYSQL");
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
}
