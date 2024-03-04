package com.lacus.function;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BinlogProcessWindowFunction implements AllWindowFunction<Map<String, String>, Map<String, String>, TimeWindow> {
    private static final long serialVersionUID = -8894923258129405159L;

    @Override
    public void apply(TimeWindow timeWindow, Iterable<Map<String, String>> iterable, Collector<Map<String, String>> collector) {
        Map<String, JSONArray> resultMap = new HashMap<>();
        long cnt = 0L;
        for (Map<String, String> value : iterable) {
            for (Map.Entry<String, String> entry : value.entrySet()) {
                String key = entry.getKey();
                String data = entry.getValue();
                JSONArray jsonArray = JSON.parseArray(data);
                if (resultMap.containsKey(key)) {
                    JSONArray binlog = resultMap.get(key);
                    binlog.addAll(jsonArray);
                } else {
                    resultMap.put(key, jsonArray);
                }
                cnt = cnt + jsonArray.size();
            }
        }
        Map<String, String> result = new HashMap<>();
        if (cnt > 0) {
            log.info("本次处理数据量：{}", cnt);
            resultMap.forEach((key, value) -> result.put(key, JSON.toJSONString(value, JSONWriter.Feature.LargeObject)));
        }
        collector.collect(result);
    }
}
