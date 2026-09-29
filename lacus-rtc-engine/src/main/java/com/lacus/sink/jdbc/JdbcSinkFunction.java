package com.lacus.sink.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author shengyu
 * @date 2025/9/18 19:15
 */
@Slf4j
public class JdbcSinkFunction extends RichSinkFunction<Map<String, String>> {
    private static final long serialVersionUID = -1L;

    private final Map<String, JdbcSinkOption> jdbcSinkOptionMap;

    public JdbcSinkFunction(Map<String, JdbcSinkOption> jdbcSinkOptionMap) {
        this.jdbcSinkOptionMap = jdbcSinkOptionMap;
    }

    private transient Connection connection;
    private transient ObjectMapper objectMapper;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        objectMapper = new ObjectMapper();
    }

    @Override
    public void invoke(Map<String, String> map, Context context) throws SQLException {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            JdbcSinkOption jdbcSinkOption = jdbcSinkOptionMap.get(entry.getKey());
            String db = jdbcSinkOption.getDb();
            String table = jdbcSinkOption.getTbl();
            String jdbcUrl = String.format("jdbc:%s://%s:%s/%s",
                    jdbcSinkOption.getProtocol(),
                    jdbcSinkOption.getHost(),
                    jdbcSinkOption.getPort(),
                    jdbcSinkOption.getDb());
            connection = DriverManager.getConnection(jdbcUrl, jdbcSinkOption.getUser(), jdbcSinkOption.getPasswd());
            connection.setAutoCommit(false);

            String jsonArrayStr = entry.getValue();   // [{"id":"1","name":"casey"}]

            ArrayNode records;
            try {
                records = (ArrayNode) objectMapper.readTree(jsonArrayStr);
            } catch (JsonProcessingException e) {
                log.error("json parse exception", e);
                continue;
            }

            if (records != null && !records.isEmpty()) {
                JsonNode firstRecord = records.get(0);
                List<String> columns = new ArrayList<>();
                Iterator<Map.Entry<String, JsonNode>> firstFields = firstRecord.fields();
                while (firstFields.hasNext()) {
                    columns.add(firstFields.next().getKey());
                }

                String sql = String.format("INSERT INTO %s.%s (%s) VALUES (%s)",
                        db,
                        table,
                        String.join(",", columns),
                        String.join(",", columns.stream().map(col -> "?").toArray(String[]::new)));

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    for (JsonNode record : records) {
                        for (int i = 0; i < columns.size(); i++) {
                            stmt.setString(i + 1, record.get(columns.get(i)).asText());
                        }
                        stmt.addBatch();
                    }

                    int[] count = stmt.executeBatch();
                    connection.commit();
                    log.info("成功插入了 {} 行数据到表 {}", count.length, table);
                } catch (Exception e) {
                    try {
                        connection.rollback();
                    } catch (Exception rollbackEx) {
                        log.error("事务回滚失败", rollbackEx);
                    }
                    log.error("批量写入失败: {}, 数据: {}", sql, jsonArrayStr, e);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
            log.info("JDBC connection closed.");
        }
    }
}