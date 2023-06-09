package com.lacus.admin.controller.datasync;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import com.lacus.domain.datasync.job.JobService;
import com.lacus.domain.datasync.job.command.AddJobCommand;
import com.lacus.domain.datasync.job.dto.TableDTO;
import com.lacus.domain.datasync.job.query.JobQuery;
import com.lacus.domain.datasync.job.query.MappedColumnQuery;
import com.lacus.domain.datasync.job.query.MappedTableQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "数据同步相关接口", tags = {"数据同步相关接口"})
@RestController
@RequestMapping("/datasync/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @ApiOperation("任务列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pageList(JobQuery query) {
        PageDTO page = jobService.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("保存任务")
    @PreAuthorize("@permission.has('datasync:job:add')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/add")
    public ResponseDTO<?> add(@Valid @RequestBody AddJobCommand command) {
        jobService.addJob(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("查询映射表列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @PostMapping("/listMappedTable")
    public ResponseDTO<?> listMappedTable(@RequestBody MappedTableQuery query) {
        return ResponseDTO.ok(jobService.listMappedTable(query));
    }

    @ApiOperation("查询映射字段列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @PostMapping("/listMappedColumn")
    public ResponseDTO<?> listMappedColumn(@RequestBody MappedColumnQuery query) {
        return ResponseDTO.ok(jobService.listMappedColumn(query));
    }

    @ApiOperation("根据dbName查询已接入的表")
    @GetMapping("/listSavedDbTableByDbName")
    public ResponseDTO<?> listSavedDbTable(TableDTO query) {
        return ResponseDTO.ok(jobService.listSavedDbTable(query.getDatasourceId(), query.getDbName()));
    }

    @ApiOperation("根据jobId查询已接入的表")
    @GetMapping("/listSavedTableByJobId/{jobId}")
    public ResponseDTO<?> listSavedTableByJobId(@PathVariable("jobId") Long jobId) {
        return ResponseDTO.ok(jobService.listSavedTableByJobId(jobId));
    }

    @ApiOperation("任务详情")
    @GetMapping("/detail/{jobId}")
    public ResponseDTO<?> detail(@PathVariable("jobId") Long jobId) {
        return ResponseDTO.ok(jobService.detail(jobId));
    }
}
