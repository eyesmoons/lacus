package com.lacus.admin.controller.rtc;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.enums.dictionary.BusinessTypeEnum;
import com.lacus.domain.rtc.job.JobDefinitionBusiness;
import com.lacus.domain.rtc.job.command.AddJobCommand;
import com.lacus.domain.rtc.job.command.UpdateJobCommand;
import com.lacus.domain.rtc.job.dto.TableDTO;
import com.lacus.domain.rtc.job.query.JobPageQuery;
import com.lacus.domain.rtc.job.query.MappedColumnQuery;
import com.lacus.domain.rtc.job.query.MappedTableColumnQuery;
import com.lacus.domain.rtc.job.query.MappedTableQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "任务定义相关接口", tags = {"任务定义相关接口"})
@RestController
@RequestMapping("/datasync/job/definition")
public class JobDefinitionController {

    @Autowired
    private JobDefinitionBusiness jobDefinitionBusiness;

    @ApiOperation("任务定义列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pageList(JobPageQuery query) {
        PageDTO page = jobDefinitionBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("保存任务定义")
    @PreAuthorize("@permission.has('datasync:job:add')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping("/add")
    public ResponseDTO<?> add(@Valid @RequestBody AddJobCommand command) {
        jobDefinitionBusiness.addJob(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("更新任务定义")
    @PreAuthorize("@permission.has('datasync:job:update')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.MODIFY)
    @PostMapping("/modify")
    public ResponseDTO<?> modify(@Valid @RequestBody UpdateJobCommand command) {
        jobDefinitionBusiness.updateJob(command);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除任务定义")
    @PreAuthorize("@permission.has('datasync:job:remove')")
    @AccessLog(title = "任务管理", businessType = BusinessTypeEnum.DELETE)
    @GetMapping("/remove/{jobId}")
    public ResponseDTO<?> remove(@PathVariable("jobId") Long jobId) {
        jobDefinitionBusiness.remove(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("查询映射表列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @PostMapping("/listMappedTable")
    public ResponseDTO<?> listMappedTable(@RequestBody MappedTableQuery query) {
        return ResponseDTO.ok(jobDefinitionBusiness.listMappedTable(query));
    }

    @ApiOperation("查询映射字段列表")
    @PreAuthorize("@permission.has('datasync:job:list')")
    @PostMapping("/listMappedColumn")
    public ResponseDTO<?> listMappedColumn(@RequestBody MappedColumnQuery query) {
        return ResponseDTO.ok(jobDefinitionBusiness.listMappedColumn(query));
    }

    @ApiOperation("根据dbName查询已接入的表")
    @GetMapping("/listSavedDbTableByDbName")
    public ResponseDTO<?> listSavedDbTable(TableDTO query) {
        return ResponseDTO.ok(jobDefinitionBusiness.listSavedSourceDbTable(query.getDatasourceId(), query.getDbName()));
    }

    @ApiOperation("根据jobId查询已接入的表")
    @GetMapping("/listSavedTableByJobId/{jobId}")
    public ResponseDTO<?> listSavedTableByJobId(@PathVariable("jobId") Long jobId) {
        return ResponseDTO.ok(jobDefinitionBusiness.listSavedSourceTableByJobId(jobId));
    }

    @ApiOperation("任务定义详情")
    @GetMapping("/detail/{jobId}")
    public ResponseDTO<?> detail(@PathVariable("jobId") Long jobId) {
        return ResponseDTO.ok(jobDefinitionBusiness.detail(jobId));
    }

    @ApiOperation("预检查")
    @PostMapping("/preCheck")
    public ResponseDTO<?> preCheck(@RequestBody MappedTableColumnQuery query) {
        return ResponseDTO.ok(jobDefinitionBusiness.preCheck(query));
    }

    @ApiOperation("根据catalogId获取作业运行信息")
    @GetMapping("/jobDetail")
    public ResponseDTO<?> jobDetail(@RequestParam(value = "jobId") Long jobId) {
        return ResponseDTO.ok(jobDefinitionBusiness.jobDetail(jobId));
    }
}
