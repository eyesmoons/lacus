package com.lacus.domain.datasync.job;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.datasync.entity.*;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDbTableEntity;
import com.lacus.domain.datasync.job.command.AddJobCommand;
import com.lacus.domain.datasync.job.dto.*;
import com.lacus.domain.datasync.job.model.DataSyncJobModel;
import com.lacus.domain.datasync.job.model.DataSyncJobModelFactory;
import com.lacus.domain.datasync.job.query.JobQuery;
import com.lacus.domain.datasync.job.query.MappedColumnQuery;
import com.lacus.domain.datasync.job.query.MappedTableQuery;
import com.lacus.service.datasync.IDataSyncColumnMappingService;
import com.lacus.service.datasync.IDataSyncJobService;
import com.lacus.service.datasync.IDataSyncTableMappingService;
import com.lacus.service.metadata.IMetaColumnService;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.metadata.IMetaTableService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private IDataSyncJobService dataSyncJobService;

    @Autowired
    private IMetaDataSourceService dataSourceService;

    @Autowired
    private IDataSyncTableMappingService tableMappingService;

    @Autowired
    private IDataSyncColumnMappingService columnMappingService;

    @Autowired
    private IMetaTableService metaTableService;

    @Autowired
    private IMetaColumnService metaColumnService;

    public PageDTO pageList(JobQuery query) {
        Page page = dataSyncJobService.page(query.toPage(), query.toQueryWrapper());
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public void addJob(AddJobCommand addJobCommand) {
        // save job info
        DataSyncJobModel jobModel = saveJob(addJobCommand);
        // save job source table and column
        saveTableMappings(jobModel.getJobId(), addJobCommand);
    }

    private void saveTableMappings(Long jobId, AddJobCommand addJobCommand) {
        // TODO 重置表和字段映射关系
        List<TableMapping> tableMappings = addJobCommand.getTableMappings();
        List<DataSyncTableMappingEntity> tableMappingEntities = new ArrayList<>();
        List<DataSyncColumnMappingEntity> columnMappingEntities = new ArrayList<>();
        for (TableMapping tableMapping : tableMappings) {
            // save source table
            DataSyncSourceTableEntity sourceTableEntity = new DataSyncSourceTableEntity();
            sourceTableEntity.setJobId(jobId);
            sourceTableEntity.setSourceDbName(addJobCommand.getSourceDbName());
            sourceTableEntity.setSourceTableName(tableMapping.getSourceTableName());
            sourceTableEntity.insert();

            // save sink table
            DataSyncSinkTableEntity sinkTableEntity = new DataSyncSinkTableEntity();
            sinkTableEntity.setJobId(jobId);
            sinkTableEntity.setSinkDbName(addJobCommand.getSinkDbName());
            sinkTableEntity.setSinkTableName(tableMapping.getSinkTableName());
            sinkTableEntity.insert();

            // add table mapping list
            DataSyncTableMappingEntity tableMappingEntity = new DataSyncTableMappingEntity();
            tableMappingEntity.setJobId(jobId);
            tableMappingEntity.setSourceTableId(sourceTableEntity.getSourceTableId());
            tableMappingEntity.setSinkTableId(sinkTableEntity.getSinkTableId());
            tableMappingEntities.add(tableMappingEntity);

            List<ColumnMapping> mappingColumns = tableMapping.getColumns();
            for (ColumnMapping mappingColumn : mappingColumns) {
                // save source column
                DataSyncSourceColumnEntity sourceColumnEntity = new DataSyncSourceColumnEntity();
                sourceColumnEntity.setJobId(jobId);
                sourceColumnEntity.setSourceTableId(sourceTableEntity.getSourceTableId());
                sourceColumnEntity.setSourceColumnName(mappingColumn.getSourceColumn());
                sourceColumnEntity.insert();

                // save sink column
                DataSyncSinkColumnEntity sinkColumnEntity = new DataSyncSinkColumnEntity();
                sinkColumnEntity.setJobId(jobId);
                sinkColumnEntity.setSinkTableId(sinkTableEntity.getSinkTableId());
                sinkColumnEntity.setSinkColumnName(mappingColumn.getSinkColumn());
                sinkColumnEntity.insert();

                // add column mapping list
                DataSyncColumnMappingEntity columnMappingEntity = new DataSyncColumnMappingEntity();
                columnMappingEntity.setJobId(jobId);
                columnMappingEntity.setSourceColumnId(sourceColumnEntity.getSourceColumnId());
                columnMappingEntity.setSinkColumnId(sinkColumnEntity.getSinkColumnId());
                columnMappingEntities.add(columnMappingEntity);
            }
        }
        // save table mapping list
        tableMappingService.saveBatch(tableMappingEntities);

        // save column mapping list
        columnMappingService.saveBatch(columnMappingEntities);
    }

    /**
     * 保存任务信息
     * @param addJobCommand 入参
     */
    private DataSyncJobModel saveJob(AddJobCommand addJobCommand) {
        DataSyncJobModel model = DataSyncJobModelFactory.loadFromAddCommand(addJobCommand, new DataSyncJobModel());
        model.insert();
        return model;
    }

    private String buildSourceConf(AddJobCommand command) {
        SourceConf sourceConf = new SourceConf();
        String sourceDbName = command.getSourceDbName();
        List<TableMapping> tableMappings = command.getTableMappings();
        List<String> sourceTables = tableMappings.stream().map(TableMapping::getSourceTableName).collect(Collectors.toList());
        MetaDatasourceEntity metaDatasource = dataSourceService.getById(command.getSourceDatasourceId());
        if (ObjectUtils.isNotEmpty(metaDatasource)) {
            sourceConf.setHostname(metaDatasource.getIp());
            sourceConf.setPort(metaDatasource.getPort());
            sourceConf.setUsername(metaDatasource.getUsername());
            sourceConf.setPassword(metaDatasource.getPassword());
            sourceConf.setDatabaseList(Collections.singletonList(sourceDbName));
            sourceConf.setTableList(sourceTables);
        }
        return JSON.toJSONString(sourceConf);
    }

    public MappedTableDTO listMappedTable(MappedTableQuery query) {
        Long jobId = query.getJobId();
        MappedTableDTO result = new MappedTableDTO();
        LinkedList<TableDTO> sourceTables = new LinkedList<>();
        LinkedList<TableDTO> sinkTables = new LinkedList<>();
        result.setJobId(jobId);
        result.setMappedSourceTables(sourceTables);
        result.setMappedSinkTables(sinkTables);
        Long sourceDatasourceId = query.getSourceDatasourceId();
        String sourceDbName = query.getSourceDbName();
        List<String> sourceTableNames = query.getSourceTableNames();
        Long sinkDatasourceId = query.getSinkDatasourceId();
        String sinkDbName = query.getSinkDbName();

        // 新增
        if (ObjectUtils.isEmpty(jobId)) {
            // 查询输出源所有的表元数据
            List<MetaDbTableEntity> sinkMetaTables = metaTableService.getMetaTables(sinkDatasourceId, sinkDbName);
            Map<String, MetaDbTableEntity> sinkMetaTablesMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(sinkMetaTables)) {
                sinkMetaTablesMap = sinkMetaTables.stream().collect(Collectors.toMap(MetaDbTableEntity::getTableName, t -> t));
            }

            // 查询选中的输入源表元数据
            List<MetaDbTableEntity> params = new ArrayList<>();
            for (String dbTable : sourceTableNames) {
                String[] dbTableArr = dbTable.split("\\.");
                String dbName = dbTableArr[0];
                String tableName = dbTableArr[1];
                MetaDbTableEntity dbTableEntity = new MetaDbTableEntity();
                dbTableEntity.setDatasourceId(sourceDatasourceId);
                dbTableEntity.setDbName(dbName);
                dbTableEntity.setTableName(tableName);
                params.add(dbTableEntity);
            }
            // 批量查询元数据表信息
            List<MetaDbTableEntity> sourceMetaTables = metaTableService.listMetaTable(params);

            // 则按照对应原则自动匹配表
            for (String sourceDbTable : sourceTableNames) {
                String[] sourceDbTableArr = sourceDbTable.split("\\.");
                String sourceTableName = sourceDbTableArr[1];
                TableDTO sourceTableDTO = getMetaTableDTO(sourceMetaTables, sourceDatasourceId, sourceDbName, sourceTableName);
                sourceTables.add(sourceTableDTO);

                if (ObjectUtils.isNotEmpty(sinkMetaTablesMap)) {
                    // 按照ods_库名_表名_delta匹配
                    MetaDbTableEntity sinkMetaTblByDbTableRule = sinkMetaTablesMap.get("ods_" + sourceDbName + "_" + sourceTableName + "_delta");
                    // 按照ods_表名_delta匹配
                    MetaDbTableEntity sinkMetaTblByTableRule = sinkMetaTablesMap.get("ods_" + sourceTableName + "_delta");
                    // 按照dim_库名_表名匹配
                    MetaDbTableEntity sinkMetaTblByDimRule = sinkMetaTablesMap.get("dim_" + sourceDbName + "_" + sourceTableName);
                    // 按照表名相同匹配
                    MetaDbTableEntity targetMetaTblByTheSame = sinkMetaTablesMap.get(sourceTableName);
                    if (Objects.nonNull(sinkMetaTblByDbTableRule)) {
                        sinkTables.add(getMetaTableInfo(sinkDatasourceId, sinkDbName, sinkMetaTblByDbTableRule.getTableName(), sinkMetaTblByDbTableRule.getTableId()));
                    } else if (Objects.nonNull(sinkMetaTblByTableRule)) {
                        sinkTables.add(getMetaTableInfo(sinkDatasourceId, sinkDbName, sinkMetaTblByTableRule.getTableName(), sinkMetaTblByTableRule.getTableId()));
                    } else if (Objects.nonNull(sinkMetaTblByDimRule)) {
                        sinkTables.add(getMetaTableInfo(sinkDatasourceId, sinkDbName, sinkMetaTblByDimRule.getTableName(), sinkMetaTblByDimRule.getTableId()));
                    } else if (Objects.nonNull(targetMetaTblByTheSame)) {
                        sinkTables.add(getMetaTableInfo(sinkDatasourceId, sinkDbName, targetMetaTblByTheSame.getTableName(), targetMetaTblByTheSame.getTableId()));
                    }
                }
            }
        } else {
            // 如果只传了jobId，则查询所有的已接入的表(任务详情页面)
            if (ObjectUtils.isEmpty(sourceTableNames)) {
                LinkedList<DataSyncSavedTable> savedTables = tableMappingService.listSavedTables(jobId);
                if (ObjectUtils.isNotEmpty(savedTables)) {
                    Map<String, List<MetaDbTableEntity>> metaTablesMap = convertMetaTables(savedTables);
                    List<MetaDbTableEntity> sourceMetaTables = metaTablesMap.get("sourceTables");
                    List<MetaDbTableEntity> sinkMetaTables = metaTablesMap.get("sinkTables");
                    for (DataSyncSavedTable savedTable : savedTables) {
                        TableDTO sourceTableDTO = getMetaTableDTO(sourceMetaTables, savedTable.getSourceDatasourceId(), savedTable.getSourceDbName(), savedTable.getSourceTableName());
                        sourceTables.add(sourceTableDTO);
                        TableDTO sinkTableDTO = getMetaTableDTO(sinkMetaTables, savedTable.getSinkDatasourceId(), savedTable.getSinkDbName(), savedTable.getSinkTableName());
                        sinkTables.add(sinkTableDTO);
                    }
                }
            } else { // 已保存和未保存的表合集（任务编辑页面）
                Map<String, Object> mappedAndSourceMetaTablesMap = convertMappedAndSourceMetaTables(sourceTableNames, jobId, sourceDatasourceId);
                List<DataSyncSavedTable> allMappedTables = (List<DataSyncSavedTable>)mappedAndSourceMetaTablesMap.get("allMappedTables");
                List<MetaDbTableEntity> sourceMetaTables = (List<MetaDbTableEntity>)mappedAndSourceMetaTablesMap.get("sourceMetaTables");
                for (String sourceDbTable : sourceTableNames) {
                    String[] sourceDbTableArr = sourceDbTable.split("\\.");
                    String sourceTableName = sourceDbTableArr[1];
                    List<DataSyncSavedTable> mappedTables = new ArrayList<>();
                    if (ObjectUtils.isNotEmpty(allMappedTables)) {
                        mappedTables = (List<DataSyncSavedTable>)CollectionUtils.select(allMappedTables, item ->
                                item.getSourceDatasourceId().equals(sourceDatasourceId) &&
                                        item.getSourceDbName().equals(sourceDbName) &&
                                        item.getSourceTableName().equals(sourceTableName));
                    }

                    // 处理未匹配的表（新增或者修改的表），sink 为 null
                    if (ObjectUtils.isEmpty(mappedTables)) {
                        TableDTO sourceTableDTO = getMetaTableDTO(sourceMetaTables, sourceDatasourceId, sourceDbName, sourceTableName);
                        sourceTables.add(sourceTableDTO);
                    } else { // 处理已经存在的映射表（之前已经保存过的映射关系）
                        DataSyncSavedTable mappedTable = mappedTables.get(0);
                        TableDTO sourceTableDTO = getMetaTableDTO(sourceMetaTables, mappedTable.getSourceDatasourceId(), mappedTable.getSourceDbName(), mappedTable.getSourceTableName());
                        sourceTables.add(sourceTableDTO);

                        // 批量查询元数据表信息
                        MetaDbTableEntity sinkTableParam = new MetaDbTableEntity();
                        sinkTableParam.setDatasourceId(mappedTable.getSinkDatasourceId());
                        sinkTableParam.setDbName(mappedTable.getSinkDbName());
                        sinkTableParam.setTableName(mappedTable.getSinkTableName());
                        List<MetaDbTableEntity> sinkMetaTables = metaTableService.listMetaTable(Collections.singletonList(sinkTableParam));
                        TableDTO sinkTableDTO = getMetaTableDTO(sinkMetaTables, mappedTable.getSinkDatasourceId(), mappedTable.getSinkDbName(), mappedTable.getSinkTableName());
                        sinkTables.add(sinkTableDTO);
                    }
                }
            }
        }
        return result;
    }

    private Map<String, Object> convertMappedAndSourceMetaTables(List<String> sourceDbTables, Long jobId, Long sourceDatasourceId) {
        Map<String, Object> mappedAndSourceMetaTablesMap = new HashMap<>();
        List<DataSyncSavedTable> params = new ArrayList<>();
        List<MetaDbTableEntity> sourceParams = new ArrayList<>();
        for (String item : sourceDbTables) {
            String[] dbTableArr = item.split("\\.");
            String sourceTableName = dbTableArr[0];
            String sourceDbName = dbTableArr[1];
            DataSyncSavedTable tpl = new DataSyncSavedTable();
            tpl.setJobId(jobId);
            tpl.setSourceDatasourceId(sourceDatasourceId);
            tpl.setSourceDbName(sourceTableName);
            tpl.setSourceTableName(sourceDbName);
            params.add(tpl);

            MetaDbTableEntity sourceMetaTbl = new MetaDbTableEntity();
            sourceMetaTbl.setDatasourceId(sourceDatasourceId);
            sourceMetaTbl.setDbName(sourceDbName);
            sourceMetaTbl.setTableName(sourceTableName);
            sourceParams.add(sourceMetaTbl);
        }
        mappedAndSourceMetaTablesMap.put("allMappedTables", tableMappingService.listSavedTables(params));
        mappedAndSourceMetaTablesMap.put("sourceMetaTables", metaTableService.listMetaTable(sourceParams));
        return mappedAndSourceMetaTablesMap;
    }

    private TableDTO getMetaTableDTO(List<MetaDbTableEntity> metaTables, Long datasourceId, String dbName, String tableName) {
        TableDTO tableDTO = new TableDTO();
        tableDTO.setDatasourceId(datasourceId);
        tableDTO.setDbName(dbName);
        tableDTO.setTableName(tableName);
        setMetaTableId(datasourceId, dbName, metaTables, tableName, tableDTO);
        return tableDTO;
    }

    /**
     * 批量查询输入和输出表元数据
     * @param mappedTables 已经保存的表
     */
    private Map<String, List<MetaDbTableEntity>> convertMetaTables(List<DataSyncSavedTable> mappedTables) {
        Map<String, List<MetaDbTableEntity>> metaTablesMap = new HashMap<>();
        List<MetaDbTableEntity> sourceParams = new ArrayList<>();
        List<MetaDbTableEntity> targetParams = new ArrayList<>();
        for (DataSyncSavedTable item : mappedTables) {
            MetaDbTableEntity sourceMetaTbl = new MetaDbTableEntity();
            sourceMetaTbl.setDatasourceId(item.getSourceDatasourceId());
            sourceMetaTbl.setDbName(item.getSourceDbName());
            sourceMetaTbl.setTableName(item.getSourceTableName());
            sourceParams.add(sourceMetaTbl);

            MetaDbTableEntity targetMetaTbl = new MetaDbTableEntity();
            targetMetaTbl.setDatasourceId(item.getSinkDatasourceId());
            targetMetaTbl.setDbName(item.getSinkDbName());
            targetMetaTbl.setTableName(item.getSinkTableName());
            targetParams.add(targetMetaTbl);
        }
        metaTablesMap.put("sourceTables", metaTableService.listMetaTable(sourceParams));
        metaTablesMap.put("sinkTables", metaTableService.listMetaTable(targetParams));
        return metaTablesMap;
    }

    private TableDTO getMetaTableInfo(Long datasourceId, String dbName, String tableName, Long tableId) {
        TableDTO metaTable = new TableDTO();
        metaTable.setDatasourceId(datasourceId);
        metaTable.setDbName(dbName);
        metaTable.setTableName(tableName);
        metaTable.setMetaTableId(tableId);
        return metaTable;
    }

    /**
     * 设置metaTableId
     * @param datasourceId 数据源ID
     * @param DbName 数据库名
     * @param metaDbTableEntities 元数据库表
     * @param tableName 表名
     * @param tableDTO 映射表信息
     */
    private void setMetaTableId(Long datasourceId, String DbName, List<MetaDbTableEntity> metaDbTableEntities, String tableName, TableDTO tableDTO) {
        List<MetaDbTableEntity> metaTables = (List<MetaDbTableEntity>) CollectionUtils.select(metaDbTableEntities, item ->
                        item.getDatasourceId().equals(datasourceId) &&
                        item.getDbName().equals(DbName) &&
                        item.getTableName().equals(tableName));
        if (ObjectUtils.isNotEmpty(metaTables)) {
            tableDTO.setMetaTableId(metaTables.get(0).getTableId());
        }
    }

    public MappedColumnDTO listMappedColumn(MappedColumnQuery query) {
        String sourceTableNameStr = query.getSourceTableName();
        String[] sourceTableNameArr = sourceTableNameStr.split("\\.");
        String sourceTableName = sourceTableNameArr[1];
        MappedColumnDTO result = new MappedColumnDTO();
        LinkedList<ColumnDTO> sourceColumns = new LinkedList<>();
        LinkedList<ColumnDTO> sinkColumns = new LinkedList<>();
        result.setMappedSourceColumns(sourceColumns);
        result.setMappedSinkColumns(sinkColumns);

        DataSyncSavedColumn tpl = new DataSyncSavedColumn();
        tpl.setJobId(query.getJobId());
        List<DataSyncSavedColumn> savedColumnList = columnMappingService.querySavedColumns(tpl);
        if (ObjectUtils.isNotEmpty(savedColumnList)) {
            List<String> savedColumnNames = savedColumnList.stream().map(DataSyncSavedColumn::getSourceColumnName).collect(Collectors.toList());
            DataSyncSavedColumn savedColumn0 = savedColumnList.get(0);
            List<MetaColumnEntity> sourceMetaColumns = metaColumnService.getColumnsByTableName(savedColumn0.getSourceDatasourceId(), savedColumn0.getSourceDbName(), savedColumn0.getSourceTableName());
            List<MetaColumnEntity> sinkMetaColumns = metaColumnService.getColumnsByTableName(savedColumn0.getSinkDatasourceId(), savedColumn0.getSinkDbName(), savedColumn0.getSinkTableName());

            Map<String, MetaColumnEntity> sourceMetaColumnMap = new HashMap<>();
            Map<String, MetaColumnEntity> sinkMetaColumnMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(sourceMetaColumns)) {
                sourceMetaColumnMap = sourceMetaColumns.stream().collect(Collectors.toMap(MetaColumnEntity::getColumnName, t -> t));
            }
            if (ObjectUtils.isNotEmpty(sinkMetaColumns)) {
                sinkMetaColumnMap = sinkMetaColumns.stream().collect(Collectors.toMap(MetaColumnEntity::getColumnName, t -> t));
            }

            // 遍历已经保存的字段
            for (DataSyncSavedColumn savedColumn : savedColumnList) {
                MetaColumnEntity sourceMetaColumn = sourceMetaColumnMap.get(savedColumn.getSourceColumnName());
                MetaColumnEntity sinkMetaColumn = sinkMetaColumnMap.get(savedColumn.getSinkColumnName());
                // 输入源填充元数据信息
                fillMetaColumn(sourceColumns, savedColumn.getSourceDatasourceId(), savedColumn.getSourceDbName(), savedColumn.getSourceTableName(), savedColumn.getSourceColumnName(), sourceMetaColumn);
                // 输出源填充元数据信息
                fillMetaColumn(sinkColumns, savedColumn.getSinkDatasourceId(), savedColumn.getSinkDbName(), savedColumn.getSinkTableName(), savedColumn.getSinkColumnName(), sinkMetaColumn);
            }

            // 处理未保存的字段
            for (MetaColumnEntity sourceMetaColumn : sourceMetaColumns) {
                if (!savedColumnNames.contains(sourceMetaColumn.getColumnName())) {
                    // 输入源填充元数据信息
                    fillMetaColumn(sourceColumns, savedColumn0.getSourceDatasourceId(), savedColumn0.getSourceDbName(), savedColumn0.getSourceTableName(), sourceMetaColumn.getColumnName(), sourceMetaColumn);
                    // 输出字段信息设置为空
                    sinkColumns.add(new ColumnDTO());
                }
            }
        } else {
            List<MetaColumnEntity> sourceMetaColumns = metaColumnService.getColumnsByTableName(query.getSourceDatasourceId(), query.getSourceDbName(), sourceTableName);
            for (MetaColumnEntity sourceMetaColumn : sourceMetaColumns) {
                // 输入源填充元数据信息
                fillMetaColumn(sourceColumns, query.getSourceDatasourceId(), query.getSourceDbName(), query.getSourceTableName(), sourceMetaColumn.getColumnName(), sourceMetaColumn);
                // 输出字段信息设置为空
                sinkColumns.add(new ColumnDTO());
            }
        }
        return result;
    }

    /**
     * 填充元数据信息
     * @param columnDTOs 已保存的字段列表
     * @param datasourceId 数据源ID
     * @param dbName 数据库名称
     * @param tableName 数据表名称
     * @param columnName 字段名
     * @param metaColumn 元数据信息
     */
    private void fillMetaColumn(LinkedList<ColumnDTO> columnDTOs,
                                            Long datasourceId,
                                            String dbName,
                                            String tableName,
                                            String columnName,
                                            MetaColumnEntity metaColumn) {
        ColumnDTO columnDTO = new ColumnDTO();
        columnDTO.setDatasourceId(datasourceId);
        columnDTO.setDbName(dbName);
        columnDTO.setTableName(tableName);
        columnDTO.setColumnName(columnName);
        columnDTO.setDataType(metaColumn.getDataType());
        columnDTO.setColumnLength(metaColumn.getColumnLength());
        columnDTO.setComment(metaColumn.getComment());
        columnDTOs.add(columnDTO);
    }
}