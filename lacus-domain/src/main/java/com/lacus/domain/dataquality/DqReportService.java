package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqReportVO;
import com.lacus.dao.dataquality.entity.DqReportVO.*;
import com.lacus.dao.dataquality.mapper.DqReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据质量报告聚合 Service（编排 DqReportMapper 的六个聚合查询，装配 DqReportVO 各子视图）
 */
@Service
public class DqReportService {

    @Autowired
    private DqReportMapper dqReportMapper;

    /** 维度英文→中文映射 */
    private static final Map<String, String> DIMENSION_MAP = new HashMap<>();

    static {
        DIMENSION_MAP.put("completeness", "完整性");
        DIMENSION_MAP.put("uniqueness", "唯一性");
        DIMENSION_MAP.put("timeliness", "及时性");
        DIMENSION_MAP.put("validity", "有效性");
        DIMENSION_MAP.put("consistency", "一致性");
        DIMENSION_MAP.put("stability", "稳定性");
    }

    /**
     * 概览：调检测概览 + 执行汇总两查询，计算 passRate。
     * <p>
     * Map 值由 MySQL SUM/COUNT 返回 Long，需转 Integer；passRate 在 total==0 时为 null。
     */
    public OverviewVO aggregateOverview(Date startTime, Date endTime) {
        Map<String, Object> overview = dqReportMapper.aggregateOverview(startTime, endTime);
        Map<String, Object> exec = dqReportMapper.aggregateExecSummary(startTime, endTime);

        OverviewVO vo = new OverviewVO();
        Integer total = toInt(overview.get("total"));
        Integer passed = toInt(overview.get("passed"));
        Integer failed = toInt(overview.get("failed"));
        vo.setTotal(total);
        vo.setPassed(passed);
        vo.setFailed(failed);
        vo.setExecSuccess(toInt(exec.get("execSuccess")));
        vo.setExecFailed(toInt(exec.get("execFailed")));

        if (total == null || total == 0) {
            vo.setPassRate(null);
        } else {
            double rate = (double) passed / total * 100;
            vo.setPassRate(round1(rate));
        }
        return vo;
    }

    /**
     * 趋势：天数差 ≤30 调日粒度，否则调周粒度。
     */
    public TrendVO aggregateTrend(Date startTime, Date endTime) {
        long diffMs = endTime.getTime() - startTime.getTime();
        long days = diffMs / (24 * 60 * 60 * 1000);

        TrendVO trend = new TrendVO();
        if (days <= 30) {
            trend.setGranularity("day");
            trend.setBuckets(dqReportMapper.aggregateTrendDay(startTime, endTime));
        } else {
            trend.setGranularity("week");
            trend.setBuckets(dqReportMapper.aggregateTrendWeek(startTime, endTime));
        }
        return trend;
    }

    /**
     * 分布：Top5 失败规则 + 按维度失败统计（维度中文映射，未识别的保留原值）。
     */
    public DistributionVO aggregateDistribution(Date startTime, Date endTime) {
        List<RuleFailVO> byRule = dqReportMapper.aggregateTopRulesByFails(startTime, endTime);
        List<DimensionFailVO> byDimension = dqReportMapper.aggregateByDimension(startTime, endTime);

        for (DimensionFailVO d : byDimension) {
            String cn = DIMENSION_MAP.get(d.getDimension());
            if (cn != null) {
                d.setDimension(cn);
            }
        }

        DistributionVO vo = new DistributionVO();
        vo.setByRule(byRule);
        vo.setByDimension(byDimension);
        return vo;
    }

    // ==================== 私有工具方法 ====================

    /** Map 值 Long→Integer 安全转换，null 视为 0 */
    private Integer toInt(Object value) {
        if (value == null) {
            return 0;
        }
        return ((Number) value).intValue();
    }

    /** 四舍五入保留 1 位小数 */
    private Double round1(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
