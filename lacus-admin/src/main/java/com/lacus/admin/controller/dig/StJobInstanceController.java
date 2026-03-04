package com.lacus.admin.controller.dig;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.dig.StJobInstanceQueryBusiness;
import com.lacus.domain.dig.query.StJobInstanceQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据集成任务实例", tags = {"数据集成任务实例管理"})
@RestController
@RequestMapping("/st/job/instance")
public class StJobInstanceController {

    @Autowired
    private StJobInstanceQueryBusiness queryBusiness;

    @ApiOperation("查询任务实例列表")
    @GetMapping("/list")
    public ResponseDTO<PageDTO> list(StJobInstanceQuery query) {
        PageDTO page = queryBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }

    @ApiOperation("查询任务实例详情")
    @GetMapping("/{instanceId}")
    public ResponseDTO<?> detail(@PathVariable("instanceId") Long instanceId) {
        return ResponseDTO.ok(queryBusiness.getInstanceById(instanceId));
    }
}
