package com.lacus.job.flink.impl;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lacus.job.flink.BaseFlinkJob;
import com.lacus.job.flink.TaskTrigger;
import com.lacus.job.flink.warehouse.DorisExecutorSink;
import com.lacus.job.utils.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * flink sink端任务
 */
public class SinkFlinkJob extends BaseFlinkJob {

    private static final Logger log = LoggerFactory.getLogger(SinkFlinkJob.class);

    public SinkFlinkJob(String[] args) {
        super(args);
    }



    @Override
    public void handle() throws Throwable {

        DataStreamSource<ConsumerRecord<String, String>> dataStreamSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "lacus-flink-kafka-source");

        SingleOutputStreamOperator<Tuple2<String, String>> dataTuple2 = dataStreamSource
                .filter(record -> StringUtils.checkValNotNull(record.value()))
                .map(record -> {
                    String topic = record.topic();
                    String value = record.value();
                    return Tuple2.of(topic, value);
                }).returns(Types.TUPLE(Types.STRING, Types.STRING));

        SingleOutputStreamOperator<Map<String, List<String>>> result = dataTuple2.keyBy(kv -> kv.f0).window(TumblingEventTimeWindows.of(Time.seconds(maxBatchInterval))).trigger(TaskTrigger.build(maxBatchSize, maxBatchCount)).apply(new WindowFunction<Tuple2<String, String>, Map<String, List<String>>, String, TimeWindow>() {
            @Override
            public void apply(String k, TimeWindow window, Iterable<Tuple2<String, String>> iterable, Collector<Map<String, List<String>>> collector) throws Exception {
                Map<String, List<String>> dataMap = Maps.newHashMap();
                long count = 0L;
                for (Tuple2<String, String> tuple2 : iterable) {
                    String key = tuple2.f0;
                    List<String> dataList = dataMap.containsKey(key) ? dataMap.get(key) : Lists.newArrayList();
                    dataList.add(tuple2.f1);
                    dataMap.put(key, dataList);
                    count++;
                }
                log.info("本次处理数据量:{}", count);
                collector.collect(dataMap);
            }
        });


        result.sinkTo(new DorisExecutorSink(engine).sink(null));


    }


}
