package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.dataquality.entity.DqExecutionLogEntity;
import com.lacus.domain.dataquality.DqTaskBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据质量任务管理", tags = {"数据质量任务管理"})
@RestController
@RequestMapping("/dq/task")
public class DqTaskController {

    @Autowired
    private DqTaskBusiness dqTaskBusiness;

    @ApiOperation("提交执行任务")
    @PostMapping("/submit/{ruleId}")
    public ResponseDTO<Long> submitTask(@PathVariable Long ruleId) {
        Long logId = dqTaskBusiness.submitTask(ruleId);
        return ResponseDTO.ok(logId);
    }

    @ApiOperation("查询任务状态")
    @GetMapping("/status/{logId}")
    public ResponseDTO<DqExecutionLogEntity> getTaskStatus(@PathVariable Long logId) {
        return ResponseDTO.ok(dqTaskBusiness.getTaskStatus(logId));
    }

    @ApiOperation("停止任务")
    @PostMapping("/stop/{logId}")
    public ResponseDTO<Void> stopTask(@PathVariable Long logId) {
        dqTaskBusiness.stopTask(logId);
        return ResponseDTO.ok();
    }
}
