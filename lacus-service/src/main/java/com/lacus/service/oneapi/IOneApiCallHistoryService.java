package com.lacus.service.oneapi;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.common.core.dto.oneapi.HistoryRowDTO;
import com.lacus.common.core.dto.oneapi.MonitorOverviewItemDTO;
import com.lacus.common.core.dto.oneapi.StatsSummaryDTO;
import com.lacus.common.core.dto.oneapi.TopApiDTO;
import com.lacus.common.core.dto.oneapi.TrendBucketDTO;
import com.lacus.dao.oneapi.entity.OneApiCallHistoryEntity;

import java.util.Date;
import java.util.List;

public interface IOneApiCallHistoryService extends IService<OneApiCallHistoryEntity> {

    List<MonitorOverviewItemDTO> selectMonitorOverview(Date startTime, Date endTime, Long datasourceId);

    List<Long> selectDelaysByApiUrl(String apiUrl, Date startTime, Date endTime);

    StatsSummaryDTO selectStatsSummary(Date startTime, Date endTime, Long datasourceId);

    List<TopApiDTO> selectTopApis(Date startTime, Date endTime, Long datasourceId, Integer limit);

    List<TrendBucketDTO> selectTrend(Date startTime, Date endTime, String dateFormat);

    List<HistoryRowDTO> selectHistoryPage(Date startTime, Date endTime, String apiUrl,
                                          String callStatus, long offset, int pageSize);

    Long selectHistoryCount(Date startTime, Date endTime, String apiUrl, String callStatus);
}
