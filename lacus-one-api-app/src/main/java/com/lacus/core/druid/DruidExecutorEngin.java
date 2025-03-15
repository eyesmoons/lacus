package com.lacus.core.druid;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysql.cj.jdbc.exceptions.MySQLTimeoutException;
import com.lacus.common.exception.ResultCode;
import com.lacus.common.exception.ApiException;
import com.lacus.core.buried.BuriedFunc;
import com.lacus.core.buried.LogsBuried;
import com.lacus.service.dto.ApiConfigDTO;
import com.lacus.dao.entity.DataSourceEntity;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;
import java.util.Map;

@Slf4j
public class DruidExecutorEngin {

    public static List<Map<String, Object>> execute(DataSourceEntity datasource, ApiConfigDTO config) {
        List<Map<String, Object>> result = Lists.newLinkedList();
        DruidConnector druidConnector = DruidConnector.getInstance();
        Connection connect = druidConnector.getConnect(datasource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = config.getSql();
        String apiName = config.getApiName();
        try {
            List<String> preSQLs = config.getPreSQL();
            if (CollectionUtil.isNotEmpty(preSQLs)) {
                for (String preSQL : preSQLs) {
                    ps = connect.prepareStatement(preSQL);
                    ps.executeUpdate();
                }
                BuriedFunc.addLog(LogsBuried.setInfo(String.format("前置SQL执行完成，前置SQL列表为:【%s】！", preSQLs)));
                log.info("前置SQL执行完毕,当前接口:{},前置SQL:{}", apiName, preSQLs);
            }
            ps = connect.prepareStatement(sql);
            ps.setQueryTimeout(config.getQueryTimeout());
            rs = ps.executeQuery();
            if (rs != null) {
                // 获取列数
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                // 遍历ResultSet中的每条数据
                while (rs.next()) {
                    Map<String, Object> row = Maps.newHashMap();
                    // 遍历每一列
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(columnName);
                        if (value instanceof Double || value instanceof Float) {
                            value = rs.getBigDecimal(columnName).stripTrailingZeros();
                        }
                        row.put(columnName, value);
                    }
                    result.add(row);
                }
            }
            return result;
        } catch (MySQLTimeoutException mtx) {
            log.info("SQL查询超时，api名称:{},错误SQL:{}", apiName, sql);
            throw new ApiException(ResultCode.SQL_RUNNING_TIMEOUT, mtx.getMessage());
        } catch (Exception ex) {
            log.info("SQL执行失败，api名称:{},错误SQL:{},错误原因:", apiName, sql, ex);
            throw new ApiException(ResultCode.SQL_RUNTIME_ERROR, ex.getMessage());
        } finally {
            try {
                if (connect != null) {
                    ((DruidPooledConnection) connect).recycle();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                log.info("数据源连接回收失败:", e);
            }
        }
    }
}
