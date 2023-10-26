package com.lacus.job.flink.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lacus.job.constants.Constant;
import com.lacus.job.enums.OperatorEnums;
import com.lacus.job.enums.SinkEnums;
import com.lacus.job.flink.BaseFlinkJob;
import com.lacus.job.flink.TaskTrigger;
import com.lacus.job.flink.deserialization.CustomerDeserializationSchemaMysql;
import com.lacus.job.flink.warehouse.DorisExecutorSink;
import com.lacus.job.model.*;
import com.lacus.job.utils.DateUtils;
import com.lacus.job.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.List;
import java.util.Map;

/**
 * 数据同步任务
 */
@Slf4j
public class DataSyncJob extends BaseFlinkJob {
    private static final long serialVersionUID = -5029825537386780174L;

    public DataSyncJob(String[] args) {
        super(args);
    }

    @Override
    public void handle() throws Throwable {
        log.info("jobName：{}", jobName);
        List<DataSyncJobConf> dataSyncJobConfList = JSON.parseArray(jobConf, DataSyncJobConf.class);
        log.info("dataSyncJobConfList：{}", JSON.toJSONString(dataSyncJobConfList));
        for (DataSyncJobConf dataSyncJobConf : dataSyncJobConfList) {
            FlinkJobSource source = dataSyncJobConf.getSource();
            FlinkConf flinkConf = dataSyncJobConf.getFlinkConf();
            FlinkTaskSink sink = dataSyncJobConf.getSink();
            FlinkTaskEngine engine = sink.getEngine();
            String sinkType = sink.getSinkType();
            StartupOptions startupOptions = getStartupOptions(source.getSyncType(), source.getTimeStamp());
            DebeziumSourceFunction<String> mysqlSource = MySQLSource.<String>builder()
                    .hostname(source.getHostname())
                    .port(source.getPort())
                    .username(source.getUsername())
                    .password(source.getPassword())
                    .databaseList(source.getDatabaseList().toArray(new String[0]))
                    // 可选配置项,如果不指定该参数,则会读取上一个配置下的所有表的数据，注意：指定的时候需要使用"db.table"的方式
                    .tableList(source.getTableList().toArray(new String[0]))
                    // 指定数据读取位置：initial，latest-offset，timestamp，specific-offset
                    .startupOptions(startupOptions)
                    .deserializer(new CustomerDeserializationSchemaMysql())
                    .build();
            SingleOutputStreamOperator<String> mysqlDS = env.addSource(mysqlSource).name(flinkConf.getJobName());

            SingleOutputStreamOperator<Tuple2<String, String>> dataTuple2 = mysqlDS
                    .filter((FilterFunction<String>) StringUtils::checkValNotNull)
                    .map(new FormatFunction()).returns(Types.TUPLE(Types.STRING, Types.STRING));

            SingleOutputStreamOperator<Map<String, String>> result = dataTuple2.keyBy(kv -> kv.f0).window(TumblingProcessingTimeWindows.of(Time.seconds(flinkConf.getMaxBatchInterval()))).trigger(new TaskTrigger<>(flinkConf.getMaxBatchSize(), flinkConf.getMaxBatchRows())).apply((WindowFunction<Tuple2<String, String>, Map<String, String>, String, TimeWindow>) (k, window, iterable, collector) -> {
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
            });

            //sink data
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
        env.execute(jobName);
    }

    private static StartupOptions getStartupOptions(String syncType, Long timeStamp) {
        StartupOptions startupOptions = null;
        switch (syncType) {
            case "initial":
                startupOptions = StartupOptions.initial();
                break;
            case "earliest":
                startupOptions = StartupOptions.earliest();
                break;
            case "latest":
                startupOptions = StartupOptions.latest();
                break;
            case "timestamp":
                startupOptions = StartupOptions.timestamp(timeStamp);
                break;
        }
        return startupOptions;
    }

    private static class FormatFunction implements MapFunction<String, Tuple2<String, String>> {
        private static final long serialVersionUID = 5873400146844630909L;

        @Override
        public Tuple2<String, String> map(String value) {
            JSONObject source = JSONObject.parseObject(value);
            String db = source.getString(Constant.DB);
            String table = source.getString(Constant.TABLE);
            String key = String.join(".", db, table);
            return Tuple2.of(key, loadFormat(value));
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
        new DataSyncJob(args).run();
    }
}
