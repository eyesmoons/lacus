package com.lacus.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JdbcUtil {

    public static void main(String[] args) {
        JdbcUtil.executeQuery(null, null, null, "", "show backends");
    }

    public static List<JSONObject> executeQuery(String hostUrl, String db, String user, String password, String sql) {
        List<JSONObject> beJson = new ArrayList<>();
        String connectionUrl = String.format("jdbc:mysql://%s/%s", hostUrl, db);
        String jdbcDriverName = "com.mysql.cj.jdbc.Driver";
        Connection con = null;
        try {

            Class.forName(jdbcDriverName);
            con = DriverManager.getConnection(connectionUrl, user, password);
            PreparedStatement ps = con.prepareStatement(sql);
            long start = System.currentTimeMillis();
            ResultSet rs = ps.executeQuery();
            long end = System.currentTimeMillis();
            log.info("SQL执行耗时:{}ms", (end - start));
            beJson = resultSetToJson(rs);
        } catch (SQLException e) {
            log.error("SQL执行异常", e);
        } catch (Exception e) {
            log.error("SQL执行错误", e);
        } finally {
            try {
                assert con != null;
                con.close();
            } catch (Exception e) {
                log.warn("连接关闭错误", e);
            }
        }
        return beJson;
    }


    public static List<JSONObject> resultSetToJson(ResultSet rs) throws SQLException {
        //定义接收的集合
        List<JSONObject> list = new ArrayList<>();
        // 获取列数
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 遍历ResultSet中的每条数据
        while (rs.next()) {
            JSONObject jsonObj = new JSONObject();
            // 遍历每一列
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                String value = rs.getString(columnName);
                jsonObj.put(columnName, value);
            }
            list.add(jsonObj);
        }
        return list;
    }
}
