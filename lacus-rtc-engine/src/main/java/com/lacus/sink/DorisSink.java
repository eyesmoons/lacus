package com.lacus.sink;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.lacus.common.exception.StreamLoadException;
import com.lacus.common.utils.DorisUtil;
import com.lacus.handler.FailExecutionHandler;
import com.lacus.model.RespContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Map;

import static com.lacus.common.constant.Constants.SUCCESS_CODE;
import static com.lacus.common.enums.StatusCodeEnum.STREAM_LOAD_ERROR;

@Slf4j
public class DorisSink extends RichSinkFunction<Map<String, String>> {
    private static final long serialVersionUID = -3872752300519439640L;

    private final Map<String, DorisStreamLoad> dorisStreamLoadMap;
    private final FailExecutionHandler failExecutionHandler;
    private final static Integer RETRY_COUNT = 5;
    private final static Long RETRY_SLEEP_MILLS = 5000L;

    public DorisSink(Map<String, DorisStreamLoad> dorisStreamLoadMap, FailExecutionHandler failExecutionHandler) {
        this.dorisStreamLoadMap = dorisStreamLoadMap;
        this.failExecutionHandler = failExecutionHandler;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void invoke(Map<String, String> value, Context context) throws Exception {
        for (Map.Entry<String, String> entry : value.entrySet()) {
            DorisStreamLoad dorisStreamLoad = dorisStreamLoadMap.get(entry.getKey());
            if (dorisStreamLoad == null) {
                log.warn("未取到dorisStreamLoad配置:{}", entry.getKey());
                continue;
            }
            flush(dorisStreamLoad, entry);
        }
    }

    /**
     * flush data to doris
     */
    private void flush(DorisStreamLoad dorisStreamLoad, Map.Entry<String, String> entry) {
        String data = entry.getValue();
        for (int i = 0; i <= RETRY_COUNT; i++) {
            try {
                RespContent respContent = processData(dorisStreamLoad, entry.getValue());
                log.info("返回数据:{}", JSON.toJSONString(respContent));
                return;
            } catch (StreamLoadException e1) {
                if (i >= RETRY_COUNT) {
                    if (failExecutionHandler != null) {
                        log.warn("重试失败,发送至错误消息队列");
                        String stackTrace = ExceptionUtils.getStackTrace(e1);
                        RespContent resp = e1.getRespContent();
                        try {
                            failExecutionHandler.failExecution(entry.getKey(), data, dorisStreamLoad.getDb(), dorisStreamLoad.getTbl(), resp, stackTrace, STREAM_LOAD_ERROR.getCode());
                        } catch (Exception e2) {
                            log.error("kafka消息发送失败：{}", e1.getMessage());
                        }
                        long totalRows = resp.getNumberTotalRows();
                        if (totalRows == 0) {
                            JSONArray dataArr = JSON.parseArray(data);
                            resp.setNumberTotalRows(dataArr.size());
                        }
                        return;
                    } else {
                        log.warn("未配置失败策略，丢弃数据:{}", data);
                    }
                }
                try {
                    Thread.sleep(RETRY_SLEEP_MILLS);
                } catch (InterruptedException e) {
                    log.error("sleep error,", e);
                }
            }
        }
    }

    private RespContent processData(DorisStreamLoad dorisStreamLoad, String value) {
        DorisStreamLoad.LoadResponse loadResponse = dorisStreamLoad.loadBatchV2(value);
        if (loadResponse != null && loadResponse.status == SUCCESS_CODE) {
            RespContent respContent = JSON.parseObject(loadResponse.respContent, RespContent.class);
            if (!DorisUtil.checkStreamLoadStatus(respContent)) {
                log.error("Stream Load 失败:{}", loadResponse);
                throw new StreamLoadException("Stream Load 失败:" + JSON.toJSONString(loadResponse), respContent, value);
            } else {
                return respContent;
            }
        } else {
            log.error("Stream Load 请求URL失败:{}", loadResponse);
            throw new StreamLoadException("Stream Load 请求URL失败:" + JSON.toJSONString(loadResponse), value);
        }
    }
}
