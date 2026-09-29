package com.lacus.api;

import com.lacus.common.annotation.LogAnnotation;
import com.lacus.common.config.CommonResult;
import com.lacus.common.parser.ApiThreadLocal;
import com.lacus.core.buried.BuriedFunc;
import com.lacus.service.dto.DataResult;
import com.lacus.service.dto.TestResult;
import com.lacus.service.IApiService;
import com.lacus.service.dto.ApiInfoDTO;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/one/api")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OneApiController {

    private final IApiService apiService;

    @RequestMapping("/query")
    @ApiOperation("统一查询接口")
    @LogAnnotation(title = "统一查询服务", action = "统一查询接口")
    public CommonResult<DataResult> query() {
        ApiThreadLocal api = ApiThreadLocal.get();
        BuriedFunc.setApi("query: " + api.getApiUrl());
        return apiService.query(api.getApiUrl(), api.getParams(), api.getMethod());
    }

    @PostMapping("/test")
    @ApiOperation("统一查询服务测试运行接口")
    @LogAnnotation(title = "统一查询服务", action = "统一查询服务测试运行")
    public CommonResult<TestResult> testRun(@RequestBody ApiInfoDTO apiInfo) {
        BuriedFunc.setApi("testRun: " + apiInfo.getApiUrl());
        TestResult response = apiService.testApi(apiInfo);
        return CommonResult.success(response);
    }

    @PostMapping("/flush/cache")
    @ApiOperation("统一查询服务刷新缓存接口")
    @LogAnnotation(title = "统一查询服务", action = "统一查询服务刷新缓存接口")
    public CommonResult<Boolean> flushRedis(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        return CommonResult.success(apiService.flushCache(id, status));
    }

}
