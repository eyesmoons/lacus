package com.lacus.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.exception.ApiException;
import com.lacus.service.IRedisService;
import com.lacus.core.build.RedisKeyBuilder;
import com.lacus.dao.entity.DataSourceEntity;
import com.lacus.dao.mapper.DataSourceMapper;
import com.lacus.service.IDataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("apiDatasourceService")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataSourceServiceImpl implements IDataSourceService {

    private final DataSourceMapper dataSourceMapper;

    private final IRedisService IRedisService;

    @Override
    public DataSourceEntity getDatasourceById(Long id) {
        String datasourceKey = RedisKeyBuilder.datasourceKeyBuild(id);
        DataSourceEntity datasource = null;
        if (!IRedisService.hasKey(datasourceKey)) {
            datasource = dataSourceMapper.getDatasourceById(id);
            if (Objects.isNull(datasource)) {
                log.info("当前接口请求数据源地址不存在:{}", id);
                throw new ApiException(ResultCode.API_DATASOURCE_NOT_FOUND);
            }
            IRedisService.set(datasourceKey, datasource);
        } else {
            Object obj = IRedisService.get(datasourceKey);
            datasource = JSONObject.parseObject(JSON.toJSONString(obj), DataSourceEntity.class);
        }
        return datasource;
    }
}
