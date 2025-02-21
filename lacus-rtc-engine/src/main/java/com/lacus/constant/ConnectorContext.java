package com.lacus.constant;

import java.io.Serializable;

/**
 * 处理器常量
 * @created by shengyu on 2024/1/21 20:29
 */
public class ConnectorContext implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String MYSQL_SOURCE = "mysql";
    public static final String SQLSERVER_SOURCE = "sqlserver";
    public static final String ORACLE_SOURCE = "oracle";
    public static final String POSTGRES_SOURCE = "postgres";
    public static final String MONGODB_SOURCE = "mongodb";
    public static final String KAFKA_SOURCE = "kafka";

    public static final String DORIS_SINK = "doris";
    public static final String CLICKHOUSE_SINK = "clickhouse";
}
