package com.lacus.domain.metadata.virtual;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.entity.MetaDbEntity;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.service.metadata.IMetaColumnService;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.metadata.IMetaDbService;
import com.lacus.service.metadata.IMetaTableService;
import com.lacus.service.metadata.ILineageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟数据源元数据登记：虚拟库/虚拟表/虚拟字段仅写入 meta_* 表并带 VIRTUAL 标记，不创建物理资源。
 * 虚拟源判定依据 datasource.type（插件名，KAFKA/HDFS，与前端 VIRTUAL_DATASOURCE_TYPES 一致）。
 */
@Slf4j
@Service
public class VirtualCatalogBusiness {

    private static final String KAFKA = "KAFKA";
    private static final String HDFS = "HDFS";
    private static final String VIRTUAL_KAFKA = "VIRTUAL_KAFKA";
    private static final String VIRTUAL_HDFS = "VIRTUAL_HDFS";

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private IMetaDbService metaDbService;

    @Autowired
    private IMetaTableService metaTableService;

    @Autowired
    private IMetaColumnService metaColumnService;

    @Autowired
    private ILineageService lineageService;

    public Long createDb(Long datasourceId, String dbName, String comment) {
        MetaDatasourceEntity ds = metaDataSourceService.getById(datasourceId);
        if (ds == null) {
            throw new CustomException("数据源不存在");
        }
        assertVirtualSource(ds.getType());
        Long count = metaDbService.count(new LambdaQueryWrapper<MetaDbEntity>()
                .eq(MetaDbEntity::getDatasourceId, datasourceId)
                .eq(MetaDbEntity::getDbName, dbName));
        if (count != null && count > 0) {
            throw new CustomException("数据库已存在");
        }
        MetaDbEntity entity = new MetaDbEntity();
        entity.setDatasourceId(datasourceId);
        entity.setDbName(dbName);
        entity.setComment(StringUtils.defaultString(comment));
        metaDbService.save(entity);
        return entity.getDbId();
    }

    public Long createTable(Long datasourceId, String dbName, String tableName, String comment) {
        MetaDbEntity db = metaDbService.getOne(new LambdaQueryWrapper<MetaDbEntity>()
                .eq(MetaDbEntity::getDatasourceId, datasourceId)
                .eq(MetaDbEntity::getDbName, dbName)
                .last("limit 1"));
        if (db == null) {
            throw new CustomException("虚拟数据库不存在，请先创建");
        }
        Long count = metaTableService.count(new LambdaQueryWrapper<MetaTableEntity>()
                .eq(MetaTableEntity::getDbId, db.getDbId())
                .eq(MetaTableEntity::getTableName, tableName));
        if (count != null && count > 0) {
            throw new CustomException("表已存在");
        }
        MetaTableEntity entity = new MetaTableEntity();
        entity.setDbId(db.getDbId());
        entity.setTableName(tableName);
        entity.setTableType(tableTypeOf(datasourceId));
        entity.setComment(StringUtils.defaultString(comment));
        metaTableService.save(entity);
        return entity.getTableId();
    }

    public Long createColumn(Long datasourceId, String dbName, String tableName,
                             String columnName, String dataType, String comment) {
        MetaDbEntity db = metaDbService.getOne(new LambdaQueryWrapper<MetaDbEntity>()
                .eq(MetaDbEntity::getDatasourceId, datasourceId)
                .eq(MetaDbEntity::getDbName, dbName)
                .last("limit 1"));
        if (db == null) {
            throw new CustomException("虚拟数据库不存在，请先创建");
        }
        MetaTableEntity table = metaTableService.getOne(new LambdaQueryWrapper<MetaTableEntity>()
                .eq(MetaTableEntity::getDbId, db.getDbId())
                .eq(MetaTableEntity::getTableName, tableName)
                .last("limit 1"));
        if (table == null) {
            throw new CustomException("虚拟表不存在，请先创建");
        }
        Long count = metaColumnService.count(new LambdaQueryWrapper<MetaColumnEntity>()
                .eq(MetaColumnEntity::getTableId, table.getTableId())
                .eq(MetaColumnEntity::getColumnName, columnName));
        if (count != null && count > 0) {
            throw new CustomException("字段已存在");
        }
        MetaColumnEntity entity = new MetaColumnEntity();
        entity.setTableId(table.getTableId());
        entity.setColumnName(columnName);
        entity.setDataType(StringUtils.defaultIfBlank(dataType, "string"));
        entity.setColumnType(StringUtils.defaultIfBlank(dataType, "string"));
        entity.setComment(StringUtils.defaultString(comment));
        metaColumnService.save(entity);
        return entity.getColumnId();
    }

