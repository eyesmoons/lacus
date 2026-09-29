package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqReportVO;
import com.lacus.dao.dataquality.entity.DqReportVO.*;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 验证 DqReportBusiness 的装配逻辑（薄透传：调三段 Service 方法并装配到 DqReportVO）。
 *
 * <p>通过率 null 判断 / 天周切换 / 维度中文映射等真实逻辑位于 DqReportService，
 * 由 {@link DqReportServiceTest} 覆盖。此处只验证 Business 的装配职责。
 */
public class DqReportBusinessTest {

    private final DqReportBusiness business = new DqReportBusiness();
    private final DqReportService dqReportService = mock(DqReportService.class);

    {
        ReflectionTestUtils.setField(business, "dqReportService", dqReportService);
    }

    // ==================== 正常聚合（case 1） ====================

    @Test
    public void aggregate_assemblesOverviewTrendAndDistribution() {
        // mock 三段 Service 返回已知值
        OverviewVO overview = new OverviewVO();
        overview.setTotal(100);
        overview.setPassed(80);
        overview.setFailed(20);
        overview.setPassRate(80.0);
        overview.setExecSuccess(50);
        overview.setExecFailed(5);

        TrendVO trend = new TrendVO();
        trend.setGranularity("day");
        trend.setBuckets(Collections.emptyList());

        DistributionVO distribution = new DistributionVO();
        distribution.setByRule(Collections.emptyList());
        distribution.setByDimension(Collections.emptyList());

        Date start = new Date(0);
        Date end = new Date(30L * 24 * 60 * 60 * 1000);

        when(dqReportService.aggregateOverview(start, end)).thenReturn(overview);
        when(dqReportService.aggregateTrend(start, end)).thenReturn(trend);
        when(dqReportService.aggregateDistribution(start, end)).thenReturn(distribution);

        DqReportVO vo = business.aggregate(start, end);

        // 核对三段装配正确（同一引用，透传）
        assertSame("overview 应透传", overview, vo.getOverview());
        assertSame("trend 应透传", trend, vo.getTrend());
        assertSame("distribution 应透传", distribution, vo.getDistribution());

        // 核对三个 Service 方法各被调用一次
        verify(dqReportService, times(1)).aggregateOverview(start, end);
        verify(dqReportService, times(1)).aggregateTrend(start, end);
        verify(dqReportService, times(1)).aggregateDistribution(start, end);
    }
}
