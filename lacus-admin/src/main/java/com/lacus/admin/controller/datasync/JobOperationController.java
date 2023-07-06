package com.lacus.admin.controller.datasync;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.datasync.job.JobOperationService;
import com.lacus.domain.datasync.job.dto.JobSubmitDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "数据同步任务操作相关接口", tags = {"数据同步任务操作相关接口"})
@RestController
@RequestMapping("/datasync/job/operation")
public class JobOperationController {

    @Autowired
    private JobOperationService operationService;

    @ApiOperation("启动任务")
    @PostMapping("/submitJob")
    public ResponseDTO<?> submitJob(@RequestBody JobSubmitDTO jobSubmitDTO) {
        operationService.submitJob(jobSubmitDTO.getCatalogId(), jobSubmitDTO.getSyncType(), jobSubmitDTO.getTimeStamp());
        return ResponseDTO.ok();
    }

    @ApiOperation("停止任务")
    @GetMapping("/stopJob/{catalogId}")
    public ResponseDTO<?> stopJob(@PathVariable("catalogId") String catalogId) {
        operationService.stopJob(catalogId);
        return ResponseDTO.ok();
    }
}
