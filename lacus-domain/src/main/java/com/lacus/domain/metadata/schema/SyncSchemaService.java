package com.lacus.domain.metadata.schema;

import com.lacus.common.exception.CustomException;
import com.lacus.core.datasource.DynamicDataSourceContextHolder;
import com.lacus.dao.metadata.entity.*;
import com.lacus.dao.metadata.mapper.MetaTableMapper;
import com.lacus.dao.metadata.mapper.SchemaMapper;
import com.lacus.domain.metadata.schema.dto.SchemaDbDTO;
import com.lacus.domain.metadata.schema.dto.SchemaTableDTO;
import com.lacus.domain.metadata.schema.model.SchemaDbTreeNode;
import com.lacus.domain.metadata.table.dto.TableDTO;
import com.lacus.service.metadata.IMetaColumnService;
import com.lacus.service.metadata.IMetaDbService;
import com.lacus.service.metadata.IMetaTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SyncSchemaService {

    @Autowired
    private IMetaDbService metaDbService;

    @Autowired
    private IMetaTableService metaTableService;

    @Autowired
    private IMetaColumnService metaColumnService;

    @Autowired
    private SchemaMapper schemaMapper;

    @Autowired
    private MetaTableMapper metaTableMapper;

    /**
     * query schema databases by datasourceId
     * @param datasourceId datasourceId
     */
    public List<SchemaDbDTO> getSchemaDbList(Long datasourceId) {
        try {
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
            List<SchemaDbEntity> schemaDbList = schemaMapper.listAllSchemaDb();
            return schemaDbList.stream().map(entity -> {
                SchemaDbDTO schemaDbDTO = new SchemaDbDTO(entity);
                schemaDbDTO.setDatasourceId(datasourceId);
                return schemaDbDTO;
            }).collect(Collectors.toList());
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * query schema tables
     * @param datasourceId datasourceId
     * @param dbName dbName
     * @param tableName tableName
     */
    public List<SchemaTableDTO> getSchemaTableList(Long datasourceId, String dbName, String tableName) {
        try {
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
            List<SchemaTableEntity> schemaTableList = schemaMapper.listSchemaTable(dbName, tableName);
            return schemaTableList.stream().map(SchemaTableDTO::new).collect(Collectors.toList());
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * sync databases and tables
     * @param datasourceId datasourceId
     * @param dbTables dbTables
     */
    public boolean syncDbTables(Long datasourceId, List<String> dbTables) {
        try {
            // remove empty items
            dbTables.removeIf(item -> !item.contains("."));
            // extract dbNames
            Set<String> dbNames = dbTables.stream().map(dbTable -> {
                String[] dbTableArr = dbTable.split("\\.");
                return dbTableArr[0];
            }).collect(Collectors.toSet());
            // sync metaDbs
            syncMetaDbs(datasourceId, dbNames);
            // sync metaTables
            List<TableDTO> tableList = syncMetaTables(datasourceId, dbTables);
            // sync metaColumns
            syncColumns(datasourceId, tableList);
            return true;
        } catch (Exception e) {
            throw new CustomException("error occurs when sync metadata schema：" + e.getMessage());
        }
    }

    /**
     * sync schema columns
     * @param datasourceId datasourceId
     * @param tableList tableList
     */
    private void syncColumns(Long datasourceId, List<TableDTO> tableList) {
        for (TableDTO tableDTO : tableList) {
            // remove old columns
            boolean remove = metaColumnService.removeColumnsByTableId(tableDTO.getTableId());
            List<SchemaColumnEntity> columnList;
            try {
                DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
                columnList = schemaMapper.listSchemaColumn(tableDTO.getDbName(), tableDTO.getTableName());
            } finally {
                DynamicDataSourceContextHolder.clearDataSourceType();
            }
            List<MetaColumnEntity> list = columnList.stream().map(entity -> {
                MetaColumnEntity column = new MetaColumnEntity();
                column.setTableId(tableDTO.getTableId());
                column.setColumnName(entity.getColumnName());
                column.setDataType(entity.getDataType());
                column.setColumnType(entity.getColumnType());
                column.setNumericPrecision(entity.getNumericPrecision());
                column.setNumericScale(entity.getNumericScale());
                column.setColumnLength(entity.getCharacterOctetLength());
                column.setComment(entity.getColumnComment());
                column.setIsNullable(entity.getIsNullable());
                column.setColumnDefault(entity.getColumnDefault());
                return column;
            }).collect(Collectors.toList());
            // add new columns
            metaColumnService.saveBatch(list);
        }
    }

    /**
     * sync schema tables
     * @param datasourceId datasourceId
     * @param dbTables dbTables
     */
    private List<TableDTO> syncMetaTables(Long datasourceId, List<String> dbTables) {
        List<MetaTableEntity> tableList = new ArrayList<>();
        for (String dbTable : dbTables) {
            String[] dbTableArr = dbTable.split("\\.");
            String dbName = dbTableArr[0];
            String tableName = dbTableArr[1];
            MetaDbEntity metaDb = metaDbService.getMetaDb(datasourceId, dbName);
            Long dbId = metaDb.getDbId();
            MetaTableEntity metaTable = metaTableService.getMetaTable(dbId, tableName);
            if (ObjectUtils.isEmpty(metaTable)) {
                List<SchemaTableDTO> schemaTableList = getSchemaTableList(datasourceId, dbName, tableName);
                if (ObjectUtils.isNotEmpty(schemaTableList)) {
                    SchemaTableDTO schemaTableDTO = schemaTableList.get(0);
                    MetaTableEntity tableEntity = new MetaTableEntity();
                    tableEntity.setDbId(dbId);
                    tableEntity.setTableName(tableName);
                    tableEntity.setComment(schemaTableDTO.getTableComment());
                    tableEntity.setType(schemaTableDTO.getTableType());
                    tableEntity.setEngine(schemaTableDTO.getEngine());
                    tableEntity.setTableCreateTime(schemaTableDTO.getCreateTime());
                    tableEntity.setDbName(dbName);
                    tableList.add(tableEntity);
                }
            } else {
                metaTable.setDbName(dbName);
                tableList.add(metaTable);
            }
        }
        metaTableService.saveOrUpdateBatch(tableList);
        return tableList.stream().map(TableDTO::new).collect(Collectors.toList());
    }

    /**
     * sync schema dbs
     * @param datasourceId datasourceId
     * @param dbNames dbNames
     */
    private void syncMetaDbs(Long datasourceId, Set<String> dbNames) {
        List<MetaDbEntity> dbList = new ArrayList<>();
        for (String dbName : dbNames) {
            boolean metaDbExists = metaDbService.isMetaDbExists(datasourceId, dbName);
            if (!metaDbExists) {
                MetaDbEntity dbEntity = new MetaDbEntity();
                dbEntity.setDbName(dbName);
                dbEntity.setDatasourceId(datasourceId);
                dbList.add(dbEntity);
            }
        }
        metaDbService.saveBatch(dbList);
    }

    public SchemaDbTreeNode getSchemaDbTree(Long datasourceId) {
        SchemaDbTreeNode node = new SchemaDbTreeNode();
        List<SchemaDbDTO> schemaDbList = this.getSchemaDbList(datasourceId);
        node.setSchemaDbList(schemaDbList);
        List<MetaDbTableEntity> metaDbTables = metaTableMapper.listMetaDbTable(datasourceId, null, null);
        Set<String> checkedKeys = new HashSet<>();
        Set<String> expandedKeys = new HashSet<>();
        for (MetaDbTableEntity metaDbTable : metaDbTables) {
            expandedKeys.add(metaDbTable.getDbName());
            checkedKeys.add(metaDbTable.getDbName() + "." + metaDbTable.getTableName());
        }
        node.setCheckedKeys(checkedKeys);
        node.setExpandedKeys(expandedKeys);
        return node;
    }
}
