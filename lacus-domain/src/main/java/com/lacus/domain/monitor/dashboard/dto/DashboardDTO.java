package com.lacus.domain.monitor.dashboard.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 工作台聚合数据
 */
@Data
public class DashboardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 资源总览（KPI 卡片区） */
    private ResourceOverviewDTO resources;

    /** 近 N 天逐日趋势，按日期升序，缺省日补零 */
    private List<TrendPointDTO> trend;

    /** 状态分布：近 N 天 + 今日 */
    private StatusDistributionDTO statusDistribution;

    /** 四引擎实例合并按时间倒序取前 8 */
    private List<InstanceActivityDTO> recentInstances;

    /** 最近告警前 6 条 */
    private List<RecentAlertDTO> recentAlerts;

    /** 服务器健康信息，获取失败为 null */
    private ServerHealthDTO health;

    /** 服务端生成时间戳（毫秒） */
    private Long generatedAt;
}
