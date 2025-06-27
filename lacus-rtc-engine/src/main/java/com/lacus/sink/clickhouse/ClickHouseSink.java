package com.lacus.sink.clickhouse;

import com.google.auto.service.AutoService;
import com.lacus.sink.BaseSink;
import com.lacus.sink.jdbc.JdbcSink;
import lombok.extern.slf4j.Slf4j;

import static com.lacus.constant.ConnectorContext.CLICKHOUSE_SINK;

@Slf4j
@AutoService(BaseSink.class)
public class ClickHouseSink extends JdbcSink {

    public ClickHouseSink(String sinkName) {
        super(CLICKHOUSE_SINK, "clickhouse");
    }
}
