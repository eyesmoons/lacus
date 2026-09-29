package com.lacus.sink.mysql;

import com.google.auto.service.AutoService;
import com.lacus.sink.BaseSink;
import com.lacus.sink.jdbc.JdbcSink;
import lombok.extern.slf4j.Slf4j;

import static com.lacus.constant.ConnectorContext.MYSQL_SINK;

/**
 * @author shengyu
 * @date 2025/2/23 19:49
 */
@Slf4j
@AutoService(BaseSink.class)
public class MysqlSink extends JdbcSink {

    public MysqlSink() {
        super(MYSQL_SINK, "mysql");
    }
}
