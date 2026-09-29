package com.lacus.domain.monitor.dashboard;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.dao.alert.entity.AlertRecordEntity;
import com.lacus.dao.alert.mapper.AlertRecordMapper;
import com.lacus.dao.dashboard.dto.EngineStatusCount;
import com.lacus.dao.dashboard.dto.InstanceTrendRow;
import com.lacus.dao.dashboard.mapper.DashboardMapper;
import com.lacus.dao.dataquality.entity.DqRuleEntity;
import com.lacus.dao.dataquality.mapper.DqRuleMapper;
import com.lacus.dao.dig.entity.StJobEntity;
import com.lacus.dao.dig.entity.StJobInstanceEntity;
import com.lacus.dao.dig.mapper.StJobInstanceMapper;
import com.lacus.dao.dig.mapper.StJobMapper;
import com.lacus.dao.flink.entity.FlinkJobEntity;
import com.lacus.dao.flink.entity.FlinkJobInstanceEntity;
import com.lacus.dao.flink.mapper.FlinkJobInstanceMapper;
import com.lacus.dao.flink.mapper.FlinkJobMapper;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.metadata.mapper.MetaDatasourceMapper;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.dao.oneapi.mapper.OneApiInfoMapper;
import com.lacus.dao.rtc.entity.DataSyncJobEntity;
import com.lacus.dao.rtc.entity.DataSyncJobInstanceEntity;
import com.lacus.dao.rtc.mapper.DataSyncJobInstanceMapper;
import com.lacus.dao.rtc.mapper.DataSyncJobMapper;
import com.lacus.dao.spark.entity.SparkJobEntity;
import com.lacus.dao.spark.entity.SparkJobInstanceEntity;
import com.lacus.dao.spark.mapper.SparkJobInstanceMapper;
import com.lacus.dao.spark.mapper.SparkJobMapper;
import com.lacus.domain.monitor.dashboard.dto.DashboardDTO;
import com.lacus.domain.monitor.dashboard.dto.InstanceActivityDTO;
import com.lacus.domain.monitor.dashboard.dto.RecentAlertDTO;
import com.lacus.domain.monitor.dashboard.dto.ResourceOverviewDTO;
import com.lacus.domain.monitor.dashboard.dto.ServerHealthDTO;
import com.lacus.domain.monitor.dashboard.dto.StatusDistributionDTO;
import com.lacus.domain.monitor.dashboard.dto.TrendPointDTO;
import com.lacus.domain.system.monitor.MonitorBusiness;
import com.lacus.domain.system.monitor.dto.ServerInfo;
import com.lacus.enums.UnifiedStatusEnum;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工作台聚合业务：一次返回首页所需的全部指标
 */
@Slf4j
@Service
public class DashboardBusiness {

    private static final int RECENT_INSTANCE_LIMIT = 8;
    private static final int RECENT_INSTANCE_FETCH = 15;
    private static final int RECENT_ALERT_LIMIT = 6;
    private static final String ENGINE_FLINK = "FLINK";
    private static final String ENGINE_SPARK = "SPARK";
    private static final String ENGINE_DATASYNC = "DATASYNC";
    private static final String ENGINE_DIG = "DIG";

    @Autowired
    private DashboardMapper dashboardMapper;

    @Autowired
    private MetaDatasourceMapper metaDatasourceMapper;

    @Autowired
    private DataSyncJobMapper dataSyncJobMapper;

    @Autowired
    private FlinkJobMapper flinkJobMapper;

    @Autowired
    private SparkJobMapper sparkJobMapper;

    @Autowired
    private StJobMapper stJobMapper;

    @Autowired
    private OneApiInfoMapper oneApiInfoMapper;

    @Autowired
    private DqRuleMapper dqRuleMapper;

    @Autowired
    private FlinkJobInstanceMapper flinkJobInstanceMapper;

    @Autowired
    private SparkJobInstanceMapper sparkJobInstanceMapper;

    @Autowired
    private DataSyncJobInstanceMapper dataSyncJobInstanceMapper;

    @Autowired
    private StJobInstanceMapper stJobInstanceMapper;

    @Autowired
    private AlertRecordMapper alertRecordMapper;

    @Autowired
    private MonitorBusiness monitorBusiness;

