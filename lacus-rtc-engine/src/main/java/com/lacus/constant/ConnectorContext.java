package com.lacus.constant;

import java.io.Serializable;

/**
 * 处理器常量
 * @created by shengyu on 2024/1/21 20:29
 */
public class ConnectorContext implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String MYSQL_SOURCE = "MYSQL";
    public static final String SQLSERVER_SOURCE = "SQLSERVER";
    public static final String ORACLE_SOURCE = "ORACLE";
    public static final String POSTGRES_SOURCE = "POSTGRES";
    public static final String MONGODB_SOURCE = "MONGODB";
    public static final String KAFKA_SOURCE = "KAFKA";

    public static final String DORIS_SINK = "DORIS";
    public static final String MYSQL_SINK = "MYSQL";
    public static final String CLICKHOUSE_SINK = "CLICKHOUSE";
}
