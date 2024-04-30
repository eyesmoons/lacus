package com.lacus.writer;

import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Maps;
import com.lacus.common.utils.DorisUtil;
import com.lacus.function.BinlogFilterFunction;
import com.lacus.function.BinlogMapFunction;
import com.lacus.function.BinlogProcessWindowFunction;
import com.lacus.model.*;
import com.lacus.sink.DorisSink;
import com.lacus.sink.DorisStreamLoad;
import com.lacus.trigger.CountAndSizeTrigger;
import com.lacus.utils.KafkaUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lacus.common.constant.Constants.*;
import static com.lacus.common.constant.ProcessorConstants.DORIS_WRITER;

/**
 * @author shengyu
 * @date 2024/4/30 14:44
 */
public class DorisWriter extends BaseWriter {

    public DorisWriter() {
        super(DORIS_WRITER);
    }

    @Override
    public void write(StreamExecutionEnvironment env, JobConf jobConf) {
        JobInfo jobInfo = jobConf.getJobInfo();
        Sink sink = jobConf.getSink();
        FlinkConf flinkConf = jobConf.getFlinkConf();
        SourceV2 source = jobConf.getSource();
        List<String> topics = source.getTopics();
        String bootStrapServers = source.getBootStrapServers();
        Long jobId = jobInfo.getJobId();
        // read from kafka
        DataStreamSource<ConsumerRecord<String, String>> kafkaSourceDs = env.fromSource(KafkaUtil.getKafkaSource(bootStrapServers, topics, source.getGroupId()), WatermarkStrategy.noWatermarks(), "kafka_source");
        DynamicETL dynamicETL = sink.getDynamicETL();
        // kafka消息过滤
        SingleOutputStreamOperator<ConsumerRecord<String, String>> filterDs = kafkaSourceDs.filter(new BinlogFilterFunction()).name("kafka_filter");
        // kafka消息增加删除标识和落库时间
        SingleOutputStreamOperator<Map<String, String>> mapDs = filterDs.map(new BinlogMapFunction()).name("kafka_map");
        // 构建doris stream load配置
        Map<String, DorisStreamLoad> dorisStreamLoadMap = buildDorisStreamConfig(sink);
        // 应用开窗函数，主要起限流作用
        SingleOutputStreamOperator<Map<String, String>> triggerDs = mapDs.windowAll(TumblingProcessingTimeWindows.of(Time.seconds(flinkConf.getMaxBatchInterval())))
                .trigger(new CountAndSizeTrigger<>(flinkConf.getMaxBatchRows(), flinkConf.getMaxBatchSize()))
                .apply(new BinlogProcessWindowFunction()).name("kafka_trigger");
        // 将kafka中的数据写入doris
        triggerDs.addSink(new DorisSink(dorisStreamLoadMap)).name("doris_sink");
    }

    /**
     * 构建doris stream load参数
     *
     * @param sink sink
     */
    private static Map<String, DorisStreamLoad> buildDorisStreamConfig(Sink sink) {
        Map<String, DorisStreamLoad> dorisStreamLoadMap = Maps.newHashMap();
        SinkDataSource sinkDataSource = sink.getSinkDataSource();
        String hostPort = sinkDataSource.getIp() + ":" + sinkDataSource.getPort();
        sinkDataSource.setHostPort(hostPort);
        Map<String, Integer> beConfig = DorisUtil.getBeConfig(sinkDataSource);

        Map<String, StreamLoadProperty> streamLoadPropertyMap = sink.getStreamLoadPropertyMap();
        for (Map.Entry<String, StreamLoadProperty> entry : streamLoadPropertyMap.entrySet()) {
            StreamLoadProperty streamLoadProperty = entry.getValue();
            Map<String, String> conf = new HashMap<>();

            // 填充 columns
            conf.put(STREAM_LOAD_COLUMNS, convertColumns(streamLoadProperty.getColumns()));

            // 填充json Path
            conf.put(STREAM_LOAD_JSONPATH, convertJsonPaths(streamLoadProperty.getJsonpaths()));
            conf.put(STREAM_LOAD_FORMAT, streamLoadProperty.getFormat());
            conf.put(STREAM_LOAD_MAX_FILTER_RATIO, streamLoadProperty.getMaxFilterRatio());
            conf.put(STREAM_LOAD_STRIP_OUTER_ARRAY, streamLoadProperty.getStripOuterArray());

            DorisStreamLoad dorisStreamLoad = new DorisStreamLoad(
                    hostPort,
                    sinkDataSource.getDbName(),
                    streamLoadProperty.getSinkTable(),
                    sinkDataSource.getUserName(),
                    sinkDataSource.getPassword(),
                    conf,
                    beConfig);
            dorisStreamLoadMap.put(entry.getKey(), dorisStreamLoad);
        }
        return dorisStreamLoadMap;
    }

    /**
     * 转换json path，添加 删除标识 和 落库时间
     */
    private static String convertJsonPaths(String jsonpaths) {
        JSONArray jsonArr = JSONArray.parseArray(jsonpaths);
        if (!jsonArr.contains("$." + DELETE_KEY)) {
            jsonArr.add("$." + DELETE_KEY);
        }
        if (!jsonArr.contains("$." + UPDATE_STAMP_KEY)) {
            jsonArr.add("$." + UPDATE_STAMP_KEY);
        }
        return jsonArr.toJSONString();
    }

    private static String convertColumns(String columns) {
        List<String> columnList = Arrays.asList(columns.split(","));
        columnList = columnList.stream().map(column -> {
            String replaceColumn = column.trim().replace("`", "");
            return "`" + replaceColumn + "`";
        }).collect(Collectors.toList());
        if (!columnList.contains("`" + DELETE_KEY + "`")) {
            columns = columns + "," + "`" + DELETE_KEY + "`";
        }
        if (!columnList.contains("`" + UPDATE_STAMP_KEY + "`")) {
            columns = columns + "," + "`" + UPDATE_STAMP_KEY + "`";
        }
        return columns;
    }

}