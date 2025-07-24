package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.dig.StExecuteBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据集成任务操作", tags = {"数据集成任务操作管理"})
@RestController
@RequestMapping("/st/execute")
public class StJobExecuteController {

    @Autowired
    private StExecuteBusiness stExecuteBusiness;

    @ApiOperation("启动任务")
    @GetMapping("/{jobId}")
    public ResponseDTO<?> start(@PathVariable("jobId") Long jobId) {
        stExecuteBusiness.start(jobId);
        return ResponseDTO.ok();
    }
}
