package com.lacus.sink.jdbc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Data
@Slf4j
public class JdbcSinkOption implements Serializable {

    private static final long serialVersionUID = -7541222854019372396L;

    private String protocol;
    private String host;
    private Integer port;
    private String db;
    private String tbl;
    private String user;
    private String passwd;
    private Map<String, String> conf;
    private String columns;


    public JdbcSinkOption(String protocol, String host, Integer port, String db, String tbl, String user, String passwd, Map<String, String> conf, String columns) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.db = db;
        this.tbl = tbl;
        this.user = user;
        this.passwd = passwd;
        this.conf = conf;
        this.columns = columns;
    }
}
