package com.lacus.sink.impl;

import com.google.auto.service.AutoService;
import com.lacus.model.JobConf;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.Map;

import static com.lacus.constant.ConnectorContext.MYSQL_SINK;

/**
 * @author shengyu
 * @date 2025/2/23 19:49
 */
@AutoService(BaseSink.class)
public class MysqlSink extends BaseSink{
    public MysqlSink() {
        super(MYSQL_SINK);
    }

    @Override
    public RichSinkFunction<Map<String, String>> getSink(JobConf jobConf) {
        // TODO 演示功能，尚未实现
        return new RichSinkFunction<Map<String, String>>() {
            @Override
            public void invoke(Map<String, String> map, Context context) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + "->" + entry.getValue());
                }
            }
        };
    }
}
