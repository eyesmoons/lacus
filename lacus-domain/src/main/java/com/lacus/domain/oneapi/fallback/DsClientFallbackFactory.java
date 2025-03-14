package com.lacus.domain.oneapi.fallback;

import com.qtone.data.platform.common.api.CommonResult;
import com.qtone.data.platform.common.api.ResultCode;
import com.qtone.data.platform.modules.dataservice.dto.ApiInfoDTO;
import com.qtone.data.platform.modules.dataservice.feign.DsFeignClient;
import com.qtone.data.platform.modules.dataservice.vo.RunningTestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author: zhangbw
 * @date: 2023/10/10
 **/
@Slf4j
@Component
public class DsClientFallbackFactory implements FallbackFactory<DsFeignClient> {

    @Override
    public DsFeignClient create(Throwable cause) {
        return new DsFeignClient() {
            @Override
            public CommonResult<RunningTestResponse> testRun(ApiInfoDTO apiDTO) {
                return CommonResult.failed(ResultCode.FAILED, "test run api call error ,fallback! ");
            }

            @Override
            public CommonResult<Boolean> flushRedis(Long id, Integer status) {
                return CommonResult.failed(ResultCode.FAILED, "flush redis api call error ,fallback! ");
            }
        };


    }

}
