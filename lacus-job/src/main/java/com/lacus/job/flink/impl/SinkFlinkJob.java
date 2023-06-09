package com.lacus.job.flink.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
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
import org.apache.flink.connector.kafka.source.KafkaSource;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * flink sink端任务
 */
public class SinkFlinkJob extends BaseFlinkJob {

    private static final Logger log = LoggerFactory.getLogger(SinkFlinkJob.class);

    public SinkFlinkJob(String[] args) {
        super(args);
    }


    private KafkaSource<ConsumerRecord<String, String>> build(JSONObject kafkaConfig) {
        String bootstrapServers = kafkaConfig.getString(Constant.SINK_SOURCE_BOOTSTRAP_SERVERS);
        String groupId = kafkaConfig.getString(Constant.SINK_SOURCE_GROUP_ID);
        List<String> topics = kafkaConfig.getJSONArray(Constant.SINK_SOURCE_TOPICS).stream().map(Object::toString).collect(Collectors.toList());
        return super.buildKafkaSource(bootstrapServers, groupId, topics, new Properties());
    }


    @Override
    public void handle() throws Throwable {
        JSONArray sinkConfList = JSONArray.parseArray(super.jobConf);
        for (Object sinkObj : sinkConfList) {
            JSONObject sinkConsumer = JSONObject.parseObject(sinkObj.toString());
            JSONObject sinkKafkaConf = sinkConsumer.getJSONObject(Constant.SINK_SOURCE);
            JSONObject sinkFlinkConf = sinkConsumer.getJSONObject(Constant.SINK_FLINK);
            JSONObject sinkEngineConf = sinkConsumer.getJSONObject(Constant.SINK_ENGINE);

            //build kafka source
            KafkaSource<ConsumerRecord<String, String>> sinkKafkaSource = build(sinkKafkaConf);


            //consumer kafka data
            DataStreamSource<ConsumerRecord<String, String>> dataStreamSource = env.fromSource(sinkKafkaSource, WatermarkStrategy.noWatermarks(), "lacus-flink-kafka-source");
            SingleOutputStreamOperator<Tuple2<String, String>> dataTuple2 = dataStreamSource
                    .filter((FilterFunction<ConsumerRecord<String, String>>) record -> StringUtils.checkValNotNull(record.value()))
                    .map(new FormatFunction()).returns(Types.TUPLE(Types.STRING, Types.STRING));

            @SuppressWarnings("unchecked")
            SingleOutputStreamOperator<Map<String, String>> result = dataTuple2.keyBy(kv -> kv.f0).window(TumblingEventTimeWindows.of(Time.seconds(sinkFlinkConf.getInteger(Constant.SINK_FLINK_MAX_BATCH_INTERVAL)))).trigger(TaskTrigger.build(sinkFlinkConf.getInteger(Constant.SINK_FLINK_MAX_BATCH_SIZE), sinkFlinkConf.getInteger(Constant.SINK_FLINK_MAX_BATCH_ROWS))).apply(new WindowFunction<Tuple2<String, String>, Map<String, String>, String, TimeWindow>() {
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
                    log.info("本次处理数据量:{}", count);
                    System.out.println(tmpMap);
                    Map<String, String> dataMap = Maps.newHashMap();
                    tmpMap.forEach((key, value) -> dataMap.put(key, value.toString()));
                    collector.collect(dataMap);
                }
            });


            //sink data
            String sinkType = sinkEngineConf.getString(Constant.SINK_ENGINE_TYPE);
            String engine = sinkEngineConf.getString(Constant.SINK_ENGINE_CONF);

            SinkEnums sinkEnum = SinkEnums.getSinkEnums(sinkType);
            if (sinkEnum == null) {
                log.debug("Flink sink executor type not found");
                return;
            }

            log.info("Initialize flink sink executor type : {} ", sinkEnum);
            switch (sinkEnum) {
                case DORIS:
                    result.addSink(new DorisExecutorSink(engine));
                    break;
                case PRESTO:
                case CLICKHOUSE:
                    break;
            }
        }
        env.execute(super.jobName);

    }


    private static class FormatFunction implements MapFunction<ConsumerRecord<String, String>, Tuple2<String, String>> {
        @Override
        public Tuple2<String, String> map(ConsumerRecord<String, String> record) throws Exception {
            String originData = record.value();
            JSONObject source = JSONObject.parseObject(originData);
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
                case CREATE_OP:
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

    public static void main(String[] args) {
        new SinkFlinkJob(args).run();
    }
}
