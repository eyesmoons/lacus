package com.lacus.function;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RateLimiter<T> extends Trigger<T, TimeWindow> {
    private static final long serialVersionUID = -7641310865650097112L;
    // 窗口最大数据量
    private final int maxCount;
    // 窗口最大数据大小
    private final int maxSize;
    //用于储存窗口当前数据量的状态对象
    private final ReducingStateDescriptor<Long> countStateDescriptor = new ReducingStateDescriptor<>("counter", new Sum(), LongSerializer.INSTANCE);

    //用于储存窗口当前数据size的状态对象
    private final ReducingStateDescriptor<Integer> sizeStateDescriptor = new ReducingStateDescriptor<>("size", new SumInt(), IntSerializer.INSTANCE);

    public RateLimiter(int maxCount, int maxSize) {
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    private TriggerResult fireAndPurge(TimeWindow window, TriggerContext ctx) throws Exception {
        clear(window, ctx);
        return TriggerResult.FIRE_AND_PURGE;
    }

    @Override
    public TriggerResult onElement(T element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        ReducingState<Long> countState = ctx.getPartitionedState(countStateDescriptor);
        ReducingState<Integer> sizeState = ctx.getPartitionedState(sizeStateDescriptor);
        Map<String, JSONArray> map = mapStringToMap(element.toString());
        if (map != null) {
            for (Map.Entry<String, JSONArray> entry : map.entrySet()) {
                JSONArray value = entry.getValue();
                countState.add((long) value.size());
            }
        } else {
            countState.add(0L);
        }
        int length = String.valueOf(element).getBytes(StandardCharsets.UTF_8).length;
        sizeState.add(length);
        ctx.registerProcessingTimeTimer(window.maxTimestamp());

        if (countState.get() >= maxCount) {
            log.info("fire count {} ", countState.get());
            return fireAndPurge(window, ctx);
        }
        if (sizeState.get() >= maxSize) {
            log.info("fire size {} ", sizeState.get());
            return fireAndPurge(window, ctx);
        } else {
            return TriggerResult.CONTINUE;
        }
    }

    private Map<String, JSONArray> mapStringToMap(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        String string = str.substring(1, str.length() - 1).replaceAll(" ", "");
        Map<String, JSONArray> map = new HashMap<>();
        String[] split = string.split("=");
        if (split.length < 2) {
            return null;
        } else {
            String key = split[0];
            String value = string.substring(string.indexOf("=") + 1);
            map.put(key, JSON.parseArray(value));
        }
        return map;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        log.info("fire time {} ", time);
        return fireAndPurge(window, ctx);
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        if (time >= window.getEnd()) {
            return TriggerResult.CONTINUE;
        } else {
            log.info("fire with event time: " + time);
            return fireAndPurge(window, ctx);
        }
    }

    @Override
    public void clear(TimeWindow window, TriggerContext ctx) {
        ctx.getPartitionedState(countStateDescriptor).clear();
        ctx.getPartitionedState(sizeStateDescriptor).clear();
    }

    static class Sum implements ReduceFunction<Long> {
        private static final long serialVersionUID = 1777535427145998598L;

        @Override
        public Long reduce(Long value1, Long value2) {
            return value1 + value2;
        }
    }

    static class SumInt implements ReduceFunction<Integer> {
        private static final long serialVersionUID = -3327907113012649556L;

        @Override
        public Integer reduce(Integer value1, Integer value2) {
            return value1 + value2;
        }
    }
}
