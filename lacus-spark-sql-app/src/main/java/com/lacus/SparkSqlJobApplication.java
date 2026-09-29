package com.lacus;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SparkSqlJobApplication {
    private static final Logger logger = LoggerFactory.getLogger(SparkSqlJobApplication.class);
    private static final String SQL_DELIMITER = ";";

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("SQL file path is required");
        }

        String sqlFilePath = args[0];
        logger.info("Reading SQL file from: {}", sqlFilePath);

        try {
            // 读取SQL文件内容
            String sqlContent = new String(Files.readAllBytes(Paths.get(sqlFilePath)));

            // 分割多条SQL语句
            List<String> sqlStatements = Arrays.stream(sqlContent.split(SQL_DELIMITER))
                    .map(String::trim)
                    .filter(sql -> !sql.isEmpty())
                    .collect(Collectors.toList());

            logger.info("Found {} SQL statements to execute", sqlStatements.size());

            // 创建SparkSession，并开启Hive支持
            try (SparkSession spark = SparkSession.builder()
                    .appName("Spark SQL Runner")
                    .enableHiveSupport()
                    .getOrCreate()) {
                // 按顺序执行每条SQL
                for (int i = 0; i < sqlStatements.size(); i++) {
                    String sql = sqlStatements.get(i);
                    logger.info("Executing SQL {}/{}: {}", i + 1, sqlStatements.size(), sql);

                    try {
                        spark.sql(sql);
                        logger.info("SQL {}/{} executed successfully", i + 1, sqlStatements.size());
                    } catch (Exception e) {
                        logger.error("Error executing SQL {}/{}: {}", i + 1, sqlStatements.size(), e.getMessage());
                        throw e;
                    }
                }
                logger.info("All SQL statements executed successfully");
            }
            // 关闭SparkSession
        } catch (IOException e) {
            logger.error("Error reading SQL file: {}", e.getMessage(), e);
            System.exit(1);
        } catch (Exception e) {
            logger.error("Error executing SQL statements: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
