package com.lacus.sink.jdbc;

import com.google.common.collect.Maps;
import com.lacus.model.JobConf;
import com.lacus.model.SinkConfig;
import com.lacus.model.SinkDataSource;
import com.lacus.model.StreamLoadProperty;
import com.lacus.sink.BaseSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lacus.constant.CommonContext.DELETE_KEY;
import static com.lacus.constant.CommonContext.UPDATE_STAMP_KEY;

/**
 * @author shengyu
 * @date 2025/2/23 19:49
 */
@Slf4j
public abstract class JdbcSink extends BaseSink {

    protected String protocol;

    public JdbcSink(String sinkName, String protocol) {
        super(sinkName);
        this.protocol = protocol;
    }

    @Override
    public RichSinkFunction<Map<String, String>> getSink(JobConf jobConf) {
        return new JdbcSinkFunction(buildJdbcOption(jobConf.getSink()));
    }

    private Map<String, JdbcSinkOption> buildJdbcOption(SinkConfig sinkConfig) {
        Map<String, JdbcSinkOption> jdbcSinkOptionMap = Maps.newHashMap();
        SinkDataSource sinkDataSource = sinkConfig.getSinkDataSource();
        String host = sinkDataSource.getIp();
        Integer port = sinkDataSource.getPort();
        String hostPort = host + ":" + port;
        sinkDataSource.setHostPort(hostPort);

        Map<String, StreamLoadProperty> streamLoadPropertyMap = sinkConfig.getStreamLoadPropertyMap();
        for (Map.Entry<String, StreamLoadProperty> entry : streamLoadPropertyMap.entrySet()) {
            StreamLoadProperty streamLoadProperty = entry.getValue();
            Map<String, String> conf = new HashMap<>();
            JdbcSinkOption jdbcSinkOption = new JdbcSinkOption(
                    protocol,
                    host,
                    port,
                    sinkDataSource.getDbName(),
                    streamLoadProperty.getSinkTable(),
                    sinkDataSource.getUserName(),
                    sinkDataSource.getPassword(),
                    conf,
                    convertColumns(streamLoadProperty.getColumns()));
            jdbcSinkOptionMap.put(entry.getKey(), jdbcSinkOption);
        }
        return jdbcSinkOptionMap;
    }

    private String convertColumns(String columns) {
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
