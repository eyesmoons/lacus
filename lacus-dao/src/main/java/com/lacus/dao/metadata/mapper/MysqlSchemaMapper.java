package com.lacus.dao.metadata.mapper;

import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MysqlSchemaMapper {
    List<SchemaDbEntity> listAllSchemaDb();
    List<SchemaTableEntity> listSchemaTable(@Param("dbName") String dbName, @Param("tableName") String tableName);
    List<SchemaColumnEntity> listSchemaColumn(@Param("dbName") String dbName, @Param("tableName") String tableName);
}
