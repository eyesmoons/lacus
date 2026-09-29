package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.domain.metadata.column.ColumnBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "字段管理", tags = {"字段管理"})
@RestController
@RequestMapping("/metadata/column")
public class ColumnController {

    @Autowired
    private ColumnBusiness columnBusiness;

    @ApiOperation("根据tableId查询字段列表")
    @GetMapping("/getColumnsByTableId/{tableId}")
    public ResponseDTO<List<MetaColumnEntity>> getColumnsBytTableId(@PathVariable("tableId") Long tableId) {
        return ResponseDTO.ok(columnBusiness.getColumnsBytTableId(tableId));
    }

    @ApiOperation("根据tableName查询字段列表")
    @GetMapping("/getColumnsByTableName")
    public ResponseDTO<List<MetaColumnEntity>> getColumnsBytTableName(@RequestParam("datasourceId") Long datasourceId, @RequestParam("dbName") String dbName, @RequestParam("tableName") String tableName) {
        return ResponseDTO.ok(columnBusiness.getColumnsBytTableName(datasourceId, dbName, tableName));
    }
}
