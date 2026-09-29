package com.lacus.admin.controller.dataquality;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.dao.dataquality.entity.DqReportVO;
import com.lacus.domain.dataquality.DqReportBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(value = "数据质量报告", tags = {"数据质量报告"})
@RestController
@RequestMapping("/dq/report")
public class DqReportController {

    @Autowired
    private DqReportBusiness dqReportBusiness;

    @ApiOperation("聚合报告数据")
    @GetMapping("/aggregate")
    public ResponseDTO<DqReportVO> aggregate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        if (startTime.after(endTime)) {
            throw new CustomException("起始时间不能晚于结束时间", 9999);
        }
        return ResponseDTO.ok(dqReportBusiness.aggregate(startTime, endTime));
    }
}
