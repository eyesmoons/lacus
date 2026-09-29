package com.lacus.domain.metadata.schema;

import com.lacus.common.exception.CustomException;
import com.lacus.core.datasource.DynamicDataSourceContextHolder;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.dao.metadata.entity.MetaDbTableEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.dao.metadata.entity.SchemaColumnEntity;
import com.lacus.dao.metadata.entity.SchemaDbEntity;
import com.lacus.dao.metadata.entity.SchemaTableEntity;
import com.lacus.dao.metadata.mapper.MetaTableMapper;
import com.lacus.dao.metadata.mapper.MysqlSchemaMapper;
import com.lacus.datasource.api.DataSourcePlugin;
import com.lacus.datasource.context.VirtualSourceContext;
import com.lacus.datasource.manager.DataSourcePluginManager;
import com.lacus.domain.metadata.schema.dto.SchemaColumnDTO;
import com.lacus.domain.metadata.schema.dto.SchemaDbDTO;
import com.lacus.domain.metadata.schema.dto.SchemaTableDTO;
import com.lacus.domain.metadata.schema.model.SchemaDbTreeNode;
import com.lacus.domain.metadata.table.dto.TableDTO;
import com.lacus.enums.DatasourceTypeEnum;
import com.lacus.service.metadata.IMetaColumnService;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.metadata.IMetaDbService;
import com.lacus.service.metadata.IMetaTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SyncSchemaBusiness {

    @Autowired
    private IMetaDbService metaDbService;

    @Autowired
    private IMetaTableService metaTableService;

    @Autowired
    private IMetaColumnService metaColumnService;

    @Autowired
    private MysqlSchemaMapper mysqlSchemaMapper;

    @Autowired
    private MetaTableMapper metaTableMapper;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private DataSourcePluginManager dataSourcePluginManager;

    /**
     * query schema databases by datasourceId
     */
    public List<SchemaDbDTO> getSchemaDbList2(Long datasourceId) {
        try {
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
            List<SchemaDbEntity> schemaDbList = mysqlSchemaMapper.listAllSchemaDb();
            return schemaDbList.stream().map(entity -> {
                SchemaDbDTO schemaDbDTO = new SchemaDbDTO(entity);
                schemaDbDTO.setDatasourceId(datasourceId);
                return schemaDbDTO;
            }).collect(Collectors.toList());
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    public List<SchemaDbDTO> getSchemaDbList(Long datasourceId) {
        MetaDatasourceEntity metaDatasource = metaDataSourceService.getById(datasourceId);
        DataSourcePlugin processor = dataSourcePluginManager.getProcessor(metaDatasource.getType().toUpperCase());
        try {
            if (isVirtualPlugin(processor)) {
                VirtualSourceContext.set(metaDatasource.getConnectionParams());
            } else {
                DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
            }
            List<SchemaDbEntity> schemaDbList = processor.listAllSchemaDb(datasourceId);
            return schemaDbList.stream().map(SchemaDbDTO::new).collect(Collectors.toList());
        } finally {
            VirtualSourceContext.clear();
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * query schema tables - 保留用于 Controller 调用
     */
    public List<SchemaTableDTO> getSchemaTableList(Long datasourceId, String dbName, String tableName) {
        MetaDatasourceEntity metaDatasource = metaDataSourceService.getById(datasourceId);
        DataSourcePlugin processor = dataSourcePluginManager.getProcessor(metaDatasource.getType().toUpperCase());
        try {
            if (isVirtualPlugin(processor)) {
                VirtualSourceContext.set(metaDatasource.getConnectionParams());
                return processor.listSchemaTable(dbName, tableName).stream()
                        .map(SchemaTableDTO::new).collect(Collectors.toList());
            }
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);

            List<SchemaTableEntity> schemaTableList;
            if (tableName != null && !tableName.isEmpty()) {
                schemaTableList = mysqlSchemaMapper.listSchemaTable(dbName, tableName);
            } else {
                List<String> dbNames = new ArrayList<>();
                dbNames.add(dbName);
                schemaTableList = mysqlSchemaMapper.listAllTablesByDatabases(dbNames);
            }

            return schemaTableList.stream().map(SchemaTableDTO::new).collect(Collectors.toList());
        } finally {
            VirtualSourceContext.clear();
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * query schema columns - 虚拟源直接走插件浏览;常规JDBC源沿用mapper
     */
    public List<SchemaColumnDTO> getSchemaColumnList(Long datasourceId, String dbName, String tableName) {
        MetaDatasourceEntity metaDatasource = metaDataSourceService.getById(datasourceId);
        DataSourcePlugin processor = dataSourcePluginManager.getProcessor(metaDatasource.getType().toUpperCase());
        try {
            if (isVirtualPlugin(processor)) {
                VirtualSourceContext.set(metaDatasource.getConnectionParams());
                return processor.listSchemaColumn(dbName, tableName).stream()
                        .map(SchemaColumnDTO::new).collect(Collectors.toList());
            }
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
            return mysqlSchemaMapper.listSchemaColumn(dbName, tableName).stream()
                    .map(SchemaColumnDTO::new).collect(Collectors.toList());
        } finally {
            VirtualSourceContext.clear();
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    private boolean isVirtualPlugin(DataSourcePlugin processor) {
        if (processor == null) {
            return false;
        }
        Integer type = processor.getType();
        return DatasourceTypeEnum.DISTRIBUTED_FILE.getValue().equals(type)
                || DatasourceTypeEnum.MESSAGE_QUEUE.getValue().equals(type);
    }

    /**
     * sync databases and tables - 终极优化版本：整个批次只切换一次数据源
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean syncDbTables(Long datasourceId, List<String> dbTables) {
        log.info("开始批量同步元数据，datasourceId={}, 待同步表数量={}", datasourceId, dbTables.size());
        
        // 移除空项
        dbTables.removeIf(item -> !item.contains("."));
        
        if (dbTables.isEmpty()) {
            log.warn("没有需要同步的表，datasourceId={}", datasourceId);
            return true;
        }
        
        // 提取数据库名
        Set<String> dbNames = dbTables.stream()
                .map(dbTable -> dbTable.split("\\.")[0])
                .collect(Collectors.toSet());
        
        log.info("涉及数据库：{}", dbNames);

        // 【关键】整个批次只切换一次数据源，在内部方法中不再重复切换
        try {
            DynamicDataSourceContextHolder.setDataSourceId(datasourceId);
            
            // 1. 同步数据库
            log.info("开始同步数据库元数据...");
            syncMetaDbsInternal(datasourceId, dbNames);
            
            // 2. 批量同步表（一次性获取所有表信息）
            log.info("开始同步表元数据...");
            List<TableDTO> tableList = syncMetaTablesOptimizedInternal(datasourceId, dbTables);
            log.info("表元数据同步完成，共 {} 张表", tableList.size());
            
            if (tableList.isEmpty()) {
                log.warn("没有成功同步任何表元数据，datasourceId={}", datasourceId);
                return true;
            }
            
            // 3. 批量同步列（按数据库分组批量查询）
            log.info("开始同步列元数据...");
            syncColumnsOptimizedInternal(tableList);
            log.info("列元数据同步完成");
            
            log.info("批量同步元数据完成，datasourceId={}, 同步表数量={}", datasourceId, tableList.size());
            return true;
        } catch (Exception e) {
            log.error("批量同步元数据失败，datasourceId={}", datasourceId, e);
            throw new CustomException("error occurs when sync metadata schema: " + e.getMessage());
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceType();
        }
    }

    /**
     * 批量同步表元数据 - 内部方法，假设数据源已设置
     */
    private List<TableDTO> syncMetaTablesOptimizedInternal(Long datasourceId, List<String> dbTables) {
        List<MetaTableEntity> tableList = new ArrayList<>();
        
        // 按数据库分组
        Map<String, List<String>> dbToTablesMap = dbTables.stream()
                .collect(Collectors.groupingBy(
                        dbTable -> dbTable.split("\\.")[0],
                        Collectors.mapping(dbTable -> dbTable.split("\\.")[1], Collectors.toList())
                ));
        
        log.info("准备批量同步表，涉及 {} 个数据库，共 {} 张表", 
                dbToTablesMap.keySet().size(), dbTables.size());

        // 批量查询所有数据库的所有表（一次查询）
        List<String> allDbNames = new ArrayList<>(dbToTablesMap.keySet());
        List<SchemaTableEntity> allSchemaTables = mysqlSchemaMapper.listAllTablesByDatabases(allDbNames);
        
        log.info("从数据源查询到 {} 张表", allSchemaTables.size());

        // 处理查询结果
        int insertCount = 0;
        int updateCount = 0;
        int skipCount = 0;
        
        for (SchemaTableEntity schemaTable : allSchemaTables) {
            String dbName = schemaTable.getTableSchema();
            List<String> tableNames = dbToTablesMap.get(dbName);
            
            if (tableNames != null && tableNames.contains(schemaTable.getTableName())) {
                MetaDbEntity metaDb = metaDbService.getMetaDb(datasourceId, dbName);
                if (metaDb == null) {
                    log.warn("数据库不存在，跳过：dbName={}", dbName);
                    skipCount++;
                    continue;
                }
                Long dbId = metaDb.getDbId();
                
                MetaTableEntity existingTable = metaTableService.getMetaTable(dbId, schemaTable.getTableName());
                if (ObjectUtils.isEmpty(existingTable)) {
                    // 新增表
                    MetaTableEntity tableEntity = new MetaTableEntity();
                    tableEntity.setDbId(dbId);
                    tableEntity.setTableName(schemaTable.getTableName());
                    tableEntity.setComment(schemaTable.getTableComment());
                    tableEntity.setType(schemaTable.getTableType());
                    tableEntity.setEngine(schemaTable.getEngine());
                    tableEntity.setTableCreateTime(schemaTable.getCreateTime());
                    tableEntity.setDbName(dbName);
                    tableList.add(tableEntity);
                    insertCount++;
                } else {
                    // 更新已有表
                    existingTable.setDbName(dbName);
                    existingTable.setComment(schemaTable.getTableComment());
                    existingTable.setType(schemaTable.getTableType());
                    existingTable.setEngine(schemaTable.getEngine());
                    existingTable.setTableCreateTime(schemaTable.getCreateTime());
                    tableList.add(existingTable);
                    updateCount++;
                }
            }
        }
        
        log.info("表元数据处理完成：新增={}, 更新={}, 跳过={}", insertCount, updateCount, skipCount);

        if (!tableList.isEmpty()) {
            log.info("准备保存/更新 {} 张表元数据", tableList.size());
            boolean success = metaTableService.saveOrUpdateBatch(tableList);
            if (success) {
                log.info("✅ 批量保存表元数据成功，保存数量={}", tableList.size());
            } else {
                log.error("❌ 批量保存表元数据失败，保存数量={}", tableList.size());
            }
        } else {
            log.info("没有需要保存的表元数据");
        }
        
        return tableList.stream().map(TableDTO::new).collect(Collectors.toList());
    }

    /**
     * sync schema columns - 内部方法，假设数据源已设置
     */
    private void syncColumnsOptimizedInternal(List<TableDTO> tableList) {
        if (tableList == null || tableList.isEmpty()) {
            log.info("没有需要同步列的表");
            return;
        }

        log.info("准备同步 {} 张表的列元数据", tableList.size());

        // 1. 按数据库分组
        Map<String, List<TableDTO>> dbTableMap = tableList.stream()
            .collect(Collectors.groupingBy(TableDTO::getDbName));
        
        // 2. 批量获取所有列信息（一次查询代替 N 次查询）
        List<SchemaColumnEntity> allColumns = new ArrayList<>();
        for (Map.Entry<String, List<TableDTO>> entry : dbTableMap.entrySet()) {
            String dbName = entry.getKey();
            List<String> tableNames = entry.getValue().stream()
                .map(TableDTO::getTableName)
                .collect(Collectors.toList());
            
            log.debug("批量查询数据库 {} 下 {} 张表的列信息", dbName, tableNames.size());
            // 批量查询该数据库下所有表的列信息
            List<SchemaColumnEntity> columns = mysqlSchemaMapper.listAllColumnsByTables(dbName, tableNames);
            log.debug("查询到 {} 个列", columns.size());
            allColumns.addAll(columns);
        }
        
        log.info("总共查询到 {} 个列", allColumns.size());
        
        // 3. 构建表 ID 到表信息的映射
        Map<Long, TableDTO> tableIdMap = tableList.stream()
            .collect(Collectors.toMap(TableDTO::getTableId, t -> t));
        
        // 4. 将列信息转换为实体列表
        List<MetaColumnEntity> columnEntities = allColumns.stream().map(entity -> {
            MetaColumnEntity column = new MetaColumnEntity();
            long tableId = findTableId(entity.getTableSchema(), entity.getTableName(), tableList);
            column.setTableId(tableId);
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
        
        log.info("准备保存 {} 个列元数据", columnEntities.size());
        
        // 5. 删除旧数据并保存新数据
        Map<Long, List<MetaColumnEntity>> columnsByTable = columnEntities.stream()
            .collect(Collectors.groupingBy(MetaColumnEntity::getTableId));
        
        int deleteCount = 0;
        for (Long tableId : columnsByTable.keySet()) {
            metaColumnService.removeColumnsByTableId(tableId);
            deleteCount++;
        }
        log.info("删除了 {} 张表的旧列数据", deleteCount);
        
        // 批量保存
        if (!columnEntities.isEmpty()) {
            boolean success = metaColumnService.saveBatch(columnEntities);
            if (success) {
                log.info("✅ 批量保存列元数据成功，保存数量={}", columnEntities.size());
            } else {
                log.error("❌ 批量保存列元数据失败，保存数量={}", columnEntities.size());
            }
        }
    }

    /**
     * 同步数据库 - 内部方法，假设数据源已设置
     */
    private void syncMetaDbsInternal(Long datasourceId, Set<String> dbNames) {
        List<MetaDbEntity> dbList = new ArrayList<>();
        int insertCount = 0;
        for (String dbName : dbNames) {
            boolean metaDbExists = metaDbService.isMetaDbExists(datasourceId, dbName);
            if (!metaDbExists) {
                MetaDbEntity dbEntity = new MetaDbEntity();
                dbEntity.setDbName(dbName);
                dbEntity.setDatasourceId(datasourceId);
                dbList.add(dbEntity);
                insertCount++;
            }
        }
        
        if (!dbList.isEmpty()) {
            boolean success = metaDbService.saveBatch(dbList);
            if (success) {
                log.info("✅ 批量保存数据库元数据成功，保存数量={}", dbList.size());
            } else {
                log.error("❌ 批量保存数据库元数据失败，保存数量={}", dbList.size());
            }
        } else {
            log.info("所有数据库已存在，无需新增");
        }
        log.info("数据库元数据同步完成，新增={}, 总计={}", insertCount, dbNames.size());
    }

    /**
     * 根据数据库名和表名查找表 ID
     */
    private long findTableId(String dbName, String tableName, List<TableDTO> tableList) {
        return tableList.stream()
            .filter(t -> t.getDbName().equals(dbName) && t.getTableName().equals(tableName))
            .findFirst()
            .map(TableDTO::getTableId)
            .orElse(0L);
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