    /**
     * 批量新增虚拟字段。每条字段含:字段名/类型/备注/是否为空/字符串长度。
     * 先做同名校验(库内现有 + 本次批量内重复),再逐条写入。
     */
    public List<Long> createColumnsBatch(Long datasourceId, String dbName, String tableName,
                                         List<ColumnItem> columns) {
        MetaDbEntity db = metaDbService.getOne(new LambdaQueryWrapper<MetaDbEntity>()
                .eq(MetaDbEntity::getDatasourceId, datasourceId)
                .eq(MetaDbEntity::getDbName, dbName)
                .last("limit 1"));
        if (db == null) {
            throw new CustomException("虚拟数据库不存在，请先创建");
        }
        MetaTableEntity table = metaTableService.getOne(new LambdaQueryWrapper<MetaTableEntity>()
                .eq(MetaTableEntity::getDbId, db.getDbId())
                .eq(MetaTableEntity::getTableName, tableName)
                .last("limit 1"));
        if (table == null) {
            throw new CustomException("虚拟表不存在，请先创建");
        }
        if (columns == null || columns.isEmpty()) {
            throw new CustomException("请至少添加一个字段");
        }
        // 本次批量内字段名去重
        List<String> seen = new ArrayList<>();
        for (ColumnItem col : columns) {
            if (StringUtils.isBlank(col.getColumnName())) {
                throw new CustomException("字段名不能为空");
            }
            if (seen.contains(col.getColumnName().trim())) {
                throw new CustomException("本次批量中存在重复字段名:" + col.getColumnName());
            }
            seen.add(col.getColumnName().trim());
        }
        // 与库内现有字段同名校验
        List<MetaColumnEntity> existing = metaColumnService.list(new LambdaQueryWrapper<MetaColumnEntity>()
                .eq(MetaColumnEntity::getTableId, table.getTableId()));
        for (MetaColumnEntity e : existing) {
            if (seen.contains(e.getColumnName())) {
                throw new CustomException("字段已存在:" + e.getColumnName());
            }
        }
        List<Long> ids = new ArrayList<>();
        for (ColumnItem col : columns) {
            MetaColumnEntity entity = new MetaColumnEntity();
            entity.setTableId(table.getTableId());
            entity.setColumnName(col.getColumnName().trim());
            entity.setDataType(StringUtils.defaultIfBlank(col.getDataType(), "string"));
            entity.setColumnType(StringUtils.defaultIfBlank(col.getDataType(), "string"));
            entity.setComment(StringUtils.defaultString(col.getComment()));
            entity.setIsNullable(col.getNullable() != null && col.getNullable() ? "YES" : "NO");
            if (col.getColumnLength() != null) {
                entity.setColumnLength(col.getColumnLength());
            }
            metaColumnService.save(entity);
            ids.add(entity.getColumnId());
        }
        return ids;
    }

    /** 批量字段项 */
    public static class ColumnItem {
        private String columnName;
        private String dataType;
        private String comment;
        private Boolean nullable;
        private Long columnLength;

        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public Boolean getNullable() { return nullable; }
        public void setNullable(Boolean nullable) { this.nullable = nullable; }
        public Long getColumnLength() { return columnLength; }
        public void setColumnLength(Long columnLength) { this.columnLength = columnLength; }
    }

    // ==================== 更新 ====================

    public void updateDb(Long dbId, String dbName, String comment) {
        MetaDbEntity db = metaDbService.getById(dbId);
        if (db == null) {
            throw new CustomException("数据库不存在");
        }
        assertVirtualSource(metaDataSourceService.getById(db.getDatasourceId()).getType());
        if (StringUtils.isNotBlank(dbName) && !dbName.equals(db.getDbName())) {
            Long count = metaDbService.count(new LambdaQueryWrapper<MetaDbEntity>()
                    .eq(MetaDbEntity::getDatasourceId, db.getDatasourceId())
                    .eq(MetaDbEntity::getDbName, dbName));
            if (count != null && count > 0) {
                throw new CustomException("数据库已存在");
            }
            db.setDbName(dbName);
        }
        if (comment != null) {
            db.setComment(comment);
        }
        metaDbService.updateById(db);
    }

