package com.lacus.admin.controller.datasync;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.core.annotations.AccessLog;
import com.lacus.dao.datasync.entity.DataSyncJobCatelogEntity;
import com.lacus.dao.system.enums.dictionary.BusinessTypeEnum;
import com.lacus.domain.datasync.jobCatelog.JobCatelogService;
import com.lacus.domain.datasync.jobCatelog.command.AddJobCatelogCommand;
import com.lacus.domain.datasync.jobCatelog.command.UpdateJobCatelogCommand;
import com.lacus.domain.datasync.jobCatelog.query.JobCateLogQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Api(value = "数据同步分组相关接口", tags = {"数据同步分组相关接口"})
@RestController
@RequestMapping("/datasync/job/catelog")
public class JobCatelogController {

    @Autowired
    private JobCatelogService jobCatelogService;

    @ApiOperation("任务分组列表")
    @PreAuthorize("@permission.has('datasync:catelog:list')")
    @GetMapping("/list")
    public ResponseDTO<List<DataSyncJobCatelogEntity>> list(@RequestParam("catelogName") String catelogName) {
        List<DataSyncJobCatelogEntity> list = jobCatelogService.list(catelogName);
        return ResponseDTO.ok(list);
    }

    @ApiOperation("任务分组列表")
    @PreAuthorize("@permission.has('datasync:catelog:list')")
    @GetMapping("/pageList")
    public ResponseDTO<PageDTO> pageList(JobCateLogQuery query) {
        PageDTO page = jobCatelogService.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("任务分组详情")
    @PreAuthorize("@permission.has('datasync:catelog:list')")
    @GetMapping("/{catelogId}")
    public ResponseDTO<DataSyncJobCatelogEntity> detail(@PathVariable("catelogId") Long catelogId) {
        DataSyncJobCatelogEntity entity = jobCatelogService.detail(catelogId);
        return ResponseDTO.ok(entity);
    }

    @ApiOperation("新建分组")
    @PreAuthorize("@permission.has('datasync:catelog:add')")
    @AccessLog(title = "分组管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<?> add(@RequestBody @Validated AddJobCatelogCommand addCommand) {
        jobCatelogService.addJobCatelog(addCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("修改分组")
    @PreAuthorize("@permission.has('datasync:catelog:edit')")
    @AccessLog(title = "分组管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<?> edit(@Validated @RequestBody UpdateJobCatelogCommand updateCommand) {
        jobCatelogService.updateJobCatelog(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("删除分组")
    @PreAuthorize("@permission.has('datasync:catelog:remove')")
    @AccessLog(title = "分组管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{catelogIds}")
    public ResponseDTO<?> remove(@PathVariable @NotNull List<Long> catelogIds) {
        jobCatelogService.removeJobCatelog(catelogIds);
        return ResponseDTO.ok();
    }
}
