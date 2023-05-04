package com.lacus.admin.controller.metadata;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.metadata.entity.MetaTableEntity;
import com.lacus.domain.metadata.table.TableService;
import com.lacus.domain.metadata.table.dto.TableDTO;
import com.lacus.domain.metadata.table.query.TableDetailQuery;
import com.lacus.domain.metadata.table.query.TableQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Api(value = "数据表管理", tags = {"数据表管理"})
@RestController
@RequestMapping("/metadata/table")
public class TableController {

    @Autowired
    private TableService tableService;

    @ApiOperation("数据表分页列表")
    @GetMapping("/pageList")
    @PreAuthorize("@permission.has('metadata:table:list')")
    public ResponseDTO<PageDTO> pageList(TableQuery query) {
        PageDTO page = tableService.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("查询表详情")
    @GetMapping("/detail/{tableId}")
    @PreAuthorize("@permission.has('metadata:table:query')")
    public ResponseDTO<TableDTO> detail(@PathVariable("tableId") Long tableId) {
        return ResponseDTO.ok(tableService.getTableDetailById(tableId));
    }

    @ApiOperation("查询表详情")
    @GetMapping("/info")
    @PreAuthorize("@permission.has('metadata:table:query')")
    public ResponseDTO<TableDTO> info(TableDetailQuery query) {
        return ResponseDTO.ok(tableService.getTableDetail(query));
    }
}
