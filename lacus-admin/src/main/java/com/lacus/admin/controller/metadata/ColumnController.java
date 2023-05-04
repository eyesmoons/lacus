package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.metadata.entity.MetaColumnEntity;
import com.lacus.domain.metadata.column.ColumnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "字段管理", tags = {"字段管理"})
@RestController
@RequestMapping("/metadata/column")
public class ColumnController {

    @Autowired
    private ColumnService columnService;

    @ApiOperation("根据tableId查询字段列表")
    @GetMapping("/getColumnsBytTableId/{tableId}")
    public ResponseDTO<List<MetaColumnEntity>> getColumnsBytTableId(@PathVariable("tableId") Long tableId) {
        return ResponseDTO.ok(columnService.getColumnsBytTableId(tableId));
    }
}
