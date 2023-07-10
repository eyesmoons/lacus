package com.lacus.admin.controller.datasync;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import com.lacus.domain.datasync.job.JobManagerService;
import com.lacus.domain.datasync.job.command.AddJobCommand;
import com.lacus.domain.datasync.job.command.UpdateJobCommand;
import com.lacus.domain.datasync.job.dto.TableDTO;
import com.lacus.domain.datasync.job.query.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "数据同步任务管理相关接口", tags = {"数据同步任务管理相关接口"})
@RestController
@RequestMapping("/datasync/job/manager")
public class JobManagerController {

    @Autowired
    private JobManagerService jobManagerService;

    @ApiOperation("任务列表树")
    @GetMapping("/jobListTree")
    public ResponseDTO<?> jobListTree(JobQuery query) {
        return ResponseDTO.ok(jobManagerService.jobListTree(query));
    }

    @ApiOperation("任务列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pageList(JobPageQuery query) {
        PageDTO page = jobManagerService.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("保存任务")
    @PreAuthorize("@permission.has('datasync:job:add')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/add")
    public ResponseDTO<?> add(@Valid @RequestBody AddJobCommand command) {
        jobManagerService.addJob(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("更新任务")
    @PreAuthorize("@permission.has('datasync:job:add')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/modify")
    public ResponseDTO<?> modify(@Valid @RequestBody UpdateJobCommand command) {
        jobManagerService.updateJob(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除任务")
    @PreAuthorize("@permission.has('datasync:job:remove')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.DELETE)
    @GetMapping("/remove/{jobId}")
    public ResponseDTO<?> remove(@PathVariable("jobId") String jobId) {
        jobManagerService.remove(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("查询映射表列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @PostMapping("/listMappedTable")
    public ResponseDTO<?> listMappedTable(@RequestBody MappedTableQuery query) {
        return ResponseDTO.ok(jobManagerService.listMappedTable(query));
    }

    @ApiOperation("查询映射字段列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @PostMapping("/listMappedColumn")
    public ResponseDTO<?> listMappedColumn(@RequestBody MappedColumnQuery query) {
        return ResponseDTO.ok(jobManagerService.listMappedColumn(query));
    }

    @ApiOperation("根据dbName查询已接入的表")
    @GetMapping("/listSavedDbTableByDbName")
    public ResponseDTO<?> listSavedDbTable(TableDTO query) {
        return ResponseDTO.ok(jobManagerService.listSavedSourceDbTable(query.getDatasourceId(), query.getDbName()));
    }

    @ApiOperation("根据jobId查询已接入的表")
    @GetMapping("/listSavedTableByJobId/{jobId}")
    public ResponseDTO<?> listSavedTableByJobId(@PathVariable("jobId") String jobId) {
        return ResponseDTO.ok(jobManagerService.listSavedSourceTableByJobId(jobId));
    }

    @ApiOperation("任务详情")
    @GetMapping("/detail/{jobId}")
    public ResponseDTO<?> detail(@PathVariable("jobId") String jobId) {
        return ResponseDTO.ok(jobManagerService.detail(jobId));
    }

    @ApiOperation("预检查")
    @PostMapping("/preCheck")
    public ResponseDTO<?> preCheck(@RequestBody MappedTableColumnQuery query) {
        return ResponseDTO.ok(jobManagerService.preCheck(query));
    }

    @ApiOperation("根据catalogId获取作业运行信息")
    @GetMapping("/jobDetail")
    public ResponseDTO<?> jobDetail(@RequestParam(value = "catalogId") String catalogId, @RequestParam("type") Integer type) {
        return ResponseDTO.ok(jobManagerService.jobDetail(catalogId, type));
    }
}
