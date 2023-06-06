package com.lacus.job.flink;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class TaskTrigger<T> extends Trigger<T, TimeWindow> {


    private static final Logger log = LoggerFactory.getLogger(TaskTrigger.class);


    //批量导入最大字节限制
    private Integer maxBatchSize;

    //批量导入最大条数限制
    private Integer maxBatchCount;


    private TaskTrigger(Integer maxBatchSize, Integer maxBatchCount) {
        this.maxBatchSize = maxBatchSize;
        this.maxBatchCount = maxBatchCount;
    }

    //计数器
    private final ReducingStateDescriptor<Long> counter =
            new ReducingStateDescriptor<>("batch-counter", new Counter(), LongSerializer.INSTANCE);

    //容量器
    private final ReducingStateDescriptor<Integer> capacity =
            new ReducingStateDescriptor<>("batch-size", new Capacity(), IntSerializer.INSTANCE);


    @Override
    public TriggerResult onElement(T ele, long l, TimeWindow window, TriggerContext context) throws Exception {
        if (window.maxTimestamp() <= context.getCurrentWatermark()) {
            return TriggerResult.FIRE;
        }
        //判断counter定时器是否注册
        ReducingState<Long> counterState = context.getPartitionedState(counter);
        counterState.add(1L);
        if (counterState.get() >= maxBatchCount) {
            log.info("fired counter trigger：{}", counterState.get());
            clear(window, context);
            return TriggerResult.FIRE_AND_PURGE;
        }

        ReducingState<Integer> capacityState = context.getPartitionedState(capacity);
        int bytes = String.valueOf(ele).getBytes(StandardCharsets.UTF_8).length;
        capacityState.add(bytes);
        if (capacityState.get() >= maxBatchSize) {
            log.info("fired capacity trigger：{}", capacityState.get());
            clear(window, context);
            return TriggerResult.FIRE_AND_PURGE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext context) throws Exception {
        clear(window, context);
        log.info("fired time is :{}", time);
        return TriggerResult.FIRE_AND_PURGE;
    }

    @Override
    public TriggerResult onEventTime(long l, TimeWindow timeWindow, TriggerContext triggerContext) throws Exception {
        return null;
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


    public static Trigger build(Integer maxBatchSize, Integer maxBatchCount) {
        return new TaskTrigger<>(maxBatchSize, maxBatchCount);

    }
}
