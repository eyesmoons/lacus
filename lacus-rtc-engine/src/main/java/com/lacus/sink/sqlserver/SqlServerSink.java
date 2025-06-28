package com.lacus.sink.sqlserver;

import com.google.auto.service.AutoService;
import com.lacus.sink.BaseSink;
import com.lacus.sink.jdbc.JdbcSink;
import lombok.extern.slf4j.Slf4j;

import static com.lacus.constant.ConnectorContext.MYSQL_SINK;
import static com.lacus.constant.ConnectorContext.SQLSERVER_SINK;

/**
 * @author shengyu
 * @date 2025/2/23 19:49
 */
@Slf4j
@AutoService(BaseSink.class)
public class SqlServerSink extends JdbcSink {

    public SqlServerSink() {
        super(SQLSERVER_SINK, "sqlserver");
    }
}
