package com.lacus.domain.oneapi.feign;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.RunningTestResponse;
import com.lacus.domain.oneapi.fallback.DsClientFallbackFactory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "dsClient",
        url = "${lacus.ds.api-address}",
        contextId = "dsClient",
        path = "/ds/api",
        fallbackFactory = DsClientFallbackFactory.class)
public interface DsFeignClient {

    @PostMapping("/testRun")
    @ApiOperation("数据服务测试运行统一入口")
    ResponseDTO<RunningTestResponse> testRun(@RequestBody ApiInfoDTO apiDTO);

    @PostMapping("flushRedis")
    @ApiOperation("数据服务刷新缓存接口")
    ResponseDTO<Boolean> flushRedis(@RequestParam("id") Long id, @RequestParam("status") Integer status);

}
