package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 数据质量报告业务编排（供 Controller 调用）
 */
@Service
public class DqReportBusiness {

    @Autowired
    private DqReportService dqReportService;

    /**
     * 聚合数据质量报告（概览 + 趋势 + 分布）
     */
    public DqReportVO aggregate(Date startTime, Date endTime) {
        DqReportVO vo = new DqReportVO();
        vo.setOverview(dqReportService.aggregateOverview(startTime, endTime));
        vo.setTrend(dqReportService.aggregateTrend(startTime, endTime));
        vo.setDistribution(dqReportService.aggregateDistribution(startTime, endTime));
        return vo;
    }
}
