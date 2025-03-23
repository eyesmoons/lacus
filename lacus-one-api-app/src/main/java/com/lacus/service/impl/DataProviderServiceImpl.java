package com.lacus.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lacus.core.buried.BuriedFunc;
import com.lacus.core.buried.LogsBuried;
import com.lacus.service.IDataProviderService;
import com.lacus.service.dto.ApiConfigDTO;
import com.lacus.dao.entity.DataSourceEntity;
import com.lacus.common.enums.PageFlagEnum;
import com.lacus.core.provider.AbstractProvider;
import com.lacus.core.provider.ProviderManager;
import com.lacus.service.dto.DataResult;
import com.lacus.service.dto.ResultManager;
import com.lacus.service.IDataSourceService;
import com.lacus.common.utils.FieldConvertUtils;
import com.lacus.service.vo.DataPageVO;
import com.lacus.service.vo.ReturnParamsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("dataProviderService")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataProviderServiceImpl implements IDataProviderService {

    private final IDataSourceService dataSourceService;

    private static final ThreadPoolExecutor executor =
            new ThreadPoolExecutor(10, 50, 200L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());


    @Override
    public DataResult provider(Long datasourceId, ApiConfigDTO apiConfig, Map<String, Object> params) {
        DataSourceEntity datasource = dataSourceService.getDatasourceById(datasourceId);
        DataPageVO dataPage = JSON.parseObject(JSON.toJSONString(params), DataPageVO.class);
        AbstractProvider dataProvider = ProviderManager.provide(datasource);
        Integer pageFlag = apiConfig.getPageFlag();
        dataPage.setPageFlag(pageFlag);
        //如果要求分页,则异步获取数据总量
        Future<Long> asynResult = null;
        if (Objects.equals(pageFlag, PageFlagEnum.FLAG_TRUE.getCode())) {
            BuriedFunc.addLog(LogsBuried.setInfo("当前接口属于分页查询接口，开始构建分页查询SQL！"));
            //这块这样写主要是因为，异步调用时，用同一个apiConfig对象，可能会导致前面的sql覆盖后面的sql，导致查询异常
            ApiConfigDTO asyncApiConfig = new ApiConfigDTO();
            BeanUtils.copyProperties(apiConfig, asyncApiConfig);
            asynResult = executor.submit(() -> dataProvider.processTotal(asyncApiConfig, params));
        }
        List<Map<String, Object>> dataList = dataProvider.process(apiConfig, params);
        //按照返回值字段配置来映射数据
        List<Map<String, Object>> result = resultConvert(dataList, apiConfig.getApiParams().getReturnParams());
        return ResultManager.push(result, asynResult, dataPage);
    }

    private List<Map<String, Object>> resultConvert(List<Map<String, Object>> dataList, List<ReturnParamsVO> returnColList) {
        Set<String> returnColSet = returnColList.stream().map(ReturnParamsVO::getColumnName).collect(Collectors.toSet());
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Map<String, Object> data : dataList) {
            Map<String, Object> map = Maps.newHashMap();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String key = entry.getKey();
                if (returnColSet.contains(key)) {
                    map.put(FieldConvertUtils.lineToHump(key), entry.getValue());
                }
            }
            result.add(map);
        }
        return result;
    }


}
