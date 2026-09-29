package com.lacus.domain.oneapi;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lacus.common.constant.Constants;
import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.core.page.PageDTO;
import com.lacus.common.exception.CustomException;
import com.lacus.common.core.dto.oneapi.HistoryDetailDTO;
import com.lacus.common.core.dto.oneapi.HistoryRowDTO;
import com.lacus.common.core.dto.oneapi.MonitorOverviewDTO;
import com.lacus.common.core.dto.oneapi.MonitorOverviewItemDTO;
import com.lacus.common.core.dto.oneapi.StatsSummaryDTO;
import com.lacus.common.core.dto.oneapi.StatsTrendDTO;
import com.lacus.common.core.dto.oneapi.TopApiDTO;
import com.lacus.common.core.dto.oneapi.TrendBucketDTO;
import com.lacus.dao.metadata.entity.MetaDatasourceEntity;
import com.lacus.dao.oneapi.entity.OneApiCallHistoryEntity;
import com.lacus.dao.oneapi.entity.OneApiInfoEntity;
import com.lacus.domain.common.command.BulkOperationCommand;
import com.lacus.domain.oneapi.command.ApiAddCommand;
import com.lacus.domain.oneapi.command.ApiUpdateCommand;
import com.lacus.domain.oneapi.dto.ApiConfigDTO;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.ApiParamsDTO;
import com.lacus.domain.oneapi.dto.ApiParseDTO;
import com.lacus.domain.oneapi.dto.ApiTestResp;
import com.lacus.domain.oneapi.dto.RequestParamsDTO;
import com.lacus.domain.oneapi.dto.ReturnParamsDTO;
import com.lacus.domain.oneapi.feign.OneApiFeignClient;
import com.lacus.domain.oneapi.model.OneApiInfoModel;
import com.lacus.domain.oneapi.model.OneApiInfoModelFactory;
import com.lacus.domain.oneapi.parse.MySQLParseProcessor;
import com.lacus.domain.oneapi.query.OneApiInfoQuery;
import com.lacus.service.metadata.IMetaDataSourceService;
import com.lacus.service.oneapi.IOneApiCallHistoryService;
import com.lacus.service.oneapi.IOneApiInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class OneApiInfoBusiness {

    @Autowired
    private IOneApiInfoService oneApiInfoService;

    @Autowired
    private IMetaDataSourceService metaDataSourceService;

    @Autowired
    private IOneApiCallHistoryService oneApiCallHistoryService;

    @Autowired
    private OneApiFeignClient oneApiFeignClient;

    private static final Pattern SQL_COMMENT_REGEX = Pattern.compile("(/\\*+?[\\w\\W]+?\\*/)");

    private static final Integer RESPONSE_MAX_SIZE = 5;

    public PageDTO pageList(OneApiInfoQuery query) {
        Page<OneApiInfoEntity> page = oneApiInfoService.page(query.toPage(), query.toQueryWrapper());
        page.getRecords().forEach(item -> {
            item.setDatasourceName(metaDataSourceService.getById(item.getDatasourceId()).getDatasourceName());
        });
        return new PageDTO(page.getRecords(), page.getTotal());
    }

    public OneApiInfoModel addApi(ApiAddCommand addCommand) {
        checkApi(addCommand, true);
        OneApiInfoModel model = OneApiInfoModelFactory.loadFromAddCommand(addCommand, new OneApiInfoModel());
        extraResponse(addCommand);
        boolean insert = model.insert();
        if (insert) {
            oneApiFeignClient.flushCache(model.getApiId(), model.getStatus());
        }
        return model;
    }

    public void updateApi(ApiUpdateCommand updateCommand) {
        checkApi(updateCommand, false);
        OneApiInfoModel model = OneApiInfoModelFactory.loadFromDb(updateCommand.getApiId(), oneApiInfoService);
        extraResponse(updateCommand);
        OneApiInfoModelFactory.loadFromAddCommand(updateCommand, model);
        boolean update = model.updateById();
        if (update) {
            oneApiFeignClient.flushCache(model.getApiId(), model.getStatus());
        }
    }

    public void deleteApi(BulkOperationCommand<Long> command) {
        boolean delete = oneApiInfoService.removeBatchByIds(command.getIds());
        if (delete) {
            for (Long id : command.getIds()) {
                oneApiFeignClient.flushCache(id, 0);
            }
        }
    }

    public OneApiInfoEntity getApiInfo(Long apiId) {
        OneApiInfoEntity byId = oneApiInfoService.getById(apiId);
        if (Objects.isNull(byId)) {
            throw new CustomException(String.format("数据[%s]不存在", apiId));
        }
        MetaDatasourceEntity metaDatasource = metaDataSourceService.getById(byId.getDatasourceId());
        if (Objects.isNull(metaDatasource)) {
            throw new CustomException(String.format("数据源[%s]不存在", byId.getDatasourceId()));
        }
        byId.setDatasourceName(metaDatasource.getDatasourceName());
        return byId;
    }

    public void checkApi(ApiAddCommand addCommand, boolean add) {
        String apiUrl = addCommand.getApiUrl();
        //校验接口地址是否存在
        if (add) {
            OneApiInfoEntity oneApiInfo = oneApiInfoService.queryApiByUrl(apiUrl);
            if (Objects.nonNull(oneApiInfo)) {
                throw new CustomException(String.format("接口[%s]地址已存在", apiUrl));
            }
        }
        if (addCommand.getLimitCount() > 1000) {
            throw new CustomException(String.format("接口[%s]最大返回条数超过限制[%s]", apiUrl, addCommand.getLimitCount()));
        }
        //校验sql脚本
        String apiConfig = addCommand.getApiConfig();
        ApiConfigDTO apiConfigDTO = JSONObject.parseObject(apiConfig, ApiConfigDTO.class);
        this.checkSqlScript(apiConfigDTO);
    }

    private void checkSqlScript(ApiConfigDTO apiConfig) {
        String sql = apiConfig.getSql().trim().toUpperCase();
        if (sql.startsWith("/*")) {
            sql = this.hasSqlComment(sql);
        }
        if (!sql.startsWith("SELECT")) {
            throw new CustomException("只支持SELECT语句！");
        }
    }

    private String hasSqlComment(String sqlScript) {
        Matcher matcher = SQL_COMMENT_REGEX.matcher(sqlScript);
        if (matcher.find()) {
            String offCommentSql = matcher.group(0);
            sqlScript = sqlScript.replace(offCommentSql, "");
        }
        return sqlScript;
    }

    private void extraResponse(ApiAddCommand addCommand) {
        String apiResponse = addCommand.getApiResponse();
        JSONObject jsonObject = JSONObject.parseObject(apiResponse);
        JSONArray list = jsonObject.getJSONArray("list");
        if (list.size() > RESPONSE_MAX_SIZE) {
            List<Object> subList = list.subList(0, RESPONSE_MAX_SIZE);
            jsonObject.put("list", subList);
        }
        addCommand.setApiResponse(JSON.toJSONString(jsonObject, JSONWriter.Feature.PrettyFormat));
    }

    public ApiParamsDTO parse(ApiParseDTO parseDTO) {
        String apiUrl = parseDTO.getApiUrl();
        String sqlScript = parseDTO.getSqlScript();
        OneApiInfoEntity oldApiInfo = oneApiInfoService.queryApiByUrl(apiUrl);
        ApiConfigDTO oldApiConfig = null;
        List<RequestParamsDTO> requestParams = new ArrayList<>();
        if (Objects.nonNull(oldApiInfo)) {
            oldApiConfig = JSONObject.parseObject(oldApiInfo.getApiConfig(), ApiConfigDTO.class);
        }
        try {
            ApiParamsDTO apiParamsDTO = new ApiParamsDTO();
            List<ReturnParamsDTO> returnParams = new ArrayList<>();
            MySQLParseProcessor mySQLDynamicParseAdapter = new MySQLParseProcessor();
            Map<String, Set<String>> resultMap = mySQLDynamicParseAdapter.doParse(sqlScript);
            Set<String> reqSet = resultMap.get("req");
            if (CollectionUtil.isNotEmpty(reqSet)) {
                Map<String, RequestParamsDTO> oldReqMap = null;
                if (Objects.nonNull(oldApiConfig)) {
                    List<RequestParamsDTO> oldRequestParams = oldApiConfig.getApiParams().getRequestParams();
                    oldReqMap = oldRequestParams.stream().collect(Collectors.toMap(RequestParamsDTO::getColumnName, req -> req));
                    if (Objects.equals(1, oldApiConfig.getPageFlag())) {
                        requestParams.add(oldReqMap.get(Constants.PAGE_NUM));
                        requestParams.add(oldReqMap.get(Constants.PAGE_SIZE));
                    }
                }
                for (String req : reqSet) {
                    if (CollectionUtil.isNotEmpty(oldReqMap) && oldReqMap.containsKey(req)) {
                        requestParams.add(oldReqMap.get(req));
                        continue;
                    }
                    RequestParamsDTO requestParamsVO = new RequestParamsDTO();
                    requestParamsVO.setColumnName(req);
                    requestParamsVO.setRequired(0);
                    requestParams.add(requestParamsVO);
                }
            }

            apiParamsDTO.setRequestParams(requestParams);
            Set<String> returnSet = resultMap.get("return");
            if (CollectionUtil.isNotEmpty(returnSet)) {
                Map<String, ReturnParamsDTO> oldReturnMap = null;
                if (Objects.nonNull(oldApiConfig)) {
                    List<ReturnParamsDTO> oldRequestParams = oldApiConfig.getApiParams().getReturnParams();
                    oldReturnMap = oldRequestParams.stream().collect(Collectors.toMap(ReturnParamsDTO::getColumnName, req -> req));
                }
                for (String ret : returnSet) {
                    if (CollectionUtil.isNotEmpty(oldReturnMap) && oldReturnMap.containsKey(ret)) {
                        returnParams.add(oldReturnMap.get(ret));
                        continue;
                    }
                    ReturnParamsDTO returnParamsVO = new ReturnParamsDTO();
                    returnParamsVO.setColumnName(ret);
                    returnParams.add(returnParamsVO);
                }
            }
            apiParamsDTO.setReturnParams(returnParams);
            return apiParamsDTO;
        } catch (Exception e) {
            throw new CustomException("动态SQL解析异常，请检查SQL语句", e);
        }
    }

    public ResponseDTO<ApiTestResp> testApi(ApiInfoDTO apiDTO) {
        ResponseDTO<ApiTestResp> result = oneApiFeignClient.testApi(apiDTO);
        if (result.getData().getCode() != 0) {
            return result;
        }
        Object data = result.getData();
        ApiTestResp response = JSONObject.parseObject(JSONObject.toJSONString(data), ApiTestResp.class);
        String formatResult = JSON.toJSONString(response.getData(), JSONWriter.Feature.PrettyFormat);
        response.setData(formatResult);
        result.setData(response);
        return result;
    }

    public Boolean updateStatus(Long id, Integer status) {
        OneApiInfoEntity entity = new OneApiInfoEntity();
        entity.setApiId(id);
        entity.setStatus(status);
        boolean update = oneApiInfoService.updateById(entity);
        if (update) {
            oneApiFeignClient.flushCache(id, status);
        }
        return update;
    }

    public ResponseDTO<ApiTestResp> onlineTest(ApiInfoDTO apiInfoDTO) {
        String apiUrl = apiInfoDTO.getApiUrl();
        OneApiInfoEntity apiInfo = oneApiInfoService.queryApiByUrl(apiUrl);

        ApiConfigDTO apiConfig = JSONObject.parseObject(apiInfoDTO.getApiConfig(), ApiConfigDTO.class);
        List<RequestParamsDTO> requestParams = apiConfig.getApiParams().getRequestParams();
        ApiConfigDTO initApiConfig = JSONObject.parseObject(apiInfo.getApiConfig(), ApiConfigDTO.class);

        ApiParamsDTO apiParams = initApiConfig.getApiParams();
        apiParams.setRequestParams(requestParams);
        initApiConfig.setApiParams(apiParams);
        apiInfo.setApiConfig(JSONObject.toJSONString(initApiConfig));
        BeanUtils.copyProperties(apiInfo, apiInfoDTO);
        return this.testApi(apiInfoDTO);
    }

    // ========================= 可观测性：监控/统计/历史/文档导出 =========================

    /**
     * 监控概览：按 api_url 聚合调用次数/成功/失败/错误率/平均耗时/P95 耗时。
     * 当 startTime/endTime 未传时，按 range（1h/24h/7d，默认 24h）自动推导时间区间。
     */
    public MonitorOverviewDTO monitorOverview(Date startTime, Date endTime, String range, Long datasourceId) {
        if (startTime == null || endTime == null) {
            String resolved = (range == null || range.isEmpty()) ? "24h" : range;
            Date[] interval = resolveInterval(resolved);
            startTime = interval[0];
            endTime = interval[1];
        }
        List<MonitorOverviewItemDTO> items = oneApiCallHistoryService.selectMonitorOverview(startTime, endTime, datasourceId);
        for (MonitorOverviewItemDTO item : items) {
            // 关联 one_api_info 补全 apiName/datasourceName
            OneApiInfoEntity info = oneApiInfoService.queryApiByUrl(item.getApiUrl());
            if (info != null) {
                item.setApiName(info.getApiName());
                MetaDatasourceEntity ds = metaDataSourceService.getById(info.getDatasourceId());
                if (ds != null) {
                    item.setDatasourceName(ds.getDatasourceName());
                }
            }
            // P95 耗时
            List<Long> delays = oneApiCallHistoryService.selectDelaysByApiUrl(item.getApiUrl(), startTime, endTime);
            item.setP95Cost(computeP95(delays));
            // 错误率
            item.setErrorRate(item.getCallCount() == null || item.getCallCount() == 0
                    ? 0.0 : (double) item.getFailCount() / item.getCallCount());
        }
        return new MonitorOverviewDTO(items);
    }

    /**
     * 统计概览：总量/成功率/平均耗时/失败次数 + Top10 接口
     * 当 startTime/endTime 未传时，按 range（1h/24h/7d，默认 24h）自动推导时间区间。
     */
    public StatsSummaryDTO statsSummary(Date startTime, Date endTime, String range, Long datasourceId) {
        if (startTime == null || endTime == null) {
            String resolved = (range == null || range.isEmpty()) ? "24h" : range;
            Date[] interval = resolveInterval(resolved);
            startTime = interval[0];
            endTime = interval[1];
        }
        StatsSummaryDTO summary = oneApiCallHistoryService.selectStatsSummary(startTime, endTime, datasourceId);
        List<TopApiDTO> tops = oneApiCallHistoryService.selectTopApis(startTime, endTime, datasourceId, 10);
        for (TopApiDTO t : tops) {
            OneApiInfoEntity info = oneApiInfoService.queryApiByUrl(t.getApiUrl());
            if (info != null) {
                t.setApiName(info.getApiName());
            }
            t.setErrorRate(t.getCallCount() == null || t.getCallCount() == 0
                    ? 0.0 : (double) t.getFailCount() / t.getCallCount());
        }
        summary.setTopApis(tops);
        return summary;
    }

    /**
     * 统计趋势：按时间分桶的次数/耗时/错误数
     * 当 startTime/endTime 未传时，按 range（1h/24h/7d，默认 24h）自动推导时间区间。
     */
    public StatsTrendDTO statsTrend(Date startTime, Date endTime, String range) {
        if (startTime == null || endTime == null) {
            String resolved = (range == null || range.isEmpty()) ? "24h" : range;
            Date[] interval = resolveInterval(resolved);
            startTime = interval[0];
            endTime = interval[1];
        }
        String dateFormat = resolveDateFormat(startTime, endTime);
        List<TrendBucketDTO> buckets = oneApiCallHistoryService.selectTrend(startTime, endTime, dateFormat);
        return new StatsTrendDTO(buckets);
    }

    /**
     * 调用历史分页
     * 当 startTime/endTime 未传时，默认取最近 24 小时。
     */
    public PageDTO historyPage(Date startTime, Date endTime, String apiUrl, String callStatus,
                               Integer pageNum, Integer pageSize) {
        if (startTime == null || endTime == null) {
            Date[] interval = resolveInterval("24h");
            startTime = interval[0];
            endTime = interval[1];
        }
        long offset = (long) (pageNum - 1) * pageSize;
        List<HistoryRowDTO> rows = oneApiCallHistoryService.selectHistoryPage(
                startTime, endTime, apiUrl, callStatus, offset, pageSize);
        Long total = oneApiCallHistoryService.selectHistoryCount(startTime, endTime, apiUrl, callStatus);
        return new PageDTO(rows, total == null ? 0L : total);
    }

    /**
     * 单次调用详情
     */
    public HistoryDetailDTO historyDetail(Long callId) {
        OneApiCallHistoryEntity e = oneApiCallHistoryService.getById(callId);
        if (Objects.isNull(e)) {
            throw new CustomException(String.format("调用记录[%s]不存在", callId));
        }
        HistoryDetailDTO dto = new HistoryDetailDTO();
        dto.setStatus(e.getCallStatus());
        dto.setCostTime(e.getCallDelay());
        dto.setCaller(e.getCallIp());
        dto.setErrorMessage(e.getErrorInfo());
        // requestBody / responseSummary 在 one_api_call_history 表中无对应字段，置 null
        OneApiInfoEntity info = oneApiInfoService.queryApiByUrl(e.getApiUrl());
        if (info != null) {
            dto.setApiName(info.getApiName());
        }
        return dto;
    }

    /**
     * 接口文档导出：按 apiIds 渲染 Markdown
     */
    public String exportApiDoc(List<Long> apiIds) {
        StringBuilder md = new StringBuilder("# 接口文档\n\n");
        for (Long apiId : apiIds) {
            OneApiInfoEntity info = oneApiInfoService.getById(apiId);
            if (info == null) {
                continue;
            }
            md.append("## ").append(info.getApiName()).append("\n\n");
            md.append("- **URL**: ").append(info.getApiUrl()).append("\n");
            md.append("- **请求方式**: ").append(info.getReqMethod()).append("\n");
            md.append("- **描述**: ").append(info.getApiDesc() == null ? "" : info.getApiDesc()).append("\n\n");
            try {
                ApiConfigDTO cfg = JSONObject.parseObject(info.getApiConfig(), ApiConfigDTO.class);
                ApiParamsDTO params = cfg.getApiParams();
                md.append("### 请求参数\n| 字段 | 类型 | 必填 | 说明 |\n|---|---|---|---|\n");
                if (params != null && params.getRequestParams() != null) {
                    for (RequestParamsDTO p : params.getRequestParams()) {
                        md.append("| ").append(nullToEmpty(p.getColumnName())).append(" | ")
                                .append(nullToEmpty(p.getColumnType())).append(" | ")
                                .append(p.getRequired() == null ? "" : p.getRequired()).append(" | ")
                                .append(nullToEmpty(p.getDescription())).append(" |\n");
                    }
                }
                md.append("\n### 响应参数\n| 字段 | 类型 | 说明 |\n|---|---|---|\n");
                if (params != null && params.getReturnParams() != null) {
                    for (ReturnParamsDTO p : params.getReturnParams()) {
                        md.append("| ").append(nullToEmpty(p.getColumnName())).append(" | ")
                                .append(nullToEmpty(p.getColumnType())).append(" | ")
                                .append(nullToEmpty(p.getColumnDesc())).append(" |\n");
                    }
                }
            } catch (Exception ex) {
                md.append("> 暂无参数信息\n");
            }
            md.append("\n---\n\n");
        }
        return md.toString();
    }

    // ========================= 私有工具 =========================

    /**
     * 计算 P95（输入须已升序）
     */
    private Long computeP95(List<Long> sorted) {
        if (sorted == null || sorted.isEmpty()) {
            return 0L;
        }
        int index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        if (index < 0) {
            index = 0;
        }
        return sorted.get(index);
    }

    /**
     * 将 range（1h/24h/7d）转换为 [startTime, endTime]，以当前时间为终点。
     */
    private Date[] resolveInterval(String range) {
        long millis;
        switch (range) {
            case "1h":
                millis = 60L * 60 * 1000;
                break;
            case "7d":
                millis = 7L * 24 * 60 * 60 * 1000;
                break;
            case "24h":
            default:
                millis = 24L * 60 * 60 * 1000;
                break;
        }
        Date end = new Date();
        Date start = new Date(end.getTime() - millis);
        return new Date[] {start, end};
    }

    /**
     * 按区间长度自动选桶粒度（MySQL DATE_FORMAT 格式）
     */
    private String resolveDateFormat(Date startTime, Date endTime) {
        long diffMs = endTime.getTime() - startTime.getTime();
        long dayMs = 24L * 60 * 60 * 1000;
        if (diffMs <= dayMs) {
            return "%Y-%m-%d %H:00"; // 小时桶
        } else if (diffMs <= 7L * dayMs) {
            return "%Y-%m-%d";       // 天桶
        } else {
            return "%Y-%u";          // 周桶
        }
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
