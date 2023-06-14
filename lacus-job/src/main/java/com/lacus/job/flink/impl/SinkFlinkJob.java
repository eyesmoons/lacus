package com.lacus.job.flink.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lacus.job.constants.Constant;
import com.lacus.job.enums.OperatorEnums;
import com.lacus.job.enums.SinkEnums;
import com.lacus.job.flink.BaseFlinkJob;
import com.lacus.job.flink.TaskTrigger;
import com.lacus.job.flink.warehouse.DorisExecutorSink;
import com.lacus.job.utils.DateUtils;
import com.lacus.job.utils.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
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
                .filter((FilterFunction<ConsumerRecord<String, String>>) record -> StringUtils.checkValNotNull(record.value()))
                .map(new FormatFunction()).returns(Types.TUPLE(Types.STRING, Types.STRING));


        @SuppressWarnings("unchecked")
        SingleOutputStreamOperator<Map<String, String>> result = dataTuple2.keyBy(kv -> kv.f0).window(TumblingEventTimeWindows.of(Time.seconds(maxBatchInterval))).trigger(TaskTrigger.build(maxBatchSize, maxBatchCount)).apply(new WindowFunction<Tuple2<String, String>, Map<String, String>, String, TimeWindow>() {
            @Override
            public void apply(String k, TimeWindow window, Iterable<Tuple2<String, String>> iterable, Collector<Map<String, String>> collector) throws Exception {
                Map<String, List<String>> tmpMap = Maps.newHashMap();
                long count = 0L;
                for (Tuple2<String, String> tuple2 : iterable) {
                    String key = tuple2.f0;
                    List<String> dataList = tmpMap.containsKey(key) ? tmpMap.get(key) : Lists.newArrayList();
                    dataList.add(tuple2.f1);
                    tmpMap.put(key, dataList);
                    count++;
                }
                log.info("单次处理数据量:{}", count);
                System.out.println(tmpMap);
                Map<String, String> dataMap = Maps.newHashMap();
                tmpMap.forEach((key, value) -> dataMap.put(key, JSON.toJSONString(value)));
                collector.collect(dataMap);
            }
        });

        SinkEnums sinkEnum = SinkEnums.getSinkEnums(sinkType);
        if (sinkEnum == null) {
            log.debug("flink sink executor type not found");
            return;
        }

        log.info("initialize flink sink executor type : {} ", sinkEnum);
        switch (sinkEnum) {
            case DORIS:
                DorisExecutorSink dorisSink = new DorisExecutorSink(engine);
                //result.sinkTo(dorisSink.sink());
                break;
            case PRESTO:
            case CLICKHOUSE:
                break;
        }

        env.execute(super.jobName);

    }


    private static class FormatFunction implements MapFunction<ConsumerRecord<String, String>, Tuple2<String, String>> {
        @Override
        public Tuple2<String, String> map(ConsumerRecord<String, String> record) throws Exception {
            String originData = record.value();
            JSONObject jObj = JSONObject.parseObject(originData);
            JSONObject source = jObj.getJSONObject(Constant.SOURCE);
            String db = source.getString(Constant.DB);
            String table = source.getString(Constant.TABLE);
            String key = String.join(".", db, table);
            String value = loadFormat(originData);
            return Tuple2.of(key, value);
        }

        private String loadFormat(String originData) {
            if (StringUtils.checkValNull(originData)) {
                return null;
            }
            JSONObject jData = JSONObject.parseObject(originData);
            String op = jData.getString(Constant.OP);
            OperatorEnums opEnums = OperatorEnums.getOpEnums(op);
            if (StringUtils.checkValNull(opEnums)) {
                return null;
            }
            JSONObject formatData = null;
            switch (opEnums) {
                case INSERT_OP:
                case UPDATE_OP:
                case ROW_OP:
                    formatData = jData.getJSONObject(Constant.AFTER);
                    formatData.put(Constant.IS_DELETE_FILED, Constant.DELETE_FALSE);
                    break;
                case DELETE_OP:
                    formatData = jData.getJSONObject(Constant.BEFORE);
                    formatData.put(Constant.IS_DELETE_FILED, Constant.DELETE_TRUE);
                    break;
                default:
                    break;
            }
            if (StringUtils.checkValNotNull(formatData)) {
                formatData.put(Constant.UPDATE_STAMP_FILED, DateUtils.getCurrentTime());
            }
            return JSON.toJSONString(formatData);
        }


    }

}
