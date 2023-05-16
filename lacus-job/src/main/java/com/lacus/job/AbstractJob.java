package com.lacus.job;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
public abstract class AbstractJob implements IJob{

    private static final long serialVersionUID = -8435891490675922619L;

    private static transient JSONObject param_json = new JSONObject();

    public AbstractJob(String[] args) {
        if (ObjectUtils.isNotEmpty(args)) {
            param_json = JSONObject.parseObject(args[0]);
        }
    }

    @Override
    public void init() {
        log.info("接收到参数:" + param_json);
    }

    @Override
    public void close() {log.info("job finished");}

    @Override
    public void run() {
        try {
            init();
            afterInit();
            handle();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getParamValue(String name, T defaultValue) {
        return paramContainKey(name) ? (T) param_json.get(name) : defaultValue;
    }

    private boolean paramContainKey(String name) {
        return param_json.containsKey(name);
    }
}