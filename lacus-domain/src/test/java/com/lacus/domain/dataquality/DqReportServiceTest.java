package com.lacus.domain.dataquality;

import com.lacus.dao.dataquality.entity.DqReportVO.*;
import com.lacus.dao.dataquality.mapper.DqReportMapper;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * 验证 DqReportService 的聚合逻辑（passRate 计算 / 天周切换 / TopN 透传 / 维度中文映射）。
 *
 * <p>mock DqReportMapper，不连数据库。各 case 对应 brief 中的 5 个测试点：
 * case 2 → passRate null；case 3 → 天/周切换；case 4 → TopN 透传；case 5 → 维度中文映射。
 */
public class DqReportServiceTest {

    private final DqReportService service = new DqReportService();
    private final DqReportMapper mapper = mock(DqReportMapper.class);

    {
        ReflectionTestUtils.setField(service, "dqReportMapper", mapper);
    }

    /** 固定起点：1970-01-01 UTC，便于按毫秒推算天数 */
    private static final Date START = new Date(0);
    /** 30 天后 → 日粒度边界（days==30，走 day） */
    private static final Date END_30D = new Date(30L * 24 * 60 * 60 * 1000);
    /** 31 天后 → 周粒度（days==31 > 30，走 week） */
    private static final Date END_31D = new Date(31L * 24 * 60 * 60 * 1000);

    // ==================== case 2: 无数据通过率 null ====================

    @Test
    public void aggregateOverview_passRateNull_whenTotalZero() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("total", 0L);
        overview.put("passed", 0L);
        overview.put("failed", 0L);
        Map<String, Object> exec = new HashMap<>();
        exec.put("execSuccess", 0L);
        exec.put("execFailed", 0L);

        when(mapper.aggregateOverview(START, END_30D)).thenReturn(overview);
        when(mapper.aggregateExecSummary(START, END_30D)).thenReturn(exec);

        OverviewVO vo = service.aggregateOverview(START, END_30D);

        assertEquals(Integer.valueOf(0), vo.getTotal());
        assertNull("total==0 时 passRate 应为 null", vo.getPassRate());
    }

    @Test
    public void aggregateOverview_passRateComputed_whenTotalPositive() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("total", 10L);
        overview.put("passed", 3L);
        overview.put("failed", 7L);
        Map<String, Object> exec = new HashMap<>();
        exec.put("execSuccess", 5L);
        exec.put("execFailed", 2L);

        when(mapper.aggregateOverview(START, END_30D)).thenReturn(overview);
        when(mapper.aggregateExecSummary(START, END_30D)).thenReturn(exec);

        OverviewVO vo = service.aggregateOverview(START, END_30D);

        assertEquals(Integer.valueOf(10), vo.getTotal());
        assertEquals(Integer.valueOf(3), vo.getPassed());
        assertEquals(Integer.valueOf(7), vo.getFailed());
        // 3/10*100 = 30.0，保留 1 位小数
        assertEquals(Double.valueOf(30.0), vo.getPassRate());
    }

    // ==================== case 3: 按天/按周切换 ====================

    @Test
    public void aggregateTrend_granularityDay_whenRangeWithin30Days() {
        List<TrendBucketVO> dayBuckets = Collections.singletonList(new TrendBucketVO());
        when(mapper.aggregateTrendDay(START, END_30D)).thenReturn(dayBuckets);

        TrendVO trend = service.aggregateTrend(START, END_30D);

        assertEquals("day", trend.getGranularity());
        verify(mapper).aggregateTrendDay(START, END_30D);
        verify(mapper, never()).aggregateTrendWeek(any(Date.class), any(Date.class));
    }

    @Test
    public void aggregateTrend_granularityWeek_whenRangeOver30Days() {
        List<TrendBucketVO> weekBuckets = Collections.singletonList(new TrendBucketVO());
        when(mapper.aggregateTrendWeek(START, END_31D)).thenReturn(weekBuckets);

        TrendVO trend = service.aggregateTrend(START, END_31D);

        assertEquals("week", trend.getGranularity());
        verify(mapper).aggregateTrendWeek(START, END_31D);
        verify(mapper, never()).aggregateTrendDay(any(Date.class), any(Date.class));
    }

    // ==================== case 4: 按规则 Top5 排序 ====================

    /**
     * LIMIT 5 在 Mapper SQL 中强制，Service 层只透传。
     * 此处验证：当 Mapper 返回 7 条时，Service 不做截断、保持原序（排序+LIMIT 是 SQL 职责）。
     */
    @Test
    public void aggregateDistribution_passesThroughRuleList_asIsFromMapper() {
        List<RuleFailVO> sevenRules = new ArrayList<>();
        for (int i = 7; i >= 1; i--) {
            RuleFailVO r = new RuleFailVO();
            r.setRuleName("rule-" + i);
            r.setFails(i * 10); // 10,20,...,70 — mapper 已按 fails desc 排好序
            sevenRules.add(r);
        }
        when(mapper.aggregateTopRulesByFails(START, END_30D)).thenReturn(sevenRules);
        when(mapper.aggregateByDimension(START, END_30D)).thenReturn(Collections.emptyList());

        DistributionVO vo = service.aggregateDistribution(START, END_30D);

        // Service 不截断，保持 mapper 返回的全部 7 条
        assertEquals("Service 不截断，LIMIT 由 SQL 负责", 7, vo.getByRule().size());
        // 顺序保持 mapper 返回（已按 fails desc）
        assertEquals(Integer.valueOf(70), vo.getByRule().get(0).getFails());
        assertEquals(Integer.valueOf(10), vo.getByRule().get(6).getFails());
    }

    // ==================== case 5: 按维度中文映射 ====================

    @Test
    public void aggregateDistribution_mapsDimensionToChinese() {
        List<DimensionFailVO> dims = new ArrayList<>();
        DimensionFailVO completeness = new DimensionFailVO();
        completeness.setDimension("completeness");
        completeness.setFails(12);
        dims.add(completeness);

        DimensionFailVO uniqueness = new DimensionFailVO();
        uniqueness.setDimension("uniqueness");
        uniqueness.setFails(5);
        dims.add(uniqueness);

        // 未识别的维度保留原值
        DimensionFailVO unknown = new DimensionFailVO();
        unknown.setDimension("someUnknownDim");
        unknown.setFails(3);
        dims.add(unknown);

        when(mapper.aggregateTopRulesByFails(START, END_30D)).thenReturn(Collections.emptyList());
        when(mapper.aggregateByDimension(START, END_30D)).thenReturn(dims);

        DistributionVO vo = service.aggregateDistribution(START, END_30D);

        assertEquals("完整性", vo.getByDimension().get(0).getDimension());
        assertEquals("唯一性", vo.getByDimension().get(1).getDimension());
        // 未识别维度保留原值
        assertEquals("someUnknownDim", vo.getByDimension().get(2).getDimension());
    }
}
