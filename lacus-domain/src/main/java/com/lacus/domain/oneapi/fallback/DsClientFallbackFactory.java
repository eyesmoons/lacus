package com.lacus.domain.oneapi.fallback;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.ApiTestResp;
import com.lacus.domain.oneapi.feign.OneApiFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DsClientFallbackFactory implements FallbackFactory<OneApiFeignClient> {

    @Override
    public OneApiFeignClient create(Throwable cause) {
        return new OneApiFeignClient() {
            @Override
            public ResponseDTO<ApiTestResp> testApi(ApiInfoDTO apiDTO) {
                return ResponseDTO.fail(ErrorCode.FAIL, "test run core call error ,fallback! ");
            }

            @Override
            public ResponseDTO<Boolean> flushCache(Long id, Integer status) {
                return ResponseDTO.fail(ErrorCode.FAIL, "flush redis core call error ,fallback! ");
            }
        };


    }

}
