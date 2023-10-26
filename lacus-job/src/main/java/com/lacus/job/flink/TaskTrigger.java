package com.lacus.job.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.nio.charset.StandardCharsets;

@Slf4j
public class TaskTrigger<T> extends Trigger<T, TimeWindow> {
    private static final long serialVersionUID = -378259171742648366L;

    // 批量导入最大字节限制
    private final Long maxBatchSize;
    // 批量导入最大条数限制
    private final Integer maxBatchCount;


    public TaskTrigger(Long maxBatchSize, Integer maxBatchCount) {
        this.maxBatchSize = maxBatchSize;
        this.maxBatchCount = maxBatchCount;
    }

    // 计数器
    private final ReducingStateDescriptor<Long> counter = new ReducingStateDescriptor<>("batch-counter", new Counter(), LongSerializer.INSTANCE);

    // 容量器
    private final ReducingStateDescriptor<Integer> capacity = new ReducingStateDescriptor<>("batch-size", new Capacity(), IntSerializer.INSTANCE);


    @Override
    public TriggerResult onElement(T ele, long l, TimeWindow window, TriggerContext context) throws Exception {
        if (window.maxTimestamp() <= context.getCurrentProcessingTime()) {
            return TriggerResult.FIRE_AND_PURGE;
        }
        context.registerProcessingTimeTimer(window.maxTimestamp());
        // 判断counter定时器是否注册
        ReducingState<Long> counterState = context.getPartitionedState(counter);
        counterState.add(1L);
        if (counterState.get() >= maxBatchCount) {
            log.info("Fired counter trigger：{}", counterState.get());
            clear(window, context);
            return TriggerResult.FIRE_AND_PURGE;
        }

        ReducingState<Integer> capacityState = context.getPartitionedState(capacity);
        int bytes = String.valueOf(ele).getBytes(StandardCharsets.UTF_8).length;
        capacityState.add(bytes);
        if (capacityState.get() >= maxBatchSize) {
            log.info("Fired capacity trigger：{}", capacityState.get());
            clear(window, context);
            return TriggerResult.FIRE_AND_PURGE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext context) throws Exception {
        clear(window, context);
        log.info("Fired time is :{}", time);
        return TriggerResult.FIRE_AND_PURGE;
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext context) throws Exception {
        if (time < window.getEnd()) {
            return TriggerResult.CONTINUE;
        } else {
            log.info("Fire with event time: " + time);
            clear(window, context);
            return TriggerResult.FIRE_AND_PURGE;
        }
    }

    @Override
    public void clear(TimeWindow timeWindow, TriggerContext ctx) throws Exception {
        ctx.getPartitionedState(counter).clear();
        ctx.getPartitionedState(capacity).clear();
    }

    private static class Counter implements ReduceFunction<Long> {
        @Override
        public Long reduce(Long l1, Long l2) throws Exception {
            return l1 + l2;
        }
    }

    private static class Capacity implements ReduceFunction<Integer> {
        @Override
        public Integer reduce(Integer cap1, Integer cap2) throws Exception {
            return cap1 + cap2;
        }
    }
}