    public void updateTable(Long tableId, String tableName, String comment) {
        MetaTableEntity table = metaTableService.getById(tableId);
        if (table == null) {
            throw new CustomException("表不存在");
        }
        if (StringUtils.isNotBlank(tableName) && !tableName.equals(table.getTableName())) {
            Long count = metaTableService.count(new LambdaQueryWrapper<MetaTableEntity>()
                    .eq(MetaTableEntity::getDbId, table.getDbId())
                    .eq(MetaTableEntity::getTableName, tableName));
            if (count != null && count > 0) {
                throw new CustomException("表已存在");
            }
            table.setTableName(tableName);
        }
        if (comment != null) {
            table.setComment(comment);
        }
        metaTableService.updateById(table);
    }

    public void updateColumn(Long columnId, String columnName, String dataType, String comment) {
        MetaColumnEntity column = metaColumnService.getById(columnId);
        if (column == null) {
            throw new CustomException("字段不存在");
        }
        if (StringUtils.isNotBlank(columnName) && !columnName.equals(column.getColumnName())) {
            Long count = metaColumnService.count(new LambdaQueryWrapper<MetaColumnEntity>()
                    .eq(MetaColumnEntity::getTableId, column.getTableId())
                    .eq(MetaColumnEntity::getColumnName, columnName));
            if (count != null && count > 0) {
                throw new CustomException("字段已存在");
            }
            column.setColumnName(columnName);
        }
        if (StringUtils.isNotBlank(dataType)) {
            column.setDataType(dataType);
            column.setColumnType(dataType);
        }
        if (comment != null) {
            column.setComment(comment);
        }
        metaColumnService.updateById(column);
    }

    // ==================== 删除 ====================

    public void deleteDb(Long dbId) {
        MetaDbEntity db = metaDbService.getById(dbId);
        if (db == null) {
            return;
        }
        assertVirtualSource(metaDataSourceService.getById(db.getDatasourceId()).getType());
        // 级联删除:库下的表→(字段+血缘)
        List<MetaTableEntity> tables = metaTableService.list(new LambdaQueryWrapper<MetaTableEntity>()
                .eq(MetaTableEntity::getDbId, dbId));
        for (MetaTableEntity table : tables) {
            deleteTableCascade(table.getTableId());
        }
        metaTableService.remove(new LambdaQueryWrapper<MetaTableEntity>()
                .eq(MetaTableEntity::getDbId, dbId));
        metaDbService.removeById(dbId);
    }

    public void deleteTable(Long tableId) {
        MetaTableEntity table = metaTableService.getById(tableId);
        if (table == null) {
            return;
        }
        deleteTableCascade(tableId);
        metaTableService.removeById(tableId);
    }

    /** 删除字段的级联操作:字段 + 该表关联的血缘边与节点 */
    private void deleteTableCascade(Long tableId) {
        metaColumnService.remove(new LambdaQueryWrapper<MetaColumnEntity>()
                .eq(MetaColumnEntity::getTableId, tableId));
        lineageService.deleteEdgesByTableId(tableId);
    }

    public void deleteColumn(Long columnId) {
        MetaColumnEntity column = metaColumnService.getById(columnId);
        if (column == null) {
            return;
        }
        metaColumnService.removeById(columnId);
    }

    private void assertVirtualSource(String type) {
        boolean virtual = KAFKA.equalsIgnoreCase(type) || HDFS.equalsIgnoreCase(type);
        if (!virtual) {
            throw new CustomException("仅虚拟数据源支持虚拟登记");
        }
    }

    private String tableTypeOf(Long datasourceId) {
        MetaDatasourceEntity ds = metaDataSourceService.getById(datasourceId);
        if (ds == null) {
            return VIRTUAL_HDFS;
        }
        if (KAFKA.equalsIgnoreCase(ds.getType())) {
            return VIRTUAL_KAFKA;
        }
        return VIRTUAL_HDFS;
    }
}