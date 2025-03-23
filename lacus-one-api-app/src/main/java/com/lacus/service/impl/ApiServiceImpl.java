package com.lacus.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lacus.common.config.CommonResult;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.constants.Constant;
import com.lacus.common.exception.ApiException;
import com.lacus.common.exception.BusinessException;
import com.lacus.common.parser.ApiThreadLocal;
import com.lacus.service.IDataProviderService;
import com.lacus.service.IRedisService;
import com.lacus.common.utils.DateUtils;
import com.lacus.core.build.RedisKeyBuilder;
import com.lacus.core.buried.BuriedFunc;
import com.lacus.core.buried.LogsBuried;
import com.lacus.service.dto.ApiConfigDTO;
import com.lacus.service.dto.ApiInfoDTO;
import com.lacus.dao.entity.ApiHistoryEntity;
import com.lacus.dao.entity.ApiInfoEntity;
import com.lacus.common.enums.CallStatusEnum;
import com.lacus.dao.mapper.ApiMapper;
import com.lacus.service.dto.DataResult;
import com.lacus.service.dto.TestResult;
import com.lacus.service.IApiService;
import com.lacus.service.IBuriedFuncService;
import com.lacus.service.vo.ApiParamsVO;
import com.lacus.service.vo.RequestParamsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ApiServiceImpl extends ServiceImpl<ApiMapper, ApiInfoEntity> implements IApiService {

    private final IRedisService IRedisService;
    private final IDataProviderService providerService;
    private final IBuriedFuncService buriedFuncService;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String QUERY = "query";
    private static final String LOCAL_HOST = "127.0.0.1";


    private static LambdaQueryWrapper<ApiInfoEntity> getQueryWrapper(String apiUrl) {
        LambdaQueryWrapper<ApiInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(apiUrl)) {
            queryWrapper.eq(ApiInfoEntity::getApiUrl, apiUrl);
            queryWrapper.eq(ApiInfoEntity::getDeleted, 0);
        }
        return queryWrapper;
    }


    private ApiInfoEntity queryFromMysql(String apiUrl) {
        LambdaQueryWrapper<ApiInfoEntity> wrapper = getQueryWrapper(apiUrl);
        return this.getOne(wrapper);
    }


    private ApiInfoEntity getApiInfo(String apiUrl) {
        ApiInfoEntity apiInfo = null;
        String apiKey = RedisKeyBuilder.apiKeyBuild(apiUrl);
        if (!IRedisService.hasKey(apiKey)) {
            apiInfo = queryFromMysql(apiUrl);
            if (Objects.isNull(apiInfo)) {
                log.info("当前接口请求地址不存在:{}", apiUrl);
                throw new BusinessException(ResultCode.API_URL_NOT_FOUND);
            }
            IRedisService.set(apiKey, apiInfo);
        } else {
            Object obj = IRedisService.get(apiKey);
            apiInfo = JSONObject.parseObject(JSON.toJSONString(obj), ApiInfoEntity.class);
        }
        return apiInfo;
    }


    @Override
    public CommonResult<DataResult> query(String apiUrl, Map<String, Object> params, String method) {
        ApiInfoEntity apiInfo = this.getApiInfo(apiUrl);
        String reqMethod = apiInfo.getReqMethod();
        if (!Objects.equals(reqMethod, method.toUpperCase(Locale.ROOT))) {
            throw new BusinessException(ResultCode.METHOD_NOT_MATCH);
        }
        return fetch(apiInfo, params, QUERY);
    }

    @Override
    public TestResult testApi(ApiInfoDTO apiDTO) {
        long startTime = System.currentTimeMillis();
        String config = apiDTO.getApiConfig();
        ApiConfigDTO apiConfig = JSONObject.parseObject(config, ApiConfigDTO.class);
        List<RequestParamsVO> requestParams = apiConfig.getApiParams().getRequestParams();
        Map<String, Object> paramMap = requestParams.stream().filter(req -> Objects.nonNull(req) && Objects.nonNull(req.getValue())).collect(Collectors.toMap(RequestParamsVO::getColumnName, RequestParamsVO::getValue));
        BuriedFunc.addLog(LogsBuried.setInfo(String.format("当前接口请求地址为:【%s】,请求参数列表为:【%s】！", apiDTO.getApiUrl(), paramMap)));
        ApiInfoEntity apiInfo = new ApiInfoEntity();
        BeanUtils.copyProperties(apiDTO, apiInfo);
        apiInfo.setApiConfig(apiDTO.getApiConfig());
        TestResult response = new TestResult();
        try {
            CommonResult<DataResult> result = this.fetch(apiInfo, paramMap, null);
            if (Objects.nonNull(result)) {
                response.setData(result.getData());
            }
            response.setCode(ResultCode.SUCCESS.getCode());
        } catch (Exception e) {
            log.info("在线测试运行接口调用失败:{}", e.getMessage());
            response.setCode(ResultCode.FAILED.getCode());
        } finally {
            long delay = System.currentTimeMillis() - startTime;
            log.info("查询总计耗时: {}ms", delay);
            BuriedFunc.addLog(LogsBuried.setInfo(String.format("查询总计耗时:【%s】ms！", delay)));
            response.setDebugInfo(BuriedFunc.printLog());
            response.setCostTime(delay);
        }
        return response;
    }

    @Override
    public Boolean flushCache(Long id, Integer status) {
        ApiInfoEntity apiInfo = this.getById(id);
        String apiKey = RedisKeyBuilder.apiKeyBuild(apiInfo.getApiUrl());
        Boolean flag = IRedisService.hasKey(apiKey);
        if (Objects.equals(Constant.ONLINE, status)) {
            if (flag) {
                IRedisService.del(apiKey);
            }
            IRedisService.set(apiKey, apiInfo);
        } else {
            if (flag) {
                IRedisService.del(apiKey);
            }
        }
        return true;
    }


    private CommonResult<DataResult> fetch(ApiInfoEntity apiInfo, Map<String, Object> params, String callType) {
        Integer status = apiInfo.getStatus();
        if (Objects.equals(callType, QUERY) && !Objects.equals(Constant.ONLINE, status)) {
            log.info("当前接口请求地址未发布:{},状态:{}", apiInfo, status == 0 ? "未发布(0)" : "已发布(1)");
            BuriedFunc.addLog(LogsBuried.setError(String.format("当前接口请求地址未发布,接口名称：【%s】！", apiInfo.getApiName())));
            throw new BusinessException(ResultCode.API_STATUS_NOT_ONLINE);
        }
        return exec(apiInfo, params, callType);
    }


    private CommonResult<DataResult> exec(ApiInfoEntity apiInfo, Map<String, Object> params, String callType) {
        long start = System.currentTimeMillis();
        String apiUrl = apiInfo.getApiUrl();
        ApiHistoryEntity callHistoryEntity = new ApiHistoryEntity();
        try {
            Long datasourceId = apiInfo.getDatasourceId();
            String config = apiInfo.getApiConfig();
            ApiConfigDTO apiConfig = JSONObject.parseObject(config, ApiConfigDTO.class);
            callHistoryEntity.apiUrl(apiUrl).callDate(DateUtils.getDate()).callStatus(CallStatusEnum.SUCCESS).callCode(0L).callTime(DateUtils.getCurrentDate());
            this.checkParams(apiUrl, apiConfig, params);
            BuriedFunc.addLog(LogsBuried.setInfo("基础校验通过,开始执行接口！"));
            apiConfig.setLimitCount(apiInfo.getLimitCount());
            apiConfig.setQueryTimeout(apiInfo.getQueryTimeout());
            apiConfig.setApiName(apiInfo.getApiName());
            DataResult result = providerService.provider(datasourceId, apiConfig, params);
            return CommonResult.success(result);
        } catch (BusinessException bex) {
            callHistoryEntity.callStatus(CallStatusEnum.FAIL).callCode(bex.getErrorCode().getCode()).errorInfo(bex.getMessage());
            BuriedFunc.addLog(LogsBuried.setError(String.format("接口请求异常,接口名称:【%s】,异常原因: 【%s】！", apiInfo.getApiName(), bex.getMessage())));
            throw new BusinessException(bex.getErrorCode(), bex.getMessage());
        } catch (ApiException aex) {
            callHistoryEntity.callStatus(CallStatusEnum.FAIL).callCode(aex.getErrorCode().getCode()).errorInfo(aex.getMessage());
            BuriedFunc.addLog(LogsBuried.setError(String.format("接口请求错误,接口名称:【%s】,错误原因: 【%s】！", apiInfo.getApiName(), aex.getMessage())));
            throw new ApiException(aex.getErrorCode(), String.format("接口调用失败:【%s】", aex.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            callHistoryEntity.callStatus(CallStatusEnum.FAIL).callCode(ResultCode.FAILED.getCode()).errorInfo(e.getMessage());
            throw new ApiException(ResultCode.FAILED, String.format("接口执行失败:【%s】！", e.getMessage()));
        } finally {
            if (Objects.equals(callType, QUERY)) {
                executor.submit(() -> {
                    long end = System.currentTimeMillis();
                    callHistoryEntity.setCallDelay(end - start);
                    callHistoryEntity.setCallIp(ApiThreadLocal.get() == null ? LOCAL_HOST : ApiThreadLocal.get().getRemoteIp());
                    buriedFuncService.buriedFunc(callHistoryEntity);
                });
            }
        }
    }


    /**
     * 校验参数
     *
     * @param apiConfig
     * @param params
     */
    private void checkParams(String apiUrl, ApiConfigDTO apiConfig, Map<String, Object> params) {
        ApiParamsVO apiParams = apiConfig.getApiParams();
        List<RequestParamsVO> requestParams = apiParams.getRequestParams();
        Set<String> mustColumnSet = requestParams.stream().filter(req -> Objects.equals(req.getRequired(), 1)).map(RequestParamsVO::getColumnName).collect(Collectors.toSet());
        for (String col : mustColumnSet) {
            Object res = params.get(col);
            if (ObjectUtils.isEmpty(res)) {
                log.info("接口参数必填项缺失，接口地址:{},缺失参数字段:{}", apiUrl, col);
                throw new BusinessException(ResultCode.API_MUST_COLUMN_IS_NULL, String.format("接口参数必填项缺失，接口地址:【%s】,缺失参数字段名:【%s】！", apiUrl, col));
            }
        }

    }
}
