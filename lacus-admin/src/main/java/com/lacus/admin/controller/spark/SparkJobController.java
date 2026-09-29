package com.lacus.admin.controller.spark;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.domain.spark.job.SparkJobBusiness;
import com.lacus.domain.spark.job.command.AddSparkJarJobCommand;
import com.lacus.domain.spark.job.command.AddSparkSqlJobCommand;
import com.lacus.domain.spark.job.command.UpdateSparkJarJobCommand;
import com.lacus.domain.spark.job.command.UpdateSparkSqlJobCommand;
import com.lacus.domain.spark.job.query.JobPageQuery;
import com.lacus.enums.SparkJobTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Spark任务定义
 */
@Api(value = "Spark任务定义", tags = {"Spark数据开发"})
@RestController
@RequestMapping("/spark/job")
public class SparkJobController {

    @Autowired
    private SparkJobBusiness sparkJobBusiness;

    @ApiOperation("任务分页列表")
    @GetMapping
    @PreAuthorize("@permission.has('spark:job:list')")
    public ResponseDTO<?> jobListPaging(@Valid JobPageQuery query) {
        PageDTO page = sparkJobBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("新建spark sql任务")
    @PostMapping("/sql/batch")
    public ResponseDTO<?> addSparkSqlJob(@RequestBody @Valid AddSparkSqlJobCommand addCommand) {
        addCommand.setJobType(SparkJobTypeEnum.BATCH_SQL.name());
        return ResponseDTO.ok(sparkJobBusiness.addSparkSqlJob(addCommand));
    }

    @ApiOperation("新建spark jar任务")
    @PostMapping("/jar")
    public ResponseDTO<?> addSparkJarJob(@RequestBody @Valid AddSparkJarJobCommand addCommand) {
        addCommand.setJobType(SparkJobTypeEnum.JAR.name());
        return ResponseDTO.ok(sparkJobBusiness.addSparkJarJob(addCommand));
    }

    @ApiOperation("编辑spark sql任务")
    @PutMapping("/sql/batch")
    public ResponseDTO<?> updateSparkSqlJob(@RequestBody @Valid UpdateSparkSqlJobCommand updateCommand) {
        sparkJobBusiness.updateSparkSqlJob(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("编辑spark jar任务")
    @PutMapping("/jar")
    public ResponseDTO<?> updateSparkJarJob(@RequestBody @Valid UpdateSparkJarJobCommand updateCommand) {
        sparkJobBusiness.updateSparkJarJob(updateCommand);
        return ResponseDTO.ok();
    }

    @ApiOperation("查看spark任务详情")
    @GetMapping("/{jobId}")
    public ResponseDTO<SparkJobEntity> detail(@PathVariable("jobId") Long jobId) {
        return ResponseDTO.ok(sparkJobBusiness.detail(jobId));
    }

    @ApiOperation("删除spark任务")
    @DeleteMapping("/{jobId}")
    public ResponseDTO<?> deleteSparkJob(@PathVariable("jobId") Long jobId) {
        sparkJobBusiness.deleteSparkJob(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("启动任务")
    @GetMapping("/start/{jobId}")
    public ResponseDTO<?> start(@PathVariable("jobId") Long jobId) {
        sparkJobBusiness.start(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("停止任务")
    @GetMapping("/stop/{jobId}")
    public ResponseDTO<?> stop(@PathVariable("jobId") Long jobId) {
        sparkJobBusiness.stop(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("上线定时任务")
    @GetMapping("/online/{jobId}")
    public ResponseDTO<?> online(@PathVariable("jobId") Long jobId) {
        sparkJobBusiness.online(jobId);
        return ResponseDTO.ok();
    }

    @ApiOperation("下线定时任务")
    @GetMapping("/offline/{jobId}")
    public ResponseDTO<?> offline(@PathVariable("jobId") Long jobId) {
        sparkJobBusiness.offline(jobId);
        return ResponseDTO.ok();
    }
}
