package com.lacus.job.utils;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.collect.Lists;
import com.lacus.job.exception.SinkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lacus.job.constants.SinkResponse.*;

public class DruidJdbcUtils {

    private static final Logger log = LoggerFactory.getLogger(DruidJdbcUtils.class);

    private final String driverClassName = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://%s:%s/%s?serverTimezone=UTC";
    private final String initialSize = "2";
    private final String maxActive = "5";
    private final String maxWait = "3000";
    private final String minIdle = "1";
    private String jdbcUrl;
    private String userName;
    private String password;
    private DataSource ds = null;
    private boolean inited = false;


    private final Map<String, Object> connectMap = new HashMap<>(8);


    public DruidJdbcUtils(String ip, Integer port, String dbName, String userName, String password) {
        this.jdbcUrl = String.format(url, ip, port, dbName);
        this.userName = userName;
        this.password = password;
    }

    private void init() {
        connectMap.put("driverClassName", this.driverClassName);
        connectMap.put("url", this.jdbcUrl);
        connectMap.put("username", this.userName);
        connectMap.put("password", this.password);
        connectMap.put("initialSize", this.initialSize);
        connectMap.put("maxActive", this.maxActive);
        connectMap.put("maxWait", this.maxWait);
        connectMap.put("minIdle", this.minIdle);
    }


    // init druid datasource connect
    private Connection getConnect() {
        try {
            if (inited) {
                return ds.getConnection();
            }
            this.init();
            ds = DruidDataSourceFactory.createDataSource(this.connectMap);
            inited = true;
            return ds.getConnection();
        } catch (SQLException sex) {
            log.error("druid get mysql connect failed：{}", sex.getMessage());
            throw new SinkException(DRUID_JDBC_CONNECT_OBTAIN_FAILED, sex);
        } catch (Exception e) {
            log.error("druid datasource init failed:{}", e.getMessage());
            throw new SinkException(DRUID_DATASOURCE_INIT_FAILED, e);
        }
    }

    //close connect
    private void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //query sql result
    public List<Map<String, Object>> execQuery(String sql) {
        List<Map<String, Object>> resultList = Lists.newArrayList();
        Connection cn = getConnect();
        PreparedStatement ps = null;
        try {
            ps = cn.prepareStatement(sql);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet == null) {
                throw new SinkException(QUERY_SQL_RESULT_ERROR);
            }
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> resultMap = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    resultMap.put(metaData.getColumnName(i), resultSet.getString(i));
                }
                resultList.add(resultMap);
            }
            close(resultSet, ps, cn);
        } catch (SQLException sex) {
            log.error("query sql result error:{}", sex.getMessage());
            throw new SinkException(QUERY_SQL_RESULT_ERROR, sex);
        }
        return resultList;
    }


}