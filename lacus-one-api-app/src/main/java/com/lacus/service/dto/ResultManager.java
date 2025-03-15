package com.lacus.service.dto;

import com.lacus.common.exception.ResultCode;
import com.lacus.common.exception.ApiException;
import com.lacus.service.vo.DataPageVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

@Slf4j
public class ResultManager {

    public static DataResult push(List<Map<String, Object>> result, Future<Long> asynResult, DataPageVO dataPage) {
        if (Objects.equals(dataPage.getPageFlag(), 1)) {
            try {
                Long total = asynResult.get();
                return new PageDataResult(total, dataPage.getPageNum(), dataPage.getPageSize(), result);
            } catch (Exception e) {
                log.info("异步获取数据结果失败:", e);
                throw new ApiException(ResultCode.SQL_RUNTIME_ERROR, e.getMessage());
            }
        }
        return new DataResult(result);
    }
}
