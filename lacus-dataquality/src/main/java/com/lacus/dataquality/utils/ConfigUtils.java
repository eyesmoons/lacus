package com.lacus.dataquality.utils;

import java.util.Map;

/**
 * 配置参数工具类
 */
public class ConfigUtils {

    // Reader 常量
    public static final String URL = "url";
    public static final String DATABASE = "database";
    public static final String TABLE = "table";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String DRIVER = "driver";
    public static final String OUTPUT_TABLE = "output_table";
    public static final String INPUT_TABLE = "input_table";
    public static final String TMP_TABLE = "tmp_table";
    public static final String SQL = "sql";
    public static final String SAVE_MODE = "save_mode";
    public static final String PATH = "path";
    public static final String FORMAT = "format";
    public static final String SPARK_APP_NAME = "spark.app.name";

    private ConfigUtils() {
    }

    /**
     * 从 Map 中安全获取字符串值
     */
    public static String getString(Map<String, Object> config, String key) {
        if (config == null) {
            return null;
        }
        Object value = config.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 判断 key 是否存在且非空
     */
    public static boolean has(Map<String, Object> config, String key) {
        if (config == null) {
            return false;
        }
        Object value = config.get(key);
        return value != null && !String.valueOf(value).trim().isEmpty();
    }
}
