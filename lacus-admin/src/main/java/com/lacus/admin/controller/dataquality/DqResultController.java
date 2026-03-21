package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.domain.dataquality.DqResultBusiness;
import com.lacus.domain.dataquality.query.DqExecutionLogQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "数据质量执行结果", tags = {"数据质量执行结果查询"})
@RestController
@RequestMapping("/dq/result")
public class DqResultController {

    @Autowired
    private DqResultBusiness dqResultBusiness;

    @ApiOperation("查询执行记录列表")
    @GetMapping("/list")
    public ResponseDTO<?> listResults(DqExecutionLogQuery query) {
        PageDTO page = dqResultBusiness.pageList(query);
        return ResponseDTO.ok(page);
    }
}
