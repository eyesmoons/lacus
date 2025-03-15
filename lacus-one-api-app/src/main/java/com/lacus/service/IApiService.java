package com.lacus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lacus.common.config.CommonResult;
import com.lacus.service.dto.ApiInfoDTO;
import com.lacus.dao.entity.ApiInfoEntity;
import com.lacus.service.dto.DataResult;
import com.lacus.service.dto.TestResult;

import java.util.Map;

public interface IApiService extends IService<ApiInfoEntity> {

    CommonResult<DataResult> query(String apiUrl, Map<String, Object> params, String method);

    TestResult testApi(ApiInfoDTO apiInfo);

    Boolean flushCache(Long id, Integer status);

}
