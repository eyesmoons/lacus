package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.dig.entity.StTaskEntity;
import com.lacus.domain.dig.StTaskBusiness;
import com.lacus.domain.dig.dto.JobDag;
import com.lacus.domain.dig.dto.JobTaskInfo;
import com.lacus.domain.dig.dto.StTaskConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据集成子任务", tags = {"数据集成子任务管理"})
@RestController
@RequestMapping("/st/job/task")
public class StJobTaskController {

    @Autowired
    private StTaskBusiness stTaskBusiness;

    @ApiOperation("新增或更新任务节点")
    @PostMapping
    public ResponseDTO<StTaskEntity> saveOrUpdateTask(@RequestBody StTaskConfig command) {
        return ResponseDTO.ok(stTaskBusiness.saveOrUpdateTask(command));
    }

    @ApiOperation("根据任务节点Id获取节点信息")
    @GetMapping("/{taskId}")
    public ResponseDTO<StTaskConfig> getTaskById(@PathVariable("taskId") String taskId) {
        return ResponseDTO.ok(stTaskBusiness.getTaskById(taskId));
    }

    @ApiOperation("删除任务节点")
    @DeleteMapping("/{taskId}")
    public ResponseDTO<?> removeTask(@PathVariable("taskId") String taskId) {
        stTaskBusiness.deleteTask(taskId);
        return ResponseDTO.ok();
    }

    @ApiOperation("保存任务DAG")
    @PostMapping("/dag")
    public ResponseDTO<?> saveDag(@RequestBody JobDag dag) {
        stTaskBusiness.saveDag(dag);
        return ResponseDTO.ok();
    }

    @ApiOperation("获取任务DAG信息")
    @GetMapping("/dag/{jobId}")
    public ResponseDTO<JobTaskInfo> getDag(@PathVariable("jobId") Long jobId) {
        return ResponseDTO.ok(stTaskBusiness.getDag(jobId));
    }
}
