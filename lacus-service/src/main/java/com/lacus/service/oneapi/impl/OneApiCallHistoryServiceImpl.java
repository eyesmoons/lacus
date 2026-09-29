package com.lacus.service.oneapi.impl;

import com.lacus.common.core.dto.oneapi.HistoryRowDTO;
import com.lacus.common.core.dto.oneapi.MonitorOverviewItemDTO;
import com.lacus.common.core.dto.oneapi.StatsSummaryDTO;
import com.lacus.common.core.dto.oneapi.TopApiDTO;
import com.lacus.common.core.dto.oneapi.TrendBucketDTO;
import com.lacus.dao.oneapi.entity.OneApiCallHistoryEntity;
import com.lacus.dao.oneapi.mapper.OneApiCallHistoryMapper;
import com.lacus.service.oneapi.IOneApiCallHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OneApiCallHistoryServiceImpl
        extends ServiceImpl<OneApiCallHistoryMapper, OneApiCallHistoryEntity>
        implements IOneApiCallHistoryService {

    @Override
    public List<MonitorOverviewItemDTO> selectMonitorOverview(Date startTime, Date endTime, Long datasourceId) {
        return baseMapper.selectMonitorOverview(startTime, endTime, datasourceId);
    }

    @Override
    public List<Long> selectDelaysByApiUrl(String apiUrl, Date startTime, Date endTime) {
        return baseMapper.selectDelaysByApiUrl(apiUrl, startTime, endTime);
    }

    @Override
    public StatsSummaryDTO selectStatsSummary(Date startTime, Date endTime, Long datasourceId) {
        return baseMapper.selectStatsSummary(startTime, endTime, datasourceId);
    }

    @Override
    public List<TopApiDTO> selectTopApis(Date startTime, Date endTime, Long datasourceId, Integer limit) {
        return baseMapper.selectTopApis(startTime, endTime, datasourceId, limit);
    }

    @Override
    public List<TrendBucketDTO> selectTrend(Date startTime, Date endTime, String dateFormat) {
        return baseMapper.selectTrend(startTime, endTime, dateFormat);
    }

    @Override
    public List<HistoryRowDTO> selectHistoryPage(Date startTime, Date endTime, String apiUrl,
                                                 String callStatus, long offset, int pageSize) {
        return baseMapper.selectHistoryPage(startTime, endTime, apiUrl, callStatus, offset, pageSize);
    }

    @Override
    public Long selectHistoryCount(Date startTime, Date endTime, String apiUrl, String callStatus) {
        return baseMapper.selectHistoryCount(startTime, endTime, apiUrl, callStatus);
    }
}
