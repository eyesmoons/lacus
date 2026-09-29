package com.lacus.admin.controller.spark;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.domain.spark.instance.SparkJobInstanceBusiness;
import com.lacus.domain.spark.instance.query.JobInstancePageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Spark任务实例管理
 */
@Api(value = "Spark任务实例", tags = {"Spark数据开发"})
@RestController
@RequestMapping("/spark/job/instance")
public class SparkJobInstanceController {

    @Autowired
    private SparkJobInstanceBusiness sparkJobInstanceBusiness;

    @ApiOperation("任务分页列表")
    @GetMapping("/list/paging")
    @PreAuthorize("@permission.has('spark:job:list')")
    public ResponseDTO<?> listPaging(@Valid JobInstancePageQuery query) {
        PageDTO page = sparkJobInstanceBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("查看spark任务实例详情")
    @GetMapping("/{instanceId}")
    @PreAuthorize("@permission.has('spark:job:list')")
    public ResponseDTO<SparkJobInstanceEntity> detail(@PathVariable("instanceId") Long instanceId) {
        SparkJobInstanceEntity detail = sparkJobInstanceBusiness.detail(instanceId);
        return ResponseDTO.ok(detail);
    }
}
