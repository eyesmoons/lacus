package com.lacus.dao.dashboard.mapper;

import com.lacus.dao.dashboard.dto.EngineStatusCount;
import com.lacus.dao.dashboard.dto.InstanceTrendRow;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 工作台聚合查询（跨引擎实例表 UNION ALL 统计）
 */
public interface DashboardMapper {

    /**
     * 近 N 天四引擎实例按日×状态分组计数
     *
     * @param begin 起始时间（含），Java 侧算好传入
     * @param end   结束时间（不含），Java 侧算好传入
     */
    List<InstanceTrendRow> selectTrend(@Param("begin") Date begin, @Param("end") Date end);

    /**
     * 时间窗口内四引擎状态分布；begin 为 null 时统计全量（用于"当前运行中"口径）
     */
    List<EngineStatusCount> selectStatusDistribution(@Param("begin") Date begin, @Param("end") Date end);
}
