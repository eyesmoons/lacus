package com.lacus.sink.postgresql;

import com.google.auto.service.AutoService;
import com.lacus.sink.BaseSink;
import com.lacus.sink.jdbc.JdbcSink;
import lombok.extern.slf4j.Slf4j;

import static com.lacus.constant.ConnectorContext.POSTGRES_SINK;

/**
 * @author shengyu
 * @date 2025/2/23 19:49
 */
@Slf4j
@AutoService(BaseSink.class)
public class PostgresqlSink extends JdbcSink {

    public PostgresqlSink() {
        super(POSTGRES_SINK, "postgresql");
    }
}
