package com.lacus.domain.oneapi.feign;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.ApiTestResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "oneApiClient",
        url = "${lacus.one-api.address}",
        contextId = "oneApiClient",
        path = "/one/api",
        fallbackFactory = DsClientFallbackFactory.class)
public interface OneApiFeignClient {

    @PostMapping("/test")
    @ApiOperation("数据服务测试运行统一入口")
    ResponseDTO<ApiTestResp> testApi(@RequestBody ApiInfoDTO apiDTO);

    @PostMapping("/flush/cache")
    @ApiOperation("数据服务刷新缓存接口")
    ResponseDTO<Boolean> flushCache(@RequestParam("id") Long id, @RequestParam("status") Integer status);

}
