package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.dig.StExecuteBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据集成任务操作", tags = {"数据集成任务操作管理"})
@RestController
@RequestMapping("/st/execute")
public class StJobExecuteController {

    private static final Logger logger = LoggerFactory.getLogger(StJobExecuteController.class);

    @Autowired
    private StExecuteBusiness stExecuteBusiness;

    @ApiOperation("启动任务")
    @GetMapping("/start/{jobId}")
    public ResponseDTO<?> start(@PathVariable("jobId") Long jobId) {
        try {
            logger.info("Starting job with ID: {}", jobId);
            stExecuteBusiness.start(jobId);
            logger.info("Successfully started job with ID: {}", jobId);
            return ResponseDTO.ok("任务启动成功");
        } catch (Exception e) {
            logger.error("Failed to start job with ID: {}", jobId, e);
            return ResponseDTO.fail(e.getMessage());
        }
    }
}
