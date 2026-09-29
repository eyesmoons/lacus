package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.metadata.virtual.VirtualCatalogBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "虚拟登记", tags = {"虚拟登记"})
@RestController
@RequestMapping("/metadata/virtual")
public class VirtualCatalogController {

    @Autowired
    private VirtualCatalogBusiness virtualCatalogBusiness;

    @ApiOperation("虚拟数据源下新增虚拟数据库")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PostMapping("/db")
    public ResponseDTO<Long> createDb(@RequestBody DbCommand command) {
        Long dbId = virtualCatalogBusiness.createDb(
                command.getDatasourceId(), command.getDbName(), command.getComment());
        return ResponseDTO.ok(dbId);
    }

    @ApiOperation("虚拟数据库下新增虚拟表")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PostMapping("/table")
    public ResponseDTO<Long> createTable(@RequestBody TableCommand command) {
        Long tableId = virtualCatalogBusiness.createTable(
                command.getDatasourceId(), command.getDbName(), command.getTableName(), command.getComment());
        return ResponseDTO.ok(tableId);
    }

    @ApiOperation("虚拟表下新增虚拟字段")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PostMapping("/column")
    public ResponseDTO<Long> createColumn(@RequestBody ColumnCommand command) {
        Long columnId = virtualCatalogBusiness.createColumn(
                command.getDatasourceId(), command.getDbName(), command.getTableName(),
                command.getColumnName(), command.getDataType(), command.getComment());
        return ResponseDTO.ok(columnId);
    }

    @ApiOperation("批量新增虚拟字段")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PostMapping("/column/batch")
    public ResponseDTO<List<Long>> createColumnsBatch(@RequestBody BatchColumnCommand command) {
        List<Long> ids = virtualCatalogBusiness.createColumnsBatch(
                command.getDatasourceId(), command.getDbName(), command.getTableName(), command.getColumns());
        return ResponseDTO.ok(ids);
    }

    // ==================== 更新 ====================

    @ApiOperation("编辑虚拟数据库")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PutMapping("/db/{dbId}")
    public ResponseDTO<Void> updateDb(@PathVariable Long dbId, @RequestBody DbCommand command) {
        virtualCatalogBusiness.updateDb(dbId, command.getDbName(), command.getComment());
        return ResponseDTO.ok();
    }

    @ApiOperation("编辑虚拟表")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PutMapping("/table/{tableId}")
    public ResponseDTO<Void> updateTable(@PathVariable Long tableId, @RequestBody TableCommand command) {
        virtualCatalogBusiness.updateTable(tableId, command.getTableName(), command.getComment());
        return ResponseDTO.ok();
    }

    @ApiOperation("编辑虚拟字段")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @PutMapping("/column/{columnId}")
    public ResponseDTO<Void> updateColumn(@PathVariable Long columnId, @RequestBody ColumnCommand command) {
        virtualCatalogBusiness.updateColumn(columnId, command.getColumnName(), command.getDataType(), command.getComment());
        return ResponseDTO.ok();
    }

    // ==================== 删除 ====================

    @ApiOperation("删除虚拟数据库(级联删除其下虚拟表/字段)")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @DeleteMapping("/db/{dbId}")
    public ResponseDTO<Void> deleteDb(@PathVariable Long dbId) {
        virtualCatalogBusiness.deleteDb(dbId);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除虚拟表(级联删除其下虚拟字段)")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @DeleteMapping("/table/{tableId}")
    public ResponseDTO<Void> deleteTable(@PathVariable Long tableId) {
        virtualCatalogBusiness.deleteTable(tableId);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除虚拟字段")
    @PreAuthorize("@permission.has('metadata:datasource:edit')")
    @DeleteMapping("/column/{columnId}")
    public ResponseDTO<Void> deleteColumn(@PathVariable Long columnId) {
        virtualCatalogBusiness.deleteColumn(columnId);
        return ResponseDTO.ok();
    }

    public static class DbCommand {
        private Long datasourceId;
        private String dbName;
        private String comment;

        public Long getDatasourceId() {
            return datasourceId;
        }

        public void setDatasourceId(Long datasourceId) {
            this.datasourceId = datasourceId;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class TableCommand {
        private Long datasourceId;
        private String dbName;
        private String tableName;
        private String comment;

        public Long getDatasourceId() {
            return datasourceId;
        }

        public void setDatasourceId(Long datasourceId) {
            this.datasourceId = datasourceId;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class ColumnCommand {
        private Long datasourceId;
        private String dbName;
        private String tableName;
        private String columnName;
        private String dataType;
        private String comment;

        public Long getDatasourceId() {
            return datasourceId;
        }

        public void setDatasourceId(Long datasourceId) {
            this.datasourceId = datasourceId;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    /** 批量新增虚拟字段入参 */
    public static class BatchColumnCommand {
        private Long datasourceId;
        private String dbName;
        private String tableName;
        private List<VirtualCatalogBusiness.ColumnItem> columns;

        public Long getDatasourceId() { return datasourceId; }
        public void setDatasourceId(Long datasourceId) { this.datasourceId = datasourceId; }
        public String getDbName() { return dbName; }
        public void setDbName(String dbName) { this.dbName = dbName; }
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        public List<VirtualCatalogBusiness.ColumnItem> getColumns() { return columns; }
        public void setColumns(List<VirtualCatalogBusiness.ColumnItem> columns) { this.columns = columns; }
    }
}