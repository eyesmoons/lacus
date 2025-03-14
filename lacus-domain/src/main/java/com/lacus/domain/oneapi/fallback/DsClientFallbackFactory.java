package com.lacus.domain.oneapi.fallback;

import com.lacus.common.core.dto.ResponseDTO;
import com.lacus.common.exception.error.ErrorCode;
import com.lacus.domain.oneapi.dto.ApiInfoDTO;
import com.lacus.domain.oneapi.dto.RunningTestResponse;
import com.lacus.domain.oneapi.feign.DsFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DsClientFallbackFactory implements FallbackFactory<DsFeignClient> {

    @Override
    public DsFeignClient create(Throwable cause) {
        return new DsFeignClient() {
            @Override
            public ResponseDTO<RunningTestResponse> testRun(ApiInfoDTO apiDTO) {
                return ResponseDTO.fail(ErrorCode.FAIL, "test run api call error ,fallback! ");
            }

            @Override
            public ResponseDTO<Boolean> flushRedis(Long id, Integer status) {
                return ResponseDTO.fail(ErrorCode.FAIL, "flush redis api call error ,fallback! ");
            }
        };


    }

}