    public DashboardDTO getDashboard(int days) {
        Date todayBegin = beginOfToday();
        Date windowBegin = addDays(todayBegin, -Math.max(0, days - 1));
        Date windowEnd = addDays(todayBegin, 1);

        DashboardDTO dto = new DashboardDTO();
        dto.setResources(buildResourceOverview());
        dto.setTrend(buildTrend(windowBegin, windowEnd));
        dto.setStatusDistribution(buildStatusDistribution(windowBegin, windowEnd, todayBegin));
        dto.setRecentInstances(buildRecentInstances());
        dto.setRecentAlerts(buildRecentAlerts());
        dto.setHealth(buildServerHealth());
        dto.setGeneratedAt(System.currentTimeMillis());
        return dto;
    }

    // ==================== 资源总览 ====================

    private ResourceOverviewDTO buildResourceOverview() {
        ResourceOverviewDTO overview = new ResourceOverviewDTO();
        overview.setDatasourceTotal(metaDatasourceMapper.selectCount(null));
        overview.setDatasourceEnabled(
            metaDatasourceMapper.selectCount(new QueryWrapper<MetaDatasourceEntity>().eq("status", 1)));
        overview.setDatasyncJobTotal(dataSyncJobMapper.selectCount(null));
        overview.setFlinkJobTotal(flinkJobMapper.selectCount(null));
        overview.setSparkJobTotal(sparkJobMapper.selectCount(null));
        overview.setDigJobTotal(stJobMapper.selectCount(null));
        overview.setApiTotal(oneApiInfoMapper.selectCount(null));
        overview.setApiPublished(
            oneApiInfoMapper.selectCount(new QueryWrapper<OneApiInfoEntity>().eq("status", 1)));
        overview.setQualityRuleTotal(dqRuleMapper.selectCount(null));

        // 当前运行中实例数：RUNNING 组全量计数（流式任务 RUNNING 是常态，未结束即仍在运行）
        Map<String, Long> runningByEngine = countAllRunning();
        overview.setFlinkRunning(runningByEngine.getOrDefault(ENGINE_FLINK, 0L));
        overview.setSparkRunning(runningByEngine.getOrDefault(ENGINE_SPARK, 0L));
        overview.setDatasyncRunning(runningByEngine.getOrDefault(ENGINE_DATASYNC, 0L));
        overview.setDigRunning(runningByEngine.getOrDefault(ENGINE_DIG, 0L));
        return overview;
    }

    /**
     * 全量状态分布里取 RUNNING 组计数（一次查询覆盖四引擎）。
     */
    private Map<String, Long> countAllRunning() {
        List<EngineStatusCount> rows = dashboardMapper.selectStatusDistribution(null, null);
        Map<String, Long> runningByEngine = new HashMap<>();
        for (EngineStatusCount row : rows) {
            if (row.getCnt() == null) {
                continue;
            }
            if (resolveUnified(row.getEngine(), row.getRawStatus()) == UnifiedStatusEnum.RUNNING) {
                runningByEngine.merge(row.getEngine(), row.getCnt(), Long::sum);
            }
        }
        return runningByEngine;
    }

    // ==================== 趋势 ====================

    private List<TrendPointDTO> buildTrend(Date begin, Date end) {
        Map<String, TrendPointDTO> byDate = new LinkedHashMap<>();
        int dayCount = (int) ((end.getTime() - begin.getTime()) / (24L * 3600 * 1000));
        for (int i = 0; i < dayCount; i++) {
            TrendPointDTO point = new TrendPointDTO();
            point.setDate(formatDay(addDays(begin, i)));
            point.setSubmitted(0L);
            point.setSuccess(0L);
            point.setFailed(0L);
            point.setOthers(0L);
            byDate.put(point.getDate(), point);
        }

        List<InstanceTrendRow> rows = dashboardMapper.selectTrend(begin, end);
        for (InstanceTrendRow row : rows) {
            TrendPointDTO point = byDate.get(row.getStatDate());
            if (point == null || row.getCnt() == null) {
                continue;
            }
            UnifiedStatusEnum unified = resolveUnified(row.getEngine(), row.getRawStatus());
            point.setSubmitted(point.getSubmitted() + row.getCnt());
            if (unified == UnifiedStatusEnum.SUCCESS) {
                point.setSuccess(point.getSuccess() + row.getCnt());
            } else if (unified == UnifiedStatusEnum.FAILED) {
                point.setFailed(point.getFailed() + row.getCnt());
            } else {
                point.setOthers(point.getOthers() + row.getCnt());
            }
        }

        List<TrendPointDTO> trend = new ArrayList<>(byDate.values());
        for (TrendPointDTO point : trend) {
            long terminal = point.getSuccess() + point.getFailed();
            if (terminal > 0) {
                double rate = Math.round(point.getSuccess() * 1000.0 / terminal) / 10.0;
                point.setSuccessRate(rate);
            }
        }
        return trend;
    }

