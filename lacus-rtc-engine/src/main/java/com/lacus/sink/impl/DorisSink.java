package com.lacus.sink.impl;

import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Maps;
import com.lacus.function.DorisSinkFunction;
import com.lacus.function.DorisStreamLoad;
import com.lacus.model.JobConf;
import com.lacus.model.SinkConfig;
import com.lacus.model.SinkDataSource;
import com.lacus.model.StreamLoadProperty;
import com.lacus.utils.DorisUtil;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lacus.constant.CommonContext.DELETE_KEY;
import static com.lacus.constant.CommonContext.STREAM_LOAD_COLUMNS;
import static com.lacus.constant.CommonContext.STREAM_LOAD_FORMAT;
import static com.lacus.constant.CommonContext.STREAM_LOAD_JSONPATH;
import static com.lacus.constant.CommonContext.STREAM_LOAD_MAX_FILTER_RATIO;
import static com.lacus.constant.CommonContext.STREAM_LOAD_STRIP_OUTER_ARRAY;
import static com.lacus.constant.CommonContext.UPDATE_STAMP_KEY;
import static com.lacus.constant.ConnectorContext.DORIS_WRITER;

/**
 * @author shengyu
 * @date 2024/4/30 14:44
 */
public class DorisSink extends BaseSink {

    public DorisSink() {
        super(DORIS_WRITER);
    }

    @Override
    public RichSinkFunction<Map<String, String>> getSink(JobConf jobConf) {
        return new DorisSinkFunction(buildDorisStreamConfig(jobConf.getSink()));
    }

    /**
     * 构建doris stream load参数
     *
     * @param sinkConfig sinkConfig
     */
    private static Map<String, DorisStreamLoad> buildDorisStreamConfig(SinkConfig sinkConfig) {
        Map<String, DorisStreamLoad> dorisStreamLoadMap = Maps.newHashMap();
        SinkDataSource sinkDataSource = sinkConfig.getSinkDataSource();
        String hostPort = sinkDataSource.getIp() + ":" + sinkDataSource.getPort();
        sinkDataSource.setHostPort(hostPort);
        Map<String, Integer> beConfig = DorisUtil.getBeConfig(sinkDataSource);

        Map<String, StreamLoadProperty> streamLoadPropertyMap = sinkConfig.getStreamLoadPropertyMap();
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
