package com.lacus.domain.metadata.table;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.domain.metadata.table.dto.TableDTO;
import com.lacus.domain.metadata.table.query.TableDetailQuery;
import com.lacus.domain.metadata.table.query.TableQuery;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.metadata.IMetaDbService;
import com.lacus.service.metadata.IMetaTableService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TableBusiness {

    @Autowired
    private IMetaTableService metaTableService;

    @Autowired
    private IMetaDbService metaDbService;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    public PageDTO pageList(TableQuery query) {
        Long metaDatasourceId = query.getDatasourceId();
        Long dbId = query.getDbId();
        if (metaDatasourceId == null && dbId != null) {
            query.setDbIds(Collections.singletonList(dbId));
        } else {
            List<MetaDbEntity> dbList = metaDbService.listByDatasourceId(metaDatasourceId);
            if (ObjectUtils.isEmpty(dbList)) {
                query.setDbIds(Collections.singletonList(-1L));
            } else {
                List<Long> dbIds = dbList.stream().map(MetaDbEntity::getDbId).collect(Collectors.toList());
                query.setDbIds(dbIds);
            }
        }
        Page<MetaTableEntity> page = metaTableService.page(query.toPage(), query.toQueryWrapper());
        List<TableDTO> records = page.getRecords().stream().map(entity -> {
            TableDTO tableDTO = new TableDTO(entity);
            MetaDbEntity dbEntity = metaDbService.getById(entity.getDbId());
            if (ObjectUtils.isNotEmpty(dbEntity)) {
                tableDTO.setDbName(dbEntity.getDbName());
                Long datasourceId = dbEntity.getDatasourceId();
                MetaDatasourceEntity datasourceEntity = metaDataSourceService.getById(datasourceId);
                if (ObjectUtils.isNotEmpty(datasourceEntity)) {
                    tableDTO.setDatasourceId(datasourceId);
                    tableDTO.setDatasourceName(datasourceEntity.getDatasourceName());
                }
            }
            return tableDTO;
        }).collect(Collectors.toList());
        return new PageDTO(records, page.getTotal());
    }

    public TableDTO getTableDetail(TableDetailQuery query) {
        TableDTO tableDTO = null;
        Long datasourceId = query.getDatasourceId();
        String dbName = query.getDbName();
        String tableName = query.getTableName();
        MetaDbEntity dbEntity = metaDbService.getMetaDb(datasourceId, dbName);
        if (ObjectUtils.isNotEmpty(dbEntity)) {
            Long dbId = dbEntity.getDbId();
            MetaTableEntity tableEntity = metaTableService.getMetaTable(dbId, tableName);
            MetaDatasourceEntity datasourceEntity = metaDataSourceService.getById(datasourceId);
            tableDTO = new TableDTO(tableEntity);
            tableDTO.setDbName(dbEntity.getDbName());
            tableDTO.setDatasourceId(datasourceEntity.getDatasourceId());
            tableDTO.setDatasourceName(datasourceEntity.getDatasourceName());
        }
        return tableDTO;
    }

    public TableDTO getTableDetailById(Long tableId) {
        MetaTableEntity tableEntity = metaTableService.getById(tableId);
        TableDTO tableDTO = new TableDTO(tableEntity);
        Long dbId = tableEntity.getDbId();
        MetaDbEntity dbEntity = metaDbService.getById(dbId);
        tableDTO.setDbName(dbEntity.getDbName());
        Long datasourceId = dbEntity.getDatasourceId();
        MetaDatasourceEntity datasourceEntity = metaDataSourceService.getById(datasourceId);
        tableDTO.setDatasourceId(datasourceId);
        tableDTO.setDatasourceName(datasourceEntity.getDatasourceName());
        return tableDTO;
    }

    public List<MetaTableEntity> listTable(TableQuery query) {
        Long metaDatasourceId = query.getDatasourceId();
        List<String> dbNames;
        if (ObjectUtils.isNotEmpty(query.getDbName())) {
            dbNames = Collections.singletonList(query.getDbName());
        } else if (ObjectUtils.isNotEmpty(query.getDbNames())){
            dbNames = query.getDbNames();
        } else {
            throw new ApiException(ErrorCode.Internal.INVALID_PARAMETER, "dbName或dbNames参数为空");
        }
        List<MetaDbEntity> dbList = metaDbService.getMetaDbs(metaDatasourceId, dbNames);
        Map<Long, String> dbMap = dbList.stream().collect(Collectors.toMap(MetaDbEntity::getDbId, MetaDbEntity::getDbName));
        List<Long> dbIds = dbList.stream().map(MetaDbEntity::getDbId).collect(Collectors.toList());
        List<MetaTableEntity> metaTables = metaTableService.getMetaTables(dbIds);
        for (MetaTableEntity table : metaTables) {
            String dbName = dbMap.get(table.getDbId());
            table.setDbName(dbName);
        }
        return metaTables;
    }
}