    // ==================== 状态分布 ====================

    private StatusDistributionDTO buildStatusDistribution(Date windowBegin, Date windowEnd, Date todayBegin) {
        StatusDistributionDTO distribution = new StatusDistributionDTO();
        distribution.setRecent(groupByCategory(dashboardMapper.selectStatusDistribution(windowBegin, windowEnd)));
        distribution.setToday(groupByCategory(dashboardMapper.selectStatusDistribution(todayBegin, windowEnd)));
        return distribution;
    }

    private List<StatusDistributionDTO.CategoryCount> groupByCategory(List<EngineStatusCount> rows) {
        Map<UnifiedStatusEnum, Long> buckets = new EnumMap<>(UnifiedStatusEnum.class);
        for (EngineStatusCount row : rows) {
            if (row.getCnt() == null) {
                continue;
            }
            UnifiedStatusEnum unified = resolveUnified(row.getEngine(), row.getRawStatus());
            buckets.merge(unified, row.getCnt(), Long::sum);
        }
        List<StatusDistributionDTO.CategoryCount> result = new ArrayList<>();
        for (UnifiedStatusEnum category : UnifiedStatusEnum.values()) {
            StatusDistributionDTO.CategoryCount item = new StatusDistributionDTO.CategoryCount();
            item.setCategory(category.name());
            item.setCount(buckets.getOrDefault(category, 0L));
            result.add(item);
        }
        return result;
    }

    // ==================== 近期实例 ====================

