package com.lacus.dao.oneapi.mapper;

import com.lacus.common.core.dto.oneapi.HistoryRowDTO;
import com.lacus.common.core.dto.oneapi.MonitorOverviewItemDTO;
import com.lacus.common.core.dto.oneapi.StatsSummaryDTO;
import com.lacus.common.core.dto.oneapi.TopApiDTO;
import com.lacus.common.core.dto.oneapi.TrendBucketDTO;
import com.lacus.dao.oneapi.entity.OneApiCallHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface OneApiCallHistoryMapper extends BaseMapper<OneApiCallHistoryEntity> {

    /**
     * 监控概览：按 api_url 分组聚合
     */
    List<MonitorOverviewItemDTO> selectMonitorOverview(@Param("startTime") Date startTime,
                                                      @Param("endTime") Date endTime,
                                                      @Param("datasourceId") Long datasourceId);

    /**
     * 单接口有序 delay 列表（供 Java 取 P95）
     */
    List<Long> selectDelaysByApiUrl(@Param("apiUrl") String apiUrl,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime);

    /**
     * 统计概览：总量
     */
    StatsSummaryDTO selectStatsSummary(@Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime,
                                       @Param("datasourceId") Long datasourceId);

    /**
     * TopN 接口（按 callCount 降序）
     */
    List<TopApiDTO> selectTopApis(@Param("startTime") Date startTime,
                                  @Param("endTime") Date endTime,
                                  @Param("datasourceId") Long datasourceId,
                                  @Param("limit") Integer limit);

    /**
     * 趋势分桶（databaseId 切换日期函数）
     */
    List<TrendBucketDTO> selectTrend(@Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     @Param("dateFormat") String dateFormat);

    /**
     * 历史分页（关联 one_api_info 取 apiName/reqMethod）
     */
    List<HistoryRowDTO> selectHistoryPage(@Param("startTime") Date startTime,
                                          @Param("endTime") Date endTime,
                                          @Param("apiUrl") String apiUrl,
                                          @Param("callStatus") String callStatus,
                                          @Param("offset") long offset,
                                          @Param("pageSize") int pageSize);

    /**
     * 历史分页总数
     */
    Long selectHistoryCount(@Param("startTime") Date startTime,
                            @Param("endTime") Date endTime,
                            @Param("apiUrl") String apiUrl,
                            @Param("callStatus") String callStatus);
}
