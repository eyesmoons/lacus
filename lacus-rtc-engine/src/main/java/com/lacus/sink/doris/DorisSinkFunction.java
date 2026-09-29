package com.lacus.sink.doris;

import com.alibaba.fastjson2.JSON;
import com.lacus.exception.StreamLoadException;
import com.lacus.sink.doris.utils.DorisUtil;
import com.lacus.model.StreamLoadResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Map;

import static com.lacus.constant.CommonContext.SUCCESS_CODE;

@Slf4j
public class DorisSinkFunction extends RichSinkFunction<Map<String, String>> {
    private static final long serialVersionUID = -3872752300519439640L;

    private final Map<String, DorisStreamLoad> dorisStreamLoadMap;
    private final static Integer RETRY_COUNT = 5;
    private final static Long RETRY_SLEEP_MILLS = 5000L;

    public DorisSinkFunction(Map<String, DorisStreamLoad> dorisStreamLoadMap) {
        this.dorisStreamLoadMap = dorisStreamLoadMap;
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
                log.warn("doris stream load config is empty：{}", entry.getKey());
                continue;
            }
            writeData(dorisStreamLoad, entry);
        }
    }

    private void writeData(DorisStreamLoad dorisStreamLoad, Map.Entry<String, String> entry) {
        for (int i = 0; i <= RETRY_COUNT; i++) {
            try {
                StreamLoadResponse streamLoadResponse = loadData(dorisStreamLoad, entry.getValue());
                log.info("return data：{}", JSON.toJSONString(streamLoadResponse));
                return;
            } catch (StreamLoadException e1) {
                if (i >= RETRY_COUNT) {
                    log.error("failed to retry, please see the logs.");
                }
                try {
                    Thread.sleep(RETRY_SLEEP_MILLS);
                } catch (InterruptedException e) {
                    log.error("sleep error,", e);
                }
            }
        }
    }

    private StreamLoadResponse loadData(DorisStreamLoad dorisStreamLoad, String value) {
        DorisStreamLoad.LoadResponse loadResponse = dorisStreamLoad.doLoad(value);
        if (loadResponse != null && loadResponse.status == SUCCESS_CODE) {
            StreamLoadResponse streamLoadResponse = JSON.parseObject(loadResponse.respContent, StreamLoadResponse.class);
            if (!DorisUtil.checkStreamLoadStatus(streamLoadResponse)) {
                log.error("failed to stream load：{}", loadResponse);
                throw new StreamLoadException("failed to stream load：" + JSON.toJSONString(loadResponse), streamLoadResponse, value);
            } else {
                return streamLoadResponse;
            }
        } else {
            log.error("failed to request stream Load：{}", loadResponse);
            throw new StreamLoadException("failed to request stream Load：" + JSON.toJSONString(loadResponse), value);
        }
    }
}