    private List<InstanceActivityDTO> buildRecentInstances() {
        List<InstanceActivityDTO> merged = new ArrayList<>();

        Page<FlinkJobInstanceEntity> flinkPage = flinkJobInstanceMapper.selectPage(
            new Page<>(1, RECENT_INSTANCE_FETCH, false),
            new QueryWrapper<FlinkJobInstanceEntity>().isNotNull("submit_time").orderByDesc("submit_time"));
        for (FlinkJobInstanceEntity entity : flinkPage.getRecords()) {
            InstanceActivityDTO activity = new InstanceActivityDTO();
            activity.setSource("Flink");
            activity.setInstanceId(entity.getInstanceId());
            activity.setName(firstNonBlank(entity.getInstanceName(), entity.getApplicationId()));
            activity.setRawStatus(enumName(entity.getStatus()));
            activity.setStatusGroup(resolveUnified(ENGINE_FLINK, enumName(entity.getStatus())).name());
            activity.setTime(entity.getSubmitTime());
            activity.setPath("/flink/instance/detail/" + entity.getInstanceId());
            merged.add(activity);
        }

        Page<SparkJobInstanceEntity> sparkPage = sparkJobInstanceMapper.selectPage(
            new Page<>(1, RECENT_INSTANCE_FETCH, false),
            new QueryWrapper<SparkJobInstanceEntity>().isNotNull("submit_time").orderByDesc("submit_time"));
        for (SparkJobInstanceEntity entity : sparkPage.getRecords()) {
            InstanceActivityDTO activity = new InstanceActivityDTO();
            activity.setSource("Spark");
            activity.setInstanceId(entity.getInstanceId());
            activity.setName(firstNonBlank(entity.getInstanceName(), entity.getApplicationId()));
            activity.setRawStatus(enumName(entity.getJobStatus()));
            activity.setStatusGroup(resolveUnified(ENGINE_SPARK, enumName(entity.getJobStatus())).name());
            activity.setTime(entity.getSubmitTime());
            activity.setPath("/spark/instance/detail/" + entity.getInstanceId());
            merged.add(activity);
        }

        Page<DataSyncJobInstanceEntity> datasyncPage = dataSyncJobInstanceMapper.selectPage(
            new Page<>(1, RECENT_INSTANCE_FETCH, false),
            new QueryWrapper<DataSyncJobInstanceEntity>().isNotNull("submit_time").orderByDesc("submit_time"));
        for (DataSyncJobInstanceEntity entity : datasyncPage.getRecords()) {
            InstanceActivityDTO activity = new InstanceActivityDTO();
            activity.setSource("采集");
            activity.setInstanceId(entity.getInstanceId());
            activity.setName(firstNonBlank(entity.getInstanceName(), entity.getApplicationId()));
            activity.setRawStatus(entity.getStatus());
            activity.setStatusGroup(resolveUnified(ENGINE_DATASYNC, entity.getStatus()).name());
            activity.setTime(entity.getSubmitTime());
            activity.setPath("/datasync/instance");
            merged.add(activity);
        }

        Page<StJobInstanceEntity> digPage = stJobInstanceMapper.selectPage(
            new Page<>(1, RECENT_INSTANCE_FETCH, false),
            new QueryWrapper<StJobInstanceEntity>().isNotNull("start_time").orderByDesc("start_time"));
        for (StJobInstanceEntity entity : digPage.getRecords()) {
            InstanceActivityDTO activity = new InstanceActivityDTO();
            activity.setSource("集成");
            activity.setInstanceId(entity.getInstanceId());
            activity.setName(firstNonBlank(entity.getInstanceName(), "--"));
            activity.setRawStatus(entity.getStatus() == null ? null : String.valueOf(entity.getStatus()));
            activity.setStatusGroup(resolveUnified(ENGINE_DIG, entity.getStatus() == null ? null : String.valueOf(entity.getStatus())).name());
            activity.setTime(entity.getStartTime());
            activity.setPath("/dig/instance");
            merged.add(activity);
        }

        merged.sort(Comparator.comparing(
            InstanceActivityDTO::getTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return merged.size() > RECENT_INSTANCE_LIMIT ? new ArrayList<>(merged.subList(0, RECENT_INSTANCE_LIMIT)) : merged;
    }

    // ==================== 最近告警 ====================

    private List<RecentAlertDTO> buildRecentAlerts() {
        List<RecentAlertDTO> alerts = new ArrayList<>();
        Page<AlertRecordEntity> page = alertRecordMapper.selectPage(
            new Page<>(1, RECENT_ALERT_LIMIT, false),
            new QueryWrapper<AlertRecordEntity>()
                .select("id", "record_no", "title", "alert_level", "group_name", "requested_time")
                .orderByDesc("requested_time"));
        for (AlertRecordEntity entity : page.getRecords()) {
            RecentAlertDTO alert = new RecentAlertDTO();
            alert.setId(entity.getId());
            alert.setRecordNo(entity.getRecordNo());
            alert.setTitle(entity.getTitle());
            alert.setAlertLevel(entity.getAlertLevel());
            alert.setGroupName(entity.getGroupName());
            alert.setRequestedTime(entity.getRequestedTime());
            alerts.add(alert);
        }
        return alerts;
    }

    // ==================== 服务器健康 ====================

    private ServerHealthDTO buildServerHealth() {
        // getServerInfo 内部 OSHI 采样会 sleep 1 秒且开销不小，只调用一次
        try {
            ServerInfo serverInfo = monitorBusiness.getServerInfo();
            ServerHealthDTO health = new ServerHealthDTO();
            health.setCpuUsage(serverInfo.getCpuInfo().getUsed());
            health.setMemoryUsed(serverInfo.getMemoryInfo().getUsed());
            health.setMemoryTotal(serverInfo.getMemoryInfo().getTotal());
            health.setJvmUsed(serverInfo.getJvmInfo().getUsed());
            health.setJvmMax(serverInfo.getJvmInfo().getMax());
            return health;
        } catch (Exception e) {
            log.warn("获取服务器健康信息失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 状态归一化 ====================

    private UnifiedStatusEnum resolveUnified(String engine, String rawStatus) {
        switch (engine) {
            case ENGINE_FLINK:
                return UnifiedStatusEnum.fromFlink(rawStatus);
            case ENGINE_SPARK:
                return UnifiedStatusEnum.fromSpark(rawStatus);
            case ENGINE_DATASYNC:
                return UnifiedStatusEnum.fromDataSync(rawStatus);
            case ENGINE_DIG:
                Integer digStatus = rawStatus == null ? null : Integer.valueOf(rawStatus.trim());
                return UnifiedStatusEnum.fromDig(digStatus);
            default:
                log.warn("未知引擎标识: {}", engine);
                return UnifiedStatusEnum.STOPPED;
        }
    }

    private String enumName(Object statusEnum) {
        return statusEnum == null ? null : statusEnum.toString();
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isEmpty()) {
            return primary;
        }
        return fallback != null && !fallback.isEmpty() ? fallback : "--";
    }

    private Date beginOfToday() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date addDays(Date date, int days) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    private String formatDay(Date date) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
