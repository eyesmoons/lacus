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
    
    /**
     * 批量获取指定数据库的所有表信息
     * 使用 INFORMATION_SCHEMA.TABLES 一次性查询多个数据库的表
     *
     * @param dbNames 数据库名称列表
     * @return 所有表的实体列表
     */
    List<SchemaTableEntity> listAllTablesByDatabases(@Param("dbNames") List<String> dbNames);
    
    /**
     * 批量获取指定数据库下多个表的列信息
     * 使用 INFORMATION_SCHEMA.COLUMNS 一次性查询多个表的列
     *
     * @param dbName 数据库名称
     * @param tableNames 表名列表
     * @return 所有列的实体列表
     */
    List<SchemaColumnEntity> listAllColumnsByTables(
        @Param("dbName") String dbName, 
        @Param("tableNames") List<String> tableNames);
}
