package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.dao.dataquality.entity.DqCheckResultVO;
import com.lacus.domain.dataquality.DqCheckResultBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "数据质量检测结果", tags = {"数据质量检测结果明细"})
@RestController
@RequestMapping("/dq/check-result")
public class DqCheckResultController {

    @Autowired
    private DqCheckResultBusiness dqCheckResultBusiness;

    @ApiOperation("根据执行记录ID查询检测结果明细")
    @GetMapping("/list/{logId}")
    public ResponseDTO<List<DqCheckResultVO>> listByLogId(@PathVariable Long logId) {
        return ResponseDTO.ok(dqCheckResultBusiness.listByLogId(logId));
    }
}
